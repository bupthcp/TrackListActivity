package com.hu.iJogging.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class IJoggingDatabaseUtils {
  
  private SQLiteDatabase db;
  
  public IJoggingDatabaseUtils(Context context){
    IJoggingDatabaseHelper dbHelper = new IJoggingDatabaseHelper(context);
    db = dbHelper.getWritableDatabase();
  }
}
