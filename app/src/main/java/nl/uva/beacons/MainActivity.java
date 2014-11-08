/*
 * Created by Sander Lijbrink.
 * Copyright (c) 2014 Sander Lijbrink
 */

package nl.uva.beacons;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;

import java.util.prefs.PreferenceChangeEvent;

import nl.uva.beacons.api.BeaconApiClient;
import nl.uva.beacons.fragments.AssistantListFragment;
import nl.uva.beacons.fragments.HelpFragment;
import nl.uva.beacons.fragments.LoginFragment;
import nl.uva.beacons.fragments.NavigationDrawerFragment;
import nl.uva.beacons.fragments.SettingsFragment;
import nl.uva.beacons.fragments.StudentListFragment;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
    BeaconConsumer, LoginFragment.LoginListener {

  public static final String KEY_SHARED_PREFS = "key_shared_prefs";

  private static final int REQUEST_ENABLE_BT = 1234;
  private static final String TAG = MainActivity.class.getSimpleName();
  private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
  private BeaconTracker mBeaconTracker;

  private static final int PAGE_ASSISTANT_LIST = 0;
  private static final int PAGE_STUDENT_LIST = 1;
  private static final int PAGE_HELP = 2;
  private static final int PAGE_QUESTIONS = 3;

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */
  private NavigationDrawerFragment mNavigationDrawerFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mNavigationDrawerFragment = (NavigationDrawerFragment)
       getFragmentManager().findFragmentById(R.id.navigation_drawer);

    DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    Log.d(TAG, "onCreate...");
    if (isLoggedIn()) {
      Log.d(TAG, "Logged in");
      onLoginSuccess();
    } else {
      Log.d(TAG, "Not logged in");
      mNavigationDrawerFragment.setDrawerEnabled(drawerLayout, false);
      addFragment(LoginFragment.newInstance(this));
    }
  }

  private void checkBluetoothSupport() {
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
      Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
      return;
    }

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter != null) {
      if (!mBluetoothAdapter.isEnabled()) {
        /* Bluetooth is not enabled
         * Ask user to enable bluetooth
         */
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
      }
    }
  }

  public void setDrawerBackButton(boolean enabled) {
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
  }

  @Override
  protected void onStart() {
    super.onStart();
    checkBluetoothSupport();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_ENABLE_BT) {
      if (resultCode != Activity.RESULT_OK) {
        Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
      }
    } else if(resultCode == SettingsFragment.RESULT_LOG_OUT) {
      recreate();
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onDestroy() {
    mBeaconManager.unbind(this);
    super.onDestroy();
  }

  @Override
  public void onNavigationDrawerItemSelected(int position) {
    /* Update the main content by replacing fragments */
    if(isLoggedIn()) {
      Log.d(TAG, "Selected drawer item at position: " + position);
      switch (position) {
        case PAGE_ASSISTANT_LIST:
          replaceFragment(new AssistantListFragment());
          break;
        case PAGE_STUDENT_LIST:
          replaceFragment(new StudentListFragment());
          break;
        case PAGE_HELP:
          replaceFragment(new HelpFragment());
          break;
      }
    }
  }

  private boolean isLoggedIn() {
    return getSharedPreferences(KEY_SHARED_PREFS, Context.MODE_PRIVATE).contains(getString(R.string.pref_key_user_token));
  }

  private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction()
        .replace(R.id.container, fragment)
        .commit();
  }

  private void addFragment(Fragment fragment) {
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction()
        .add(R.id.container, fragment)
        .commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.d(TAG, "Options item clicked");

    if (item.getItemId() == R.id.action_settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivityForResult(intent, 0);
      return true;
    }
    return false;
  }

  @Override
  public void onBackPressed() {
    if (getFragmentManager().getBackStackEntryCount() == 0) {
      super.onBackPressed();
    } else getFragmentManager().popBackStack();
  }

  @Override
  public void onBeaconServiceConnect() {
    mBeaconTracker = new BeaconTracker(mBeaconManager, this);
    mBeaconTracker.startTracking();
  }

  @Override
  public void onLoginSuccess() {
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    BeaconApiClient.init(this);

    mNavigationDrawerFragment.setUp(
        R.id.navigation_drawer,
        (DrawerLayout) findViewById(R.id.drawer_layout));

    /* Set up the beacon manager */
    mBeaconManager.bind(this);

    onNavigationDrawerItemSelected(0);
  }

  @Override
  public void onLoginFailure() {

  }

}