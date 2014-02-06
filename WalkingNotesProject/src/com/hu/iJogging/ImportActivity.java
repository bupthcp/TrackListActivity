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

package com.hu.iJogging;

import com.google.android.apps.mytracks.io.file.TrackFileFormat;
import com.google.android.apps.mytracks.util.DialogUtils;
import com.google.android.apps.mytracks.util.FileUtils;
import com.google.android.apps.mytracks.util.IntentUtils;
import com.google.android.apps.mytracks.util.UriUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * An activity to import files from the external storage. Optionally to import
 * one specific file.
 * 
 * @author Rodrigo Damazio
 */
public class ImportActivity extends Activity {

  public static final String EXTRA_IMPORT_ALL = "import_all";
  public static final String EXTRA_TRACK_FILE_FORMAT = "track_file_format";

  private static final String TAG = ImportActivity.class.getSimpleName();
  private static final int DIALOG_PROGRESS_ID = 0;
  private static final int DIALOG_RESULT_ID = 1;

  private ImportAsyncTask importAsyncTask;
  private ProgressDialog progressDialog;

  private boolean importAll;
  private TrackFileFormat trackFileFormat;

  // the path on the external storage to import
  private String path;

  // the number of files successfully imported
  private int successCount;

  // the number of files to import
  private int totalCount;

  // the last successfully imported track id
  private long trackId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    importAll = intent.getBooleanExtra(EXTRA_IMPORT_ALL, false);
    trackFileFormat = intent.getParcelableExtra(EXTRA_TRACK_FILE_FORMAT);
    if (trackFileFormat == null) {
      trackFileFormat = TrackFileFormat.GPX;
    }

    if (!FileUtils.isExternalStorageAvailable()) {
      Toast.makeText(this, R.string.external_storage_not_available, Toast.LENGTH_LONG).show();
      finish();
      return;
    }
    if (importAll) {
      path = FileUtils.buildExternalDirectoryPath(
          trackFileFormat == TrackFileFormat.KML ? "kml" : "gpx");
      if (!FileUtils.isDirectory(new File(path))) {
        Toast.makeText(this, getString(R.string.import_no_directory, path), Toast.LENGTH_LONG)
            .show();
        finish();
        return;
      }
    } else {
      String action = intent.getAction();
      if (!(Intent.ACTION_ATTACH_DATA.equals(action) || Intent.ACTION_VIEW.equals(action))) {
        Log.d(TAG, "Invalid action: " + intent);
        finish();
        return;
      }

      Uri data = intent.getData();
      if (!UriUtils.isFileUri(data)) {
        Log.d(TAG, "Invalid data: " + intent);
        finish();
        return;
      }
      path = data.getPath();
    }

    Object retained = getLastNonConfigurationInstance();
    if (retained instanceof ImportAsyncTask) {
      importAsyncTask = (ImportAsyncTask) retained;
      importAsyncTask.setActivity(this);
    } else {
      importAsyncTask = new ImportAsyncTask(this, importAll, trackFileFormat, path);
      importAsyncTask.execute();
    }
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    importAsyncTask.setActivity(null);
    return importAsyncTask;
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case DIALOG_PROGRESS_ID:
        progressDialog = DialogUtils.createHorizontalProgressDialog(
            this, R.string.import_progress_message, new DialogInterface.OnCancelListener() {
                @Override
              public void onCancel(DialogInterface dialog) {
                importAsyncTask.cancel(true);
                dialog.dismiss();
                finish();
              }
            }, path);
        return progressDialog;
      case DIALOG_RESULT_ID:
        final boolean success;
        String message;
        String totalFiles = getResources()
            .getQuantityString(R.plurals.files, totalCount, totalCount);
        if (successCount == totalCount && totalCount > 0) {
          success = true;
          message = getString(R.string.import_success, totalFiles, path);
        } else {
          success = false;
          message = getString(R.string.import_error, successCount, totalFiles, path);
        }
        return new AlertDialog.Builder(this).setCancelable(true).setIcon(
            success ? android.R.drawable.ic_dialog_info : android.R.drawable.ic_dialog_alert)
            .setMessage(message).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
              public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                finish();
              }
            }).setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                @Override
              public void onClick(DialogInterface dialog, int which) {
                if (success && !importAll && trackId != -1L) {
                  Intent intent = IntentUtils.newIntent(
                      ImportActivity.this, IJoggingActivity.class)
                      .putExtra(IJoggingActivity.EXTRA_TRACK_ID, trackId);
                  TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(ImportActivity.this);
                  taskStackBuilder.addNextIntent(intent);
                  taskStackBuilder.startActivities();
                }
                dialog.dismiss();
                finish();
              }
            }).setTitle(success ? R.string.generic_success_title : R.string.generic_error_title)
            .create();
      default:
        return null;
    }
  }

  /**
   * Invokes when the associated AsyncTask completes.
   * 
   * @param aSuccessCount the number of files successfully imported
   * @param aTotalCount the number of files to import
   * @param aTrackId the last successfully imported track id
   */
  public void onAsyncTaskCompleted(int aSuccessCount, int aTotalCount, long aTrackId) {
    successCount = aSuccessCount;
    totalCount = aTotalCount;
    trackId = aTrackId;
    removeDialog(DIALOG_PROGRESS_ID);
    showDialog(DIALOG_RESULT_ID);
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
   * @param number the number of files imported
   * @param max the maximum number of files
   */
  public void setProgressDialogValue(int number, int max) {
    if (progressDialog != null) {
      progressDialog.setIndeterminate(false);
      progressDialog.setMax(max);
      progressDialog.setProgress(Math.min(number, max));
    }
  }
}
