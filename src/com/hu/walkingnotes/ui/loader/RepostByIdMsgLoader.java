package com.hu.walkingnotes.ui.loader;

import com.hu.walkingnotes.bean.RepostListBean;
import com.hu.walkingnotes.dao.timeline.RepostsTimeLineByIdDao;
import com.hu.walkingnotes.support.error.WeiboException;

import android.content.Context;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: qii
 * Date: 13-5-15
 */
public class RepostByIdMsgLoader extends AbstractAsyncNetRequestTaskLoader<RepostListBean> {

    private static Lock lock = new ReentrantLock();


    private String token;
    private String sinceId;
    private String maxId;
    private String id;

    public RepostByIdMsgLoader(Context context, String id, String token, String sinceId, String maxId) {
        super(context);
        this.token = token;
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.id = id;

    }


    public RepostListBean loadData() throws WeiboException {
        RepostsTimeLineByIdDao dao = new RepostsTimeLineByIdDao(token, id);


        dao.setSince_id(sinceId);
        dao.setMax_id(maxId);
        RepostListBean result = null;

        lock.lock();

        try {
            result = dao.getGSONMsgList();
        } finally {
            lock.unlock();
        }

        return result;
    }

}


