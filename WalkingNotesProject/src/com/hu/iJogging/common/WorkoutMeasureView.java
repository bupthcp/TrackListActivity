package com.hu.iJogging.common;

import com.hu.iJogging.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class WorkoutMeasureView extends LinearLayout{
  public WorkoutMeasureView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(R.layout.workout_measure_view, this);
  }

}