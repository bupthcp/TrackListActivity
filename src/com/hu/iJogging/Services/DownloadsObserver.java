package com.hu.iJogging.Services;

import android.os.FileObserver;
import android.util.Log;

import java.util.HashMap;

/*
 * 这个类是使用FileObserver去监控下载进度，但是FileObserver有一个不好的地方是，在创建时，受监控的
 * 文件必须已经存在。这个限制直接导致了不是很方便应用在下载管理上，因为初始化下载的时候，文件时并不存在的
 * 目前的下载进度监控DownloadsTimerObserver是使用了一个Timer去轮询下载进度
 */
public class DownloadsObserver extends FileObserver {

  public static final String LOG_TAG = DownloadsObserver.class.getSimpleName();

  public static HashMap<String, DownloadsObserver> observerMap = new HashMap<String, DownloadsObserver>();
  
  public static final String TAG = DownloadsObserver.class.getSimpleName();

  private static final int flags = FileObserver.CLOSE_WRITE | FileObserver.OPEN
      | FileObserver.MODIFY | FileObserver.DELETE | FileObserver.MOVED_FROM;

  public DownloadsObserver(String path) {
    super(path, flags);
  }

  @Override
  public void onEvent(int event, String path) {
    Log.d(LOG_TAG, "onEvent(" + event + ", " + path + ")");

//    if (path == null) { return; }

    switch (event) {
      case FileObserver.CLOSE_WRITE:
        // Download complete, or paused when wifi is disconnected. Possibly
        // reported more than once in a row.
        // Useful for noticing when a download has been paused. For completions,
        // register a receiver for
        // DownloadManager.ACTION_DOWNLOAD_COMPLETE.
        Log.d(TAG, "modified");
        break;
      case FileObserver.OPEN:
        // Called for both read and write modes.
        // Useful for noticing a download has been started or resumed.
        Log.d(TAG, "open");
        break;
      case FileObserver.DELETE:
      case FileObserver.MOVED_FROM:
        // These might come in handy for obvious reasons.
        break;
      case FileObserver.MODIFY:
        // Called very frequently while a download is ongoing (~1 per ms).
        // This could be used to trigger a progress update, but that should
        // probably be done less often than this.
        Log.d(TAG, "modified");
        break;
    }
  }
}