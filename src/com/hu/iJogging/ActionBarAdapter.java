package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.common.UIConfig;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ActionBarAdapter implements SpinnerAdapter{
  public static final int ENDOMONDO_ACTIVITY = 1;
  public static final int FRIENDS_ACTIVITY = 5;
  public static final int HIDDEN_POS = 0;
  public static final int HISTORY_ACTIVITY = 3;
  public static final int NEWSFEED_ACTIVITY = 2;
  public static final int ROUTES_ACTIVITY = 4;
  public static final int SETTINGS_ACTIVITY = 6;
  public static final int SETTINGS_AUDIO_ACTIVITY = 7;
  private static SpinnerConfig sEndomondoActivitySpinnerConfig;
  private static SpinnerItem sEndomondoSpinnerItem;
  private static SpinnerConfig sFriendsActivitySpinnerConfig;
  private static SpinnerItem sFriendsSpinnerItem;
  private static SpinnerItem sHiddenSpinnerItem = new SpinnerItem(R.string.strEmptyString, 0, null, false, -1);
  private static SpinnerConfig sHistoryActivitySpinnerConfig;
  private static SpinnerItem sHistorySpinnerItem;
  private static SpinnerConfig sNewsFeedActivitySpinnerConfig;
  private static SpinnerItem sNewsFeedSpinnerItem;
  private static SpinnerConfig sRoutesActivitySpinnerConfig;
  private static SpinnerItem sRoutesSpinnerItem;
  private static SpinnerConfig sSettingsActivitySpinnerConfig;
  private static SpinnerItem sSettingsSpinnerItem;
  private int mActivityInt;
  private Context mContext = null;
  private LayoutInflater mInflater = null;
  private Typeface mRobotoRegular;
  private SpinnerConfig mSpinnerConfig;
  
  
  static
  {
    sEndomondoSpinnerItem = new SpinnerItem(R.string.strWorkoutTab, R.drawable.ab_icon_home, null, false, -1);
    sNewsFeedSpinnerItem = new SpinnerItem(R.string.strNewsFeed, R.drawable.ab_icon_newsfeed, null, false, -1);
    sHistorySpinnerItem = new SpinnerItem(R.string.strHistoryTab, R.drawable.ab_icon_history, null, false, -1);
    sRoutesSpinnerItem = new SpinnerItem(R.string.strRoutes, R.drawable.ab_icon_routes, null, true, 27);
    sFriendsSpinnerItem = new SpinnerItem(R.string.strFriends, R.drawable.ab_icon_friends, null, false, -1);
    sSettingsSpinnerItem = new SpinnerItem(R.string.strSettingsTab, R.drawable.ab_icon_settings, null, false, -1);
    int i = UIConfig.TitleConfig.titleBannerImageId;
    SpinnerItem[] arrayOfSpinnerItem1 = new SpinnerItem[7];
    arrayOfSpinnerItem1[0] = sHiddenSpinnerItem;
    arrayOfSpinnerItem1[1] = sEndomondoSpinnerItem;
    arrayOfSpinnerItem1[2] = sNewsFeedSpinnerItem;
    arrayOfSpinnerItem1[3] = sHistorySpinnerItem;
    arrayOfSpinnerItem1[4] = sRoutesSpinnerItem;
    arrayOfSpinnerItem1[5] = sFriendsSpinnerItem;
    arrayOfSpinnerItem1[6] = sSettingsSpinnerItem;
    sEndomondoActivitySpinnerConfig = new SpinnerConfig(8, 0, 0, i, arrayOfSpinnerItem1);
    int j = R.string.strHistoryTab;
    int k = R.drawable.ab_icon_history_white;
    SpinnerItem[] arrayOfSpinnerItem2 = new SpinnerItem[7];
    arrayOfSpinnerItem2[0] = sHiddenSpinnerItem;
    arrayOfSpinnerItem2[1] = sEndomondoSpinnerItem;
    arrayOfSpinnerItem2[2] = sNewsFeedSpinnerItem;
    arrayOfSpinnerItem2[3] = sHistorySpinnerItem;
    arrayOfSpinnerItem2[4] = sRoutesSpinnerItem;
    arrayOfSpinnerItem2[5] = sFriendsSpinnerItem;
    arrayOfSpinnerItem2[6] = sSettingsSpinnerItem;
    sHistoryActivitySpinnerConfig = new SpinnerConfig(0, j, 0, k, arrayOfSpinnerItem2);
    int m = R.string.strNewsFeed;
    int n = R.drawable.ab_icon_newsfeed_white;
    SpinnerItem[] arrayOfSpinnerItem3 = new SpinnerItem[7];
    arrayOfSpinnerItem3[0] = sHiddenSpinnerItem;
    arrayOfSpinnerItem3[1] = sEndomondoSpinnerItem;
    arrayOfSpinnerItem3[2] = sNewsFeedSpinnerItem;
    arrayOfSpinnerItem3[3] = sHistorySpinnerItem;
    arrayOfSpinnerItem3[4] = sRoutesSpinnerItem;
    arrayOfSpinnerItem3[5] = sFriendsSpinnerItem;
    arrayOfSpinnerItem3[6] = sSettingsSpinnerItem;
    sNewsFeedActivitySpinnerConfig = new SpinnerConfig(0, m, 0, n, arrayOfSpinnerItem3);
    int i1 = R.string.strRoutes;
    int i2 = R.drawable.ab_icon_routes_white;
    SpinnerItem[] arrayOfSpinnerItem4 = new SpinnerItem[7];
    arrayOfSpinnerItem4[0] = sHiddenSpinnerItem;
    arrayOfSpinnerItem4[1] = sEndomondoSpinnerItem;
    arrayOfSpinnerItem4[2] = sNewsFeedSpinnerItem;
    arrayOfSpinnerItem4[3] = sHistorySpinnerItem;
    arrayOfSpinnerItem4[4] = sRoutesSpinnerItem;
    arrayOfSpinnerItem4[5] = sFriendsSpinnerItem;
    arrayOfSpinnerItem4[6] = sSettingsSpinnerItem;
    sRoutesActivitySpinnerConfig = new SpinnerConfig(0, i1, 0, i2, arrayOfSpinnerItem4);
    int i3 = R.string.strFriends;
    int i4 = R.drawable.ab_icon_friends_white;
    SpinnerItem[] arrayOfSpinnerItem5 = new SpinnerItem[7];
    arrayOfSpinnerItem5[0] = sHiddenSpinnerItem;
    arrayOfSpinnerItem5[1] = sEndomondoSpinnerItem;
    arrayOfSpinnerItem5[2] = sNewsFeedSpinnerItem;
    arrayOfSpinnerItem5[3] = sHistorySpinnerItem;
    arrayOfSpinnerItem5[4] = sRoutesSpinnerItem;
    arrayOfSpinnerItem5[5] = sFriendsSpinnerItem;
    arrayOfSpinnerItem5[6] = sSettingsSpinnerItem;
    sFriendsActivitySpinnerConfig = new SpinnerConfig(0, i3, 0, i4, arrayOfSpinnerItem5);
    int i5 = R.string.strSettingsTab;
    int i6 = R.drawable.ab_icon_settings_white;
    SpinnerItem[] arrayOfSpinnerItem6 = new SpinnerItem[7];
    arrayOfSpinnerItem6[0] = sHiddenSpinnerItem;
    arrayOfSpinnerItem6[1] = sEndomondoSpinnerItem;
    arrayOfSpinnerItem6[2] = sNewsFeedSpinnerItem;
    arrayOfSpinnerItem6[3] = sHistorySpinnerItem;
    arrayOfSpinnerItem6[4] = sRoutesSpinnerItem;
    arrayOfSpinnerItem6[5] = sFriendsSpinnerItem;
    arrayOfSpinnerItem6[6] = sSettingsSpinnerItem;
    sSettingsActivitySpinnerConfig = new SpinnerConfig(0, i5, 0, i6, arrayOfSpinnerItem6);
  }
  
  public ActionBarAdapter(Context paramContext, int paramInt)
  {
    this.mContext = paramContext;
    this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    this.mRobotoRegular = Typeface.createFromAsset(this.mContext.getAssets(), "fonts/Roboto-Regular.ttf");
    this.mActivityInt = paramInt;
    this.mSpinnerConfig = getSpinnerConfig(paramInt);
  }
  
  private SpinnerConfig getSpinnerConfig(int paramInt)
  {
    switch(paramInt){
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
    switch(mActivityInt){
      case 1:
        return sEndomondoActivitySpinnerConfig.mSpinnerItemArray.length;
      case 2:
        return sNewsFeedActivitySpinnerConfig.mSpinnerItemArray.length;
      case 3:
        return sHistoryActivitySpinnerConfig.mSpinnerItemArray.length;
      case 4:
        return sRoutesActivitySpinnerConfig.mSpinnerItemArray.length;
      case 5:
        return sFriendsActivitySpinnerConfig.mSpinnerItemArray.length;
      case 6:
        return sSettingsActivitySpinnerConfig.mSpinnerItemArray.length;
      default:
        return 0;
    }
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

  //这里是创建下拉菜单的标题的布局
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View localView;
    if(convertView == null){
      localView = this.mInflater.inflate(R.layout.actionbar_item_view, null);
    }else{
      localView = convertView;
    }
      TextView localTextView1 = (TextView)localView.findViewById(R.id.ActionBarItemText1);
      localTextView1.setTypeface(this.mRobotoRegular);
      localTextView1.setVisibility(this.mSpinnerConfig.mTextViewVisibility);
      if (this.mSpinnerConfig.mTextViewVisibility == 0)
        localTextView1.setText(this.mSpinnerConfig.mTextViewTextId);
      ImageView localImageView = (ImageView)localView.findViewById(R.id.ActionBarItemImage1);
      localImageView.setVisibility(this.mSpinnerConfig.mImageViewVisibility);
      if (this.mSpinnerConfig.mImageViewVisibility == 0)
        localImageView.setImageResource(this.mSpinnerConfig.mImageViewImageId);
      TextView localTextView2 = (TextView)localView.findViewById(R.id.ActionBarItemImage1Text);
      localTextView2.setVisibility(View.GONE);
      if (this.mSpinnerConfig.mImageViewImageId == R.drawable.ab_icon_history_white)
      {
        localTextView2.setVisibility(View.VISIBLE);
        int i = new GregorianCalendar().get(Calendar.DATE);
        localTextView2.setText(i);
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

  //这里是创建下拉菜单的布局
  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    View localView;
    if(convertView == null){
      localView = this.mInflater.inflate(R.layout.actionbar_dropdown_item_view, null);
    }else{
      localView = convertView;
    }
    
    TextView localTextView1;
    TextView localTextView2;
    ImageView localImageView;
    localTextView1 = (TextView)localView.findViewById(R.id.ActionBarItemImageText);
    localTextView2 = (TextView)localView.findViewById(R.id.ActionBarItemText);
    localTextView2.setTypeface(this.mRobotoRegular);
    localTextView2.setTextColor(this.mContext.getResources().getColor(R.color.black));
    localImageView = (ImageView)localView.findViewById(R.id.ActionBarItemImage);
    FrameLayout localFrameLayout = (FrameLayout)localView.findViewById(R.id.ActionBarItemImageLayout);
    if(position == 0)
    {
      localFrameLayout.setVisibility(View.GONE);
      localTextView1.setVisibility(View.GONE);
      localTextView2.setVisibility(View.GONE);
      localImageView.setVisibility(View.GONE);
    }else{
      localFrameLayout.setVisibility(View.VISIBLE);
      localTextView1.setVisibility(View.GONE);
      localTextView2.setVisibility(View.VISIBLE);
      localImageView.setVisibility(View.VISIBLE);
      localTextView2.setText(this.mSpinnerConfig.mSpinnerItemArray[position].mStringId);
      if(position == mActivityInt){
        localTextView2.setTextColor(this.mContext.getResources().getColor(R.color.VeryLightGrey));
      }
      localImageView = (ImageView)localView.findViewById(R.id.ActionBarItemImage);
      localImageView.setImageResource(this.mSpinnerConfig.mSpinnerItemArray[position].mIconId);
      if (this.mSpinnerConfig.mSpinnerItemArray[position].mIconId == R.drawable.ab_icon_history)
      {
        localTextView1.setVisibility(View.VISIBLE);
        int n = new GregorianCalendar().get(Calendar.DATE);
        localTextView1.setText(n);
      }
    }
    return localView;
  }
  
  private static class SpinnerConfig
  {
    final int mImageViewImageId;
    final int mImageViewVisibility;
    final ActionBarAdapter.SpinnerItem[] mSpinnerItemArray;
    final int mTextViewTextId;
    final int mTextViewVisibility;

    SpinnerConfig(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ActionBarAdapter.SpinnerItem[] paramArrayOfSpinnerItem)
    {
      this.mTextViewVisibility = paramInt1;
      this.mTextViewTextId = paramInt2;
      this.mImageViewVisibility = paramInt3;
      this.mImageViewImageId = paramInt4;
      this.mSpinnerItemArray = paramArrayOfSpinnerItem;
    }
  }
  
  private static class SpinnerItem
  {
    final Class<? extends Activity> mActivityClass;
    final boolean mForResult;
    final int mIconId;
    final int mRequestCode;
    final int mStringId;

    SpinnerItem(int paramInt1, int paramInt2, Class<? extends Activity> paramClass, boolean paramBoolean, int paramInt3)
    {
      this.mStringId = paramInt1;
      this.mIconId = paramInt2;
      this.mActivityClass = paramClass;
      this.mForResult = paramBoolean;
      this.mRequestCode = paramInt3;
    }
  }

  public class OnNaviListener implements ActionBar.OnNavigationListener
  {

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
      // TODO Auto-generated method stub
      return false;
    }
    
  }

}
