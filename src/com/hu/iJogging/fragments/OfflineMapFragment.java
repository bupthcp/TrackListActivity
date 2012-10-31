package com.hu.iJogging.fragments;

import com.baidu.mapapi.MKOLSearchRecord;
import com.baidu.mapapi.MKOfflineMap;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.IJoggingActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class OfflineMapFragment extends Fragment{
  
  private IJoggingActivity mActivity;
  private MKOfflineMap mOffline = null;
  private View mFragmentView;
  private ListView listView;
  private ArrayList<MKOLSearchRecord> installedMapList;  
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
    mFragmentView = getActivity().getLayoutInflater().inflate(R.layout.offline_map_fragment, container, false);
    EditText edittext = (EditText)mFragmentView.findViewById(R.id.search_bar_hot_city);
    edittext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_bar_icon_normal, 0, 0, 0);
    listView = (ListView) mFragmentView.findViewById(R.id.list_installed);
    installedMapList = mOffline.getOfflineCityList();
    View catalogView = View.inflate(mActivity,R.layout.offline_list_catalog, null);
    TextView catalogText =(TextView) catalogView.findViewById(R.id.list_item_catalog);
    catalogText.setText(R.string.strInstalledMap);
    listView.addHeaderView(catalogView);
    OfflineMapAdapter installedAdapter = new OfflineMapAdapter(installedMapList);
    listView.setAdapter((ListAdapter)installedAdapter);
    return mFragmentView;
  }
  
  class OfflineMapAdapter extends BaseAdapter{
    
    private ArrayList<MKOLSearchRecord> mapInfoList;
    
    public OfflineMapAdapter(ArrayList<MKOLSearchRecord> infoList){
      mapInfoList = infoList;
    }

    @Override
    public int getCount() {
      // TODO Auto-generated method stub
      return mapInfoList.size();
    }

    @Override
    public Object getItem(int position) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public long getItemId(int position) {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder viewholder;
      if (convertView == null) {
        convertView = View.inflate(mActivity, R.layout.offline_map_list_item, null);
        viewholder = new ViewHolder();
        viewholder.list_item_name = (TextView) convertView.findViewById(R.id.list_item_name);
        viewholder.list_item_category = (TextView) convertView
            .findViewById(R.id.list_item_category);
        viewholder.list_item_total_size = (TextView) convertView
            .findViewById(R.id.list_item_total_size);
        viewholder.list_item_update_time = (TextView) convertView
            .findViewById(R.id.list_item_update_time);
        convertView.setTag(viewholder);
      } else {
        viewholder = (ViewHolder) convertView.getTag();
      }
      viewholder.list_item_name.setText(((MKOLSearchRecord)mapInfoList.get(position)).cityName);
      viewholder.list_item_total_size.setText(Integer.toString(((MKOLSearchRecord)mapInfoList.get(position)).size));
      return convertView;
    }
    
  }
  
  private class ViewHolder
  {
      TextView list_item_name;
      TextView list_item_category;
      TextView list_item_total_size;
      TextView list_item_update_time;
      
      ViewHolder()
      {
      }
  }
  
}
