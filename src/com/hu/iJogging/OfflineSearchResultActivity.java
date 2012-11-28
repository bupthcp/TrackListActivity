package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.fragments.OfflineMapAdapter;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class OfflineSearchResultActivity extends SherlockActivity{
  private ListView listview = null;
  private OfflineMapAdapter adapter = null;
  public static final String OFFLIE_RESULT_STRING = "city_name";
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.offline_map_activity);
    setupActionBar();
    String searchStr = this.getIntent().getExtras().getString(OFFLIE_RESULT_STRING);
    if(searchStr != null){
//      mOffline = DownloadOfflineMapService.mOffline;
//      listview =  (ListView)findViewById(R.id.map_list);
//      searchedMapList = mOffline.searchCity(searchStr);
//      adapter = new OfflineMapAdapter(this,null,searchedMapList,OfflineMapAdapter.TYPE_SEARCHED);
//      listview.setAdapter(adapter);
    }
  }
  
  
  private void setupActionBar(){
    ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setDisplayUseLogoEnabled(false);
    actionBar.setCustomView(R.layout.actionbar_cunstom_simple);
    View customView = actionBar.getCustomView();
    TextView tv = (TextView)customView.findViewById(R.id.simple_action_bar_title);
    tv.setText(R.string.strOfflineSearchResultActivity);
    
    View iv = customView.findViewById(R.id.icon_back);
    iv.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        finish();
      }    
    });
  }
}
