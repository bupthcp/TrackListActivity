package com.hu.walkingnotes.support.asyncdrawable;

import com.hu.walkingnotes.support.file.FileDownloaderHttpHelper;
import com.hu.walkingnotes.support.file.FileLocationMethod;
import com.hu.walkingnotes.support.file.FileManager;
import com.hu.walkingnotes.support.imageutility.ImageUtility;
import com.hu.walkingnotes.support.lib.MyAsyncTask;

import android.os.Build.VERSION;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User: qii
 * Date: 13-2-9
 * support to insert progressbar update
 */
public class DownloadWorker extends MyAsyncTask<String, Integer, Boolean> implements IPictureWorker {


    private String url = "";
    private CopyOnWriteArrayList<FileDownloaderHttpHelper.DownloadListener> downloadListenerList = new CopyOnWriteArrayList<FileDownloaderHttpHelper.DownloadListener>();

    private FileLocationMethod method;

    public String getUrl() {
        return url;
    }

    public DownloadWorker(String url, FileLocationMethod method) {

        this.url = url;
        this.method = method;
    }


    public void addDownloadListener(FileDownloaderHttpHelper.DownloadListener listener) {
        downloadListenerList.addIfAbsent(listener);
    }


    @Override
    protected Boolean doInBackground(String... d) {

        synchronized (TimeLineBitmapDownloader.pauseDownloadWorkLock) {
            while (TimeLineBitmapDownloader.pauseDownloadWork && !isCancelled()) {
                try {
                    TimeLineBitmapDownloader.pauseDownloadWorkLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (isCancelled())
            return false;

        String filePath = FileManager.getFilePathFromUrl(url, method);

        String actualDownloadUrl = url;

        //这里不知道是原作者出于什么考虑，进行了url的替换。经过实验发现，替换后的url所下载
        //的图片，在4.3的手机上能够显示，但是在2.3的手机上就无法显示了；在windows上无法显示
        //在windows的浏览器中能够显示。很奇怪的错误
        if (VERSION.SDK_INT >= 16) {
          switch (method) {
            case picture_thumbnail:
              actualDownloadUrl = url.replace("thumbnail", "webp180");
              break;
            case picture_bmiddle:
              actualDownloadUrl = url.replace("bmiddle", "webp720");
              break;
            case picture_large:
              actualDownloadUrl = url.replace("large", "woriginal");
              break;
    
          }
        }


        boolean result = ImageUtility.getBitmapFromNetWork(actualDownloadUrl, filePath, new FileDownloaderHttpHelper.DownloadListener() {
            @Override
            public void pushProgress(int progress, int max) {
                publishProgress(progress, max);
            }
        });
        TaskCache.removeDownloadTask(url, DownloadWorker.this);
        return result;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        for (FileDownloaderHttpHelper.DownloadListener downloadListener : downloadListenerList) {
            if (downloadListener != null)
                downloadListener.pushProgress(values[0], values[1]);
        }
    }


}