package nl.uva.beacons.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;

import nl.uva.beacons.BeaconsApplication;
import nl.uva.beacons.R;
import nl.uva.beacons.api.ApiClient;
import nl.uva.beacons.fragments.LoginFragment;
import nl.uva.beacons.fragments.LoginManagementFragment;
import nl.uva.beacons.fragments.SelectCourseFragment;
import nl.uva.beacons.fragments.SettingsFragment;

/**
 * Created by sander on 11/8/14.
 */
public class SettingsActivity extends BaseActivity implements SelectCourseFragment.CourseSelectedListener, LoginFragment.LoginListener {
  public static final int RESULT_LOG_OUT = 321;
  public static final String KEY_MANAGE_LOGIN = "key_manage_login";

  /* Stored in SharedPreferences course that the user is logged in,
   * saved as key-value pairs:
   * "key_course_uuid_set" -> set of UUIDs
   * uuid -> identifier
   * uuid+"url" => url
   */

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    if(getIntent() != null && getIntent().getBooleanExtra(KEY_MANAGE_LOGIN, false)) {
      replaceFragment(new LoginManagementFragment());
    } else if(savedInstanceState == null) {
      replaceFragment(new SettingsFragment());
    }
  }

  @Override
  public void onBackPressed() {
    if(getFragmentManager().getBackStackEntryCount() > 0) {
      getFragmentManager().popBackStack();
    } else {
      super.onBackPressed();
    }
  }

  public void logOut() {
    ((BeaconsApplication)getApplication()).stopTracking();
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    sp.edit().clear().apply();
    setResult(RESULT_LOG_OUT);
    finish();
  }

  @Override
  public void onCourseSelected() {
    getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
        .replace(R.id.container, new LoginFragment()).commit();
  }

  @Override
  public void onLoginSuccess(boolean startUp) {
    getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    /* Re-initialize the api client for the new URL
     * Refresh the beacon tracker
     */
    ApiClient.init(this);
    ((BeaconsApplication) getApplication()).getBeaconTracker().stop();
    ((BeaconsApplication) getApplication()).getBeaconTracker().start();
    replaceFragment(new LoginManagementFragment());
  }

  @Override
  public void onLoginFailure() {

  }

  @Override
  public ActionBarDrawerToggle getDrawerToggle() {
    return null;
  }
}
