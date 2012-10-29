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
  //mCurrentSpinner ���ڼ�¼spinner�������˵����ĸ�item��ѡ�У��Ա�
  //���´������˵�����ʱ���˲˵���ɫ
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
    arrayOfSpinnerItem[0] = sHiddenSpinnerItem;  //���item���ò����ģ�ֻ��Ϊ��ʹ�����±���positionһ�¶��������
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

  // Spinner�Ļ�����ʵ�����list�ǻ������Ƶģ�
  //1. Spinner�ı��⴦���Կ���List�ĵ�0��Ԫ�أ�Spinner��dropdown�����Ĳ˵����Կ���
  //List�ĵ�1������2��....Ԫ�ء�
  //2. Spinner�Ļ��ư����������̡����Ȼ����dropdown�����Ĳ˵���ÿ��item�Ŀ�ȸ߶ȣ�
  //���������getDropDownView��ɡ������getDropDownView�ж���position==0����Ŀ�ǲ�����
  //�ģ��ڵ�ǰ�Ĵ����У�getDropDownView������14�Σ������߶�7�Σ��������7�Ρ�
  //��֮����Ҫ����Spinner�ı���Ŀ�ȣ�������̿���ͨ����ϵ�ķ�ʽ���٣�����getViewҪ
  //����8�Σ���һ��getView�ǵõ���ǰѡ�е�Item��View�������7�����ǰ���˳��7��item��
  //��һ��
  //3.��������̿��Կ�����Spinner�Ļ�����ʵ�����飬һ������getDropDownView���Ƶ��ڲ㣬
  //��getView�����ɱ����Ⱦ�������㣬Ȼ������������һ��getView���ص�viewֻ�б�ѡ�е�
  //�Ǹ�position��item�Ż���Ч���ڱ��⴦��ʾ������
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

  // �����Ǵ��������˵��Ĳ���
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

  //SpinnerConfig�����˱����dropdown�˵��Ĳ�����Դ��Ϣ
  //paramArrayOfSpinnerItem������鱣�����dropdown�˵��Ĳ�����Դ��Ϣ
  //ʣ���4����Ա�����Ǳ���Ĳ�����Դ��Ϣ
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
  
  //��ͨ��ActionBar����fragment���л�ʱ����û�а취��fragment�ڲ�����ǰһ��fragment��ɾ��������
  //ֻ���ڵ��ActionBar���֮�󣬸��ݵ�ǰ��ʹ�õ�fragment container��res id����fragment��ɾ��
  private void removeFragment(){
    FragmentManager fragmentManager = ((IJoggingActivity)mContext).getSupportFragmentManager();
    FragmentTransaction ft = fragmentManager.beginTransaction();
    Fragment fragment;
    switch(mCurrentSpinner){
      case 1:
        //��ҳ�洦����ѵ��ʱ����������viewpager���й���ġ��������飬����viewpager���Լ�����fragment��
        //����������Ҫ���ǽ����ֶ���remove����
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

  
  // ����ص������ڲ������֮��Ż���ã���getView�Լ�getDropDownView����ɺ󣬲ŵ���
  // mActionBar.setSelectedNavigationItem ���������������²��֣�����Ҳ���������ص�����
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
