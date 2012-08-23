package com.hu.iJogging;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class DashBoardStretchSpace extends LinearLayout{
  public static final int NOT_INTERVALS = 27;
  private float border;
  private int mColor;
  private Context mContext;
  private int mIntensity;
  private int mMinH;
  private Paint mPaint;
  private Path mPath;
//  private IntervalsPSCtrl mPointerSausage;
  private float mPtrHeight;
  private float triangle;

  public DashBoardStretchSpace(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.mPath = new Path();
    this.mPaint = new Paint();
    this.mPaint.setColor(-1);
//    this.mPtrHeight = this.mContext.getResources().getDimension(R.dimen.pointer_height);
//    this.border = this.mContext.getResources().getDimension(R.dimen.border);
//    this.triangle = this.mContext.getResources().getDimension(R.dimen.triangle);
  }
}
