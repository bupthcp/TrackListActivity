package com.hu.iJogging;

import com.google.android.maps.mytracks.R;
import com.hu.iJogging.common.UIConfig;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MotivationMainButton extends LinearLayout{

  private static final int layout = R.layout.motivation_main_button;
  private TextView iInfo;
  private ImageView iTriangle;
  private TextView iType;
  private Context mContext;
  private Handler mSettingsChangedHandler;

  public MotivationMainButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    inflateAndConfigure(paramContext);
    setTexts();
  }
  private void inflateAndConfigure(Context paramContext)
  {
    ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(layout, this);
    this.iType = ((TextView)findViewById(R.id.mmbType));
    this.iInfo = ((TextView)findViewById(R.id.mmbInfo));
    this.iTriangle = ((ImageView)findViewById(R.id.ImageDbMoreTriangle));
    this.iTriangle.setImageResource(UIConfig.DashboardConfig.triangleRestId);
    setClickable(true);
    setColors();
  }

  private void setColors()
  {
    setColorsPressed(isPressed());
  }

  private void setColorsPressed(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.iType.setTextColor(this.mContext.getResources().getColor(UIConfig.DashboardConfig.motivationTextTypeFontColorPressed));
      this.iInfo.setTextColor(this.mContext.getResources().getColor(UIConfig.DashboardConfig.motivationTextInfoFontColorPressed));
      this.iTriangle.setImageResource(UIConfig.DashboardConfig.trianglePressedId);
    }else{
      invalidate();
      this.iType.setTextColor(this.mContext.getResources().getColor(UIConfig.DashboardConfig.motivationTextTypeFontColorRest));
      this.iInfo.setTextColor(this.mContext.getResources().getColor(UIConfig.DashboardConfig.motivationTextInfoFontColorRest));
      this.iTriangle.setImageResource(UIConfig.DashboardConfig.triangleRestId);
    }

  }

  private void setTexts()
  {
      this.iInfo.setVisibility(View.GONE);
      this.iType.setMaxLines(2);
      this.iType.setEllipsize(TextUtils.TruncateAt.MARQUEE);
  }


  public void updateView()
  {
    setTexts();
  }
}
