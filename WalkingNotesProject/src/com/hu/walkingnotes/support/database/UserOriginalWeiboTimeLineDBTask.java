package com.hu.walkingnotes.support.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hu.walkingnotes.bean.MessageBean;
import com.hu.walkingnotes.bean.MessageListBean;
import com.hu.walkingnotes.bean.android.TimeLinePosition;
import com.hu.walkingnotes.bean.android.UserOriginalTimeLineData;
import com.hu.walkingnotes.support.database.table.UserOriginalTable;
import com.hu.walkingnotes.support.debug.AppLogger;
import com.hu.walkingnotes.support.utils.AppConfig;

import java.util.ArrayList;
import java.util.List;

public class UserOriginalWeiboTimeLineDBTask {
    private UserOriginalWeiboTimeLineDBTask() {

    }

    private static SQLiteDatabase getWsd() {

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getWritableDatabase();
    }

    private static SQLiteDatabase getRsd() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getReadableDatabase();
    }


    public static UserOriginalTimeLineData getUserOriginalMsgList(String accountId) {
        TimeLinePosition position = getPosition(accountId);

        Gson gson = new Gson();
        MessageListBean result = new MessageListBean();

        int limit = position.position + AppConfig.DB_CACHE_COUNT_OFFSET > AppConfig.DEFAULT_MSG_COUNT_50 ? position.position + AppConfig.DB_CACHE_COUNT_OFFSET : AppConfig.DEFAULT_MSG_COUNT_50;

        List<MessageBean> msgList = new ArrayList<MessageBean>();
        String sql = "select * from " + UserOriginalTable.UserOriginalDataTable.TABLE_NAME + " where " + UserOriginalTable.UserOriginalDataTable.ACCOUNTID + "  = "
                + "\""+accountId+"\"" + " order by " + UserOriginalTable.UserOriginalDataTable.MBLOGID + " desc limit " + limit;
        Cursor c = getRsd().rawQuery(sql, null);
        while (c.moveToNext()) {
            String json = c.getString(c.getColumnIndex(UserOriginalTable.UserOriginalDataTable.JSONDATA));
            if (!TextUtils.isEmpty(json)) {
                try {
                    MessageBean value = gson.fromJson(json, MessageBean.class);
                    value.getListViewSpannableString();
                    msgList.add(value);
                } catch (JsonSyntaxException e) {
                    AppLogger.e(e.getMessage());
                }
            } else {
                msgList.add(null);
            }

        }

        result.setStatuses(msgList);
        c.close();
        UserOriginalTimeLineData userOriginalTimeLineData = new UserOriginalTimeLineData(result, position);

        return userOriginalTimeLineData;

    }

    public static void addUserOriginalLineMsg(MessageListBean list, String accountId) {
        Gson gson = new Gson();
        List<MessageBean> msgList = list.getItemList();
        int size = msgList.size();

        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(getWsd(), UserOriginalTable.UserOriginalDataTable.TABLE_NAME);
        final int mblogidColumn = ih.getColumnIndex(UserOriginalTable.UserOriginalDataTable.MBLOGID);
        final int accountidColumn = ih.getColumnIndex(UserOriginalTable.UserOriginalDataTable.ACCOUNTID);
        final int jsondataColumn = ih.getColumnIndex(UserOriginalTable.UserOriginalDataTable.JSONDATA);

        try {
            getWsd().beginTransaction();
            for (int i = 0; i < size; i++) {

                MessageBean msg = msgList.get(i);
                ih.prepareForInsert();
                if (msg != null) {
                    ih.bind(mblogidColumn, msg.getId());
                    ih.bind(accountidColumn, accountId);
                    String json = gson.toJson(msg);
                    ih.bind(jsondataColumn, json);
                } else {
                    ih.bind(mblogidColumn, "-1");
                    ih.bind(accountidColumn, accountId);
                    ih.bind(jsondataColumn, "");
                }
                ih.execute();


            }
            getWsd().setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            getWsd().endTransaction();
            ih.close();
        }
        reduceUserOriginalTable(accountId);
    }


    private static void reduceUserOriginalTable(String accountId) {
        String searchCount = "select count(" + UserOriginalTable.UserOriginalDataTable.ID + ") as total" + " from " + UserOriginalTable.UserOriginalDataTable.TABLE_NAME + " where " + UserOriginalTable.UserOriginalDataTable.ACCOUNTID
                + " = "+ "\"" + accountId+ "\"";
        int total = 0;
        Cursor c = getWsd().rawQuery(searchCount, null);
        if (c.moveToNext()) {
            total = c.getInt(c.getColumnIndex("total"));
        }

        c.close();
    }


    static void deleteAllUserOriginals(String accountId) {
        String sql = "delete from " + UserOriginalTable.UserOriginalDataTable.TABLE_NAME + " where " + UserOriginalTable.UserOriginalDataTable.ACCOUNTID + " = " + "\"" + accountId + "\"";
        getWsd().execSQL(sql); 

    }

    public static void asyncUpdatePosition(final TimeLinePosition position, final String accountId) {
        if (position == null)
            return;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updatePosition(position, accountId);
            }
        };

        new Thread(runnable).start();
    }


    private static void updatePosition(TimeLinePosition position, String accountId) {
        String sql = "select * from " + UserOriginalTable.TABLE_NAME + " where " + UserOriginalTable.ACCOUNTID + "  = "
                + "\"" + accountId+ "\"";
        Cursor c = getRsd().rawQuery(sql, null);
        Gson gson = new Gson();
        if (c.moveToNext()) {
            try {
                String[] args = {accountId};
                ContentValues cv = new ContentValues();
                cv.put(UserOriginalTable.TIMELINEDATA, gson.toJson(position));
                getWsd().update(UserOriginalTable.TABLE_NAME, cv, UserOriginalTable.ACCOUNTID + "=?", args);
            } catch (JsonSyntaxException e) {

            }
        } else {

            ContentValues cv = new ContentValues();
            cv.put(UserOriginalTable.ACCOUNTID, accountId);
            cv.put(UserOriginalTable.TIMELINEDATA, gson.toJson(position));
            getWsd().insert(UserOriginalTable.TABLE_NAME,
                    UserOriginalTable.ID, cv);
        }
    }

    public static TimeLinePosition getPosition(String accountId) {
        String sql = "select * from " + UserOriginalTable.TABLE_NAME + " where " + UserOriginalTable.ACCOUNTID + "  = "
                + "\""+ accountId+ "\"";
        Cursor c = getRsd().rawQuery(sql, null);
        Gson gson = new Gson();
        while (c.moveToNext()) {
            String json = c.getString(c.getColumnIndex(UserOriginalTable.TIMELINEDATA));
            if (!TextUtils.isEmpty(json)) {
                try {
                    TimeLinePosition value = gson.fromJson(json, TimeLinePosition.class);
                    return value;

                } catch (JsonSyntaxException e) {

                }
            }

        }
        c.close();
        return new TimeLinePosition(0, 0);
    }

    public static void asyncReplace(final MessageListBean list, final String accountId) {
        final MessageListBean data = new MessageListBean();
        data.replaceData(list);
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteAllUserOriginals(accountId);
                addUserOriginalLineMsg(data, accountId);
            }
        }).start();

    }
}
