package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.Services.DownloadOfflineMapService.DownloadOfflineMapServiceBinder;
import com.hu.iJogging.Services.DownloadOfflineMapServiceConnection;
import com.hu.iJogging.fragments.OfflineMapAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class InstalledOfflineMapActivity extends SherlockActivity implements OnClickListener{
  
  private static final String TAG = InstalledOfflineMapActivity.class.getSimpleName();
 
  private MKOfflineMap mOffline = null;
  private ListView listview = null;
  private OfflineMapAdapter adapter = null;
  private ArrayList<MKOLUpdateElement> installedMapList;  
  
  private DownloadOfflineMapServiceConnection downloadOfflineMapServiceConnection;
  private DownloadOfflineMapServiceBinder downloadOfflineMapServiceBinder;
  
  private final Runnable bindChangedCallback = new Runnable() {
    @Override
    public void run() {
      downloadOfflineMapServiceBinder = downloadOfflineMapServiceConnection.getServiceIfBound();
      if (downloadOfflineMapServiceBinder == null) {
        Log.d(TAG, "downloadOfflineMapService service not available");
        return;
      }
      mOffline = downloadOfflineMapServiceBinder.getOfflineInstance();
      mOffline.scan();
      installedMapList = mOffline.getAllUpdateInfo();
      adapter = new OfflineMapAdapter(InstalledOfflineMapActivity.this,installedMapList,null,OfflineMapAdapter.TYPE_INSTALLED);
      listview.setAdapter(adapter);
    }
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.offline_map_activity);
    setupActionBar();
    downloadOfflineMapServiceConnection = new DownloadOfflineMapServiceConnection(this ,bindChangedCallback);
    downloadOfflineMapServiceConnection.bindService();
    listview =  (ListView)findViewById(R.id.map_list);
  }
  
  @Override
  protected void onDestroy(){
    if(downloadOfflineMapServiceConnection != null){
      downloadOfflineMapServiceConnection.unbind();
    }
    super.onDestroy();
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
      Log.i(TAG, "cityId= "+updateElement.cityID);
//      downloadOfflineMapServiceBinder.deleteOfflineMap(updateElement.cityID);
      mOffline.remove(updateElement.cityID);
    }
  }
}
