package com.hu.iJogging.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
  public static final String BytesDownloadedSoFar = "BytesDownloadedSoFar";
  public static final String TotalSizeBytes = "TotalSizeBytes";
  
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
    values.put(BytesDownloadedSoFar, item.BytesDownloadedSoFar);
    values.put(TotalSizeBytes, item.TotalSizeBytes);
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
  
  public void updateDownloadBytes(String cityName, int downloadBytes){
    ContentValues values = new ContentValues();
    values.put(BytesDownloadedSoFar, downloadBytes);
    String[] args = new String[1];
    args[0] = cityName;
    db.update(TABLE_OFFLINE_NAME, values, "name=? ",args);
  }
  
  public void updateTotalSizeBytes(String cityName, int totalSizeBytes){
    ContentValues values = new ContentValues();
    values.put(TotalSizeBytes, totalSizeBytes);
    String[] args = new String[1];
    args[0] = cityName;
    db.update(TABLE_OFFLINE_NAME, values, "name=? ",args);
  }
  
  public Cursor getOfflineCitiesCursor(){
    Cursor cursor = null;
    cursor = db.query(TABLE_OFFLINE_NAME, null, null, null, null, null, null);
    return cursor;
  }
  
  public int getAllOfflineCitiesCount(){
    Cursor cursor = db.rawQuery("select count(*) from "+TABLE_OFFLINE_NAME,null);
    cursor.moveToFirst();
    int count= cursor.getInt(0);
    cursor.close();
    return count;
  }
  
  /*
   * cityName 为null，则返回全部城市
   * 如果不为null，则是进行搜索
   */
  public Cursor queryOfflineCities(String cityName){
    String[] selectionArgs = new String[1];
    Cursor cursor = null;
    if(cityName != null){
      String select = "name "+"like ?";
      selectionArgs[0] = "%"+cityName+"%";
      cursor = db.query(TABLE_OFFLINE_NAME, null, select, selectionArgs, null, null, null);
    }else{
      cursor = db.query(TABLE_OFFLINE_NAME, null, null, null, null, null, null);
    }
    return cursor;
  }
}
