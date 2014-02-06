package com.hu.walkingnotes.ui.maintimeline;

import com.hu.iJogging.R;
import com.hu.walkingnotes.bean.AccountBean;
import com.hu.walkingnotes.bean.GroupBean;
import com.hu.walkingnotes.bean.MessageBean;
import com.hu.walkingnotes.bean.MessageListBean;
import com.hu.walkingnotes.bean.MessageReCmtCountBean;
import com.hu.walkingnotes.bean.UserBean;
import com.hu.walkingnotes.bean.android.AsyncTaskLoaderResult;
import com.hu.walkingnotes.bean.android.MessageTimeLineData;
import com.hu.walkingnotes.bean.android.TimeLinePosition;
import com.hu.walkingnotes.dao.maintimeline.TimeLineReCmtCountDao;
import com.hu.walkingnotes.support.asyncdrawable.TaskCache;
import com.hu.walkingnotes.support.database.FriendsTimeLineDBTask;
import com.hu.walkingnotes.support.debug.AppLogger;
import com.hu.walkingnotes.support.error.WeiboException;
import com.hu.walkingnotes.support.file.FileLocationMethod;
import com.hu.walkingnotes.support.file.FileManager;
import com.hu.walkingnotes.support.lib.MyAsyncTask;
import com.hu.walkingnotes.support.lib.TopTipBar;
import com.hu.walkingnotes.support.lib.VelocityListView;
import com.hu.walkingnotes.support.settinghelper.SettingUtility;
import com.hu.walkingnotes.support.utils.AppConfig;
import com.hu.walkingnotes.support.utils.BundleArgsConstants;
import com.hu.walkingnotes.support.utils.GlobalContext;
import com.hu.walkingnotes.support.utils.Utility;
import com.hu.walkingnotes.ui.adapter.AbstractAppListAdapter;
import com.hu.walkingnotes.ui.adapter.StatusListAdapter;
import com.hu.walkingnotes.ui.basefragment.AbstractMessageTimeLineFragment;
import com.hu.walkingnotes.ui.browser.BrowserWeiboMsgActivity;
import com.hu.walkingnotes.ui.loader.FriendsMsgLoader;
import com.hu.walkingnotes.ui.main.LeftMenuFragment;
import com.hu.walkingnotes.ui.main.MainTimeLineActivity;
import com.hu.walkingnotes.ui.send.WriteWeiboActivity;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.holoeverywhere.app.Activity;

/**
 * User: qii
 * Date: 12-7-29
 */
