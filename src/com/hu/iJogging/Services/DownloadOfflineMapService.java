package com.hu.iJogging.Services;

import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.maps.mytracks.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.hu.iJogging.common.NotificationCode;
import com.hu.iJogging.common.OfflineCity;
import com.hu.iJogging.common.OfflineCityItem;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

public class DownloadOfflineMapService extends Service {
  
  public static final String TAG = DownloadOfflineMapService.class.getSimpleName();

  static DownloadOfflineMapService downloadOfflineMapService;
  private IBinder downloadOfflineMapServiceBinder = null;

  public static Set<OfflineCityItem> offlineCities = new HashSet<OfflineCityItem>();
  // ��ȨKey
  // TODO: ����������Key,
  // �����ַ��http://dev.baidu.com/wiki/static/imap/key/
  public String mStrKey = "9D523C2DF19F58B614526AD0B1270698A9B8234C";
  public boolean m_bKeyRight = true; // ��ȨKey��ȷ����֤ͨ��
  private boolean isBaiduMapInited = false;
  
  private String baiduMapUrl = "http://shouji.baidu.com/resource/xml/map/city.xml";
  private String baiduMapUrlVector = "http://shouji.baidu.com/resource/xml/map/city_vector.xml";

  private boolean isDownloading = false;
  private InitOfflineMapTask initOfflineMapTask = new InitOfflineMapTask();
  
  private HandlerThread listenerHandlerThread;
  private Handler listenerHandler;
  private DownloadOfflineListeners downloadOfflineListeners;
  
  public static final String BMAP_SDK_PATH ="/BaiduMapSdk";
  private DownloadManager downloadMgr=null;
  
  private BroadcastReceiver onOfflineDownloadComplete=new BroadcastReceiver() {
    public void onReceive(Context ctxt, Intent intent) {
      long downloadId = PreferencesUtils.getLong(DownloadOfflineMapService.this, R.string.offline_download_id_key);
      if(downloadId == -1L)
        return;
      Cursor cursor = downloadMgr.query(new DownloadManager.Query().setFilterById(downloadId));
      if(cursor != null){
        cursor.moveToFirst();
        int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
        String uri = cursor.getString(fileNameIndex);
        cursor.close();
        InstallOfflineMapTask installOfflineMapTask = new InstallOfflineMapTask();
        installOfflineMapTask.execute(uri);
      }
    }
  };

  @Override
  public void onCreate() {
    //showNotification();
  }
  
  /*baidu��ͼ��sdk��init��ʱ����http://dl.imap.baidu.com/update/VerDatset.dat
  *�����ĸ�dat�ļ�,���ĸ����������������sdk��Ҫ�����ߵ�ͼ��Ϣ������ͨ��sdkȥ����
  *���ߵ�ͼ���г�����������֣���������ʹ���Լ���ȡ��ͼѹ�����ķ�ʽ���й�����
  *��Ӧ�ó���ʼ����ʱ���ͽ�baidumapsdk���Ŀ¼��ʼ���ã�����sdk�ٴδ���������
  */
  private void initBaiduMapSdkDir(){
    
  }
  
  

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    listenerHandlerThread = new HandlerThread("downLoadOfflineListenerThread");
    listenerHandlerThread.start();
    listenerHandler = new Handler(listenerHandlerThread.getLooper());
    downloadOfflineListeners = new DownloadOfflineListeners();
    downloadOfflineMapService = this;
    downloadOfflineMapServiceBinder = new DownloadOfflineMapServiceBinder();
    Log.d(TAG,"DownloadOfflineMapService created");
    if(null == downloadMgr){
      downloadMgr = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
      registerReceiver(onOfflineDownloadComplete,
          new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    return START_NOT_STICKY;
  }
  
  
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    listenerHandlerThread.getLooper().quit();
    listenerHandlerThread = null;
    unregisterReceiver(onOfflineDownloadComplete);
  }
  
  
  private void runInListenerThread(Runnable runnable) {
    if (listenerHandler == null) {
      // Use a Throwable to ensure the stack trace is logged.
      Log.e(TAG, "Tried to use listener thread before start()", new Throwable());
      return;
    }
    listenerHandler.post(runnable);
  }
  
