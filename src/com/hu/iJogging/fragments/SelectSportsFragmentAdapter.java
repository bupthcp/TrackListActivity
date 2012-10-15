package com.hu.iJogging.fragments;

import com.google.android.maps.mytracks.R;
import com.hu.iJogging.common.IconUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectSportsFragmentAdapter<T> extends ArrayAdapter<T>{

  private Context mCtx;
  private LayoutInflater mInflater;
  
  public SelectSportsFragmentAdapter(Context context, int textViewResourceId) {
    super(context, textViewResourceId);
    mCtx = context;
    mInflater = ((LayoutInflater) mCtx.getSystemService("layout_inflater"));
  }
  
  public SelectSportsFragmentAdapter(Context context, int resource, T[] objects) {
    super(context,resource,objects);
    mCtx = context;
    mInflater = ((LayoutInflater) mCtx.getSystemService("layout_inflater"));
}
  
  public static SelectSportsFragmentAdapter<CharSequence> createFromResource(Context context,
          int textArrayResId, int textViewResId) {
      CharSequence[] strings = context.getResources().getTextArray(textArrayResId);
      return new SelectSportsFragmentAdapter<CharSequence>(context, textViewResId, strings);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    String iconString = (String)super.getItem(position);
    View itemView = mInflater.inflate(R.layout.select_sports_item, null);
    int iconResId = IconUtils.getInstance(mCtx).getIconDrawable(iconString);
    ImageView iv = (ImageView)itemView.findViewById(R.id.icon_select_sport);
    iv.setImageResource(iconResId);
    TextView tv=(TextView)itemView.findViewById(R.id.sport_description);
    tv.setText(iconString);
    return itemView;
  }
  
  

}
