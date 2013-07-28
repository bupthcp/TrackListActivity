package com.hu.iJogging;

import com.google.android.maps.mytracks.R;
import com.hu.iJogging.Services.DownloadOfflineListener;
import com.hu.iJogging.Services.DownloadOfflineMapService.DownloadOfflineMapServiceBinder;
import com.hu.iJogging.Services.DownloadOfflineMapServiceConnection;
import com.hu.iJogging.common.IJoggingDatabaseUtils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class AllOfflineMapActivity  extends ActionBarActivity implements OnClickListener, DownloadOfflineListener{
  
  private static final String TAG = AllOfflineMapActivity.class.getSimpleName();
  private ListView listView = null;
  private ResourceCursorAdapter resourceCursorAdapter;
  private LoadOfflineMapTask loadOfflineMapTask;
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
      downloadOfflineMapServiceBinder.registerDownloadOfflineListener(AllOfflineMapActivity.this);
    }
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.offline_map_activity);
    setupActionBar();
    listView =  (ListView)findViewById(R.id.map_list);
    resourceCursorAdapter = new ResourceCursorAdapter(this, R.layout.offline_map_list_item, null, 0){
      
      @Override
      public void bindView(View view, Context context, Cursor cursor) {
        int idx_name = cursor.getColumnIndex(IJoggingDatabaseUtils.name);
        int idx_province = cursor.getColumnIndex(IJoggingDatabaseUtils.province);
        int idx_ArHighUrl = cursor.getColumnIndex(IJoggingDatabaseUtils.ArHighUrl);
        int idx_ArLowUrl = cursor.getColumnIndex(IJoggingDatabaseUtils.ArLowUrl);
        int idx_ArHighSize = cursor.getColumnIndex(IJoggingDatabaseUtils.ArHighSize);
        int idx_ArLowSize = cursor.getColumnIndex(IJoggingDatabaseUtils.ArLowSize);
        int idx_BytesDownloadedSoFar = cursor.getColumnIndex(IJoggingDatabaseUtils.BytesDownloadedSoFar);
        int idx_TotalSizeBytes = cursor.getColumnIndex(IJoggingDatabaseUtils.TotalSizeBytes);
        String name = cursor.getString(idx_name);
        String province = cursor.getString(idx_province);
        String ArHighUrl = cursor.getString(idx_ArHighUrl);
        String ArLowUrl = cursor.getString(idx_ArLowUrl);
        String ArHighSize = cursor.getString(idx_ArHighSize);
        String ArLowSize = cursor.getString(idx_ArLowSize);
        int BytesDownloadedSoFar = cursor.getInt(idx_BytesDownloadedSoFar);
        int TotalSizeBytes = cursor.getInt(idx_TotalSizeBytes);
        ViewHolder viewHolder = new ViewHolder();
        TextView list_item_name = (TextView) view.findViewById(R.id.list_item_name);
        if((name == null)||(name.equals(""))){
          list_item_name.setText(province);
          viewHolder.cityName = province;
        }else{
          list_item_name.setText(name);
          viewHolder.cityName = name;
        }        
        TextView list_item_total_size = (TextView) view.findViewById(R.id.list_item_total_size);
        list_item_total_size.setText(ArHighSize);
        View button_download = view.findViewById(R.id.button_download);
        button_download.setClickable(true);
        button_download.setOnClickListener(AllOfflineMapActivity.this);
        viewHolder.url = ArHighUrl;
        button_download.setTag(viewHolder);
        TextView percentage = (TextView) button_download.findViewById(R.id.download_percentage);
        float per = 0f;
        if(TotalSizeBytes != 0){
          if(BytesDownloadedSoFar < 0)
            BytesDownloadedSoFar = 0;
          per = ((float)BytesDownloadedSoFar)/((float)TotalSizeBytes) ;
        }
        String perStr = String.format("%1$3d%%", (int)(per*100));
        percentage.setText(perStr); 
      }
    };
    downloadOfflineMapServiceConnection = new DownloadOfflineMapServiceConnection(this ,bindChangedCallback);
    downloadOfflineMapServiceConnection.bindService();
    loadOfflineMapTask = new LoadOfflineMapTask();
    listView.setAdapter(resourceCursorAdapter);
    loadOfflineMapTask.execute();
  }
  
  private class LoadOfflineMapTask extends AsyncTask<Void, Void, Cursor> {
    @Override
    protected Cursor doInBackground(Void... params) {
      IJoggingDatabaseUtils iJoggingDatabaseUtils =((IJoggingApplication)(AllOfflineMapActivity.this.getApplication())).getIJoggingDatabaseUtils();
      Cursor cursor = iJoggingDatabaseUtils.getOfflineCitiesCursor();
      return cursor;
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
      if(cursor != null){
        resourceCursorAdapter.swapCursor(cursor);
      }
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
    tv.setText(R.string.strHotOfflineMapActivity);
    
    View iv = customView.findViewById(R.id.icon_back);
    iv.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        finish();
      }    
    });
  }
  
  private class ViewHolder{
    String url;
    String cityName;
  }
  
  @Override
  protected void onDestroy(){
    downloadOfflineMapServiceBinder.unRegisterDownloadOfflineListener(AllOfflineMapActivity.this);
    if(downloadOfflineMapServiceConnection != null){
      downloadOfflineMapServiceConnection.unbind();
    }
    Cursor cursor = resourceCursorAdapter.getCursor();
    if(cursor != null){
      cursor.close();
    }
    super.onDestroy();
  }
  

  @Override
  public void onClick(View v) {
    Object tmp = v.getTag();
    if(tmp != null){
      ViewHolder viewHolder = (ViewHolder)tmp;
//      try{
//        Class<?> classtype = Class.forName("android.provider.Downloads");
//        Field temp = classtype.getDeclaredField("CONTENT_URI");
//        Uri uri = (Uri)temp.get(null);
//        Log.i(TAG, uri.getPath());
//      }catch(Exception e){
//        e.printStackTrace();
//      }
      
      downloadOfflineMapServiceBinder.startDownloadZip(viewHolder.url,viewHolder.cityName);
//      try{
//        ZipUtils.unZipOneFolder("//mnt/sdcard/BaiduMapSdk/½ð²ýÊÐ.zip","/sdcard/BaiduMapSdk","BaiduMap","utf-8");
//      }catch(Exception e){
//        e.printStackTrace();
//      }
    }
  }


  @Override
  public void notifyOfflineMapStateUpdate() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Cursor cursor = resourceCursorAdapter.getCursor();
        cursor.requery();
        resourceCursorAdapter.notifyDataSetChanged();
      }
    });
  }

}
