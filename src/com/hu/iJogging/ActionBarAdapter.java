package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.google.android.maps.mytracks.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ActionBarAdapter implements SpinnerAdapter {
  private static SpinnerConfig sInitialSpinnerConfig;
  private static SpinnerConfig sEndomondoActivitySpinnerConfig;
  private static SpinnerItem sEndomondoSpinnerItem;
  private static SpinnerConfig sFriendsActivitySpinnerConfig;
  private static SpinnerItem sFriendsSpinnerItem;
  private static SpinnerItem sHiddenSpinnerItem = new SpinnerItem(R.string.strEmptyString, 0);
  private static SpinnerConfig sHistoryActivitySpinnerConfig;
  private static SpinnerItem sHistorySpinnerItem;
  private static SpinnerConfig sNewsFeedActivitySpinnerConfig;
  private static SpinnerItem sNewsFeedSpinnerItem;
  private static SpinnerConfig sRoutesActivitySpinnerConfig;
  private static SpinnerItem sRoutesSpinnerItem;
  private static SpinnerConfig sSettingsActivitySpinnerConfig;
  private static SpinnerItem sSettingsSpinnerItem;

  private static final int mSpinnerLength = 7;
  //mCurrentSpinner 用于记录spinner的下拉菜单中哪个item被选中，以便
  //在下次下拉菜单出现时将此菜单反色
  private int mCurrentSpinner = 1;
  private Context mContext = null;
  private LayoutInflater mInflater = null;
  private Typeface mRobotoRegular;
  private SpinnerConfig mSpinnerConfig;

  static {
    sEndomondoSpinnerItem = new SpinnerItem(R.string.strWorkoutTab, R.drawable.ab_icon_home);
    sNewsFeedSpinnerItem = new SpinnerItem(R.string.strNewsFeed, R.drawable.ab_icon_newsfeed);
    sHistorySpinnerItem = new SpinnerItem(R.string.strHistoryTab, R.drawable.ab_icon_history);
    sRoutesSpinnerItem = new SpinnerItem(R.string.strRoutes, R.drawable.ab_icon_routes);
    sFriendsSpinnerItem = new SpinnerItem(R.string.strFriends, R.drawable.ab_icon_friends);
    sSettingsSpinnerItem = new SpinnerItem(R.string.strSettingsTab, R.drawable.ab_icon_settings);

    SpinnerItem[] arrayOfSpinnerItem = new SpinnerItem[mSpinnerLength];
    arrayOfSpinnerItem[0] = sHiddenSpinnerItem;  //这个item是用不到的，只是为了使数组下标与position一致而做的填充
    arrayOfSpinnerItem[1] = sEndomondoSpinnerItem;
    arrayOfSpinnerItem[2] = sNewsFeedSpinnerItem;
    arrayOfSpinnerItem[3] = sHistorySpinnerItem;
    arrayOfSpinnerItem[4] = sRoutesSpinnerItem;
    arrayOfSpinnerItem[5] = sFriendsSpinnerItem;
    arrayOfSpinnerItem[6] = sSettingsSpinnerItem;
    sInitialSpinnerConfig = new SpinnerConfig(View.VISIBLE, R.string.strWorkoutTab, View.VISIBLE,
        R.drawable.ab_icon_home_white, arrayOfSpinnerItem);

    sEndomondoActivitySpinnerConfig = new SpinnerConfig(View.VISIBLE, R.string.strWorkoutTab,
        View.VISIBLE, R.drawable.ab_icon_home_white, arrayOfSpinnerItem);

    sHistoryActivitySpinnerConfig = new SpinnerConfig(View.VISIBLE, R.string.strHistoryTab,
        View.VISIBLE, R.drawable.ab_icon_history_white, arrayOfSpinnerItem);

    sNewsFeedActivitySpinnerConfig = new SpinnerConfig(View.VISIBLE, R.string.strNewsFeed,
        View.VISIBLE, R.drawable.ab_icon_newsfeed_white, arrayOfSpinnerItem);

    sRoutesActivitySpinnerConfig = new SpinnerConfig(View.VISIBLE, R.string.strRoutes,
        View.VISIBLE, R.drawable.ab_icon_routes_white, arrayOfSpinnerItem);

    sFriendsActivitySpinnerConfig = new SpinnerConfig(View.VISIBLE, R.string.strFriends,
        View.VISIBLE, R.drawable.ab_icon_friends_white, arrayOfSpinnerItem);

    sSettingsActivitySpinnerConfig = new SpinnerConfig(View.VISIBLE, R.string.strSettingsTab,
        View.VISIBLE, R.drawable.ab_icon_settings_white, arrayOfSpinnerItem);
  }

  public ActionBarAdapter(Context paramContext) {
    this.mContext = paramContext;
    this.mInflater = ((LayoutInflater) paramContext.getSystemService("layout_inflater"));
    this.mRobotoRegular = Typeface.createFromAsset(this.mContext.getAssets(),
        "fonts/Roboto-Regular.ttf");
    this.mSpinnerConfig = getSpinnerConfig(0);
  }

  private SpinnerConfig getSpinnerConfig(int paramInt) {
    switch (paramInt) {
      case 0:
        return sInitialSpinnerConfig;
      case 1:
        return sEndomondoActivitySpinnerConfig;
      case 2:
        return sNewsFeedActivitySpinnerConfig;
      case 3:
        return sHistoryActivitySpinnerConfig;
      case 4:
        return sRoutesActivitySpinnerConfig;
      case 5:
        return sFriendsActivitySpinnerConfig;
      case 6:
        return sSettingsActivitySpinnerConfig;
      default:
        return null;
    }
  }

  @Override
  public int getCount() {
    return mSpinnerLength;
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getItemViewType(int position) {
    // TODO Auto-generated method stub
    return 0;
  }

  // Spinner的绘制其实与绘制list是基本类似的：
  //1. Spinner的标题处可以看做List的第0个元素，Spinner的dropdown出来的菜单可以看做
  //List的第1个，第2个....元素。
  //2. Spinner的绘制包括两个过程。首先会计算dropdown出来的菜单的每个item的宽度高度，
  //这个工作由getDropDownView完成。因此在getDropDownView中对于position==0的条目是不处理
  //的，在当前的代码中，getDropDownView会运行14次，测量高度7次，测量宽度7次。
  //而之后需要计算Spinner的标题的宽度，这个过程可以通过打断点的方式跟踪，发现getView要
  //运行8次，第一次getView是得到当前选中的Item的View，后面的7次则是按照顺序将7个item都
  //走一遍
  //3.从这个过程可以看出，Spinner的绘制其实是两块，一个是由getDropDownView绘制的内层，
  //而getView绘制由标题宽度决定的外层，然后两层会叠放在一起。getView返回的view只有被选中的
  //那个position的item才会生效，在标题处显示出来。
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View localView;
    if (convertView == null) {
      localView = this.mInflater.inflate(R.layout.actionbar_item_view, null);
    } else {
      localView = convertView;
    }
    SpinnerConfig spinnerConfigTmp;
    spinnerConfigTmp = getSpinnerConfig(position);

    TextView localTextView1 = (TextView) localView.findViewById(R.id.ActionBarItemText1);
    localTextView1.setTypeface(this.mRobotoRegular);
    localTextView1.setVisibility(spinnerConfigTmp.mTextViewVisibility);
    if (spinnerConfigTmp.mTextViewVisibility == View.VISIBLE)
      localTextView1.setText(spinnerConfigTmp.mTextViewTextId);
    ImageView localImageView = (ImageView) localView.findViewById(R.id.ActionBarItemImage1);
    localImageView.setVisibility(spinnerConfigTmp.mImageViewVisibility);
    if (spinnerConfigTmp.mImageViewVisibility == View.VISIBLE)
      localImageView.setImageResource(spinnerConfigTmp.mImageViewImageId);
    TextView localTextView2 = (TextView) localView.findViewById(R.id.ActionBarItemImage1Text);
    localTextView2.setVisibility(View.GONE);
    if (spinnerConfigTmp.mImageViewImageId == R.drawable.ab_icon_history_white) {
      localTextView2.setVisibility(View.VISIBLE);
      int i = new GregorianCalendar().get(Calendar.DATE);
      localTextView2.setText(Integer.toString(i));
    }
    return localView;
  }

  @Override
  public int getViewTypeCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean hasStableIds() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isEmpty() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void registerDataSetObserver(DataSetObserver observer) {
    // TODO Auto-generated method stub

  }

  @Override
  public void unregisterDataSetObserver(DataSetObserver observer) {
    // TODO Auto-generated method stub

  }

  // 这里是创建下拉菜单的布局
  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    View localView;
    if (convertView == null) {
      localView = this.mInflater.inflate(R.layout.actionbar_dropdown_item_view, null);
    } else {
      localView = convertView;
    }

    TextView localTextView1;
    TextView localTextView2;
    ImageView localImageView;
    localTextView1 = (TextView) localView.findViewById(R.id.ActionBarItemImageText);
    localTextView2 = (TextView) localView.findViewById(R.id.ActionBarItemText);
    localTextView2.setTypeface(this.mRobotoRegular);
    localTextView2.setTextColor(this.mContext.getResources().getColor(R.color.black));
    localImageView = (ImageView) localView.findViewById(R.id.ActionBarItemImage);
    FrameLayout localFrameLayout = (FrameLayout) localView
        .findViewById(R.id.ActionBarItemImageLayout);
    if (position == 0) {
      localFrameLayout.setVisibility(View.GONE);
      localTextView1.setVisibility(View.GONE);
      localTextView2.setVisibility(View.GONE);
      localImageView.setVisibility(View.GONE);
    } else {
      localFrameLayout.setVisibility(View.VISIBLE);
      localTextView1.setVisibility(View.GONE);
      localTextView2.setVisibility(View.VISIBLE);
      localImageView.setVisibility(View.VISIBLE);
      localTextView2.setText(this.mSpinnerConfig.mSpinnerItemArray[position].mStringId);
      if (position == mCurrentSpinner) {
        localTextView2.setTextColor(this.mContext.getResources().getColor(R.color.VeryLightGrey));
      }
      localImageView = (ImageView) localView.findViewById(R.id.ActionBarItemImage);
      localImageView.setImageResource(this.mSpinnerConfig.mSpinnerItemArray[position].mIconId);
      if (this.mSpinnerConfig.mSpinnerItemArray[position].mIconId == R.drawable.ab_icon_history) {
        localTextView1.setVisibility(View.VISIBLE);
        int n = new GregorianCalendar().get(Calendar.DATE);
        localTextView1.setText(Integer.toString(n));
      }
    }
    return localView;
  }

  //SpinnerConfig保存了标题和dropdown菜单的布局资源信息
  //paramArrayOfSpinnerItem这个数组保存的是dropdown菜单的布局资源信息
  //剩余的4个成员变量是标题的布局资源信息
  private static class SpinnerConfig {
    final int mImageViewImageId;
    final int mImageViewVisibility;
    final ActionBarAdapter.SpinnerItem[] mSpinnerItemArray;
    final int mTextViewTextId;
    final int mTextViewVisibility;

    SpinnerConfig(int paramInt1, int paramInt2, int paramInt3, int paramInt4,
        ActionBarAdapter.SpinnerItem[] paramArrayOfSpinnerItem) {
      this.mTextViewVisibility = paramInt1;
      this.mTextViewTextId = paramInt2;
      this.mImageViewVisibility = paramInt3;
      this.mImageViewImageId = paramInt4;
      this.mSpinnerItemArray = paramArrayOfSpinnerItem;
    }
  }

  private static class SpinnerItem {
    final int mIconId;
    final int mStringId;

    SpinnerItem(int paramInt1, int paramInt2) {
      this.mStringId = paramInt1;
      this.mIconId = paramInt2;
    }
  }
  
  private void switchFragment(int position){
    switch (position) {
      case 1:
        removeFragment();
        ((IJoggingActivity)mContext).switchToTrainingDetailContainer();
        break;
      case 2:
        break;
      case 3:
        removeFragment();
        ((IJoggingActivity)mContext).switchToTrackListFragment();
        break;
      case 4:
        break;
      case 5:
        break;
      case 6:
        break;
      default:
        break;
    }
  }
  
  //当通过ActionBar进行fragment的切换时，就没有办法在fragment内部进行前一个fragment的删除工作了
  //只能在点击ActionBar完成之后，根据当前所使用的fragment container的res id进行fragment的删除
  private void removeFragment(){
    FragmentManager fragmentManager = ((IJoggingActivity)mContext).getSupportFragmentManager();
    FragmentTransaction ft = fragmentManager.beginTransaction();
    Fragment fragment;
    switch(mCurrentSpinner){
      case 1:
        //当页面处于新训练时，界面是由viewpager进行管理的。经过试验，发现viewpager会自己管理fragment的
        //管理，并不需要我们进行手动的remove操作
        break;
      case 2:
        break;
      case 3:
        fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        ft.remove(fragment);
        break;
      case 4:
        break;
      case 5:
        break;
      case 6:
        break;
      default:
        break;
    }
    ft.commit();
  }

  
  // 这个回调函数在布局完成之后才会调用，即getView以及getDropDownView都完成后，才调用
  // mActionBar.setSelectedNavigationItem 这个方法会出发重新布局，所以也会出发这个回调函数
  public class OnNaviListener implements ActionBar.OnNavigationListener {

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
      if (itemPosition == 0) return false;
      switchFragment(itemPosition);
      mCurrentSpinner = itemPosition;
      return true;
    }
  }
  
  public int getCurrentSpinner(){
    return mCurrentSpinner;
  }

}
