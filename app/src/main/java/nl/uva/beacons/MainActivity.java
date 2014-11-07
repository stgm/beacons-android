/*
 * Created by Sander Lijbrink.
 * Copyright (c) 2014 Sander Lijbrink
 */

package nl.uva.beacons;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.utils.L;

import java.util.List;

import nl.uva.beacons.api.BeaconApi;
import nl.uva.beacons.api.BeaconApiClient;
import nl.uva.beacons.fragments.LoginFragment;
import nl.uva.beacons.fragments.NavigationDrawerFragment;
import nl.uva.beacons.fragments.OverviewFragment;

/* Main activity that uses the Estimote SDK for Android (https://github.com/Estimote/Android-SDK) */
public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
    BeaconManager.RangingListener, BeaconManager.MonitoringListener {

  /* TODO: which proximity UUID? */
  private static final String ESTIMOTE_PROXIMITY_UUID = "EBEFD083-70A2-47C8-9837-E7B5634DF524";
  private static final Region ALL_ESTIMOTE_BEACONS = new Region("minprog", ESTIMOTE_PROXIMITY_UUID, null, null);
  private static final int REQUEST_ENABLE_BT = 1234;

  private static final String TAG = MainActivity.class.getSimpleName();
  private BeaconManager mBeaconManager;

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */
  private NavigationDrawerFragment mNavigationDrawerFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.inflateMenu(R.menu.main);
    setSupportActionBar(toolbar);

    mNavigationDrawerFragment = (NavigationDrawerFragment)
        getFragmentManager().findFragmentById(R.id.navigation_drawer);

    /* Set up the navigation drawer. */
    mNavigationDrawerFragment.setUp(
        R.id.navigation_drawer,
        (DrawerLayout) findViewById(R.id.drawer_layout),
        toolbar);

    /* Set up the beacon manager */
    L.enableDebugLogging(true);
    mBeaconManager = new BeaconManager(this);
    mBeaconManager.setRangingListener(this);
    mBeaconManager.setMonitoringListener(this);
  }

  @Override
  public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
    Log.d(TAG, "onBeaconsDiscovered: " + beacons);
  }

  @Override
  public void onEnteredRegion(Region region, List<Beacon> beacons) {
    Log.d(TAG, "onEnteredRegion: " + region.toString());
  }

  @Override
  public void onExitedRegion(Region region) {
    Log.d(TAG, "onExitedRegion: " + region.toString());
  }

  @Override
  protected void onStart() {
    super.onStart();
    /* Check if device supports Bluetooth Low Energy. */
    if (!mBeaconManager.hasBluetooth()) {
      Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
      return;
    }

    /* If Bluetooth is not enabled, let user enable it. */
    if (!mBeaconManager.isBluetoothEnabled()) {
      startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
    } else {
      connectBeaconManager();
    }

  }

  @Override
  protected void onStop() {
    try {
      mBeaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
    } catch (RemoteException e) {
      Log.e(TAG, "Cannot stop but it does not matter now", e);
    }

    super.onStop();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_ENABLE_BT) {
      if (resultCode == Activity.RESULT_OK) {
        connectBeaconManager();
      } else {
        Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onDestroy() {
    mBeaconManager.disconnect();
    super.onDestroy();
  }

  @Override
  public void onNavigationDrawerItemSelected(int position) {
    /* update the main content by replacing fragments */
    FragmentManager fragmentManager = getFragmentManager();
    LoginFragment overviewFragment = new LoginFragment();
    //OverviewFragment overviewFragment = new OverviewFragment();
    //Bundle args = new Bundle();
    //args.putInt("POSITION", position);
    //overviewFragment.setArguments(args);
    fragmentManager.beginTransaction()
        .replace(R.id.container, overviewFragment)
        .commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  private void connectBeaconManager() {
    mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
        try {
          mBeaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
          Log.e(TAG, "Cannot start ranging", e);
        }
      }
    });
  }

}