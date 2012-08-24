package com.hu.iJogging;

import com.google.android.maps.mytracks.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class IntervalZone extends LinearLayout{
  private Context mContext;
  private int mIntvHash;
  private int mIpHash;
  private LinearLayout mLl;
  private LinearLayout mPtr;
  private LinearLayout mSsg;
//  private IntervalsPSCtrl mPointerSausage;
  
  public IntervalZone(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.mLl = ((LinearLayout)((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(R.layout.interval_zone, this));
    this.mPtr = ((LinearLayout)this.mLl.findViewById(R.id.pointer));
    this.mSsg = ((LinearLayout)this.mLl.findViewById(R.id.sausage));
//    this.mPointerSausage = new IntervalsPSCtrl(this.mContext, null, false, true);
  }
}
