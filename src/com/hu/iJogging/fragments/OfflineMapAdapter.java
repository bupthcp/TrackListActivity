package com.hu.iJogging.fragments;


import com.google.android.maps.mytracks.R;
import com.hu.iJogging.HotOfflineMapActivity;
import com.hu.iJogging.Services.DownloadOfflineMapService.DownloadOfflineMapServiceBinder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OfflineMapAdapter extends BaseAdapter{
  
  Context mCtx;

  public final static  int TYPE_INSTALLED = 1;
  public final static  int TYPE_SEARCHED= 2;
  private int mType;
  private DownloadOfflineMapServiceBinder downloadOfflineMapServiceBinder;
  
  
  public OfflineMapAdapter(Context ctx,ArrayList installedMapList,ArrayList searchedMapList,int type){
    mCtx = ctx;
    mType = type;
    if(type == TYPE_INSTALLED){

    }else if(type == TYPE_SEARCHED){

      downloadOfflineMapServiceBinder = ((HotOfflineMapActivity)mCtx).getDownloadOfflineService();
    }else{

    }
  }

  @Override
  public int getCount() {
    int count=0;
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
      viewholder.button_download = convertView.findViewById(R.id.button_download);
      viewholder.download = (TextView)convertView.findViewById(R.id.download);
      viewholder.download_percentage = (TextView)convertView.findViewById(R.id.download_percentage);
      convertView.setTag(viewholder);
    } else {
      viewholder = (ViewHolder) convertView.getTag();
    }
    if(mType == TYPE_INSTALLED){

    }else if(mType == TYPE_SEARCHED){

    }else{
    }

    return convertView;
  }
  
  private class ViewHolder
  {
      TextView list_item_name;
      TextView list_item_category;
      TextView list_item_total_size;
      TextView list_item_update_time;
      View button_download;
      TextView download;
      TextView download_percentage;
      
      ViewHolder()
      {
      }
  }

}
