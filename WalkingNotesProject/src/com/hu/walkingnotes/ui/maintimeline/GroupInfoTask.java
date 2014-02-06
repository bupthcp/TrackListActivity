package com.hu.walkingnotes.ui.maintimeline;

import com.hu.walkingnotes.bean.GroupListBean;
import com.hu.walkingnotes.dao.maintimeline.FriendGroupDao;
import com.hu.walkingnotes.support.database.GroupDBTask;
import com.hu.walkingnotes.support.error.WeiboException;
import com.hu.walkingnotes.support.lib.MyAsyncTask;
import com.hu.walkingnotes.support.utils.GlobalContext;

/**
 * User: qii
 * Date: 12-12-28
 */
public class GroupInfoTask extends MyAsyncTask<Void, GroupListBean, GroupListBean> {


    private WeiboException e;

    private String token;
    private String accountId;

    public GroupInfoTask(String token, String accountId) {
        this.token = token;
        this.accountId = accountId;
    }

    @Override
    protected GroupListBean doInBackground(Void... params) {
        try {
            return new FriendGroupDao(token).getGroup();
        } catch (WeiboException e) {
            this.e = e;
            cancel(true);
        }
        return null;
    }


    @Override
    protected void onPostExecute(GroupListBean groupListBean) {
        super.onPostExecute(groupListBean);

        GroupDBTask.update(groupListBean, accountId);
        if (accountId.equalsIgnoreCase(GlobalContext.getInstance().getCurrentAccountId()))
            GlobalContext.getInstance().setGroup(groupListBean);

    }

}