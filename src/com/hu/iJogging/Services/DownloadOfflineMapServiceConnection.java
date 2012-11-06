package com.hu.iJogging.Services;

import com.google.android.maps.mytracks.BuildConfig;
import com.hu.iJogging.Services.DownloadOfflineMapService.DownloadOfflineMapServiceBinder;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

public class DownloadOfflineMapServiceConnection {
  
  private static final String TAG = DownloadOfflineMapServiceConnection.class.getSimpleName();
  
  private DownloadOfflineMapServiceBinder boundService;
  
  private final Context context;
  private final Runnable bindChangedCallback;

  private final DeathRecipient deathRecipient = new DeathRecipient() {
    @Override
    public void binderDied() {
      Log.d(TAG, "Service died");
      setBoundService(null);
    }
  };
  
  public DownloadOfflineMapServiceConnection(Context ctx,Runnable bindChangedCallback){
    context = ctx;
    this.bindChangedCallback = bindChangedCallback;
  }
  
  
  public DownloadOfflineMapServiceBinder getServiceIfBound() {
    checkBindingAlive();
    return boundService;
  }

  private void checkBindingAlive() {
    if (boundService != null &&
        !boundService.isBinderAlive()) {
      setBoundService(null);
    }
  }
  
  public void startService(){
    Log.i(TAG, "Starting the service");
    Intent intent = new Intent(context, DownloadOfflineMapService.class);
    context.startService(intent);
  }
  
  public void bindService(){
    if(!isOfflineServiceRunning(context)){
      Log.d(TAG, "service not started");
      return;
    }
    Log.i(TAG, "Binding to the service");
    Intent intent = new Intent(context, DownloadOfflineMapService.class);
    int flags = BuildConfig.DEBUG ? Context.BIND_DEBUG_UNBIND : 0;
    context.bindService(intent, serviceConnection, flags);
  }
  
  
  public static boolean isOfflineServiceRunning(Context context) {
    ActivityManager activityManager = (ActivityManager) context.getSystemService(
        Context.ACTIVITY_SERVICE);
    List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

    for (RunningServiceInfo serviceInfo : services) {
      ComponentName componentName = serviceInfo.service;
      String serviceName = componentName.getClassName();
      if (DownloadOfflineMapService.class.getName().equals(serviceName)) {
        return true;
      }
    }
    return false;
  }
  
  private final ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      Log.i(TAG, "Connected to service");
      try {
        service.linkToDeath(deathRecipient, 0);
      } catch (RemoteException e) {
        Log.e(TAG, "Failed to bind a death recipient", e);
      }
      setBoundService((DownloadOfflineMapServiceBinder)service);
    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
      Log.i(TAG, "Disconnected from service");
      setBoundService(null);
    }
  };
  
  private void setBoundService(DownloadOfflineMapServiceBinder service) {
    boundService = service;
    if (bindChangedCallback != null) {
      bindChangedCallback.run();
    }
  }
  
  public void unbind() {
    Log.d(TAG, "Unbinding from the service");
    try {
      context.unbindService(serviceConnection);
    } catch (IllegalArgumentException e) {
      // Means we weren't bound, which is ok.
    }

    setBoundService(null);
  }
  
  public void stop() {
    unbind();

    Log.d(TAG, "Stopping service");
    Intent intent = new Intent(context, DownloadOfflineMapService.class);
    context.stopService(intent);
  }
}
