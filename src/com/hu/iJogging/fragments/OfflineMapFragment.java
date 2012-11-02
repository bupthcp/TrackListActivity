package com.hu.iJogging.fragments;

import com.baidu.mapapi.MKOLSearchRecord;
import com.baidu.mapapi.MKOLUpdateElement;
import com.baidu.mapapi.MKOfflineMap;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.IJoggingActivity;
import com.hu.iJogging.SearchedOfflineMapActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class OfflineMapFragment extends Fragment{
  
  private static String TAG =  "OfflineMapFragment";
  
  private IJoggingActivity mActivity;
  private MKOfflineMap mOffline = null;
  private View mFragmentView;
  private ListView listView;
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
    mOffline = ((IJoggingActivity)mActivity).getOfflineMap();
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//    mFragmentView = getActivity().getLayoutInflater().inflate(R.layout.offline_map_fragment, container, false);
//    EditText edittext = (EditText)mFragmentView.findViewById(R.id.search_bar_hot_city);
//    edittext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_bar_icon_normal, 0, 0, 0);
//    listView = (ListView) mFragmentView.findViewById(R.id.list_installed);
//    installedMapList = mOffline.getAllUpdateInfo();
//    Bundle a = new Bundle();
//    a.putInt("act", 16011500);
//    if (Mj.sendBundle(a) != 0){
//      Set<String> keySet = a.keySet();
//      Iterator<String> ii = keySet.iterator();
//      while(ii.hasNext()){
//        String str = (String) ii.next();
//        Log.i(TAG, "iterator"+str);
//      }
//    }
//    View catalogView = View.inflate(mActivity,R.layout.offline_list_catalog, null);
//    TextView catalogText =(TextView) catalogView.findViewById(R.id.list_item_catalog);
//    catalogText.setText(R.string.strInstalledMap);
//    listView.addHeaderView(catalogView);
//    OfflineMapAdapter installedAdapter = new OfflineMapAdapter(installedMapList);
//    listView.setAdapter((ListAdapter)installedAdapter);
    mFragmentView = getActivity().getLayoutInflater().inflate(R.layout.offline_map_introduction, container, false);
    installedMapList = mOffline.getAllUpdateInfo();
    searchedMapList = mOffline.getOfflineCityList();
    TextView tvHotOfflineMapValue = (TextView)mFragmentView.findViewById(R.id.hot_offline_map_value);
    tvHotOfflineMapValue.setText(Integer.toString(searchedMapList.size()));
    tvHotOfflineMapValue.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v) {
        Intent startSearchedIntent = new Intent(getActivity(), SearchedOfflineMapActivity.class);
      }
    });
    TextView tvInstalledOfflineMapValue = (TextView)mFragmentView.findViewById(R.id.installed_offline_map_value);
    tvInstalledOfflineMapValue.setText(Integer.toString(installedMapList.size()));
    tvInstalledOfflineMapValue.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v) {
        
      }
    });
    return mFragmentView;
  }
  
}
