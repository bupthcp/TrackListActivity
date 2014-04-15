package com.hu.walkingnotes.ui.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.hu.walkingnotes.bean.android.UserOriginalTimeLineData;
import com.hu.walkingnotes.support.database.UserOriginalWeiboTimeLineDBTask;

public class UserOriginalWeiboDBLoader extends AsyncTaskLoader<UserOriginalTimeLineData>{
    private String accountId;
    private UserOriginalTimeLineData result;

    public UserOriginalWeiboDBLoader(Context context, String accountId) {
        super(context);
        this.accountId = accountId;
    }
    
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (result == null) {
            forceLoad();
        } else {
            deliverResult(result);
        }
    }

    @Override
    public UserOriginalTimeLineData loadInBackground() {
        result = UserOriginalWeiboTimeLineDBTask.getUserOriginalMsgList(accountId);
        return result;
    }
}
