package com.hu.iJogging;

import com.hu.iJogging.fragments.ChartFragment;
import com.hu.iJogging.fragments.TrainingDetailFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ContainerPagerAdapter extends FragmentPagerAdapter {

  /**
   * 
   */
  private final Context mCtx;

  public ContainerPagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    mCtx = context;
  }

  @Override
  public Fragment getItem(int i) {
    switch (i) {
      case 0:
        return new TrainingDetailFragment();
      case 1:
        return new ChartFragment();
    }
    return null;
  }

  @Override
  public int getCount() {
    return 2;
  }

  public int getItemPosition(Object object) {
    return POSITION_NONE;
  }

  public long getItemId(int position) {
    return position;
  }
}