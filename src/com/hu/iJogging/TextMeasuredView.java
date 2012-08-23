package com.hu.iJogging;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextMeasuredView extends TextView{
  private Rect bounds = new Rect();
  private int height;
  private int mColor = 0;
  boolean mColorSet = false;
  Context mContext;
  private TextPaint textPaint;
  private int textSizeDip = 18;
  private float textSizePixels;

  public TextMeasuredView(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
  }

  public TextMeasuredView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    parseAttrs(paramContext, paramAttributeSet);
    this.mContext = paramContext;
  }

  public TextMeasuredView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    parseAttrs(paramContext, paramAttributeSet);
    this.mContext = paramContext;
  }

  private void parseAttrs(Context paramContext, AttributeSet paramAttributeSet)
  {
    String str = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "textSize").replace("@", "");
    this.textSizePixels = (Utility.getDip(paramContext) * this.textSizeDip);
    try
    {
      int i = Integer.parseInt(str);
      this.textSizePixels = paramContext.getResources().getDimensionPixelSize(i);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      localNumberFormatException.printStackTrace();
    }
  }

  protected void onDraw(Canvas paramCanvas)
  {
    int i = this.bounds.top;
    if (this.bounds.height() < this.height)
      i = this.bounds.top - (this.height - this.bounds.height()) / 2;
    if (this.mColorSet)
      this.textPaint.setColor(this.mColor);
    paramCanvas.drawText((String)getText(), -this.bounds.left, -i, this.textPaint);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    this.textPaint = getPaint();
    this.textPaint.getTextBounds((String)getText(), 0, getText().length(), this.bounds);
    this.height = Math.max(2 + (int)(0.725D * this.textSizePixels), this.bounds.height());
    setMeasuredDimension(this.bounds.width(), this.height);
  }

  public void setTextColor(int paramInt)
  {
    this.mColor = paramInt;
    this.mColorSet = true;
    super.setTextColor(paramInt);
  }
}
