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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
    tvInstalledOfflineMapValue.setText(Integer.toString(installedMapList.size()));
    View Zone3 = mFragmentView.findViewById(R.id.zone3);
    Zone3.setClickable(true);
    Zone3.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v) {
        Intent startSearchedIntent = new Intent(getActivity(), InstalledOfflineMapActivity.class);
        mActivity.startActivity(startSearchedIntent);
      }
    });
    return mFragmentView;
  }
  
}
