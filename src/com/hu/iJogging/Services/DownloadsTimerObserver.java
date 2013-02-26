package com.hu.iJogging.Services;

import com.hu.iJogging.IJoggingApplication;
import com.hu.iJogging.common.IJoggingDatabaseUtils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class DownloadsTimerObserver extends TimerTask{
  public static final String TAG = DownloadsTimerObserver.class.getSimpleName();
  
  private DownloadManager downloadMgr=null;
  private long downloadID =  -1L;
  private static Timer timer = null;
  private String cityName = null;
  private DownloadOfflineListeners downloadOfflineListeners;
  private Context context;
  private IJoggingDatabaseUtils iJoggingDatabaseUtils;
  
  
  private static HashMap<Long , DownloadsTimerObserver> timerObserverMap = new HashMap<Long , DownloadsTimerObserver>();
  
  public static DownloadsTimerObserver getObserver(long downloadID){
    DownloadsTimerObserver observer = timerObserverMap.get(downloadID);
    return observer;
  }
  
  
  public DownloadsTimerObserver(long ID,String name, DownloadManager mgr,DownloadOfflineListeners listeners, Context ctx){
    downloadID = ID; 
    cityName = name;
    downloadMgr = mgr;
    downloadOfflineListeners = listeners;
    context = ctx;
    timer = new Timer();
    iJoggingDatabaseUtils  = ((IJoggingApplication)(context.getApplicationContext())).getIJoggingDatabaseUtils();
  }
  
  public void startObserve(){
    timerObserverMap.put(downloadID, this);
    timer.schedule(this, 0, 1000);
  }
  
  public void stopObserve(){
    timer.cancel();
    timerObserverMap.remove(downloadID);
  }
  
  private void notifyOfflineUpdate(final DownloadState state){
    final Set<DownloadOfflineListener> listeners = downloadOfflineListeners
        .getRegisteredListeners();
    for (DownloadOfflineListener listener : listeners) {
      listener.notifyOfflineMapStateUpdate(state);
    }
  }
  
  @Override
  public void run() {
    Cursor cursor = downloadMgr.query(new DownloadManager.Query().setFilterById(downloadID));
    if (cursor != null) {
      try{
        cursor.moveToFirst();
        DownloadState state = new DownloadState();
        int index= cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
        state.bytesDownloadedSoFar = cursor.getInt(index);
        state.ID = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
        state.title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
        state.totalSizeBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        iJoggingDatabaseUtils.updateDownloadBytes(cityName, state.bytesDownloadedSoFar);
        Log.d(TAG, "downloadBytes "+ state.bytesDownloadedSoFar);
        notifyOfflineUpdate(state);
      }catch(Exception e){
        e.printStackTrace();
      }finally{
        cursor.close();
      }
    }
  }
  
}
