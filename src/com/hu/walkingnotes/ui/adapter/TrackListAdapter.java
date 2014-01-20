package com.hu.walkingnotes.ui.adapter;

import com.hu.iJogging.R;
import com.hu.walkingnotes.support.asyncdrawable.IWeiciyuanDrawable;
import com.hu.walkingnotes.support.asyncdrawable.PictureBitmapDrawable;
import com.hu.walkingnotes.support.lib.ListViewMiddleMsgLoadingView;
import com.hu.walkingnotes.support.lib.TimeLineAvatarImageView;
import com.hu.walkingnotes.support.lib.TimeTextView;
import com.hu.walkingnotes.support.settinghelper.SettingUtility;
import com.hu.walkingnotes.support.utils.ThemeUtility;
import com.hu.walkingnotes.support.utils.Utility;
import com.hu.walkingnotes.ui.adapter.AbstractAppListAdapter.ViewHolder;
import com.hu.walkingnotes.ui.basefragment.AbstractTimeLineFragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.GridLayout;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class TrackListAdapter extends CursorAdapter{
  protected Fragment fragment;
  protected LayoutInflater inflater;
  protected ListView listView;
  protected Cursor cursor;
  
  protected int checkedBG;
  protected int defaultBG;
  
  private final int TYPE_NORMAL = 0;
  private final int TYPE_NORMAL_BIG_PIC = 1;
  private final int TYPE_MIDDLE = 2;
  private final int TYPE_SIMPLE = 3;
  
  public static final int NO_ITEM_ID = -1;
  
  private Set<Integer> tagIndexList = new HashSet<Integer>();
  private static final int PREF_LISTVIEW_ITEM_VIEW_COUNT = 6;
  private ArrayDeque<PrefView> prefNormalViews = new ArrayDeque<PrefView>(PREF_LISTVIEW_ITEM_VIEW_COUNT);

  private int savedCurrentMiddleLoadingViewPosition = AbstractTimeLineFragment.NO_SAVED_CURRENT_LOADING_MSG_VIEW_POSITION;

  private class PrefView {
      View view;
      ViewHolder holder;
  }
  
  public TrackListAdapter(Fragment fragment, Cursor cursor, ListView listView){
    super(fragment.getActivity(), cursor, false);
    this.fragment = fragment;
    this.inflater = fragment.getActivity().getLayoutInflater();
    this.listView = listView;
    this.cursor = cursor;
    
    defaultBG = fragment.getResources().getColor(R.color.transparent);
    checkedBG = ThemeUtility.getColor(R.attr.listview_checked_color);
    
    for (int i = 0; i < PREF_LISTVIEW_ITEM_VIEW_COUNT; i++) {
      PrefView prefView = new PrefView();
      prefView.view = initNormalLayout(null);
      prefView.holder = buildHolder(prefView.view);
      prefNormalViews.add(prefView);
    }
    
    listView.setRecyclerListener(new AbsListView.RecyclerListener() {
      @Override
      public void onMovedToScrapHeap(View view) {
        Integer index = (Integer) view.getTag(R.string.listview_index_tag);
        if (index == null) return;

        for (Integer tag : tagIndexList) {

          ViewHolder holder = (ViewHolder) view.getTag(tag);

          if (holder != null) {
            Drawable drawable = holder.avatar.getImageView().getDrawable();
            clearAvatarBitmap(holder, drawable);
            drawable = holder.content_pic.getImageView().getDrawable();
            clearPictureBitmap(holder, drawable);
            drawable = holder.repost_content_pic.getImageView().getDrawable();
            clearRepostPictureBitmap(holder, drawable);

            clearMultiPics(holder.content_pic_multi);
            clearMultiPics(holder.repost_content_pic_multi);

            if (!tag.equals(index)) {
              holder.listview_root.removeAllViewsInLayout();
              holder.listview_root = null;
              view.setTag(tag, null);
            }
          }
        }
      }

      void clearMultiPics(GridLayout gridLayout) {
        if (gridLayout == null) return;
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
          ImageView iv = (ImageView) gridLayout.getChildAt(i);
          if (iv != null) {
            iv.setImageDrawable(null);
          }
        }
      }

      void clearAvatarBitmap(ViewHolder holder, Drawable drawable) {
        if (!(drawable instanceof PictureBitmapDrawable)) {
          holder.avatar.setImageDrawable(null);
          holder.avatar.getImageView().clearAnimation();
        }
      }

      void clearPictureBitmap(ViewHolder holder, Drawable drawable) {
        if (!(drawable instanceof PictureBitmapDrawable)) {
          holder.content_pic.setImageDrawable(null);
          holder.content_pic.getImageView().clearAnimation();
        }
      }

      void clearRepostPictureBitmap(ViewHolder holder, Drawable drawable) {
        if (!(drawable instanceof PictureBitmapDrawable)) {
          holder.repost_content_pic.setImageDrawable(null);
          holder.repost_content_pic.getImageView().clearAnimation();
        }
      }
    });
  }
  
  private View initNormalLayout(ViewGroup parent) {
    return inflater.inflate(R.layout.timeline_listview_item_layout, parent, false);
  }
  
  private ViewHolder buildHolder(View convertView) {
    ViewHolder holder = new ViewHolder();
    holder.username = (TextView) convertView.findViewById(R.id.username);
    TextPaint tp = holder.username.getPaint();
    tp.setFakeBoldText(true);
    holder.content = (TextView) convertView.findViewById(R.id.content);
    holder.repost_content = (TextView) convertView.findViewById(R.id.repost_content);
    holder.time = (TimeTextView) convertView.findViewById(R.id.time);
    holder.avatar = (TimeLineAvatarImageView) convertView.findViewById(R.id.avatar);

    holder.content_pic = (IWeiciyuanDrawable) convertView.findViewById(R.id.content_pic);
    holder.content_pic_multi = (GridLayout) convertView.findViewById(R.id.content_pic_multi);
    holder.repost_content_pic = (IWeiciyuanDrawable) convertView
        .findViewById(R.id.repost_content_pic);
    holder.repost_content_pic_multi = (GridLayout) convertView
        .findViewById(R.id.repost_content__pic_multi);

    holder.listview_root = (RelativeLayout) convertView.findViewById(R.id.listview_root);
    holder.repost_layout = convertView.findViewById(R.id.repost_layout);
    holder.repost_flag = (View) convertView.findViewById(R.id.repost_flag);
    holder.count_layout = (LinearLayout) convertView.findViewById(R.id.count_layout);
    holder.repost_count = (TextView) convertView.findViewById(R.id.repost_count);
    holder.comment_count = (TextView) convertView.findViewById(R.id.comment_count);
    holder.timeline_gps = (ImageView) convertView.findViewById(R.id.timeline_gps_iv);
    holder.timeline_pic = (ImageView) convertView.findViewById(R.id.timeline_pic_iv);
    holder.replyIV = (ImageView) convertView.findViewById(R.id.replyIV);
    holder.source = (TextView) convertView.findViewById(R.id.source);
    return holder;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    PrefView prefView = null;
    if (convertView == null|| convertView.getTag(R.drawable.ic_launcher + getItemViewType(position)) == null) {
      switch (getItemViewType(position)) {
        case TYPE_NORMAL:
          prefView = prefNormalViews.poll();
          if (prefView != null) {
            convertView = prefView.view;
          }
          if (convertView == null) {
            convertView = initNormalLayout(parent);
          }
          break;
        default:
          convertView = initNormalLayout(parent);
          break;
      }
      if (getItemViewType(position) != TYPE_MIDDLE) {
        if (prefView == null) {
          holder = buildHolder(convertView);
        } else {
          holder = prefView.holder;
        }
        convertView.setTag(R.drawable.ic_launcher + getItemViewType(position), holder);
        convertView.setTag(R.string.listview_index_tag, R.drawable.ic_launcher
            + getItemViewType(position));
        tagIndexList.add(R.drawable.ic_launcher + getItemViewType(position));
      }
    }else {
      holder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + getItemViewType(position));
    }
    if (getItemViewType(position) != TYPE_MIDDLE) {
      configLayerType(holder);
      configViewFont(holder);
      bindViewData(holder, position);
      bindOnTouchListener(holder);
    } else {
      if (savedCurrentMiddleLoadingViewPosition == position + listView.getHeaderViewsCount()) {
        ListViewMiddleMsgLoadingView loadingView = (ListViewMiddleMsgLoadingView) convertView;
        loadingView.load();
      }
    }
    return convertView;
  }
  
  private void bindOnTouchListener(ViewHolder holder) {
    holder.listview_root.setClickable(false);
    holder.username.setClickable(false);
    holder.time.setClickable(false);
    holder.content.setClickable(false);
    holder.repost_content.setClickable(false);

    if (holder.content != null) holder.content.setOnTouchListener(onTouchListener);
    if (holder.repost_content != null) holder.repost_content.setOnTouchListener(onTouchListener);
  }
  
  private void configLayerType(ViewHolder holder) {

    boolean disableHardAccelerated = SettingUtility.disableHardwareAccelerated();
    if (!disableHardAccelerated) return;

    int currentWidgetLayerType = holder.username.getLayerType();

    if (View.LAYER_TYPE_SOFTWARE != currentWidgetLayerType) {
      holder.username.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
      if (holder.content != null) holder.content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
      if (holder.repost_content != null)
        holder.repost_content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
      if (holder.time != null) holder.time.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
      if (holder.repost_count != null)
        holder.repost_count.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
      if (holder.comment_count != null)
        holder.comment_count.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

  }
  
  private void configViewFont(ViewHolder holder) {
    int prefFontSizeSp = SettingUtility.getFontSize();
    float currentWidgetTextSizePx;

    currentWidgetTextSizePx = holder.time.getTextSize();

    if (Utility.sp2px(prefFontSizeSp - 3) != currentWidgetTextSizePx) {
      holder.time.setTextSize(prefFontSizeSp - 3);
      if (holder.source != null) holder.source.setTextSize(prefFontSizeSp - 3);
    }

    currentWidgetTextSizePx = holder.content.getTextSize();

    if (Utility.sp2px(prefFontSizeSp) != currentWidgetTextSizePx) {
      holder.content.setTextSize(prefFontSizeSp);
      holder.username.setTextSize(prefFontSizeSp);
      holder.repost_content.setTextSize(prefFontSizeSp);

    }

    if (holder.repost_count != null) {
      currentWidgetTextSizePx = holder.repost_count.getTextSize();
      if (Utility.sp2px(prefFontSizeSp - 5) != currentWidgetTextSizePx) {
        holder.repost_count.setTextSize(prefFontSizeSp - 5);
      }
    }

    if (holder.comment_count != null) {
      currentWidgetTextSizePx = holder.comment_count.getTextSize();
      if (Utility.sp2px(prefFontSizeSp - 5) != currentWidgetTextSizePx) {
        holder.comment_count.setTextSize(prefFontSizeSp - 5);
      }
    }
  }

  protected void bindViewData(ViewHolder holder, int position){
    
  }
  
  private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
      return false;
    }
    
  };
  

  @Override
  public void bindView(View view, Context context, Cursor cursor) {

  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    PrefView prefView = null;
    View convertView = null;
    switch (getItemViewType(cursor.getPosition())) {
      case TYPE_NORMAL:
        prefView = prefNormalViews.poll();
        if (prefView != null) {
          convertView = prefView.view;
        }
        if (convertView == null) {
          convertView = initNormalLayout(parent);
        }
        break;
      default:
        convertView = initNormalLayout(parent);
        break;
    }
    if (getItemViewType(cursor.getPosition()) != TYPE_MIDDLE) {
      if (prefView == null) {
//        holder = buildHolder(convertView);
      } else {
//        holder = prefView.holder;
      }
//      convertView.setTag(R.drawable.ic_launcher + getItemViewType(cursor.getPosition()), holder);
      convertView.setTag(R.string.listview_index_tag, R.drawable.ic_launcher
          + getItemViewType(cursor.getPosition()));
      tagIndexList.add(R.drawable.ic_launcher + getItemViewType(cursor.getPosition()));
    }
    return null;
  }
}
