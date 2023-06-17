package com.example.keepfresh;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import android.os.Bundle;

import android.content.SharedPreferences;

import com.example.keepfresh.R;

//알람 설정 페이지 display
public class setting_alarm extends PreferenceFragment {
    SharedPreferences prefs;

    ListPreference datePreference;
    ListPreference timePreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.alert_preference);
        datePreference = (ListPreference) findPreference("alert_date");
        timePreference = (ListPreference) findPreference("alert_time");

        prefs= PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!prefs.getString("alert_date", "3일 전").equals("")){
            datePreference.setSummary(prefs.getString("alert_date", "3일 전"));
        }
        if(!prefs.getString("alert_time", "오전 9시").equals("")){
            timePreference.setSummary(prefs.getString("alert_time", "오전 9시"));
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("alert_date")){
                datePreference.setSummary(prefs.getString("alert_date", "3일 전"));
            }
            if(key.equals("alert_time")){
                timePreference.setSummary(prefs.getString("alert_time", "오전 9시"));
            }
        }
    };
}