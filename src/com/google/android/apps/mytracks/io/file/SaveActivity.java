/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.apps.mytracks.io.file;

import com.google.android.apps.mytracks.util.DialogUtils;
import com.google.android.apps.mytracks.util.FileUtils;
import com.google.android.apps.mytracks.util.IntentUtils;
import com.hu.iJogging.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;

/**
 * An activity for saving tracks to the external storage. If saving a specific
 * track, option to save it to a temp directory and play the track afterward.
 * 
 * @author Rodrigo Damazio
 */
public class SaveActivity extends Activity {

  public static final String EXTRA_TRACK_FILE_FORMAT = "track_file_format";
  public static final String EXTRA_TRACK_ID = "track_id";
  public static final String EXTRA_PLAY_TRACK = "play_track";

  public static final String GOOGLE_EARTH_KML_MIME_TYPE = "application/vnd.google-earth.kml+xml";
  public static final String GOOGLE_EARTH_PACKAGE = "com.google.earth";
  public static final String GOOGLE_EARTH_MARKET_URL = "market://details?id="
      + GOOGLE_EARTH_PACKAGE;
  private static final String
      GOOGLE_EARTH_TOUR_FEATURE_ID = "com.google.earth.EXTRA.tour_feature_id";
  private static final String GOOGLE_EARTH_CLASS = "com.google.earth.EarthActivity";

  private static final int DIALOG_PROGRESS_ID = 0;
  private static final int DIALOG_RESULT_ID = 1;

  private TrackFileFormat trackFileFormat;
  private long trackId;
  private boolean playTrack;
  private String directoryName;

  private SaveAsyncTask saveAsyncTask;
  private ProgressDialog progressDialog;

  // the number of tracks successfully saved
  private int successCount;

  // the number of tracks to save
  private int totalCount;

  // the last successfully saved path
  private String savedPath;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    trackFileFormat = intent.getParcelableExtra(EXTRA_TRACK_FILE_FORMAT);
    trackId = intent.getLongExtra(EXTRA_TRACK_ID, -1L);
    playTrack = intent.getBooleanExtra(EXTRA_PLAY_TRACK, false);

    if (!FileUtils.isExternalStorageWriteable()) {
      Toast.makeText(this, R.string.external_storage_not_writable, Toast.LENGTH_LONG).show();
      finish();
      return;
    }

    directoryName = playTrack ? FileUtils.buildExternalDirectoryPath(
        trackFileFormat.getExtension(), "tmp")
        : FileUtils.buildExternalDirectoryPath(trackFileFormat.getExtension());

    File directory = new File(directoryName);
    if (!FileUtils.ensureDirectoryExists(directory)) {
      Toast.makeText(this, R.string.external_storage_not_writable, Toast.LENGTH_LONG).show();
      finish();
      return;
    }

    Object retained = getLastNonConfigurationInstance();
    if (retained instanceof SaveAsyncTask) {
      saveAsyncTask = (SaveAsyncTask) retained;
      saveAsyncTask.setActivity(this);
    } else {
      saveAsyncTask = new SaveAsyncTask(this, trackFileFormat, trackId, directory);
      saveAsyncTask.execute();
    }
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    saveAsyncTask.setActivity(null);
    return saveAsyncTask;
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case DIALOG_PROGRESS_ID:
        progressDialog = DialogUtils.createHorizontalProgressDialog(
            this, R.string.save_progress_message, new DialogInterface.OnCancelListener() {
                @Override
              public void onCancel(DialogInterface dialog) {
                saveAsyncTask.cancel(true);
                dialog.dismiss();
                finish();
              }
            }, directoryName);
        return progressDialog;
      case DIALOG_RESULT_ID:
        boolean success;
        String message;
        String totalTracks = getResources()
            .getQuantityString(R.plurals.tracks, totalCount, totalCount);
        if (successCount == totalCount && totalCount > 0) {
          success = true;
          message = getString(R.string.save_success, totalTracks, directoryName);
        } else {
          success = false;
          message = getString(R.string.save_error, successCount, totalTracks, directoryName);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(true).setIcon(
            success ? android.R.drawable.ic_dialog_info : android.R.drawable.ic_dialog_alert)
            .setMessage(message).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
              public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                finish();
              }
            }).setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                @Override
              public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                finish();
              }
            }).setTitle(success ? R.string.generic_success_title : R.string.generic_error_title);

        if (success && trackId != -1L && !playTrack && savedPath != null) {
          builder.setNegativeButton(
              R.string.share_track_share_file, new DialogInterface.OnClickListener() {
                  @Override
                public void onClick(DialogInterface dialog, int which) {
                  Intent intent = IntentUtils.newShareFileIntent(
                      SaveActivity.this, trackId, savedPath, trackFileFormat);
                  startActivity(
                      Intent.createChooser(intent, getString(R.string.share_track_picker_title)));
                  finish();
                }
              });
        }
        return builder.create();
      default:
        return null;
    }
  }

  /**
   * Invokes when the associated AsyncTask completes.
   * 
   * @param aSuccessCount the number of tracks successfully saved
   * @param aTotalCount the number of tracks to save
   * @param aSavedPath the last successfully saved path
   */
  public void onAsyncTaskCompleted(int aSuccessCount, int aTotalCount, String aSavedPath) {
    successCount = aSuccessCount;
    totalCount = aTotalCount;
    savedPath = aSavedPath;
    removeDialog(DIALOG_PROGRESS_ID);
    if (successCount == 1 && totalCount == 1 && playTrack && savedPath != null) {
      Intent intent = new Intent().addFlags(
          Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
          .putExtra(GOOGLE_EARTH_TOUR_FEATURE_ID, KmlTrackWriter.TOUR_FEATURE_ID)
          .setClassName(GOOGLE_EARTH_PACKAGE, GOOGLE_EARTH_CLASS)
          .setDataAndType(Uri.fromFile(new File(savedPath)), GOOGLE_EARTH_KML_MIME_TYPE);
      startActivity(intent);
      finish();
    } else {
      showDialog(DIALOG_RESULT_ID);
    }
  }

  /**
   * Shows the progress dialog.
   */
  public void showProgressDialog() {
    showDialog(DIALOG_PROGRESS_ID);
  }

  /**
   * Sets the progress dialog value.
   * 
   * @param number the number of points saved
   * @param max the maximum number of points
   */
  public void setProgressDialogValue(int number, int max) {
    if (progressDialog != null) {
      progressDialog.setIndeterminate(false);
      progressDialog.setMax(max);
      progressDialog.setProgress(Math.min(number, max));
    }
  }
}
