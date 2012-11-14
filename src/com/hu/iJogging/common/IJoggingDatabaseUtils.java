package com.hu.iJogging.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashSet;
import java.util.Set;

public class IJoggingDatabaseUtils {
  
  private SQLiteDatabase db;
  
  public static final String TABLE_OFFLINE_NAME = "OfflineCity";
  public static final String name = "name";
  public static final String province = "province";
  public static final String ArHighUrl = "ArHighUrl";
  public static final String ArLowUrl = "ArLowUrl";
  public static final String ArHighSize = "ArHighSize";
  public static final String ArLowSize = "ArLowSize";
  
  public IJoggingDatabaseUtils(Context context){
    IJoggingDatabaseHelper dbHelper = new IJoggingDatabaseHelper(context);
    db = dbHelper.getWritableDatabase();
  }
  
  public void insertOfflineCityItem(OfflineCityItem item){
    ContentValues values = new ContentValues();
    values.put(name, item.name);
    values.put(province, item.province);
    values.put(ArHighUrl, item.ArHighUrl);
    values.put(ArLowUrl, item.ArLowUrl);
    values.put(ArHighSize, item.ArHighSize);
    values.put(ArLowSize, item.ArLowSize);
    db.insert(TABLE_OFFLINE_NAME, null, values);
  }
  
  public void updateAllCities(Set<OfflineCityItem> offlineCities){
    db.delete(TABLE_OFFLINE_NAME, null, null);
    bulkInsertOfflineCities(offlineCities);
  }
  
  public void bulkInsertOfflineCities(Set<OfflineCityItem> offlineCities){
    try{
      db.beginTransaction();
      for(OfflineCityItem offlineCity : offlineCities){
        insertOfflineCityItem(offlineCity);
      }
      db.setTransactionSuccessful();
    }finally{
      db.endTransaction();
    }
  }
  
  /*
   * cityName 为null，则返回全部城市
   * 如果不为null，则是进行搜索
   */
  public Set<OfflineCityItem> queryOfflineCities(String cityName){
    String[] selectionArgs = new String[1];
    Set<OfflineCityItem> offlineCities = null;
    Cursor cursor = null;
    if(cityName != null){
      String select = "name "+"like %?%";
      selectionArgs[0] = cityName;
      cursor = db.query(TABLE_OFFLINE_NAME, null, select, selectionArgs, null, null, null);
    }else{
      cursor = db.query(TABLE_OFFLINE_NAME, null, null, null, null, null, null);
    }
    if (cursor != null && cursor.moveToNext()) {
      offlineCities = new HashSet<OfflineCityItem>();
      do{
        int idx_name = cursor.getColumnIndex(name);
        int idx_province = cursor.getColumnIndex(province);
        int idx_ArHighUrl = cursor.getColumnIndex(ArHighUrl);
        int idx_ArLowUrl = cursor.getColumnIndex(ArLowUrl);
        int idx_ArHighSize = cursor.getColumnIndex(ArHighSize);
        int idx_ArLowSize = cursor.getColumnIndex(ArLowSize);
        OfflineCityItem item = new OfflineCityItem();
        item.name = cursor.getString(idx_name);
        item.province = cursor.getString(idx_province);
        item.ArHighUrl = cursor.getString(idx_ArHighUrl);
        item.ArLowUrl = cursor.getString(idx_ArLowUrl);
        item.ArHighSize = cursor.getString(idx_ArHighSize);
        item.ArLowSize = cursor.getString(idx_ArLowSize);
        offlineCities.add(item);
      }while(cursor.moveToNext());
    }
    cursor.close();
    return offlineCities;
  }
}
