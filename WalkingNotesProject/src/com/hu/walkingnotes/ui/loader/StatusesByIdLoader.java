package com.hu.walkingnotes.ui.loader;

import com.hu.walkingnotes.bean.MessageListBean;
import com.hu.walkingnotes.dao.user.StatusesTimeLineDao;
import com.hu.walkingnotes.support.error.WeiboException;

import android.content.Context;
import android.text.TextUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: qii
 * Date: 13-5-12
 */
public class StatusesByIdLoader extends AbstractAsyncNetRequestTaskLoader<MessageListBean> {

    public static int FEATRUE_ORIGINAL = 1;
    
    private static Lock lock = new ReentrantLock();


    private String token;
    private String sinceId;
    private String maxId;
    private String screenName;
    private String uid;
    private String count;
    
    private int feature;

    public StatusesByIdLoader(Context context, String uid, String screenName, String token, String sinceId, String maxId) {
        super(context);
        this.token = token;
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.uid = uid;
        this.screenName = screenName;

    }

    public StatusesByIdLoader(Context context, String uid, String screenName, String token, String sinceId, String maxId, String count) {
        this(context, uid, screenName, token, sinceId, maxId);
        this.count = count;

    }
    
    public void setFeature(int featureParam){
        feature = featureParam;
    }


    public MessageListBean loadData() throws WeiboException {
        StatusesTimeLineDao dao = new StatusesTimeLineDao(token, uid);
        if(feature != 0){
            dao.setFeature(Integer.toString(feature));
        }

        if (TextUtils.isEmpty(uid)) {
            dao.setScreen_name(screenName);
        }

        if (!TextUtils.isEmpty(count)) {
            dao.setCount(count);
        }

        dao.setSince_id(sinceId);
        dao.setMax_id(maxId);
        MessageListBean result = null;

        lock.lock();

        try {
            result = dao.getGSONMsgList();
        } finally {
            lock.unlock();
        }


        return result;
    }

}


