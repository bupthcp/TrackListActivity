package com.hu.iJogging;

import com.google.android.maps.mytracks.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class DashBoardView extends LinearLayout{
  public DashBoardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    int i = R.layout.dashboard_view;
    ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(i, this);
  }
}