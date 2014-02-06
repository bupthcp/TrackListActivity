package com.hu.iJogging.fragments;

import com.hu.iJogging.IJoggingActivity;
import com.hu.iJogging.R;
import com.hu.iJogging.SelectSportsActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SelectSportsFragment extends Fragment{
  
  private View mSelectSportsFragmentView;
  public static final String SELECT_SPORTS_FTAGMENT_TAG = "SelectSportsFragment";
  
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
//        ((IJoggingActivity)getActivity()).currentSport= (String) tv.getText();
//        getFragmentManager().popBackStack();
        Intent resultInent = new Intent(getActivity(),IJoggingActivity.class);
        resultInent.putExtra("currentSport", (String) tv.getText());
        getActivity().setResult(Activity.RESULT_OK,resultInent);
        getActivity().finish();
      }});
    setupActionBar();

    return mSelectSportsFragmentView;
  }
  
  private void setupActionBar(){
    ActionBar actionBar = ((SelectSportsActivity)getActivity()).getSupportActionBar();
    //�������ͨ������setDisplayHomeAsUpEnabledȥʹ��actionBar�Դ��Ļ��˰�ť
    //���޷���ɻص���һ��fragment�Ĺ��ܵ�
    //����ֻ��ʹ���Զ����viewȥʵ��actionBar
    //ʹ�õ��Զ��岼��simple_action_bar_title�Ǵ�abs_action_bar_title_item.xml���������
    //�����sherlock��title�Ĳ���
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setDisplayUseLogoEnabled(false);
    actionBar.setCustomView(R.layout.actionbar_cunstom_simple);
    View customView = actionBar.getCustomView();
    TextView tv = (TextView)customView.findViewById(R.id.simple_action_bar_title);
    tv.setText(R.string.strSelectSport);
    
    View iv = customView.findViewById(R.id.icon_back);
    iv.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
//        getFragmentManager().popBackStack();
        getActivity().finish();
      }    
    });
  }
  
  
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ViewGroup parentViewGroup = (ViewGroup) mSelectSportsFragmentView.getParent();
    if (parentViewGroup != null) {
      parentViewGroup.removeView(mSelectSportsFragmentView);
    }
  }
}
