package com.hu.iJogging;

import com.baidu.mapapi.map.MKOLSearchRecord;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.Services.DownloadOfflineListener;
import com.hu.iJogging.Services.DownloadOfflineMapService.DownloadOfflineMapServiceBinder;
import com.hu.iJogging.Services.DownloadOfflineMapServiceConnection;
import com.hu.iJogging.fragments.OfflineMapAdapter;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class HotOfflineMapActivity extends ActionBarActivity implements OnClickListener, DownloadOfflineListener{
  
  private static final String TAG = HotOfflineMapActivity.class.getSimpleName();
  
  private MKOfflineMap mOffline = null;
  private ListView listview = null;
  private OfflineMapAdapter adapter = null;
  private ArrayList<MKOLSearchRecord> searchedMapList;
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
      searchedMapList = mOffline.getOfflineCityList();
      adapter = new OfflineMapAdapter(HotOfflineMapActivity.this,null,searchedMapList,OfflineMapAdapter.TYPE_SEARCHED);
      listview.setAdapter(adapter);
      downloadOfflineMapServiceBinder.registerDownloadOfflineListener(HotOfflineMapActivity.this);
    }
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.offline_map_activity);
    setupActionBar();
    listview =  (ListView)findViewById(R.id.map_list);
    downloadOfflineMapServiceConnection = new DownloadOfflineMapServiceConnection(this ,bindChangedCallback);
    downloadOfflineMapServiceConnection.bindService();
  }
  
  
  
  
  
  @Override
  protected void onPause() {
    super.onPause();
    if(downloadOfflineMapServiceBinder != null){
      downloadOfflineMapServiceBinder.unRegisterDownloadOfflineListener(this);      
    }
  }



  @Override
  protected void onResume() {
    super.onResume();
    if(downloadOfflineMapServiceBinder != null){
      downloadOfflineMapServiceBinder.registerDownloadOfflineListener(this);
    }
  }



  @Override
  protected void onDestroy(){
    if(downloadOfflineMapServiceConnection != null){
      downloadOfflineMapServiceConnection.unbind();
    }
    super.onDestroy();
  }
  
  public DownloadOfflineMapServiceBinder getDownloadOfflineService(){
    return downloadOfflineMapServiceBinder;
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
    if((tmp !=null)&&( tmp instanceof MKOLSearchRecord)){
      MKOLSearchRecord searchRecord =(MKOLSearchRecord)tmp ;
      if(downloadOfflineMapServiceBinder != null){
        Log.d(TAG, searchRecord.cityName+" is clicked");
        MKOLUpdateElement updateInfo = downloadOfflineMapServiceBinder.getOfflineUpdateInfo(searchRecord.cityID);
        if(updateInfo != null){
          if(updateInfo.status == MKOLUpdateElement.FINISHED){
            //对已经下载完成的离线地图包不做处理
          }else{
            //正在下载的离线地图，进行暂停操作
            downloadOfflineMapServiceBinder.pauseDownload(searchRecord.cityID);
          }
        }else{
          //updateInfo为null说明下载并没有开始,则开始下载这个城市的离线地图包
          downloadOfflineMapServiceBinder.startDownload(searchRecord.cityID);
        }
      }
    }
  }



  @Override
  public void notifyOfflineMapStateUpdate() {
    // TODO Auto-generated method stub
    adapter.notifyDataSetChanged();
  }

}
