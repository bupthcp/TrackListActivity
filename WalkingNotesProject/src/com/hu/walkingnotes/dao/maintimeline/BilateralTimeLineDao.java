package com.hu.walkingnotes.dao.maintimeline;

import com.hu.walkingnotes.dao.URLHelper;

/**
 * User: qii
 * Date: 12-9-13
 */
public class BilateralTimeLineDao extends MainFriendsTimeLineDao {

    public BilateralTimeLineDao(String access_token) {
        super(access_token);
    }

    @Override
    protected String getUrl() {
        return URLHelper.BILATERAL_TIMELINE;
    }
}
