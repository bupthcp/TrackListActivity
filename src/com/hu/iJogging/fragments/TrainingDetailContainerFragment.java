package com.hu.iJogging.fragments;

import com.hu.iJogging.R;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TrainingDetailContainerFragment extends Fragment{
  
  private View mFragmentView;
  ViewPager mViewPager;
  Handler mHandler;
  SectionsPagerAdapter mSectionsPagerAdapter;
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
    
    mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
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
              mViewPager.setAdapter(mSectionsPagerAdapter);     
          }
      });        
  }

  public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
      mFragmentManager = fm;
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

    private String makeFragmentName(int viewId, long id) {
      return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      if (mCurTransaction == null) {
        mCurTransaction = mFragmentManager.beginTransaction();
      }

      final long itemId = getItemId(position);

      // Do we already have this fragment?
      String name = makeFragmentName(container.getId(), itemId);
      Fragment fragment = mFragmentManager.findFragmentByTag(name);
      if (fragment != null) {
        mCurTransaction.attach(fragment);
      } else {
        fragment = getItem(position);
        mCurTransaction.add(container.getId(), fragment,
            makeFragmentName(container.getId(), itemId));
        mCurTransaction.addToBackStack(null);
      }
      if (fragment != mCurrentPrimaryItem) {
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
      }

      return fragment;
//      return super.instantiateItem(container, position);
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment)object);
    }
    
    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }
    
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
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

}
