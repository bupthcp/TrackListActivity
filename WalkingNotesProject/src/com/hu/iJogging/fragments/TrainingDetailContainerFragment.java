package com.hu.iJogging.fragments;

import com.hu.iJogging.ContainerPagerAdapter;
import com.hu.iJogging.R;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TrainingDetailContainerFragment extends Fragment{
  
  private View mFragmentView;
  ViewPager mViewPager;
  Handler mHandler;
  ContainerPagerAdapter mContainerPagerAdapter;
  public static final String TRAINING_DETAIL_CONTAINER_FTAGMENT_TAG = "TrainingDetailContainerFragment";
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mHandler = new Handler();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mFragmentView = getActivity().getLayoutInflater().inflate(R.layout.training_detail_container, container, false);
    mViewPager = (ViewPager) mFragmentView.findViewById(R.id.pager);
    
    mContainerPagerAdapter = new ContainerPagerAdapter(getActivity(), getFragmentManager());
    return mFragmentView;
  }
  
  
  
  @Override
  public void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      mHandler.post(new Runnable() {
          @Override
          public void run() {
              mViewPager.setAdapter(mContainerPagerAdapter);     
          }
      });        
  }
}
