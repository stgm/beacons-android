package nl.uva.beacons.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import nl.uva.beacons.MainActivity;
import nl.uva.beacons.R;

/**
 * Created by sander on 11/7/14.
 */
public class SettingsFragment extends PreferenceFragment {
  public static final int RESULT_LOG_OUT = 0;

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getPreferenceScreen().setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object o) {
        if(preference.getKey().equals(getString(R.string.pref_title_checkbox_bg_scanning))) {

        } else if(preference.getKey().equals(getString(R.string.pref_title_scan_interval))) {

        }
        return true;
      }
    });
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);

    findPreference(getString(R.string.pref_title_log_out)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        SharedPreferences sp = getActivity().getSharedPreferences(MainActivity.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
        sp.edit().clear().commit();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().setResult(RESULT_LOG_OUT);
        getActivity().startActivity(intent);
        return true;
      }
    });
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.clear();
    inflater.inflate(R.menu.global, menu);
  }
}
