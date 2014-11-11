/*
 * Created by Sander Lijbrink.
 * Copyright (c) 2014 Sander Lijbrink
 */

package nl.uva.beacons.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;

import nl.uva.beacons.R;
import nl.uva.beacons.api.BeaconApiClient;
import nl.uva.beacons.fragments.AssistantListFragment;
import nl.uva.beacons.fragments.BeaconListFragment;
import nl.uva.beacons.fragments.HelpFragment;
import nl.uva.beacons.fragments.LoginFragment;
import nl.uva.beacons.fragments.NavigationDrawerFragment;
import nl.uva.beacons.fragments.QuestionsFragment;
import nl.uva.beacons.fragments.SelectCourseFragment;
import nl.uva.beacons.fragments.SettingsFragment;
import nl.uva.beacons.fragments.StudentListFragment;
import nl.uva.beacons.tracking.BeaconTracker;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
    BeaconConsumer, LoginFragment.LoginListener {

  private static final int REQUEST_ENABLE_BT = 1234;
  private static final String TAG = MainActivity.class.getSimpleName();
  private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
  private BeaconTracker mBeaconTracker;
  private boolean shouldRestoreFragments = true;

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
    mBeaconTracker= new BeaconTracker(mBeaconManager, this);

    shouldRestoreFragments = savedInstanceState == null;
    Log.d(TAG, "onCreate...");
    if (isLoggedIn()) {
      Log.d(TAG, "Logged in");
      onLoginSuccess();
    } else {
      Log.d(TAG, "Not logged in");
      mNavigationDrawerFragment.setDrawerEnabled(drawerLayout, false);
      if(shouldRestoreFragments) {
        replaceFragment(new SelectCourseFragment());
      }
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
    } else if (resultCode == SettingsFragment.RESULT_LOG_OUT) {
      Log.d(TAG, "Restarting mainActivity...");
      restartActivity();
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void restartActivity() {
    Intent intent = getIntent();
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    finish();
    overridePendingTransition(0, 0);

    startActivity(intent);
    overridePendingTransition(0, 0);
  }

  @Override
  protected void onDestroy() {
    mBeaconManager.unbind(this);
    super.onDestroy();
  }

  @Override
  public void onNavigationDrawerItemSelected(int position) {
    /* Update the main content by replacing fragments */
    if (isLoggedIn()) {
      Log.d(TAG, "Selected drawer item at position: " + position);
      switch (position) {
        case NavigationDrawerFragment.PAGE_ASSISTANT_LIST:
          replaceFragment(new AssistantListFragment());
          break;
        case NavigationDrawerFragment.PAGE_STUDENT_LIST:
          replaceFragment(new StudentListFragment());
          break;
        case NavigationDrawerFragment.PAGE_HELP:
          replaceFragment(new HelpFragment());
          break;
        case NavigationDrawerFragment.PAGE_SCAN_BEACONS:
          replaceFragment(new BeaconListFragment());
          break;
        case NavigationDrawerFragment.PAGE_QUESTIONS:
          replaceFragment(new QuestionsFragment());
          break;
      }
    }
  }

  private boolean isLoggedIn() {
    return PreferenceManager.getDefaultSharedPreferences(this).contains(getString(R.string.pref_key_user_token));
  }

  private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getFragmentManager();

    String fragmentClassNameAsTag = fragment.getClass().getName();
    Fragment f = fragmentManager.findFragmentByTag(fragmentClassNameAsTag);
    if (f != null && f.isAdded()) {
      /* This fragment is already active, do nothing */
      return;
    }
    fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .replace(R.id.container, fragment, fragmentClassNameAsTag)
        .commit();
  }

  private void addFragment(Fragment fragment) {
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .add(R.id.container, fragment, fragment.getClass().getName())
        .commit();
  }

  public void showLogin() {
    getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
        .replace(R.id.container, LoginFragment.newInstance(this)).commit();
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
    mBeaconTracker.startTracking();
  }

  @Override
  public void onLoginSuccess() {
    if(shouldRestoreFragments) {
      getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    BeaconApiClient.init(this);

    mNavigationDrawerFragment.setUp(
        R.id.navigation_drawer,
        (DrawerLayout) findViewById(R.id.drawer_layout));

    /* Set up the beacon manager */
    mBeaconManager.bind(this);

    if(shouldRestoreFragments) {
      onNavigationDrawerItemSelected(0);
    }
  }

  @Override
  public void onLoginFailure() {

  }

  public BeaconTracker getBeaconTracker() {
    return mBeaconTracker;
  }
}