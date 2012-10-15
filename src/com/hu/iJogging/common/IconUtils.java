package com.hu.iJogging.common;

import com.google.android.maps.mytracks.R;

import android.content.Context;

import java.util.HashMap;

public class IconUtils {

  private Context mCtx;
  private static IconUtils mInstatnce;
  
  private IconUtils(Context context) {
    mCtx = context;
    map.put(mCtx.getString(R.string.strRunning), R.drawable.lvt_sport0);
    map.put(mCtx.getString(R.string.strCyclingTransport), R.drawable.lvt_sport1);
    map.put(mCtx.getString(R.string.strCyclingSport), R.drawable.lvt_sport2);
    map.put(mCtx.getString(R.string.strMountainBiking), R.drawable.lvt_sport3);
    map.put(mCtx.getString(R.string.strFitnessWalking), R.drawable.lvt_sport16);
    map.put(mCtx.getString(R.string.strOrienteering), R.drawable.lvt_sport17);
    map.put(mCtx.getString(R.string.strWalking), R.drawable.lvt_sport18);
  }
  
  public static IconUtils getInstance(Context context){
    if(null == mInstatnce){
      mInstatnce = new IconUtils(context);
    }
    return mInstatnce;
  }

  private HashMap<String, Integer> map = new HashMap<String, Integer>();


  /**
   * Gets the icon drawable.
   * 
   * @param iconValue the icon value
   */
  //如果没有开始记录，是条空的记录的话，则设置为WALK
  public int getIconDrawable(String iconValue) {
    if (iconValue == null || iconValue.equals("")) {
      return R.drawable.lvt_sport18;
    }
    Integer drawable = map.get(iconValue);
    return drawable == null ? R.drawable.lvt_sport18 : drawable;
  }
  
}
