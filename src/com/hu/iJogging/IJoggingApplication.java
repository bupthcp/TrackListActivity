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
  //建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
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
