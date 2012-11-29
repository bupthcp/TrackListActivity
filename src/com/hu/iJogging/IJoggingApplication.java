package com.hu.iJogging;

import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.services.RemoveTempFilesService;
import com.google.android.apps.mytracks.util.AnalyticsUtils;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.google.android.maps.mytracks.BuildConfig;
import com.hu.iJogging.common.ConfigFree;
import com.hu.iJogging.common.IJoggingDatabaseUtils;

import android.app.Application;
import android.content.Intent;

public class IJoggingApplication extends Application{
  private TrackDataHub trackDataHub;
  private IJoggingDatabaseUtils iJoggingDatabaseUtils;
  
  @Override
  //��������app���˳�֮ǰ����mapadpi��destroy()�����������ظ���ʼ��������ʱ������
  public void onTerminate() {
      // TODO Auto-generated method stub
      super.onTerminate();
  }
  @Override
  public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      ApiAdapterFactory.getApiAdapter().enableStrictMode();
    }
    AnalyticsUtils.sendPageViews(getApplicationContext(), "/appstart");
    Intent intent = new Intent(this, RemoveTempFilesService.class);
    startService(intent);
    ConfigFree.configure();
  }

  /**
   * Gets the application's TrackDataHub.
   * 
   * Note: use synchronized to make sure only one instance is created per application.
   */
  public synchronized TrackDataHub getTrackDataHub() {
    if (trackDataHub == null) {
      trackDataHub = TrackDataHub.newInstance(getApplicationContext());
    }
    return trackDataHub;
  }
  
  public synchronized IJoggingDatabaseUtils getIJoggingDatabaseUtils(){
    if(iJoggingDatabaseUtils == null){
      iJoggingDatabaseUtils = new IJoggingDatabaseUtils(getApplicationContext());
    }
    return iJoggingDatabaseUtils;
  }
}