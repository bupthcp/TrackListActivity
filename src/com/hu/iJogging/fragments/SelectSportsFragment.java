package com.hu.iJogging.fragments;

import com.actionbarsherlock.app.ActionBar;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.IJoggingActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    ActionBar actionBar = ((IJoggingActivity)getActivity()).getSupportActionBar();
    actionBar.setDisplayOptions(15);
    actionBar.setTitle("Sport");
    return mSelectSportsFragmentView;
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
