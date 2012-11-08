package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.baidu.mapapi.MKOLSearchRecord;
import com.baidu.mapapi.MKOfflineMap;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.Services.DownloadOfflineMapService;
import com.hu.iJogging.Services.DownloadOfflineMapServiceConnection;
import com.hu.iJogging.fragments.OfflineMapAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class HotOfflineMapActivity extends SherlockActivity implements OnClickListener{
  
  private static final String TAG = HotOfflineMapActivity.class.getSimpleName();
  
  private MKOfflineMap mOffline = null;
  private ListView listview = null;
  private OfflineMapAdapter adapter = null;
  private ArrayList<MKOLSearchRecord> searchedMapList;
  private DownloadOfflineMapServiceConnection downloadOfflineMapServiceConnection;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.offline_map_activity);
    setupActionBar();
    mOffline = DownloadOfflineMapService.mOffline;
    listview =  (ListView)findViewById(R.id.map_list);
    searchedMapList = mOffline.getOfflineCityList();
    adapter = new OfflineMapAdapter(this,null,searchedMapList,OfflineMapAdapter.TYPE_SEARCHED);
    downloadOfflineMapServiceConnection = new DownloadOfflineMapServiceConnection(this ,null);
    downloadOfflineMapServiceConnection.bindService();
    listview.setAdapter(adapter);
  }
  
  
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    downloadOfflineMapServiceConnection.unbind();
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
    tv.setText(R.string.strHotOfflineMapActivity);
    
    View iv = customView.findViewById(R.id.icon_back);
    iv.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        finish();
      }    
    });
  }

  @Override
  public void onClick(View v) {
    Object tmp = v.getTag();
    if(tmp instanceof MKOLSearchRecord){
      MKOLSearchRecord searchRecord =(MKOLSearchRecord)tmp ;
      if(searchRecord != null){
        Log.d(TAG, searchRecord.cityName+" is clicked");
        
      }
    }
  }

}
