package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity{
  private ActionBar mActionBar;
  private ActionBarAdapter mAdapter = null;
  
  private void setActionBar(){
    this.mActionBar = getSupportActionBar();
    this.mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    this.mActionBar.setDisplayShowTitleEnabled(false);
    this.mActionBar.setDisplayShowHomeEnabled(false);
  }
}
