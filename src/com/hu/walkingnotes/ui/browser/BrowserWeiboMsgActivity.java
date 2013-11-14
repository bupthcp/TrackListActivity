package com.hu.walkingnotes.ui.browser;

import com.hu.iJogging.R;
import com.hu.walkingnotes.bean.MessageBean;
import com.hu.walkingnotes.dao.destroy.DestroyStatusDao;
import com.hu.walkingnotes.support.error.WeiboException;
import com.hu.walkingnotes.support.lib.MyAsyncTask;
import com.hu.walkingnotes.support.utils.GlobalContext;
import com.hu.walkingnotes.support.utils.Utility;
import com.hu.walkingnotes.ui.interfaces.AbstractAppActivity;
import com.hu.walkingnotes.ui.main.MainTimeLineActivity;
import com.hu.walkingnotes.ui.send.WriteCommentActivity;
import com.hu.walkingnotes.ui.send.WriteRepostActivity;
import com.hu.walkingnotes.ui.task.FavAsyncTask;
import com.hu.walkingnotes.ui.task.UnFavAsyncTask;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * User: Jiang Qi
 * Date: 12-8-1
 */
public class BrowserWeiboMsgActivity extends AbstractAppActivity implements RemoveWeiboMsgDialog.IRemove {

    private MessageBean msg;
    private String token;

    private FavAsyncTask favTask = null;
    private UnFavAsyncTask unFavTask = null;
    private ShareActionProvider shareActionProvider;
    private GestureDetector gestureDetector;
    private RemoveTask removeTask;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("msg", msg);
        outState.putString("token", token);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            msg = savedInstanceState.getParcelable("msg");
            token = savedInstanceState.getString("token");
        } else {
            Intent intent = getIntent();
            token = intent.getStringExtra("token");
            msg = intent.getParcelableExtra("msg");
        }
        initLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.cancelTasks(removeTask);
    }

    private void initLayout() {

        if (getSupportFragmentManager().findFragmentByTag(BrowserWeiboMsgFragment.class.getName()) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new BrowserWeiboMsgFragment(msg), BrowserWeiboMsgFragment.class.getName())
                    .commit();
        }

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.detail));

    }


    private Fragment getBrowserWeiboMsgFragment() {
        return getSupportFragmentManager().findFragmentByTag(BrowserWeiboMsgFragment.class.getName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_browserweibomsgactivity, menu);

        if (msg.getUser() != null && msg.getUser().getId().equals(GlobalContext.getInstance().getCurrentAccountId())) {
            menu.findItem(R.id.menu_delete).setVisible(true);
        }

        MenuItem item = menu.findItem(R.id.menu_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainTimeLineActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.menu_repost:
                intent = new Intent(this, WriteRepostActivity.class);
                intent.putExtra("token", getToken());
                intent.putExtra("id", getMsg().getId());
                intent.putExtra("msg", getMsg());
                startActivity(intent);
                return true;
            case R.id.menu_comment:

                intent = new Intent(this, WriteCommentActivity.class);
                intent.putExtra("token", getToken());
                intent.putExtra("id", getMsg().getId());
                intent.putExtra("msg", getMsg());
                startActivity(intent);

                return true;

            case R.id.menu_share:

                buildShareActionMenu();
                return true;
            case R.id.menu_copy:
                if(VERSION.SDK_INT >=11){
                  ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                  cm.setPrimaryClip(ClipData.newPlainText("sinaweibo", getMsg().getText()));
                }else{
                  android.text.ClipboardManager clipboard = (android.text.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                  CharSequence text = clipboard.getText();
                }
                Toast.makeText(this, getString(R.string.copy_successfully), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_fav:
                if (Utility.isTaskStopped(favTask) && Utility.isTaskStopped(unFavTask)) {
                    favTask = new FavAsyncTask(getToken(), msg.getId());
                    favTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
                return true;
            case R.id.menu_unfav:
                if (Utility.isTaskStopped(favTask) && Utility.isTaskStopped(unFavTask)) {
                    unFavTask = new UnFavAsyncTask(getToken(), msg.getId());
                    unFavTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
                return true;
            case R.id.menu_delete:
                RemoveWeiboMsgDialog dialog = new RemoveWeiboMsgDialog(msg.getId());
                dialog.show(getFragmentManager(), "");
                return true;
        }
        return false;
    }

    private void buildShareActionMenu() {
        Utility.setShareIntent(BrowserWeiboMsgActivity.this, shareActionProvider, msg);
    }

    @Override
    public void removeMsg(String id) {
        if (Utility.isTaskStopped(removeTask)) {
            removeTask = new RemoveTask(id);
            removeTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    public void updateCommentCount(int count) {
        msg.setComments_count(count);
        Intent intent = new Intent();
        intent.putExtra("msg", msg);
        setResult(0, intent);
    }

    public void updateRepostCount(int count) {
        msg.setReposts_count(count);
        Intent intent = new Intent();
        intent.putExtra("msg", msg);
        setResult(0, intent);
    }

    public String getToken() {
        return token;
    }

    public MessageBean getMsg() {
        return msg;
    }


    class RemoveTask extends MyAsyncTask<Void, Void, Boolean> {

        String id;
        WeiboException e;

        public RemoveTask(String id) {
            this.id = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            DestroyStatusDao dao = new DestroyStatusDao(token, id);
            try {
                return dao.destroy();
            } catch (WeiboException e) {
                this.e = e;
                cancel(true);
                return false;
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            if (this.e != null) {
                Toast.makeText(BrowserWeiboMsgActivity.this, e.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                finish();
            }
        }
    }
}
