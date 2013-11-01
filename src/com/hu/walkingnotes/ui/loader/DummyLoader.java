package com.hu.walkingnotes.ui.loader;

import com.hu.walkingnotes.bean.android.AsyncTaskLoaderResult;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * User: qii
 * Date: 13-5-15
 */
public class DummyLoader<T> extends AsyncTaskLoader<AsyncTaskLoaderResult<T>> {
    public DummyLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public AsyncTaskLoaderResult<T> loadInBackground() {
        return null;
    }
}
