package com.hu.iJogging;

import com.google.android.maps.mytracks.R;

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
    this.mPtrHeight = this.mContext.getResources().getDimension(R.dimen.pointer_height);
    this.border = this.mContext.getResources().getDimension(R.dimen.border);
    this.triangle = this.mContext.getResources().getDimension(R.dimen.triangle);
  }
  
  private float roundToDip(int paramInt)
  {
    int i = paramInt;
    float f = Utility.getDip(this.mContext);
    while (i<Integer.MAX_VALUE)
    {
      if (i / f - (int)(i / f) == 0.0F)
        return i;
      i++;
    }
    return i;
  }
  
  
//  protected void onDraw(Canvas paramCanvas)
//  {
//    super.onDraw(paramCanvas);
//    if ((this.mMinH == 0) && (getHeight() > 0) && (this.mPtrHeight > 0.0F))
//      this.mMinH = (int)Math.min(roundToDip(getHeight()), this.mPtrHeight);
//    if (this.mIntensity != 27)
//    {
//      this.mPath.reset();
//      this.mPath.moveTo(0.0F, 0.0F);
//      this.mPath.lineTo(getWidth(), 0.0F);
//      this.mPath.lineTo(getWidth(), this.mMinH);
//      this.mPath.lineTo(0.0F, this.mMinH);
//      this.mPath.close();
//      paramCanvas.drawPath(this.mPath, this.mPaint);
//      float f1 = -3.0F * Utility.getDip(this.mContext);
//      this.mPath.reset();
//      float f2 = f1 + (this.mPointerSausage.getTouchedX() + this.border) - this.triangle;
//      float f3 = this.mPtrHeight;
//      this.mPath.moveTo(f2, f3);
//      float f4 = f1 + (this.mPointerSausage.getTouchedX() + this.border) + this.triangle;
//      float f5 = this.mPtrHeight;
//      this.mPath.lineTo(f4, f5);
//      float f6 = f1 + (this.mPointerSausage.getTouchedX() + this.border);
//      float f7 = this.mPtrHeight + this.triangle;
//      this.mPath.lineTo(f6, f7);
//      this.mPath.close();
//      paramCanvas.drawPath(this.mPath, this.mPaint);
//    }
//  }
}
