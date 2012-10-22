package com.hu.iJogging.fragments;

import com.google.android.maps.mytracks.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TrainingDetailContainerFragment extends Fragment{
  
  private View mFragmentView;
  ViewPager mViewPager;
  SectionsPagerAdapter mSectionsPagerAdapter;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mFragmentView = getActivity().getLayoutInflater().inflate(R.layout.training_detail_container, container, false);
    mViewPager = (ViewPager) mFragmentView.findViewById(R.id.pager);
    
    mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
    mViewPager.setAdapter(mSectionsPagerAdapter);
    return mFragmentView;
  }
  
  
  
  @Override
  public void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
  }



  public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
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

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "test";
            case 1: return "test";
        }
        return null;
    }
}

}
