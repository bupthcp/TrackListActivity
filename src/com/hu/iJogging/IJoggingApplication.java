package com.hu.iJogging;

import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.services.RemoveTempFilesService;
import com.google.android.apps.mytracks.util.AnalyticsUtils;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.hu.iJogging.common.ConfigFree;

import android.app.Application;
import android.content.Intent;

public class IJoggingApplication extends Application{
  private TrackDataHub trackDataHub;
  
  public static final String mStrKey = "9D523C2DF19F58B614526AD0B1270698A9B8234C";
  
  @Override
  //��������app���˳�֮ǰ����mapadpi��destroy()�����������ظ���ʼ��������ʱ������
  public void onTerminate() {
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
  
}
