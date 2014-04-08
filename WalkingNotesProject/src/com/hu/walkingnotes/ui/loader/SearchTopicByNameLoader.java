package com.hu.walkingnotes.ui.loader;

import android.content.Context;

import com.hu.walkingnotes.bean.TopicResultListBean;
import com.hu.walkingnotes.dao.topic.SearchTopicDao;
import com.hu.walkingnotes.support.error.WeiboException;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: qii
 * Date: 13-5-12
 */
public class SearchTopicByNameLoader extends AbstractAsyncNetRequestTaskLoader<TopicResultListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String searchWord;
    private String page;

    public SearchTopicByNameLoader(Context context, String token, String searchWord, String page) {
        super(context);
        this.token = token;
        this.searchWord = searchWord;
        this.page = page;
    }


    public TopicResultListBean loadData() throws WeiboException {
        SearchTopicDao dao = new SearchTopicDao(token, searchWord);
        dao.setPage(page);

        TopicResultListBean result = null;
        lock.lock();

        try {
            result = dao.getGSONMsgList();
        } finally {
            lock.unlock();
        }


        return result;
    }

}