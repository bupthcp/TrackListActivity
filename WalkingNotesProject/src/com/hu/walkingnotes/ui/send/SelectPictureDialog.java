package com.hu.walkingnotes.ui.send;

import com.hu.iJogging.R;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;

import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * User: qii
 * Date: 12-9-6
 */
public class SelectPictureDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] items = {getString(R.string.get_the_last_picture), getString(R.string.take_camera), getString(R.string.select_pic)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.select))
                .setItems(items, (DialogInterface.OnClickListener) getActivity());
        return builder.create();
    }
}
