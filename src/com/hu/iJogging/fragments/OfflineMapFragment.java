package com.hu.iJogging.fragments;

import com.baidu.mapapi.MKOfflineMap;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.IJoggingActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class OfflineMapFragment extends Fragment{
  
  private IJoggingActivity mActivity;
  private MKOfflineMap mOffline = null;
  private View mFragmentView;
  private ListView listView;
  
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
    mFragmentView = getActivity().getLayoutInflater().inflate(R.layout.offline_map_fragment, container, false);
    listView = (ListView) mFragmentView.findViewById(R.id.track_list);
    return mFragmentView;
  }
  
}
