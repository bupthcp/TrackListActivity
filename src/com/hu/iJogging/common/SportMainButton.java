package com.hu.iJogging.common;

import com.google.android.maps.mytracks.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SportMainButton extends LinearLayout{
  private static final int layout = R.layout.sport_main_button;
  private ImageView iTriangle;
  private ImageView iSport;
  private Context mContext;
  private TextMeasuredView tvSport;

  public SportMainButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    inflateAndConfigure(paramContext);
  }

  private void inflateAndConfigure(Context paramContext)
  {
    ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(layout, this);
    this.tvSport = (TextMeasuredView)findViewById(R.id.tvWoSport);
    this.iTriangle = (ImageView)findViewById(R.id.ImageDbMoreTriangle);
    this.iTriangle.setImageResource(UIConfig.DashboardConfig.triangleRestId);
    this.iSport = (ImageView)findViewById(R.id.ImageButtonSport);
    iSport.setImageResource(R.drawable.lvt_sport0);
    tvSport.setText(R.string.strRunning);
    setClickable(true);
    setColors();
  }
  
  public void setSport(String sport){
    if(sport != null){
      tvSport.setText(sport);
    }else{
      sport = mContext.getString(R.string.strRunning);
      tvSport.setText(sport);
    }
    int iconId = IconUtils.getInstance(mContext).getIconDrawable(sport);
    if(iconId>0){
      iSport.setImageResource(iconId);
    }else{
      iSport.setImageResource(R.drawable.lvt_sport0);
    }
  }

  private void setColors()
  {
    setColorsPressed(isPressed());
  }

  private void setColorsPressed(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.tvSport.setTextColor(this.mContext.getResources().getColor(UIConfig.DashboardConfig.sportTextFontColorPressed));
      this.iTriangle.setImageResource(UIConfig.DashboardConfig.trianglePressedId);
    }else{
      invalidate();
      this.tvSport.setTextColor(this.mContext.getResources().getColor(UIConfig.DashboardConfig.sportTextFontColorRest));
      this.iTriangle.setImageResource(UIConfig.DashboardConfig.triangleRestId);
    }

  }
}
