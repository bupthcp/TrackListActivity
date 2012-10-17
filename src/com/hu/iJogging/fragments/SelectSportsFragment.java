package com.hu.iJogging.fragments;

import com.actionbarsherlock.app.ActionBar;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.IJoggingActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SelectSportsFragment extends Fragment{
  
  private View mSelectSportsFragmentView;
  
  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mSelectSportsFragmentView = getActivity().getLayoutInflater().inflate(R.layout.select_sports, container, false);
    ListView listView = (ListView)mSelectSportsFragmentView.findViewById(R.id.select_sports_list);
    SelectSportsFragmentAdapter<CharSequence> adapter = SelectSportsFragmentAdapter.createFromResource(
        getActivity(), R.array.sports, R.layout.select_sports_item);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        TextView tv=(TextView)arg1.findViewById(R.id.sport_description);
        ((IJoggingActivity)getActivity()).currentSport= (String) tv.getText();
        getFragmentManager().popBackStack();
      }});
    setupActionBar();

    return mSelectSportsFragmentView;
  }
  
  private void setupActionBar(){
    ActionBar actionBar = ((IJoggingActivity)getActivity()).getSupportActionBar();
    //这里如果通过设置setDisplayHomeAsUpEnabled去使用actionBar自带的回退按钮
    //是无法完成回到上一个fragment的功能的
    //所以只能使用自定义的view去实现actionBar
    //使用的自定义布局simple_action_bar_title是从abs_action_bar_title_item.xml剥离出来的
    //这个是sherlock的title的布局
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setCustomView(R.layout.actionbar_cunstom_simple);
    View customView = actionBar.getCustomView();
    TextView tv = (TextView)customView.findViewById(R.id.simple_action_bar_title);
    tv.setText(R.string.strSelectSport);
    
    View iv = customView.findViewById(R.id.icon_back);
    iv.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        getFragmentManager().popBackStack();
      }    
    });
  }
  
  
  @Override
  public void onDestroyView() {
    ((IJoggingActivity)getActivity()).setupActionBar();

    super.onDestroyView();
    ViewGroup parentViewGroup = (ViewGroup) mSelectSportsFragmentView.getParent();
    if (parentViewGroup != null) {
      parentViewGroup.removeView(mSelectSportsFragmentView);
    }
  }
}
