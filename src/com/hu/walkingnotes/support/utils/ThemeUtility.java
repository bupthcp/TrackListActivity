package com.hu.walkingnotes.support.utils;

import com.hu.iJogging.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * User: qii
 * Date: 13-8-4
 */
public class ThemeUtility {

    public static int getColor(int attr) {
        int[] attrs = new int[]{attr};
        Context context = GlobalContext.getInstance().getActivity();
//        if (context == null)
//            context = GlobalContext.getInstance();
        TypedArray ta = context.obtainStyledAttributes(attrs);
        return ta.getColor(0, 430);

    }

    public static Drawable getDrawable(int attr) {
        int[] attrs = new int[]{attr};
        Context context = GlobalContext.getInstance().getActivity();
        TypedArray ta = context.obtainStyledAttributes(attrs);
        return ta.getDrawable(0);

    }

    public static int getResourceId(int attr) {
        int[] attrs = new int[]{attr};
        Context context = GlobalContext.getInstance().getActivity();
        TypedArray ta = context.obtainStyledAttributes(attrs);
        return ta.getResourceId(0, 430);

    }

    //can't find a public theme attr to modify actionbar searchview text color
    public static void customActionBarSearchViewTextColor(SearchView searchView) {
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);

    }

    //android:actionModeShareDrawalbe is not a public attr
    public static int getActionBarShareItemIcon() {
        return R.drawable.ic_menu_share_holo_dark;
    }

}
