package com.hu.iJogging.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IJoggingDatabaseHelper extends SQLiteOpenHelper{
  
  private static final String DATABASE_NAME = "IJogging.db";
  private static final int DATABASE_VERSION = 20;

  private static final String TABLE_NAME = "OfflineCity";
  public static final String name = "name";
  public static final String province = "province";

  public static final String ArHighUrl = "ArHighUrl";
  public static final String ArLowUrl = "ArLowUrl";
  public static final String ArHighSize = "ArHighSize";
  public static final String ArLowSize = "ArLowSize";
  
  
  public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
      + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + name + " STRING, " 
      + province + " STRING, " 
      + ArHighUrl + " STRING, " 
      + ArLowUrl + " STRING, " 
      + ArHighSize + " STRING, " 
      + ArLowSize + " STRING, " 
      + ");";
  
  public IJoggingDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // TODO Auto-generated method stub
  }
}
