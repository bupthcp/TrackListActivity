package com.hu.iJogging.Services;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKOLUpdateElement;
import com.baidu.mapapi.MKOfflineMap;
import com.baidu.mapapi.MKOfflineMapListener;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.common.NotificationCode;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class DownloadOfflineMapService extends Service implements MKOfflineMapListener {
  
  public static final String TAG = DownloadOfflineMapService.class.getSimpleName();

  static DownloadOfflineMapService downloadOfflineMapService;
  private IBinder downloadOfflineMapServiceBinder = null;
  //百度MapAPI的管理类
  public static BMapManager mBMapMan = null;
  public static MKOfflineMap mOffline = null;
  // 授权Key
  // TODO: 请输入您的Key,
  // 申请地址：http://dev.baidu.com/wiki/static/imap/key/
  public String mStrKey = "9D523C2DF19F58B614526AD0B1270698A9B8234C";
  public boolean m_bKeyRight = true; // 授权Key正确，验证通过
  private boolean isBaiduMapInited = false;

  private boolean isDownloading = false;
  private InitOfflineMapTask initOfflineMapTask = null;

  @Override
  public void onCreate() {
    downloadOfflineMapService = this;
    downloadOfflineMapServiceBinder = new DownloadOfflineMapServiceBinder();
    mBMapMan = new BMapManager(DownloadOfflineMapService.this);
    mBMapMan.init(mStrKey, new MyGeneralListener());
    mBMapMan.getLocationManager().setNotifyInternal(10, 5);
    mBMapMan.start();
    mOffline = new MKOfflineMap();
    Log.d(TAG,"InitOfflineMapTask created");
    mOffline.init(mBMapMan, new MKOfflineMapListener() {
      @Override
      public void onGetOfflineMapState(int type, int state) {
        switch (type) {
          case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
            MKOLUpdateElement update = mOffline.getUpdateInfo(state);
            // mText.setText(String.format("%s : %d%%", update.cityName,
            // update.ratio));
          }
            break;
          case MKOfflineMap.TYPE_NEW_OFFLINE:
            Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
            break;
          case MKOfflineMap.TYPE_VER_UPDATE:
            Log.d("OfflineDemo", String.format("new offlinemap ver"));
            break;
        }
      }
    });
    Log.d(TAG,"DownloadOfflineMapService created");
    showNotification();
  }
  
  
  // 常用事件监听，用来处理通常的网络错误，授权验证错误等
  public static class MyGeneralListener implements MKGeneralListener {
      @Override
      public void onGetNetworkState(int iError) {
          Log.d("MyGeneralListener", "onGetNetworkState error is "+ iError);
          Toast.makeText(downloadOfflineMapService, "您的网络出错啦！",
                  Toast.LENGTH_LONG).show();
      }

      @Override
      public void onGetPermissionState(int iError) {
          Log.d("MyGeneralListener", "onGetPermissionState error is "+ iError);
          if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
              // 授权Key错误：
              Toast.makeText(downloadOfflineMapService, 
                      "请在BMapApiDemoApp.java文件输入正确的授权Key！",
                      Toast.LENGTH_LONG).show();
              downloadOfflineMapService.m_bKeyRight = false;
          }
      }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    return START_STICKY;
  }
  
  private class InitOfflineMapTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {

      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      isBaiduMapInited = true;
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

  private class DownloadOfflineMapServiceBinder extends Binder {
    public boolean startDownload(int cityId) {
      if (isDownloading) { return false; }
      boolean result = mOffline.start(cityId);
      isDownloading = true;
      return result;
    }

    public boolean pauseDownload(int cityId) {
      if (!isDownloading) { return false; }
      boolean result = mOffline.pause(cityId);
      isDownloading = false;
      return result;
    }
    
    
    public boolean isBaiduMapInited(){
      return isBaiduMapInited;
    }
  }

  @Override
  public void onGetOfflineMapState(int type, int state) {
    switch (type) {
      case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
        Log.d("OfflineDemo", String.format("cityid:%d update", state));
        MKOLUpdateElement update = mOffline.getUpdateInfo(state);
      }
        break;
      case MKOfflineMap.TYPE_NEW_OFFLINE:
        Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
        break;
      case MKOfflineMap.TYPE_VER_UPDATE:
        Log.d("OfflineDemo", String.format("new offlinemap ver"));
        break;
      default:
        break;
    }
  }

}
