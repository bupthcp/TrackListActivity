package com.hu.walkingnotes.support.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;

import org.holoeverywhere.widget.ListView;

/**
 * User: qii
 * Date: 13-3-31
 * https://github.com/android/platform_packages_apps_contacts/blob/master/src/com/android/contacts/widget/AutoScrollListView.java
 * <p/>
 * ref http://cyrilmottier.com/2013/01/09/back-to-top-android-vs-ios/
 */
public class AutoScrollListView extends ListView {

    /**
     * Position the element at about 1/3 of the list height
     */
    private static final float PREFERRED_SELECTION_OFFSET_FROM_TOP = 0.33f;

    private int mRequestedScrollPosition = -1;
    private boolean mSmoothScrollRequested;
    
    private static final int SCROLL_CHANGE_DELAY = 40;
    
    private ScrollStateRunnable mScrollStateChangedRunnable = new ScrollStateRunnable();
    private int mCurrentScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    private int mPreviousScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    private int mListScrollTopOffset = 2;
    private boolean mIsScrollingUp = false;
    private float mFriction = .05f;
    private float mVelocityScale = 0.333f;
    private static final int UNSCALED_LIST_SCROLL_TOP_OFFSET = 2;
    private static final int ADJUSTMENT_SCROLL_DURATION = 500;

    public AutoScrollListView(Context context) {
        super(context);
    }

    public AutoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mListScrollTopOffset = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            UNSCALED_LIST_SCROLL_TOP_OFFSET, displayMetrics);
    }

    /**
     * Brings the specified position to view by optionally performing a jump-scroll maneuver:
     * first it jumps to some position near the one requested and then does a smooth
     * scroll to the requested position.  This creates an impression of full smooth
     * scrolling without actually traversing the entire list.  If smooth scrolling is
     * not requested, instantly positions the requested item at a preferred offset.
     */
    public void requestPositionToScreen(int position, boolean smoothScroll) {
        mRequestedScrollPosition = position;
        mSmoothScrollRequested = smoothScroll;
        requestLayout();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (mRequestedScrollPosition == -1) {
            return;
        }

        final int position = mRequestedScrollPosition;
        mRequestedScrollPosition = -1;

        int firstPosition = getFirstVisiblePosition() + 1;
        int lastPosition = getLastVisiblePosition();
        if (position >= firstPosition && position <= lastPosition) {
            return; // Already on screen
        }

        final int offset = (int) (getHeight() * PREFERRED_SELECTION_OFFSET_FROM_TOP);
        if (!mSmoothScrollRequested) {
            setSelectionFromTop(position, offset);

            // Since we have changed the scrolling position, we need to redo child layout
            // Calling "requestLayout" in the middle of a layout pass has no effect,
            // so we call layoutChildren explicitly
            super.layoutChildren();

        } else {
            // We will first position the list a couple of screens before or after
            // the new selection and then scroll smoothly to it.
            int twoScreens = (lastPosition - firstPosition) * 2;
            int preliminaryPosition;
            if (position < firstPosition) {
                preliminaryPosition = position + twoScreens;
                if (preliminaryPosition >= getCount()) {
                    preliminaryPosition = getCount() - 1;
                }
                if (preliminaryPosition < firstPosition) {
                    setSelection(preliminaryPosition);
                    super.layoutChildren();
                }
            } else {
                preliminaryPosition = position - twoScreens;
                if (preliminaryPosition < 0) {
                    preliminaryPosition = 0;
                }
                if (preliminaryPosition > lastPosition) {
                    setSelection(preliminaryPosition);
                    super.layoutChildren();
                }
            }

            if (VERSION.SDK_INT >= 11) {
              smoothScrollToPositionFromTop(position, offset);
            } else {
              this.setSelectionFromTop(position, offset);
              onScrollStateChanged(this,
                      OnScrollListener.SCROLL_STATE_IDLE);
            }
        }
    }
    
    private void onScrollStateChanged(AbsListView view, int scrollState) {
      mScrollStateChangedRunnable.doScrollStateChange(view, scrollState);
    }
    
    private class ScrollStateRunnable implements Runnable {
      private int mNewState;
      private AbsListView mView;

      public void doScrollStateChange(AbsListView view, int scrollState) {
          mView = view;
          mNewState = scrollState;
          removeCallbacks(this);
          postDelayed(this, SCROLL_CHANGE_DELAY);
      }

      @Override
      @SuppressLint("NewApi")
      public void run() {
          mCurrentScrollState = mNewState;
          if (mNewState == OnScrollListener.SCROLL_STATE_IDLE
                  && mPreviousScrollState != OnScrollListener.SCROLL_STATE_IDLE) {
              View child = mView.getChildAt(0);
              if (child == null) {
                  return;
              }
              int dist = child.getBottom() - mListScrollTopOffset;
              if (dist > mListScrollTopOffset) {
                  int y = dist - (mIsScrollingUp ? child.getHeight() : 0);
                  if (VERSION.SDK_INT >= 11) {
                      mView.smoothScrollBy(y,
                              ADJUSTMENT_SCROLL_DURATION);
                  } else {
                      mView.scrollBy(0, y);
                  }
              }
          }
          mPreviousScrollState = mNewState;
      }
    }
    
    @SuppressLint("NewApi")
    private void setUpListView() {
        setDivider(null);
        setItemsCanFocus(true);
        setVerticalScrollBarEnabled(false);
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                onScroll(view, firstVisibleItem,
                        visibleItemCount, totalItemCount);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
               onScrollStateChanged(view, scrollState);
            }
        });
        if (VERSION.SDK_INT >= 11) {
            setFriction(mFriction);
            setVelocityScale(mVelocityScale);
        }
    }
}