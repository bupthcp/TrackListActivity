package com.hu.iJogging.fragments;

import com.baidu.mapapi.MKOLSearchRecord;
import com.baidu.mapapi.MKOLUpdateElement;
import com.baidu.mapapi.MKOfflineMap;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.HotOfflineMapActivity;
import com.hu.iJogging.IJoggingActivity;
import com.hu.iJogging.InstalledOfflineMapActivity;
import com.hu.iJogging.OfflineSearchResultActivity;
import com.hu.iJogging.Services.DownloadOfflineMapService;
import com.hu.iJogging.common.OfflineCityItem;
import com.hu.iJogging.common.OfflineMapCitiesParser;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

public class OfflineMapFragment extends Fragment{
  
  private static String TAG =  "OfflineMapFragment";
  
  private IJoggingActivity mActivity;
  private MKOfflineMap mOffline = null;
  private View mFragmentView;
  private ArrayList<MKOLUpdateElement> installedMapList;  
  private ArrayList<MKOLSearchRecord> searchedMapList;
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = (IJoggingActivity)activity;
    mActivity.findViewById(R.id.training_detail_container).setVisibility(View.GONE);
    mActivity.findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
    activity.findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
    activity.findViewById(R.id.training_detail_container).setVisibility(View.GONE);
    mOffline = DownloadOfflineMapService.mOffline;
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mFragmentView = getActivity().getLayoutInflater().inflate(R.layout.offline_map_introduction, container, false);
    installedMapList = mOffline.getAllUpdateInfo();
    searchedMapList = mOffline.getOfflineCityList();
    ImageView searchButton = (ImageView)mFragmentView.findViewById(R.id.search_offline_map_button);
    searchButton.setClickable(true);
    searchButton.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v) {
        EditText searchName = (EditText)mFragmentView.findViewById(R.id.search_offline_map_value);
        String searchStr = searchName.getText().toString();
        Intent startSearchResultIntent = new Intent(getActivity(),OfflineSearchResultActivity.class);
        startSearchResultIntent.putExtra(OfflineSearchResultActivity.OFFLIE_RESULT_STRING, searchStr);
        mActivity.startActivity(startSearchResultIntent);
      }     
    });
    TextView tvHotOfflineMapValue = (TextView)mFragmentView.findViewById(R.id.hot_offline_map_value);
    tvHotOfflineMapValue.setText(Integer.toString(searchedMapList.size()));
    View Zone2 = mFragmentView.findViewById(R.id.zone2);
    Zone2.setClickable(true);
    Zone2.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v) {
        Intent startHotOfflineIntent = new Intent(getActivity(), HotOfflineMapActivity.class);
        mActivity.startActivity(startHotOfflineIntent);
      }
    });
    TextView tvInstalledOfflineMapValue = (TextView)mFragmentView.findViewById(R.id.installed_offline_map_value);
    if(installedMapList != null){
      tvInstalledOfflineMapValue.setText(Integer.toString(installedMapList.size()));
    }else{
      tvInstalledOfflineMapValue.setText("0");
    }
    View Zone3 = mFragmentView.findViewById(R.id.zone3);
    Zone3.setClickable(true);
    Zone3.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v) {
        Intent startSearchedIntent = new Intent(getActivity(), InstalledOfflineMapActivity.class);
        mActivity.startActivity(startSearchedIntent);
      }
    });
    
    Button button = (Button)mFragmentView.findViewById(R.id.button1);
    button.setOnClickListener(new OnClickListener(){

      @Override
      public void onClick(View v) {
        initOfflineMapTask.execute();
      }
      
    });
    
    return mFragmentView;
  }
  
  
  private String baiduMapUrl = "http://shouji.baidu.com/resource/xml/map/city.xml";
  private String baiduMapUrlVector = "http://shouji.baidu.com/resource/xml/map/city_vector.xml";
  private InitOfflineMapTask initOfflineMapTask = new InitOfflineMapTask();

  private class InitOfflineMapTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpUriRequest request = new HttpGet(baiduMapUrl);
      try {
        HttpResponse response = (HttpResponse) httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        InputStream input = entity.getContent();
//        File file = new File("/sdcard/baiduMap.xml");
//        if (file.exists()) {
//          file.delete();
//        }
//        file.createNewFile();
//        OutputStream output = new FileOutputStream(file);
//        byte[] buffer = new byte[1024];
//        while ((input.read(buffer)) != -1) {
//          output.write(buffer);
//        }
//        output.flush();
//        output.close();

        OfflineMapCitiesParser parser = new OfflineMapCitiesParser();
        Set<OfflineCityItem> offlineCities =  parser.parse(input);
        for(OfflineCityItem offlineCity : offlineCities){
          Log.d(TAG, offlineCity.ArHighUrl);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }
  }
  
}
