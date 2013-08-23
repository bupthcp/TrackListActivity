package com.hu.walkingnotes;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.hu.iJogging.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class MyLocationMapOverlay extends MyLocationOverlay{
  
  private final Drawable[] arrows;
  private final int arrowWidth, arrowHeight;

  public MyLocationMapOverlay( Context context,MapView mapView) {
    super(mapView);
    final Resources resources = context.getResources();
    arrows = new Drawable[] {
        resources.getDrawable(R.drawable.arrow_0),
        resources.getDrawable(R.drawable.arrow_20),
        resources.getDrawable(R.drawable.arrow_40),
        resources.getDrawable(R.drawable.arrow_60),
        resources.getDrawable(R.drawable.arrow_80),
        resources.getDrawable(R.drawable.arrow_100),
        resources.getDrawable(R.drawable.arrow_120),
        resources.getDrawable(R.drawable.arrow_140),
        resources.getDrawable(R.drawable.arrow_160),
        resources.getDrawable(R.drawable.arrow_180),
        resources.getDrawable(R.drawable.arrow_200),
        resources.getDrawable(R.drawable.arrow_220),
        resources.getDrawable(R.drawable.arrow_240),
        resources.getDrawable(R.drawable.arrow_260),
        resources.getDrawable(R.drawable.arrow_280),
        resources.getDrawable(R.drawable.arrow_300),
        resources.getDrawable(R.drawable.arrow_320),
        resources.getDrawable(R.drawable.arrow_340)
    };
    arrowWidth = arrows[lastHeading].getIntrinsicWidth();
    arrowHeight = arrows[lastHeading].getIntrinsicHeight();
  }
  
  private int lastHeading = 0;
  
  /**
   * Sets the pointer heading in degrees (will be drawn on next invalidate).
   *
   * @return true if the visible heading changed (i.e. a redraw of pointer is
   *         potentially necessary)
   */
  public boolean setHeading(float heading) {
    int newhdg = Math.round(-heading / 360 * 18 + 180);
    while (newhdg < 0)
      newhdg += 18;
    while (newhdg > 17)
      newhdg -= 18;
    if (newhdg != lastHeading) {
      lastHeading = newhdg;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void setMarker(Drawable drawble) {
    super.setMarker(arrows[lastHeading]);
  }

}
