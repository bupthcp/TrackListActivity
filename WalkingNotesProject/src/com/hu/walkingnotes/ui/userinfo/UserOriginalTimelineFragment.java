package com.hu.walkingnotes.ui.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.hu.iJogging.R;
import com.hu.walkingnotes.bean.AccountBean;
import com.hu.walkingnotes.bean.MessageListBean;
import com.hu.walkingnotes.bean.android.AsyncTaskLoaderResult;
import com.hu.walkingnotes.bean.android.TimeLinePosition;
import com.hu.walkingnotes.bean.android.UserOriginalTimeLineData;
import com.hu.walkingnotes.support.database.MentionWeiboTimeLineDBTask;
import com.hu.walkingnotes.support.database.UserOriginalWeiboTimeLineDBTask;
import com.hu.walkingnotes.support.utils.GlobalContext;
import com.hu.walkingnotes.support.utils.Utility;
import com.hu.walkingnotes.ui.basefragment.AbstractMessageTimeLineFragment;
import com.hu.walkingnotes.ui.browser.BrowserWeiboMsgActivity;
import com.hu.walkingnotes.ui.loader.StatusesByIdLoader;
import com.hu.walkingnotes.ui.loader.UserOriginalWeiboDBLoader;

import org.holoeverywhere.app.Activity;

public class UserOriginalTimelineFragment extends AbstractMessageTimeLineFragment<MessageListBean>{

    protected String token;
    private MessageListBean bean = new MessageListBean();
    private TimeLinePosition timeLinePosition;
    //账户昵称,目前使用UserOriginalTimelineFragment来呈现"思密达亚克西"的原创官方内容
    private String screenName;
    private static final String LIMITED_READ_MESSAGE_COUNT = "10";
    
    public UserOriginalTimelineFragment(){
        this.token = GlobalContext.getInstance().getSpecialToken();
    }
    
    @Override
    public MessageListBean getList() {
        return bean;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("timeLinePosition", timeLinePosition);
        outState.putParcelable("bean", bean);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        screenName = getString(R.string.official_account_screen_name);
        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                timeLinePosition = (TimeLinePosition) savedInstanceState.getSerializable("timeLinePosition");

                Loader<UserOriginalTimeLineData> loader = getLoaderManager().getLoader(DB_CACHE_LOADER_ID);
                if (loader != null) {
                    getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
                }

                MessageListBean savedBean = (MessageListBean) savedInstanceState.getParcelable("bean");
                if (savedBean != null && savedBean.getSize() > 0) {
                    getList().replaceData(savedBean);
                    timeLineAdapter.notifyDataSetChanged();
                    refreshLayout(getList());
                } else {
                    getLoaderManager().initLoader(DB_CACHE_LOADER_ID, null, dbCallback);
                }

                break;
        }
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
            addNewDataAndRememberPosition(newValue);
        }
    }
    
    private void addNewDataAndRememberPosition(MessageListBean newValue) {
        int size = newValue.getSize();
        if (getActivity() != null && newValue.getSize() > 0) {
            boolean jumpToTop = getList().getSize() == 0;
            newMsgTipBar.setValue(newValue, jumpToTop);

            getList().addNewData(newValue);
            if (!jumpToTop) {
                int index = getListView().getFirstVisiblePosition();
                View v = getListView().getChildAt(1);
                int top = (v == null) ? 0 : v.getTop();
                getAdapter().notifyDataSetChanged();
                int ss = index + size;
                getListView().setSelectionFromTop(ss + 1, top);
            } else {
                newMsgTipBar.clearAndReset();
                getAdapter().notifyDataSetChanged();
                getListView().setSelection(0);
            }
            UserOriginalWeiboTimeLineDBTask.asyncReplace(getList(), screenName);
            saveTimeLinePositionToDB();
        }
    }
    
    private void saveTimeLinePositionToDB() {
        timeLinePosition = Utility.getCurrentPositionFromListView(getListView());
        timeLinePosition.newMsgIds = newMsgTipBar.getValues();
        UserOriginalWeiboTimeLineDBTask.asyncUpdatePosition(timeLinePosition, screenName);
    }

    @Override
    protected void oldMsgOnPostExecute(MessageListBean newValue) {
        if (newValue != null && newValue.getSize() > 1) {
            getList().addOldData(newValue);
            UserOriginalWeiboTimeLineDBTask.asyncReplace(getList(), screenName);
        } else {
            Toast.makeText(getActivity(), getString(R.string.older_message_empty), Toast.LENGTH_SHORT).show();
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
    
    private LoaderManager.LoaderCallbacks<UserOriginalTimeLineData> dbCallback = new LoaderManager.LoaderCallbacks<UserOriginalTimeLineData>() {
        @Override
        public Loader<UserOriginalTimeLineData> onCreateLoader(int id, Bundle args) {
            getPullToRefreshListView().setVisibility(View.INVISIBLE);
            return new UserOriginalWeiboDBLoader(getActivity(), GlobalContext.getInstance().getCurrentAccountId());
        }

        @Override
        public void onLoadFinished(Loader<UserOriginalTimeLineData> loader, UserOriginalTimeLineData result) {
            getPullToRefreshListView().setVisibility(View.VISIBLE);

            if (result != null) {
                getList().replaceData(result.msgList);
                timeLinePosition = result.position;
            }

            getAdapter().notifyDataSetChanged();
            setListViewPositionFromPositionsCache();
            refreshLayout(bean);

            /**
             * when this account first open app,if he don't have any data in database,fetch data from server automally
             */

            if (bean.getSize() == 0) {
                pullToRefreshListView.setRefreshing();
                loadNewMsg();
            }

            getLoaderManager().destroyLoader(loader.getId());

        }

        @Override
        public void onLoaderReset(Loader<UserOriginalTimeLineData> loader) {

        }
    };
    
    private void setListViewPositionFromPositionsCache() {
        if (timeLinePosition != null)
            getListView().setSelectionFromTop(timeLinePosition.position + 1, timeLinePosition.top);
        else
            getListView().setSelectionFromTop(0, 0);

        setListViewUnreadTipBar(timeLinePosition);
    }
    
    private void setListViewUnreadTipBar(TimeLinePosition p) {
        if (p != null && p.newMsgIds != null) {
            newMsgTipBar.setValue(p.newMsgIds);
        }
    }
    
}
