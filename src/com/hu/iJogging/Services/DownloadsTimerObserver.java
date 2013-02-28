package com.hu.iJogging.Services;

import com.hu.iJogging.IJoggingApplication;
import com.hu.iJogging.common.IJoggingDatabaseUtils;

import android.app.DownloadManager;
import android.database.Cursor;
import android.util.Log;

import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


class DownloadsTimerObserver extends TimerTask{
  public static final String TAG = DownloadsTimerObserver.class.getSimpleName();
  
  private long downloadID =  -1L;
  private String cityName = null;
  private IJoggingDatabaseUtils iJoggingDatabaseUtils;
  
  private DownloadOfflineMapService downloadService;
  
  
  private static HashMap<Long , DownloadsTimerObserver> timerObserverMap = new HashMap<Long , DownloadsTimerObserver>();
  private static Timer timer = new Timer();
  
  public static DownloadsTimerObserver getObserver(long downloadID){
    DownloadsTimerObserver observer = timerObserverMap.get(downloadID);
    return observer;
  }
  
  
  public DownloadsTimerObserver(long ID,String name, DownloadOfflineMapService service){
    downloadID = ID; 
    cityName = name;
    downloadService = service;
    iJoggingDatabaseUtils  = ((IJoggingApplication)(service.getApplicationContext())).getIJoggingDatabaseUtils();
  }
  
  public void startObserve(){
    timerObserverMap.put(downloadID, this);
    timer.schedule(this, 0, 1000);
  }
  
  public void stopObserve(){
    DownloadsTimerObserver.this.cancel();
    timerObserverMap.remove(downloadID);    
  }
  
  private void notifyOfflineUpdate(){
    final Set<DownloadOfflineListener> listeners = downloadService.downloadOfflineListeners.getRegisteredListeners();
    for (DownloadOfflineListener listener : listeners) {
      listener.notifyOfflineMapStateUpdate();
    }
  }
  
  @Override
  public void run() {
    Cursor cursor = downloadService.downloadMgr.query(new DownloadManager.Query().setFilterById(downloadID));
    if (cursor != null) {
      try{
        cursor.moveToFirst();
        int index= cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
        int bytesDownloadedSoFar = cursor.getInt(index);
        int totalSizeBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        iJoggingDatabaseUtils.updateDownloadBytes(cityName, bytesDownloadedSoFar);
        iJoggingDatabaseUtils.updateTotalSizeBytes(cityName, totalSizeBytes);
        Log.d(TAG, "downloadBytes "+ bytesDownloadedSoFar);
        notifyOfflineUpdate();
      }catch(Exception e){
        e.printStackTrace();
      }finally{
        cursor.close();
      }
    }
  }
  
}
