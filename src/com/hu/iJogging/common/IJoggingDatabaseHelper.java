package com.hu.iJogging.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IJoggingDatabaseHelper extends SQLiteOpenHelper{
  
  private static final String DATABASE_NAME = "IJogging.db";
  private static final int DATABASE_VERSION = 20;
  
  public static final String CREATE_OFFLINE_TABLE = "CREATE TABLE " + IJoggingDatabaseUtils.TABLE_OFFLINE_NAME + " ("
      + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + IJoggingDatabaseUtils.name + " STRING, " 
      + IJoggingDatabaseUtils.province + " STRING, " 
      + IJoggingDatabaseUtils.ArHighUrl + " STRING, " 
      + IJoggingDatabaseUtils.ArLowUrl + " STRING, " 
      + IJoggingDatabaseUtils.ArHighSize + " STRING, " 
      + IJoggingDatabaseUtils.ArLowSize + " STRING, "
      + IJoggingDatabaseUtils.BytesDownloadedSoFar + " INTEGER, "
      + IJoggingDatabaseUtils.TotalSizeBytes + " INTEGER"
      + ");";
  
  public IJoggingDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_OFFLINE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // TODO Auto-generated method stub
  }
}
