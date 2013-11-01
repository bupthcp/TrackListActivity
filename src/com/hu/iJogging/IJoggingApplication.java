package com.hu.iJogging;

import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.services.RemoveTempFilesService;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.hu.iJogging.common.ConfigFree;
import com.hu.walkingnotes.support.utils.GlobalContext;

import android.content.Intent;

public class IJoggingApplication extends GlobalContext{
  private TrackDataHub trackDataHub;
  
  public static final String mStrKey = "0Fd301589935a490ad5748edbda82c08";
  
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
