package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;

public class MainActivity extends SherlockFragmentActivity{
  private ActionBar mActionBar;
  private ActionBarAdapter mAdapter = null;
  private WorkoutPage mWorkoutPage = null;

  
  private void setupActionBar(){
    this.mActionBar = getSupportActionBar();
    this.mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    this.mActionBar.setDisplayShowTitleEnabled(false);
    this.mActionBar.setDisplayShowHomeEnabled(false);
    mAdapter = new ActionBarAdapter(this, 1);
    mAdapter.new OnNaviListener();
    mActionBar.setListNavigationCallbacks(mAdapter,mAdapter.new OnNaviListener());
  }

  @Override
  protected void onCreate(Bundle arg0) {
    // TODO Auto-generated method stub
    super.onCreate(arg0);
    mWorkoutPage = new WorkoutPage(this);
  }
  
  private void initializeDelayed(){
    mWorkoutPage.setView();
    mWorkoutPage.setFocus();
    setupActionBar();
  }

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    initializeDelayed();
  }

  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
  }
}
