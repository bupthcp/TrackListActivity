package com.hu.walkingnotes.ui.task;

import com.hu.iJogging.R;
import com.hu.walkingnotes.bean.FavBean;
import com.hu.walkingnotes.dao.fav.FavDao;
import com.hu.walkingnotes.support.error.WeiboException;
import com.hu.walkingnotes.support.lib.MyAsyncTask;
import com.hu.walkingnotes.support.utils.GlobalContext;

import android.widget.Toast;

/**
 * User: qii
 * Date: 13-2-11
 */
public class UnFavAsyncTask extends MyAsyncTask<Void, FavBean, FavBean> {

    private String token;
    private String id;
    private WeiboException e;

    public UnFavAsyncTask(String token, String id) {
        this.token = token;
        this.id = id;
    }

    @Override
    protected FavBean doInBackground(Void... params) {
        FavDao dao = new FavDao(token, id);
        try {
            return dao.unFavIt();
        } catch (WeiboException e) {
            this.e = e;
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onCancelled(FavBean favBean) {
        super.onCancelled(favBean);
        if (favBean == null && this.e != null)
            Toast.makeText(GlobalContext.getInstance(), e.getError(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(FavBean favBean) {
        super.onPostExecute(favBean);
        if (favBean != null)
            Toast.makeText(GlobalContext.getInstance(), GlobalContext.getInstance().getString(R.string.un_fav_successfully), Toast.LENGTH_SHORT).show();
    }
}
