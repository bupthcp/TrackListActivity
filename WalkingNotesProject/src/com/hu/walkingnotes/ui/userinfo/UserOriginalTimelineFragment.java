package com.hu.walkingnotes.ui.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;

import com.hu.iJogging.R;
import com.hu.walkingnotes.bean.MessageListBean;
import com.hu.walkingnotes.bean.UserBean;
import com.hu.walkingnotes.bean.android.AsyncTaskLoaderResult;
import com.hu.walkingnotes.support.utils.GlobalContext;
import com.hu.walkingnotes.ui.basefragment.AbstractMessageTimeLineFragment;
import com.hu.walkingnotes.ui.browser.BrowserWeiboMsgActivity;
import com.hu.walkingnotes.ui.loader.StatusesByIdLoader;

import org.holoeverywhere.app.Activity;

public class UserOriginalTimelineFragment extends AbstractMessageTimeLineFragment<MessageListBean>{

    protected String token;
    private MessageListBean bean = new MessageListBean();
    private static final String LIMITED_READ_MESSAGE_COUNT = "10";
    
    public UserOriginalTimelineFragment(){
        this.token = GlobalContext.getInstance().getSpecialToken();
    }
    
    @Override
    public MessageListBean getList() {
        return bean;
    }

    @Override
    protected void listViewItemClick(AdapterView parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), BrowserWeiboMsgActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("msg", getList().getItem(position));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void newMsgOnPostExecute(MessageListBean newValue, Bundle loaderArgs) {
        if (newValue != null && getActivity() != null && newValue.getSize() > 0) {
            getList().addNewData(newValue);
            getAdapter().notifyDataSetChanged();
            getListView().setSelectionAfterHeaderView();
        }
    }

    @Override
    protected void oldMsgOnPostExecute(MessageListBean newValue) {
        if (newValue != null && newValue.getSize() > 0) {
            getList().addOldData(newValue);
        }
    }
    
    @Override
    public void loadNewMsg() {
        getLoaderManager().destroyLoader(MIDDLE_MSG_LOADER_ID);
        getLoaderManager().destroyLoader(OLD_MSG_LOADER_ID);
        dismissFooterView();
        getLoaderManager().restartLoader(NEW_MSG_LOADER_ID, null, msgCallback);
    }


    @Override
    protected void loadOldMsg(View view) {
        getLoaderManager().destroyLoader(NEW_MSG_LOADER_ID);
        getPullToRefreshListView().onRefreshComplete();
        getLoaderManager().destroyLoader(MIDDLE_MSG_LOADER_ID);
        getLoaderManager().restartLoader(OLD_MSG_LOADER_ID, null, msgCallback);
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateNewMsgLoader(int id,
            Bundle args) {
        String screenName = getString(R.string.official_account_screen_name);
        String sinceId = null;
        if (getList().getItemList().size() > 0) {
            sinceId = getList().getItemList().get(0).getId();
        }
        StatusesByIdLoader loader =  new StatusesByIdLoader(getActivity(), null, screenName, token, sinceId, null, LIMITED_READ_MESSAGE_COUNT);
        loader.setFeature(StatusesByIdLoader.FEATRUE_ORIGINAL);
        return loader;
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateOldMsgLoader(int id,
            Bundle args) {
        String screenName = getString(R.string.official_account_screen_name);
        String maxId = null;
        if (getList().getItemList().size() > 0) {
            maxId = getList().getItemList().get(getList().getItemList().size() - 1).getId();
        }
        StatusesByIdLoader loader = new StatusesByIdLoader(getActivity(), null, screenName, token, null, maxId, LIMITED_READ_MESSAGE_COUNT);
        loader.setFeature(StatusesByIdLoader.FEATRUE_ORIGINAL);
        return loader;
    }
    
    public void buildActionBarNav(){
        ((Activity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
}
