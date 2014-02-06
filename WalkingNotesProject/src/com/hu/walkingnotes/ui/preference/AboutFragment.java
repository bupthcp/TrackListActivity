package com.hu.walkingnotes.ui.preference;

import com.hu.iJogging.R;
import com.hu.walkingnotes.support.debug.AppLogger;
import com.hu.walkingnotes.support.file.FileManager;
import com.hu.walkingnotes.support.lib.changelogdialog.ChangeLogDialog;
import com.hu.walkingnotes.support.utils.GlobalContext;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.PreferenceFragment;

/**
 * User: qii
 * Date: 12-9-29
 */
public class AboutFragment extends PreferenceFragment {

    private BroadcastReceiver sdCardReceiver;
    private MediaPlayer mp;
    private boolean playing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);

        addPreferencesFromResource(R.xml.about_pref);

//        findPreference(SettingActivity.SUGGEST).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
//                intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
//                intent.putExtra("account", GlobalContext.getInstance().getAccountBean());
//                intent.putExtra("content", buildContent());
//                startActivity(intent);
//                return true;
//            }
//        });
//
//        findPreference(SettingActivity.RECOMMEND).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
//                intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
//                intent.putExtra("account", GlobalContext.getInstance().getAccountBean());
//                intent.putExtra("content", getString(R.string.recommend_content));
//                startActivity(intent);
//                return true;
//            }
//        });

        findPreference(SettingActivity.VERSION).setSummary(buildVersionInfo());

        findPreference(SettingActivity.VERSION).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ChangeLogDialog changeLogDialog = new ChangeLogDialog(getActivity());
                changeLogDialog.show();
                return true;
            }
        });

        if (getResources().getBoolean(R.bool.blackmagic)) {
            Runtime rt = Runtime.getRuntime();
            long vmAlloc = rt.totalMemory() - rt.freeMemory();
            long nativeAlloc = Debug.getNativeHeapAllocatedSize();

            String vmAllocStr = "VM Allocated " + formatMemoryText(vmAlloc);
            String nativeAllocStr = "Native Allocated " + formatMemoryText(nativeAlloc);

            ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            int memoryClass = am.getMemoryClass();
            String result = "VM Max " + Integer.toString(memoryClass) + "MB";
            findPreference(SettingActivity.DEBUG_MEM_INFO).setSummary(vmAllocStr + "," + nativeAllocStr + "," + result);
        }


        findPreference(SettingActivity.AUTHOR).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mp != null && mp.isPlaying()) {
                    mp.stop();
                    playing = false;
                    return true;
                }
                if (mp == null || !playing) {
                    mp = MediaPlayer.create(getActivity(), R.raw.star);
                }
                mp.start();
                playing = true;
                Toast.makeText(getActivity(), "♩♪♫♬♭", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        buildCacheSummary();
        buildLogSummary();

        findPreference(SettingActivity.SAVED_PIC_PATH).setSummary(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath());

    }

    @Override
    public void onResume() {
        super.onResume();
        sdCardReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                buildCacheSummary();
                buildLogSummary();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addDataScheme("file");

        getActivity().registerReceiver(sdCardReceiver, filter);
    }

    private void buildCacheSummary() {
        File cachedDir = GlobalContext.getInstance().getExternalCacheDir();
        if (cachedDir != null) {
            findPreference(SettingActivity.CACHE_PATH).setSummary(cachedDir.getAbsolutePath());
        } else {
            findPreference(SettingActivity.CACHE_PATH).setSummary(getString(R.string.sd_card_in_not_mounted));
        }
    }

    private void buildLogSummary() {
        File cachedDir = GlobalContext.getInstance().getExternalCacheDir();
        if (cachedDir != null) {
            findPreference(SettingActivity.SAVED_LOG_PATH).setSummary(FileManager.getLogDir());
        } else {
            findPreference(SettingActivity.SAVED_LOG_PATH).setSummary(getString(R.string.sd_card_in_not_mounted));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sdCardReceiver != null) {
            getActivity().unregisterReceiver(sdCardReceiver);
        }

        if (mp != null && mp.isPlaying()) {
            mp.stop();
            playing = false;
        }
    }

    private String buildVersionInfo() {
        String version = "";
        PackageManager packageManager = getActivity().getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            AppLogger.e(e.getMessage());
        }

        if (packInfo != null) {
            version = packInfo.versionName;
        }

        if (!TextUtils.isEmpty(version)) {
            return version;
        } else {
            return "";
        }
    }

    private String buildContent() {

        String network = "";

        ConnectivityManager cm = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                network = "Wifi";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {

                int subType = networkInfo.getSubtype();

                if (subType == TelephonyManager.NETWORK_TYPE_GPRS) {
                    network = "GPRS";
                }
            }
        }

        return "@四次元App #四次元App反馈# " + android.os.Build.MANUFACTURER
                + " " + android.os.Build.MODEL + ",Android "
                + android.os.Build.VERSION.RELEASE + "," + network + " version:" + buildVersionInfo();
    }

    private String formatMemoryText(long memory) {
        float memoryInMB = memory * 1f / 1024 / 1024;
        return String.format("%.1f MB", memoryInMB);
    }
}
