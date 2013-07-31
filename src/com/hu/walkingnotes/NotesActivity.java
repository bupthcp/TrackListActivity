package com.hu.walkingnotes;

import com.google.android.maps.mytracks.R;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class NotesActivity extends ActionBarActivity{
  
  private ActionBar mActionBar;
  private SpinnerAdapter mAdapter = null;
  private OnNavigationListener  mOnNavigationListener = null;
  
  private void setupActionBar() {
    this.mActionBar = getSupportActionBar();
    this.mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    SpinnerAdapter mAdapter = ArrayAdapter.createFromResource(this, R.array.actionbar_navigation,
        R.layout.actionbar_dropdown_item_view);
    mOnNavigationListener = new OnNavigationListener() {
      @Override
      public boolean onNavigationItemSelected(int position, long itemId) {
        return true;
      }
    };
    mActionBar.setListNavigationCallbacks(mAdapter, mOnNavigationListener);
  }
  
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setupActionBar();
  }

}
