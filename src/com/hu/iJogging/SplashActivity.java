package com.hu.iJogging;

import com.google.android.maps.mytracks.R;
import com.hu.iJogging.Services.DownloadOfflineMapService.DownloadOfflineMapServiceBinder;
import com.hu.iJogging.Services.DownloadOfflineMapServiceConnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

public class SplashActivity extends Activity{
  
  private static final String TAG = SplashActivity.class.getSimpleName();
  
  private Handler handler= new Handler();
  private DownloadOfflineMapServiceConnection downloadOfflineMapServiceConnection;
  
  private final Runnable bindChangedCallback = new Runnable() {
    @Override
    public void run() {
      DownloadOfflineMapServiceBinder service = downloadOfflineMapServiceConnection.getServiceIfBound();
      if (service == null) {
        Log.d(TAG, "downloadOfflineMapService service not available");
        return;
      }
      downloadOfflineMapServiceConnection.unbind();
    }
  };
  
  
  private final Runnable delayedJob = new Runnable() {
    @Override
    public void run() {
      Intent intent = new Intent(SplashActivity.this, IJoggingActivity.class);
      startActivity(intent);
      finish();
    }
  };

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    downloadOfflineMapServiceConnection = new DownloadOfflineMapServiceConnection(this,bindChangedCallback);
    downloadOfflineMapServiceConnection.bindService();
    handler.postDelayed(delayedJob, 500);
    setContentView(R.layout.splash_activity);
  }
}
