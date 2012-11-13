package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.baidu.mapapi.MKOLUpdateElement;
import com.baidu.mapapi.MKOfflineMap;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.Services.DownloadOfflineMapService;
import com.hu.iJogging.fragments.OfflineMapAdapter;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class InstalledOfflineMapActivity extends SherlockActivity implements OnClickListener{
 
  private MKOfflineMap mOffline = null;
  private ListView listview = null;
  private OfflineMapAdapter adapter = null;
  private ArrayList<MKOLUpdateElement> installedMapList;  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.offline_map_activity);
    setupActionBar();
    mOffline=DownloadOfflineMapService.mOffline;
    listview =  (ListView)findViewById(R.id.map_list);
    installedMapList = mOffline.getAllUpdateInfo();
    adapter = new OfflineMapAdapter(this,installedMapList,null,OfflineMapAdapter.TYPE_INSTALLED);
    listview.setAdapter(adapter);
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
    tv.setText(R.string.strInstalledOfflineMapActivity);
    
    View iv = customView.findViewById(R.id.icon_back);
    iv.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        finish();
      }    
    });
  }

  @Override
  public void onClick(View view) {
    Object tmp = view.getTag();
    if((tmp != null)&&(tmp instanceof MKOLUpdateElement)){
      MKOLUpdateElement updateElement = (MKOLUpdateElement)tmp;
      mOffline.remove(updateElement.cityID);
    }
  }
}