public class FriendsTimeLineFragment extends AbstractMessageTimeLineFragment<MessageListBean>
        implements GlobalContext.MyProfileInfoChangeListener,
        MainTimeLineActivity.ScrollableListFragment {

    private AccountBean accountBean;
    private UserBean userBean;
    private String token;
    private DBCacheTask dbTask;

    private ScheduledExecutorService autoRefreshExecutor = null;

    public final static String ALL_GROUP_ID = "0";
    public final static String BILATERAL_GROUP_ID = "1";

    private String currentGroupId = ALL_GROUP_ID;

    private HashMap<String, MessageListBean> groupDataCache = new HashMap<String, MessageListBean>();
    private HashMap<String, TimeLinePosition> positionCache = new HashMap<String, TimeLinePosition>();

    private MessageListBean bean = new MessageListBean();

    private BaseAdapter navAdapter;

    private Thread backgroundWifiDownloadPicThread = null;

    @Override
    public MessageListBean getList() {
        return bean;
    }


    public FriendsTimeLineFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //use Up instead of Back to reach this fragment
        if (data == null)
            return;
        final MessageBean msg = (MessageBean) data.getParcelableExtra("msg");
        if (msg != null) {
            for (int i = 0; i < getList().getSize(); i++) {
                if (msg.equals(getList().getItem(i))) {
                    MessageBean ori = getList().getItem(i);
                    if (ori.getComments_count() != msg.getComments_count()
                            || ori.getReposts_count() != msg.getReposts_count()) {
                        ori.setReposts_count(msg.getReposts_count());
                        ori.setComments_count(msg.getComments_count());
                        FriendsTimeLineDBTask.asyncUpdateCount(msg.getId(), msg.getComments_count(), msg.getReposts_count());
                        getAdapter().notifyDataSetChanged();
                    }
                    break;
                }
            }

        }
    }

    public FriendsTimeLineFragment(AccountBean accountBean, UserBean userBean, String token) {
        this.accountBean = accountBean;
        this.userBean = userBean;
        this.token = token;
    }


    @Override
    protected void onListViewScrollStop() {
        savePositionToPositionsCache();
        startDownloadingOtherPicturesOnWifiNetworkEnvironment();

    }

    private void startDownloadingOtherPicturesOnWifiNetworkEnvironment() {
        if (backgroundWifiDownloadPicThread == null
                && Utility.isWifi(getActivity())
                && SettingUtility.getEnableBigPic()
                && SettingUtility.isWifiAutoDownloadPic()) {
            final int position = getListView().getFirstVisiblePosition();
            int listVewOrientation = ((VelocityListView) getListView()).getTowardsOrientation();
            WifiAutoDownloadPictureRunnable runnable = new WifiAutoDownloadPictureRunnable(getList(), position, listVewOrientation);
            backgroundWifiDownloadPicThread = new Thread(runnable);
            backgroundWifiDownloadPicThread.start();
            AppLogger.i("WifiAutoDownloadPictureRunnable startDownloadingOtherPicturesOnWifiNetworkEnvironment");

        }
    }

    @Override
    protected void onListViewScrollStateTouchScroll() {
        super.onListViewScrollStateTouchScroll();
        stopDownloadingOtherPicturesOnWifiNetworkEnvironment();
    }

    @Override
    protected void onListViewScrollStateFling() {
        super.onListViewScrollStateFling();
        stopDownloadingOtherPicturesOnWifiNetworkEnvironment();
    }

    private void stopDownloadingOtherPicturesOnWifiNetworkEnvironment() {
        if (backgroundWifiDownloadPicThread != null) {
            backgroundWifiDownloadPicThread.interrupt();
            backgroundWifiDownloadPicThread = null;
            AppLogger.i("WifiAutoDownloadPictureRunnable stopDownloadingOtherPicturesOnWifiNetworkEnvironment");

        }
    }

    private static class WifiAutoDownloadPictureRunnable implements Runnable {

        MessageListBean list;
        int position;
        static HashMap<String, Boolean> result = new HashMap<String, Boolean>();
        int listViewOrientation = -1;

        public WifiAutoDownloadPictureRunnable(MessageListBean list, int position, int listViewOrientation) {
            this.list = new MessageListBean();
            this.list.addNewData(list);
            this.position = position;
            this.listViewOrientation = listViewOrientation;
            AppLogger.i("WifiAutoDownloadPictureRunnable new Runnable");
        }

        @Override
        public void run() {

            switch (this.listViewOrientation) {
                case VelocityListView.TOWARDS_BOTTOM:
                    for (int i = position; i < list.getSize(); i++) {
                        //wait until other download tasks are finished
                        synchronized (TaskCache.backgroundWifiDownloadPicturesWorkLock) {
                            while (!TaskCache.isDownloadTaskFinished() && !Thread.currentThread().isInterrupted()) {
                                try {
                                    AppLogger.i("WifiAutoDownloadPictureRunnable wait for lock");
                                    TaskCache.backgroundWifiDownloadPicturesWorkLock.wait();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }

                        if (Thread.currentThread().isInterrupted()) {
                            AppLogger.i("WifiAutoDownloadPictureRunnable interrupted");
                            return;
                        }

                        MessageBean msg = list.getItem(i);
                        if (msg == null) {
                            continue;
                        }
                        Boolean done = result.get(msg.getId());
                        if (done != null && done) {
                            AppLogger.i("already done skipped");
                            continue;
                        }
                        AppLogger.i("WifiAutoDownloadPictureRunnable" + msg.getId() + "start");
                        startDownload(msg);
                        AppLogger.i("WifiAutoDownloadPictureRunnable" + msg.getId() + "finished");
                        result.put(msg.getId(), true);
                    }
                    break;
                case VelocityListView.TOWARDS_TOP:
                    for (int i = position; i >= 0; i--) {
                        //wait until other download tasks are finished
                        synchronized (TaskCache.backgroundWifiDownloadPicturesWorkLock) {
                            while (!TaskCache.isDownloadTaskFinished() && !Thread.currentThread().isInterrupted()) {
                                try {
                                    AppLogger.i("WifiAutoDownloadPictureRunnable wait for lock");
                                    TaskCache.backgroundWifiDownloadPicturesWorkLock.wait();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }

                        if (Thread.currentThread().isInterrupted()) {
                            return;
                        }

                        MessageBean msg = list.getItem(i);
                        if (msg == null) {
                            continue;
                        }
                        Boolean done = result.get(msg.getId());
                        if (done != null && done) {
                            AppLogger.i("already done skipped");
                            continue;
                        }

                        AppLogger.i("WifiAutoDownloadPictureRunnable" + msg.getId() + "start");
                        startDownload(msg);
                        AppLogger.i("WifiAutoDownloadPictureRunnable" + msg.getId() + "finished");

                        result.put(msg.getId(), true);
                    }
                    break;
            }


        }

        private void startDownload(MessageBean msg) {
            String url = msg.getOriginal_pic();
            if (TextUtils.isEmpty(url) && msg.getRetweeted_status() != null) {
                url = msg.getRetweeted_status().getOriginal_pic();
            }

            downloadPic(url);
            UserBean user = msg.getUser();
            if (user != null) {
                downloadAvatar(user.getAvatar_large());
            }
        }

        private void downloadPic(String url) {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            String path = FileManager.getFilePathFromUrl(url, FileLocationMethod.picture_large);
            TaskCache.waitForPictureDownload(url, null, path, FileLocationMethod.picture_large);
        }

        private void downloadAvatar(String url) {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            String path = FileManager.getFilePathFromUrl(url, FileLocationMethod.avatar_large);
            TaskCache.waitForPictureDownload(url, null, path, FileLocationMethod.avatar_large);
        }

    }

    private void savePositionToPositionsCache() {
        positionCache.put(currentGroupId, Utility.getCurrentPositionFromListView(getListView()));
    }

    private void saveNewMsgCountToPositionsCache() {
        final TimeLinePosition position = positionCache.get(currentGroupId);
        position.newMsgIds = newMsgTipBar.getValues();
    }

    private void setListViewPositionFromPositionsCache() {
        TimeLinePosition p = positionCache.get(currentGroupId);
        if (p != null)
            getListView().setSelectionFromTop(p.position + 1, p.top);
        else
            getListView().setSelectionFromTop(0, 0);

        setListViewUnreadTipBar(p);

    }

    private void setListViewUnreadTipBar(TimeLinePosition p) {
        if (p != null && p.newMsgIds != null)
            newMsgTipBar.setValue(p.newMsgIds);
    }

    private void savePositionToDB() {
        TimeLinePosition position = positionCache.get(currentGroupId);
        if (position == null) {
            savePositionToPositionsCache();
            position = positionCache.get(currentGroupId);
        }
        position.newMsgIds = newMsgTipBar.getValues();
        final String groupId = currentGroupId;
        FriendsTimeLineDBTask.asyncUpdatePosition(position, GlobalContext.getInstance().getCurrentAccountId(), groupId);
    }

    private void saveGroupIdToDB() {
        FriendsTimeLineDBTask.asyncUpdateRecentGroupId(GlobalContext.getInstance().getCurrentAccountId(), currentGroupId);
    }

    @Override
    public void onPause() {
        super.onPause();
        if ((VERSION.SDK_INT >= 11)&&(!getActivity().isChangingConfigurations())) {
            savePositionToDB();
            saveGroupIdToDB();
        }
        removeRefresh();
        stopDownloadingOtherPicturesOnWifiNetworkEnvironment();
    }

    @Override
    public void onResume() {
        super.onResume();
        addRefresh();
        GlobalContext.getInstance().registerForAccountChangeListener(this);
        if (SettingUtility.getEnableAutoRefresh()) {
            this.newMsgTipBar.setType(TopTipBar.Type.ALWAYS);
        } else {
            this.newMsgTipBar.setType(TopTipBar.Type.AUTO);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.cancelTasks(dbTask);
        GlobalContext.getInstance().unRegisterForAccountChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("account", accountBean);
        outState.putParcelable("userBean", userBean);
        outState.putString("token", token);

//        outState.putSerializable("bean", getList());
//        outState.putSerializable("groupDataCache", groupDataCache);
//        outState.putString("currentGroupId", currentGroupId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                if (Utility.isTaskStopped(dbTask) && getList().getSize() == 0) {
                    dbTask = new DBCacheTask();
                    dbTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                    GroupInfoTask groupInfoTask = new GroupInfoTask(GlobalContext.getInstance().getSpecialToken(), GlobalContext.getInstance().getCurrentAccountId());
                    groupInfoTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    getAdapter().notifyDataSetChanged();
                    refreshLayout(getList());
                }

                groupDataCache.put(ALL_GROUP_ID, new MessageListBean());
                groupDataCache.put(BILATERAL_GROUP_ID, new MessageListBean());

                if (getList().getSize() > 0) {
                    groupDataCache.put(ALL_GROUP_ID, getList().copy());
                }
                buildActionBarNav();

                break;
            case SCREEN_ROTATE:
                //nothing
                refreshLayout(getList());
                buildActionBarNav();
                setListViewPositionFromPositionsCache();
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                userBean = savedInstanceState.getParcelable("userBean");
                accountBean = savedInstanceState.getParcelable("account");
                token = savedInstanceState.getString("token");

                if (Utility.isTaskStopped(dbTask) && getList().getSize() == 0) {
                    dbTask = new DBCacheTask();
                    dbTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                    GroupInfoTask groupInfoTask = new GroupInfoTask(GlobalContext.getInstance().getSpecialToken(), GlobalContext.getInstance().getCurrentAccountId());
                    groupInfoTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    getAdapter().notifyDataSetChanged();
                    refreshLayout(getList());
                }

                groupDataCache.put(ALL_GROUP_ID, new MessageListBean());
                groupDataCache.put(BILATERAL_GROUP_ID, new MessageListBean());

                if (getList().getSize() > 0) {
                    groupDataCache.put(ALL_GROUP_ID, getList().copy());
                }
                buildActionBarNav();

                break;
        }

        super.onActivityCreated(savedInstanceState);


    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            buildActionBarNav();
            ((MainTimeLineActivity) getActivity()).setCurrentFragment(this);
        }
    }


    @Override
    protected void buildListAdapter() {
        StatusListAdapter adapter = new StatusListAdapter(this,
                getList().getItemList(),
                getListView(),
                true, false);
        adapter.setTopTipBar(newMsgTipBar);
        timeLineAdapter = adapter;
        getListView().setAdapter(timeLineAdapter);
    }

    private int getIndexFromGroupId(String id, List<GroupBean> list) {

        if (list == null || list.size() == 0) {
            return 0;
        }

        int index = 0;

        if (id.equals("0")) {
            index = 0;
        } else if (id.equals("1")) {
            index = 1;
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdstr().equals(id)) {
                index = i + 2;
                break;
            }
        }
        return index;
    }

    private String getGroupIdFromIndex(int index, List<GroupBean> list) {
        String selectedItemId;

        if (index == 0) {
            selectedItemId = "0";
        } else if (index == 1) {
            selectedItemId = "1";
        } else {
            selectedItemId = list.get(index - 2).getIdstr();
        }
        return selectedItemId;
    }


    private String[] buildListNavData(List<GroupBean> list) {
        List<String> name = new ArrayList<String>();

        name.add(getString(R.string.all_people));
        name.add(getString(R.string.bilateral));

        for (GroupBean b : list) {
            name.add(b.getName());
        }

        String[] valueArray = name.toArray(new String[0]);
        return valueArray;
    }


    public void buildActionBarNav() {
        if ((((MainTimeLineActivity) getActivity()).getMenuFragment()).getCurrentIndex()
                != LeftMenuFragment.HOME_INDEX) {
            return;
        }
        ((MainTimeLineActivity) getActivity()).setCurrentFragment(this);

        ((Activity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((Activity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(Utility.isDevicePort());
        ((Activity)getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        List<GroupBean> list = new ArrayList<GroupBean>();
        if (GlobalContext.getInstance().getGroup() != null) {
            list = GlobalContext.getInstance().getGroup().getLists();
        } else {
            list = new ArrayList<GroupBean>();
        }

        navAdapter = new FriendsTimeLineListNavAdapter(getActivity(), buildListNavData(list));
        final List<GroupBean> finalList = list;
        ((Activity)getActivity()).getSupportActionBar().setListNavigationCallbacks(navAdapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int which, long itemId) {

                if (!Utility.isTaskStopped(dbTask)) {
                    return true;
                }

                String groupId = getGroupIdFromIndex(which, finalList);

                if (!groupId.equals(currentGroupId)) {

                    switchFriendsGroup(groupId);
                }
                return true;
            }
        });
        currentGroupId = FriendsTimeLineDBTask.getRecentGroupId(GlobalContext.getInstance().getCurrentAccountId());

        if (Utility.isDevicePort()) {
            ((MainTimeLineActivity) getActivity()).setTitle("");
            ((Activity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_menu_home);
        } else {
            ((MainTimeLineActivity) getActivity()).setTitle("");
            ((Activity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_launcher);
        }

        if (((Activity)getActivity()).getSupportActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST && isVisible()) {
          ((Activity)getActivity()).getSupportActionBar().setSelectedNavigationItem(getRecentNavIndex());
        }

    }

    @Override
    public void onChange(UserBean newUserBean) {
        if (navAdapter != null)
            navAdapter.notifyDataSetChanged();
    }

    @Override
    public void scrollToTop() {
        Utility.stopListViewScrollingAndScrollToTop(getListView());
    }

    private class DBCacheTask extends MyAsyncTask<Void, MessageTimeLineData, List<MessageTimeLineData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getPullToRefreshListView().setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<MessageTimeLineData> doInBackground(Void... params) {
            MessageTimeLineData recentGroupData = FriendsTimeLineDBTask.getRecentGroupData(accountBean.getUid());
            publishProgress(recentGroupData);
            return FriendsTimeLineDBTask.getOtherGroupData(accountBean.getUid(), recentGroupData.groupId);
        }

        @Override
        protected void onPostExecute(List<MessageTimeLineData> result) {
            super.onPostExecute(result);
            if (getActivity() == null)
                return;
            if (result != null) {
                for (MessageTimeLineData single : result) {
                    putToGroupDataMemoryCache(single.groupId, single.msgList);
                    positionCache.put(single.groupId, single.position);
                }
            }
        }

        @Override
        protected void onProgressUpdate(MessageTimeLineData... result) {
            super.onProgressUpdate(result);

            if (getActivity() == null)
                return;

            if (result != null && result.length > 0) {
                MessageTimeLineData recentData = result[0];
                getList().replaceData(recentData.msgList);
                putToGroupDataMemoryCache(recentData.groupId, recentData.msgList);
                positionCache.put(recentData.groupId, recentData.position);
                currentGroupId = recentData.groupId;
            }
            getPullToRefreshListView().setVisibility(View.VISIBLE);
            getAdapter().notifyDataSetChanged();
            setListViewPositionFromPositionsCache();
            if (((Activity)getActivity()).getSupportActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST)
              ((Activity)getActivity()).getSupportActionBar().setSelectedNavigationItem(getRecentNavIndex());
            refreshLayout(getList());
            /**
             * when this account first open app,if he don't have any data in database,fetch data from server automally
             */
            if (getList().getSize() == 0) {
                getPullToRefreshListView().setRefreshing();
                loadNewMsg();
            } else {
                new RefreshReCmtCountTask().executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    private int getRecentNavIndex() {
        List<GroupBean> list = new ArrayList<GroupBean>();
        if (GlobalContext.getInstance().getGroup() != null) {
            list = GlobalContext.getInstance().getGroup().getLists();
        } else {
            list = new ArrayList<GroupBean>();
        }
        return getIndexFromGroupId(currentGroupId, list);
    }

    @Override
    protected void listViewItemClick(AdapterView parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), BrowserWeiboMsgActivity.class);
        intent.putExtra("msg", getList().getItem(position));
        intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
        startActivityForResult(intent, MainTimeLineActivity.REQUEST_CODE_UPDATE_FRIENDS_TIMELINE_COMMENT_REPOST_COUNT);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_menu_friendstimelinefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.write_weibo:
                Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("account", accountBean);
                startActivity(intent);
                break;
            case R.id.refresh:
                if (allowRefresh()) {
                    getPullToRefreshListView().setRefreshing();
                    loadNewMsg();
                }
                break;
            case R.id.switch_theme:
                //make sure activity has saved current left menu position
                ((MainTimeLineActivity) getActivity()).saveNavigationPositionToDB();
                SettingUtility.switchToAnotherTheme();
                ((MainTimeLineActivity) getActivity()).reload();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setSelected(String selectedItemId) {
        currentGroupId = selectedItemId;
    }


    @Override
    protected void newMsgOnPostExecute(MessageListBean newValue, Bundle loaderArgs) {
        if (Utility.isAllNotNull(getActivity(), newValue) && newValue.getSize() > 0) {
            if (loaderArgs != null && loaderArgs.getBoolean(BundleArgsConstants.AUTO_REFRESH, false)) {
                addNewDataAndRememberPositionAutoRefresh(newValue);
            } else {
                boolean scrollToTop = SettingUtility.isReadStyleEqualWeibo();
                if (scrollToTop) {
                    addNewDataWithoutRememberPosition(newValue);
                } else {
                    addNewDataAndRememberPosition(newValue);
                }
            }
            putToGroupDataMemoryCache(currentGroupId, getList());
            FriendsTimeLineDBTask.asyncReplace(getList(), accountBean.getUid(), currentGroupId);
        }
    }

    private void addNewDataAndRememberPositionAutoRefresh(MessageListBean newValue) {

        int size = newValue.getSize();

        if (getActivity() != null && newValue.getSize() > 0) {
            getList().addNewData(newValue);
            int index = getListView().getFirstVisiblePosition();
            newMsgTipBar.setValue(newValue, false);
            newMsgTipBar.setType(TopTipBar.Type.ALWAYS);
            View v = getListView().getChildAt(1);
            int top = (v == null) ? 0 : v.getTop();
            getAdapter().notifyDataSetChanged();
            int ss = index + size;
            getListView().setSelectionFromTop(ss + 1, top);

        }

    }

    private void addNewDataAndRememberPosition(MessageListBean newValue) {
        newMsgTipBar.setValue(newValue, false);
        newMsgTipBar.setType(TopTipBar.Type.AUTO);

        int size = newValue.getSize();

        if (getActivity() != null && newValue.getSize() > 0) {
            getList().addNewData(newValue);
            int index = getListView().getFirstVisiblePosition();

            View v = getListView().getChildAt(1);
            int top = (v == null) ? 0 : v.getTop();
            getAdapter().notifyDataSetChanged();
            int ss = index + size;
            getListView().setSelectionFromTop(ss + 1, top);
        }


    }

    protected void middleMsgOnPostExecute(int position, MessageListBean newValue, boolean towardsBottom) {

        if (newValue != null) {
            int size = newValue.getSize();

            if (getActivity() != null && newValue.getSize() > 0) {
                getList().addMiddleData(position, newValue, towardsBottom);

                if (towardsBottom) {
                    getAdapter().notifyDataSetChanged();
                } else {

                    View v = Utility.getListViewItemViewFromPosition(getListView(), position + 1 + 1);
                    int top = (v == null) ? 0 : v.getTop();
                    getAdapter().notifyDataSetChanged();
                    int ss = position + 1 + size - 1;
                    getListView().setSelectionFromTop(ss, top);
                }
            }
        }
    }

    private void addNewDataWithoutRememberPosition(MessageListBean newValue) {
        newMsgTipBar.setValue(newValue, true);

        getList().addNewData(newValue);
        getAdapter().notifyDataSetChanged();
        getListView().setSelectionAfterHeaderView();
    }

    @Override
    protected void oldMsgOnPostExecute(MessageListBean oldValue) {
        if (Utility.isAllNotNull(getActivity(), oldValue) && oldValue.getSize() > 1) {
            getList().addOldData(oldValue);
            putToGroupDataMemoryCache(currentGroupId, getList());
            FriendsTimeLineDBTask.asyncReplace(getList(), accountBean.getUid(), currentGroupId);

        } else if (Utility.isAllNotNull(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.older_message_empty), Toast.LENGTH_SHORT).show();
        }
    }

    public void switchFriendsGroup(String groupId) {
        getLoaderManager().destroyLoader(NEW_MSG_LOADER_ID);
        getLoaderManager().destroyLoader(MIDDLE_MSG_LOADER_ID);
        getLoaderManager().destroyLoader(OLD_MSG_LOADER_ID);
        getPullToRefreshListView().onRefreshComplete();
        dismissFooterView();
        savedCurrentLoadingMsgViewPositon = -1;
        if (timeLineAdapter instanceof AbstractAppListAdapter) {
            ((AbstractAppListAdapter) timeLineAdapter).setSavedMiddleLoadingViewPosition(savedCurrentLoadingMsgViewPositon);
        }

        positionCache.put(currentGroupId, Utility.getCurrentPositionFromListView(getListView()));
        saveNewMsgCountToPositionsCache();
        setSelected(groupId);
        newMsgTipBar.clearAndReset();
        if (groupDataCache.get(currentGroupId) == null || groupDataCache.get(currentGroupId).getSize() == 0) {
            getList().getItemList().clear();
            getAdapter().notifyDataSetChanged();
            getPullToRefreshListView().setRefreshing();
            loadNewMsg();

        } else {
            getList().replaceData(groupDataCache.get(currentGroupId));
            getAdapter().notifyDataSetChanged();
            setListViewPositionFromPositionsCache();
            saveGroupIdToDB();
            new RefreshReCmtCountTask().executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    private void putToGroupDataMemoryCache(String groupId, MessageListBean value) {
        MessageListBean copy = new MessageListBean();
        copy.addNewData(value);
        groupDataCache.put(groupId, copy);
    }

    private void removeRefresh() {
        if (autoRefreshExecutor != null && !autoRefreshExecutor.isShutdown())
            autoRefreshExecutor.shutdownNow();
    }

    protected void addRefresh() {

        autoRefreshExecutor = Executors.newSingleThreadScheduledExecutor();
        autoRefreshExecutor
                .scheduleAtFixedRate(new AutoTask(), AppConfig.AUTO_REFRESH_INITIALDELAY, AppConfig.AUTO_REFRESH_PERIOD, TimeUnit.SECONDS);

    }

    private class AutoTask implements Runnable {

        @Override
        public void run() {
            if (!SettingUtility.getEnableAutoRefresh()) {
                return;
            }

            if (!Utility.isTaskStopped(dbTask))
                return;

            if (!allowRefresh())
                return;

            if (!Utility.isWifi(getActivity())) {
                return;
            }

            if (isListViewFling() || !isVisible() || ((MainTimeLineActivity) getActivity()).getSlidingMenu().isMenuShowing())
                return;

            Bundle bundle = new Bundle();
            bundle.putBoolean(BundleArgsConstants.SCROLL_TO_TOP, false);
            bundle.putBoolean(BundleArgsConstants.AUTO_REFRESH, true);
            getLoaderManager().restartLoader(NEW_MSG_LOADER_ID, bundle, msgCallback);
        }
    }


    /**
     * refresh timline messages' repost and comment count
     */
    private class RefreshReCmtCountTask extends MyAsyncTask<Void, List<MessageReCmtCountBean>, List<MessageReCmtCountBean>> {
        List<String> msgIds;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            msgIds = new ArrayList<String>();
            List<MessageBean> msgList = getList().getItemList();
            for (MessageBean msg : msgList) {
                if (msg != null) {
                    msgIds.add(msg.getId());
                }
            }
        }

        @Override
        protected List<MessageReCmtCountBean> doInBackground(Void... params) {
            try {
                return new TimeLineReCmtCountDao(GlobalContext.getInstance().getSpecialToken(), msgIds).get();
            } catch (WeiboException e) {
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MessageReCmtCountBean> value) {
            super.onPostExecute(value);
            if (getActivity() == null || value == null)
                return;

            for (int i = 0; i < value.size(); i++) {
                MessageBean msg = getList().getItem(i);
                MessageReCmtCountBean count = value.get(i);
                if (msg != null && msg.getId().equals(count.getId())) {
                    msg.setReposts_count(count.getReposts());
                    msg.setComments_count(count.getComments());
                }
            }
            getAdapter().notifyDataSetChanged();
            FriendsTimeLineDBTask.asyncReplace(getList(), accountBean.getUid(), currentGroupId);
        }

    }

    @Override
    public void loadMiddleMsg(String beginId, String endId, int position) {
        getLoaderManager().destroyLoader(NEW_MSG_LOADER_ID);
        getLoaderManager().destroyLoader(OLD_MSG_LOADER_ID);
        getPullToRefreshListView().onRefreshComplete();
        dismissFooterView();

        Bundle bundle = new Bundle();
        bundle.putString("beginId", beginId);
        bundle.putString("endId", endId);
        bundle.putInt("position", position);
        VelocityListView velocityListView = (VelocityListView) getListView();
        bundle.putBoolean("towardsBottom", velocityListView.getTowardsOrientation() == VelocityListView.TOWARDS_BOTTOM);
        getLoaderManager().restartLoader(MIDDLE_MSG_LOADER_ID, bundle, msgCallback);

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


    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String accountId = accountBean.getUid();
        String token = accountBean.getAccess_token();
        String sinceId = null;
        if (getList().getItemList().size() > 0) {
            sinceId = getList().getItemList().get(0).getId();
        }
        return new FriendsMsgLoader(getActivity(), accountId, token, currentGroupId, sinceId, null);
    }

    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateMiddleMsgLoader(int id, Bundle args, String middleBeginId, String middleEndId, String middleEndTag, int middlePosition) {
        String accountId = accountBean.getUid();
        String token = accountBean.getAccess_token();
        return new FriendsMsgLoader(getActivity(), accountId, token, currentGroupId, middleBeginId, middleEndId);
    }

    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String accountId = accountBean.getUid();
        String token = accountBean.getAccess_token();
        String maxId = null;
        if (getList().getItemList().size() > 0) {
            maxId = getList().getItemList().get(getList().getItemList().size() - 1).getId();
        }
        return new FriendsMsgLoader(getActivity(), accountId, token, currentGroupId, null, maxId);
    }
}