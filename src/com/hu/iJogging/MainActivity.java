package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;

public class MainActivity extends SherlockFragmentActivity{
  private ActionBar mActionBar;
  private ActionBarAdapter mAdapter = null;

  
  private void setupActionBar(){
    this.mActionBar = getSupportActionBar();
    this.mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    this.mActionBar.setDisplayShowTitleEnabled(false);
    this.mActionBar.setDisplayShowHomeEnabled(false);
  }

  @Override
  protected void onCreate(Bundle arg0) {
    // TODO Auto-generated method stub
    super.onCreate(arg0);
  }

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    setupActionBar();
  }

  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
  }
}
