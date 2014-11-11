package nl.uva.beacons.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import nl.uva.beacons.R;

/**
 * Created by sander on 11/7/14.
 */
public class SettingsFragment extends PreferenceFragment {
  public static final int RESULT_LOG_OUT = 321;

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getPreferenceScreen().setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals(getString(R.string.pref_title_checkbox_bg_scanning))) {
          // todo
        } else if (preference.getKey().equals(getString(R.string.pref_title_scan_interval))) {
          // todo
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.edit().clear().apply();
        getActivity().setResult(RESULT_LOG_OUT);
        getActivity().finish();
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