  private void notifyOfflineUpdate(){
    final Set<DownloadOfflineListener> listeners= downloadOfflineListeners.getRegisteredListeners();
    runInListenerThread(new Runnable() {
      @Override
      public void run() {
        for (DownloadOfflineListener listener : listeners) {
          listener.notifyOfflineMapStateUpdate();
        }
      }
    });
  }
  
  private class InstallOfflineMapTask extends AsyncTask<String,Void,Void>{

    @Override
    protected Void doInBackground(String... params) {
      return null;
    }
    
  }


  private class InitOfflineMapTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpUriRequest request = new HttpGet(baiduMapUrl);
      try{
        HttpResponse response = (HttpResponse)httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        InputStream input = entity.getContent();
        File file = new File("/sdcard/baiduMap.xml");
        if(file.exists()){
          file.delete();
        }
        file.createNewFile();
        OutputStream output = new FileOutputStream(file); 
        byte[] buffer = new byte[1024];  
        int len = 0;
        while((len = input.read(buffer)) >0){  
            output.write(buffer);  
        }  
        output.flush();  
        output.close();
      }catch(Exception e){
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }
  }

  private void showNotification() {
    if (isDownloading) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
          .setContentText(getString(R.string.track_record_notification))
          .setContentTitle(getString(R.string.my_tracks_app_name)).setOngoing(true)
          .setSmallIcon(R.drawable.my_tracks_notification_icon).setWhen(System.currentTimeMillis());
      startForegroundService(builder.build());
    } else {
      stopForegroundService();
    }
  }

  protected void startForegroundService(Notification notification) {
    startForeground(NotificationCode.OFFLINE_MAP_DOWNLOAD, notification);
  }

  protected void stopForegroundService() {
    stopForeground(true);
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO Auto-generated method stub
    return downloadOfflineMapServiceBinder;
  }

  public class DownloadOfflineMapServiceBinder extends Binder {
    public boolean startDownload(int cityId) {
      boolean result = true;
      return result;
    }

    public boolean pauseDownload(int cityId) {
      boolean result = true;
      return result;
    }
    
    public void startDownloadZip(String url,String cityName){
      Uri uri=Uri.parse(url);
      Request downloadRequst = new DownloadManager.Request(uri)
      .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
      .setAllowedOverRoaming(false)
      .setTitle(getString(R.string.strDownloadOfflineTitle))
      .setDescription(cityName)
      .setDestinationInExternalPublicDir(BMAP_SDK_PATH,cityName+".zip");
      long downloadId = downloadMgr.enqueue(downloadRequst);
      PreferencesUtils.setLong(DownloadOfflineMapService.this, R.string.offline_download_id_key, downloadId);
    }
    
    public boolean isDownloading(){
      return isDownloading;
    }
    

    
    
    public void deleteOfflineMap(int cityID){
      File file = new File(Environment.getExternalStorageDirectory().getPath()+"/BaiduMapSdk/OfflineUpdate.dat");
      if(!file.exists())
        return;
      try{
        FileInputStream input = new FileInputStream(file); 
        JsonReader jsonReader = new JsonReader(new InputStreamReader(input,"GBK")); 
        Gson gson = new Gson();
        jsonReader.setLenient(true);
        Type listType = new TypeToken<ArrayList<OfflineCity>>() {}.getType();
        List<OfflineCity> cities = gson.fromJson(jsonReader, listType);
        input.close();
        jsonReader = null;
        OfflineCity cityToBeDel = null;
        for(OfflineCity city:cities){
          if(city.li == cityID){
            cityToBeDel = city;
          }
        }
        if(cityToBeDel != null){
          cities.remove(cityToBeDel);
        }
        String json = gson.toJson(cities);
        FileOutputStream output = new FileOutputStream(file);
        output.write(json.getBytes());
        output.flush();
        output.close();
        Log.i(TAG, "get json");
      }catch(Exception e){
        e.printStackTrace();
      } 
    }
    
    public boolean isBaiduMapInited(){
      return isBaiduMapInited;
    }
    
    public boolean registerDownloadOfflineListener(DownloadOfflineListener listener){
      return downloadOfflineListeners.registerDownloadOfflineListener(listener);
    }
    
    public boolean unRegisterDownloadOfflineListener(DownloadOfflineListener listener){
      return downloadOfflineListeners.unRegisterDownloadOfflineListener(listener);
    }
    
    public void startDownloadXml(){
      initOfflineMapTask.execute();
    }
  }

}