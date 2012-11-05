package com.hu.iJogging.fragments;

import com.baidu.mapapi.MKOLSearchRecord;
import com.baidu.mapapi.MKOLUpdateElement;
import com.google.android.maps.mytracks.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OfflineMapAdapter extends BaseAdapter{
  
  Context mCtx;
  private ArrayList<MKOLUpdateElement> mInstalledMapList;  
  private ArrayList<MKOLSearchRecord> mSearchedMapList;
  public final static  int TYPE_INSTALLED = 1;
  public final static  int TYPE_SEARCHED= 2;
  private int mType;
  
  
  public OfflineMapAdapter(Context ctx,ArrayList<MKOLUpdateElement> installedMapList,ArrayList<MKOLSearchRecord> searchedMapList,int type){
    mCtx = ctx;
    mType = type;
    if(type == TYPE_INSTALLED){
      mInstalledMapList = installedMapList;
    }else if(type == TYPE_SEARCHED){
      mSearchedMapList = searchedMapList;
    }else{
      mInstalledMapList = installedMapList;
    }
  }

  @Override
  public int getCount() {
    int count;
    if((mType == TYPE_INSTALLED)&&(mInstalledMapList != null)){
      count = mInstalledMapList.size();
    }else if((mType == TYPE_SEARCHED)&&(mSearchedMapList!=null)){
      count = mSearchedMapList.size();
    }else{
      count = 0;
    }
    return count;
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
      convertView = View.inflate(mCtx, R.layout.offline_map_list_item, null);
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
    if(mType == TYPE_INSTALLED){
      viewholder.list_item_name.setText(((MKOLUpdateElement)mInstalledMapList.get(position)).cityName);
      viewholder.list_item_total_size.setText(Integer.toString(((MKOLUpdateElement)mInstalledMapList.get(position)).size));
    }else if(mType == TYPE_SEARCHED){
      viewholder.list_item_name.setText(((MKOLSearchRecord)mSearchedMapList.get(position)).cityName);
      viewholder.list_item_total_size.setText(Integer.toString(((MKOLSearchRecord)mSearchedMapList.get(position)).size));
    }else{
      viewholder.list_item_name.setText(((MKOLUpdateElement)mInstalledMapList.get(position)).cityName);
      viewholder.list_item_total_size.setText(Integer.toString(((MKOLUpdateElement)mInstalledMapList.get(position)).size));
    }

    return convertView;
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
