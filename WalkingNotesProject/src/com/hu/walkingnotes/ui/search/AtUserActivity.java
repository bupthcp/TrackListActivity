package com.hu.walkingnotes.ui.search;

import com.hu.iJogging.R;
import com.hu.walkingnotes.ui.interfaces.AbstractAppActivity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

/**
 * User: qii
 * Date: 12-10-8
 */
public class AtUserActivity extends AbstractAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.at_other);

        String token = getIntent().getStringExtra("token");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new AtUserFragment(token))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
