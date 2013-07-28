package com.hu.iJogging;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity{
  private ActionBar mActionBar;
  private ActionBarAdapter mAdapter = null;
  private WorkoutPage mWorkoutPage = null;

  
  private void setupActionBar(){
    this.mActionBar = getSupportActionBar();
    this.mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    this.mActionBar.setDisplayShowTitleEnabled(false);
    this.mActionBar.setDisplayShowHomeEnabled(false);
    mAdapter = new ActionBarAdapter(this);
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
  
  private void startInitialization(){
    //TODO
    //add DelayedInitializer() here
    //if useDelayedInitializer()
    initializeDelayed();
  }

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    if(null != mActionBar){
      mActionBar.setSelectedNavigationItem(0);
    }
    WorkoutPage.setFakeView(this);
    startInitialization();
  }

  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
  }
}
