package com.hu.iJogging.fragments;

import com.baidu.mapapi.MKOLSearchRecord;
import com.baidu.mapapi.MKOLUpdateElement;
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
  private ArrayList<MKOLUpdateElement> mInstalledMapList;  
  private ArrayList<MKOLSearchRecord> mSearchedMapList;
  public final static  int TYPE_INSTALLED = 1;
  public final static  int TYPE_SEARCHED= 2;
  private int mType;
  private DownloadOfflineMapServiceBinder downloadOfflineMapServiceBinder;
  
  
  public OfflineMapAdapter(Context ctx,ArrayList<MKOLUpdateElement> installedMapList,ArrayList<MKOLSearchRecord> searchedMapList,int type){
    mCtx = ctx;
    mType = type;
    if(type == TYPE_INSTALLED){
      mInstalledMapList = installedMapList;
    }else if(type == TYPE_SEARCHED){
      mSearchedMapList = searchedMapList;
      downloadOfflineMapServiceBinder = ((HotOfflineMapActivity)mCtx).getDownloadOfflineService();
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
      viewholder.button_download = convertView.findViewById(R.id.button_download);
      viewholder.download = (TextView)convertView.findViewById(R.id.download);
      viewholder.download_percentage = (TextView)convertView.findViewById(R.id.download_percentage);
      convertView.setTag(viewholder);
    } else {
      viewholder = (ViewHolder) convertView.getTag();
    }
    if(mType == TYPE_INSTALLED){
      MKOLUpdateElement updateElement = (MKOLUpdateElement)mInstalledMapList.get(position);
      viewholder.list_item_name.setText(((MKOLUpdateElement)mInstalledMapList.get(position)).cityName);
      viewholder.list_item_total_size.setText(Integer.toString(((MKOLUpdateElement)mInstalledMapList.get(position)).size));
    }else if(mType == TYPE_SEARCHED){
      MKOLSearchRecord searchRecord = (MKOLSearchRecord)mSearchedMapList.get(position);
      viewholder.list_item_name.setText(searchRecord.cityName);
      viewholder.list_item_total_size.setText(Integer.toString(searchRecord.size));
      viewholder.button_download.setClickable(true);
      viewholder.button_download.setTag(searchRecord);
      viewholder.button_download.setOnClickListener((HotOfflineMapActivity)mCtx);
      if(downloadOfflineMapServiceBinder != null){
        MKOLUpdateElement updateInfo = downloadOfflineMapServiceBinder.getOfflineUpdateInfo(searchRecord.cityID);
        if(updateInfo != null){
          if(updateInfo.status == MKOLUpdateElement.FINISHED){
            viewholder.download_percentage.setVisibility(View.GONE);
            viewholder.download.setText(R.string.strDownloadOfflineFinished);
            viewholder.button_download.setClickable(false);
            viewholder.button_download.setBackgroundResource(R.drawable.btn_style_one);
          }else{
            String percentage = mCtx.getString(R.string.strDownloadOfflinePercetage, updateInfo.ratio);
            viewholder.download_percentage.setText(percentage);
            viewholder.download.setText(R.string.strDownloadOfflineING);
          }
        }else{
          //updateInfo为null说明下载并没有开始
          String percentage = mCtx.getString(R.string.strDownloadOfflinePercetage, 0);
          viewholder.download_percentage.setText(percentage);
        }
      }
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
      View button_download;
      TextView download;
      TextView download_percentage;
      
      ViewHolder()
      {
      }
  }

}
