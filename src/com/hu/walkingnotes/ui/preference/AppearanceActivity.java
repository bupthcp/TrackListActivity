package com.hu.walkingnotes.ui.preference;

import com.hu.iJogging.R;
import com.hu.walkingnotes.ui.interfaces.AbstractAppActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import org.holoeverywhere.preference.PreferenceManager;

/**
 * User: qii
 * Date: 12-10-4
 */
public class AppearanceActivity extends AbstractAppActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.pref_appearance_title));

        if (savedInstanceState == null) {
          getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new AppearanceFragment())
                    .commit();
        }

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        if (key.equals(SettingActivity.THEME)) {

            Intent intent = new Intent(this, AppearanceActivity.class);

            finish();
            overridePendingTransition(0, 0);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.stay, R.anim.alphaout);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        finish();
    }
}
