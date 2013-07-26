package com.hu.iJogging.Services;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.google.android.maps.mytracks.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.hu.iJogging.IJoggingApplication;
import com.hu.iJogging.common.IJoggingDatabaseUtils;
import com.hu.iJogging.common.NotificationCode;
import com.hu.iJogging.common.OfflineCity;
import com.hu.iJogging.common.OfflineCityItem;
import com.hu.iJogging.common.OfflineMapCitiesParser;
import com.hu.iJogging.common.ZipUtils;

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
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class DownloadOfflineMapService extends Service implements MKOfflineMapListener {
  
  public static final String TAG = DownloadOfflineMapService.class.getSimpleName();

  static DownloadOfflineMapService downloadOfflineMapService;
  private IBinder downloadOfflineMapServiceBinder = null;
  //�ٶ�MapAPI�Ĺ�����
  public static BMapManager mBMapMan = null;
  private static MKOfflineMap mOffline = null;
  public static Set<OfflineCityItem> offlineCities = new HashSet<OfflineCityItem>();
  // ��ȨKey
  // TODO: ����������Key,
  // �����ַ��http://dev.baidu.com/wiki/static/imap/key/
  public String mStrKey = "9D523C2DF19F58B614526AD0B1270698A9B8234C";
  public boolean m_bKeyRight = true; // ��ȨKey��ȷ����֤ͨ��
  public static final String BMAP_SDK_PATH ="/BaiduMapSdk";
  
  private boolean isBaiduMapInited = false;
  
  private String baiduMapUrl = "http://shouji.baidu.com/resource/xml/map/city.xml";
  private String baiduMapUrlVector = "http://shouji.baidu.com/resource/xml/map/city_vector.xml";
  
  private IJoggingDatabaseUtils iJoggingDatabaseUtils = null;

  private boolean isDownloading = false;
  private InitOfflineMapTask initOfflineMapTask = new InitOfflineMapTask();
  
  DownloadOfflineListeners downloadOfflineListeners;
  DownloadManager downloadMgr=null;
  
  private BroadcastReceiver onOfflineDownloadComplete = new BroadcastReceiver() {
    public void onReceive(Context ctxt, Intent intent) {
      if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
        long downloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        Log.v(TAG, " download complete! id : " + downloadID);
        if (downloadID == -1) 
          return;
        String fileUriTemp = getDownloadFileUri(downloadID);
        if(fileUriTemp != null){
          InstallOfflineMapTask installOfflineMapTask = new InstallOfflineMapTask();
          installOfflineMapTask.execute(fileUriTemp);
        }
        DownloadsTimerObserver observer = DownloadsTimerObserver.getObserver(downloadID);
        if(observer != null){
          observer.stopObserve();
        }
      }
    }
  };
  
  private BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
    public void onReceive(Context ctxt, Intent intent) {
      if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
        Intent dm = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        dm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dm);
      }
    }
  };
  
  private String getDownloadFileUri(long downloadID){
    if (downloadID == -1) 
      return null;
    Cursor cursor = downloadMgr.query(new DownloadManager.Query().setFilterById(downloadID));
    if (cursor != null) {
      try{
        cursor.moveToFirst();
        int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
        String uri = cursor.getString(fileNameIndex);
        cursor.close();
        // uri����file:��ͷ�ģ������ļ�ϵͬĬ����/��ͷ�ģ�������Ҫ��file:ȥ��
        String fileUriTemp = uri;
        if (fileUriTemp.startsWith("file:")) {
          fileUriTemp = fileUriTemp.substring(5);
        }
        return fileUriTemp;
      }catch(Exception e){
        e.printStackTrace();
        return null;
      }
    }else{
      return null;
    }
  }
  
  void notifyOfflineUpdate(){
    final Set<DownloadOfflineListener> listeners = downloadOfflineListeners
        .getRegisteredListeners();
    for (DownloadOfflineListener listener : listeners) {
      listener.notifyOfflineMapStateUpdate();
    }
  }

  @Override
  public void onCreate() {
    //showNotification();
  }
  
  /*baidu��ͼ��sdk��init��ʱ����http://dl.imap.baidu.com/update/VerDatset.dat
  *�����ĸ�dat�ļ�,���ĸ����������������sdk��Ҫ�����ߵ�ͼ��Ϣ������ͨ��sdkȥ����
  *���ߵ�ͼ���г�����������֣���������ʹ���Լ���ȡ��ͼѹ�����ķ�ʽ���й���
  *��Ӧ�ó���ʼ����ʱ���ͽ�baidumapsdk���Ŀ¼��ʼ���ã�����sdk�ٴδ���������
  */
  private void initBaiduMapSdkDir(){
    
  }
  
  
  // �����¼���������������ͨ�������������Ȩ��֤�����
  public static class MyGeneralListener implements MKGeneralListener {
      @Override
      public void onGetNetworkState(int iError) {
          Log.d("MyGeneralListener", "onGetNetworkState error is "+ iError);
          Toast.makeText(downloadOfflineMapService, "���������������",
                  Toast.LENGTH_LONG).show();
      }

      @Override
      public void onGetPermissionState(int iError) {
          Log.d("MyGeneralListener", "onGetPermissionState error is "+ iError);
          if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
              // ��ȨKey����
              Toast.makeText(downloadOfflineMapService, 
                      "����BMapApiDemoApp.java�ļ�������ȷ����ȨKey��",
                      Toast.LENGTH_LONG).show();
              downloadOfflineMapService.m_bKeyRight = false;
          }
      }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    iJoggingDatabaseUtils = ((IJoggingApplication)getApplication()).getIJoggingDatabaseUtils();
    if(null != iJoggingDatabaseUtils){
      if(iJoggingDatabaseUtils.getAllOfflineCitiesCount() == 0){
        initOfflineMapTask.execute();
      }
    }
    downloadOfflineListeners = new DownloadOfflineListeners();
    downloadOfflineMapService = this;
    downloadOfflineMapServiceBinder = new DownloadOfflineMapServiceBinder();
    Log.d(TAG,"DownloadOfflineMapService created");
    if(null == downloadMgr){
      downloadMgr = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
      registerReceiver(onOfflineDownloadComplete,
          new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
      registerReceiver(onNotificationClick,
          new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
    }
    return START_NOT_STICKY;
  }
  
  
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(onOfflineDownloadComplete);
  }
  
 
  
  private class InstallOfflineMapTask extends AsyncTask<String,Void,Void>{

    @Override
    protected Void doInBackground(String... params) {
      try{
        ZipUtils.unZipOneFolder(params[0],Environment.getExternalStorageDirectory().getPath() +"/BaiduMapSdk","BaiduMap","utf-8");
        File file = new File(params[0]);
        file.delete();
      }catch(Exception e){
        e.printStackTrace();
      }
      return null;
    }
    
  }


  private class InitOfflineMapTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpUriRequest request = new HttpGet(baiduMapUrl);
      try {
        HttpResponse response = (HttpResponse) httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        InputStream input = entity.getContent();

        OfflineMapCitiesParser parser = new OfflineMapCitiesParser();
        Set<OfflineCityItem> offlineCities =  parser.parse(input);
        iJoggingDatabaseUtils.updateAllCities(offlineCities);
      } catch (Exception e) {
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
      boolean result = mOffline.start(cityId);
      isDownloading = result;
      //showNotification();
      return result;
    }

    public boolean pauseDownload(int cityId) {
      boolean result = mOffline.pause(cityId);
      isDownloading = result;
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
      if(downloadId != -1L){
        String fileUri = getDownloadFileUri(downloadId);
        if(fileUri != null){
          DownloadsTimerObserver observer = new DownloadsTimerObserver(downloadId, cityName, DownloadOfflineMapService.this);
          observer.startObserve();
        }
      }
    }
    
    public boolean isDownloading(){
      return isDownloading;
    }
    
    public MKOLUpdateElement getOfflineUpdateInfo(int cityID){
      return mOffline.getUpdateInfo(cityID);
    }
    
    public MKOfflineMap getOfflineInstance(){
      if(mOffline == null){
        mOffline = new MKOfflineMap();
      }
      return mOffline;
    }
    
    public void resetBaiduMapSDK(){
//      if (mOffline != null) {
//        mOffline.init(mBMapMan, DownloadOfflineMapService.this);
//        mOffline.scan();
//      }
      if(mBMapMan != null){
        mBMapMan.stop();
        mBMapMan.destroy();
        mBMapMan = null;
      }
      mBMapMan = new BMapManager(DownloadOfflineMapService.this);
    }
    
    
    //���������ֱ�Ӳ���SDK�е������ļ����������е�json���󣬵��Ǵ�ʵ�ʵ�ʹ�ý������������Ч
    //����SDKʹ���˲�ͬ��json�����⣬���Ҷ�json�и�������Ƚ��ϸ����ԣ�ͨ��ֱ�Ӳ���json�ķ�ʽ
    //�޷���Ч
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
    
    public void startInitMapDatabase(){
      initOfflineMapTask.execute();
    }
  }

  @Override
  public void onGetOfflineMapState(int type, int state) {
    switch (type) {
      case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
        Log.d(TAG, String.format("cityid:%d update", state));
      }
        break;
        //����scan���֮��ᷢ�������Ϣ
      case MKOfflineMap.TYPE_NEW_OFFLINE:
        Log.d(TAG, String.format("add offlinemap num:%d", state));
        break;
      case MKOfflineMap.TYPE_VER_UPDATE:
        Log.d(TAG, String.format("new offlinemap ver"));
        break;
      default:
        break;
    }
  }

}
