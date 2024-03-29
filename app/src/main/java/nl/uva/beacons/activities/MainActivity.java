/*
 * Created by Sander Lijbrink.
 * Copyright (c) 2014 Sander Lijbrink
 */

package nl.uva.beacons.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import nl.uva.beacons.BeaconsApplication;
import nl.uva.beacons.interfaces.LoginListener;
import nl.uva.beacons.login.LoginEntry;
import nl.uva.beacons.login.LoginManager;
import nl.uva.beacons.R;
import nl.uva.beacons.fragments.AssistantListFragment;
import nl.uva.beacons.fragments.BeaconListFragment;
import nl.uva.beacons.fragments.HelpFragment;
import nl.uva.beacons.fragments.login.LoginFragment;
import nl.uva.beacons.fragments.NavigationDrawerFragment;
import nl.uva.beacons.fragments.login.SelectCourseFragment;
import nl.uva.beacons.fragments.StudentListFragment;

public class MainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
    LoginListener, SelectCourseFragment.CourseSelectedListener {

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean shouldRestoreFragments = true;
    private BeaconsApplication mApp;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApp = (BeaconsApplication) getApplication();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
            getFragmentManager().findFragmentById(R.id.navigation_drawer);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        shouldRestoreFragments = savedInstanceState == null;
        Log.d(TAG, "onCreate...");
        if (LoginManager.isLoggedIn(this)) {
            Log.d(TAG, "Logged in");
            onLoginSuccess(false, null);
        } else {
            Log.d(TAG, "Not logged in");
            mNavigationDrawerFragment.setDrawerEnabled(false, drawerLayout);
            if (shouldRestoreFragments) {
                replaceFragment(new SelectCourseFragment());
            }
        }
    }

    private void checkBluetoothSupport() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            return;
        }

        if (mApp.shouldRequestBluetooth()) {
            Log.d(TAG, "Should request bluetooth!");
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
    }

    @Override
    public ActionBarDrawerToggle getDrawerToggle() {
        return mNavigationDrawerFragment.getDrawerToggle();
    }

    @Override
    public NavigationDrawerFragment getNavigationDrawerFragment() {
        return mNavigationDrawerFragment;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == SettingsActivity.RESULT_LOG_OUT) {
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
    public void onNavigationDrawerItemSelected(int position) {
    /* Update the main content by replacing fragments */
        if (LoginManager.isLoggedIn(this)) {
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
            }
        }
    }

    public void showLogin() {
        getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null)
            .replace(R.id.container, new LoginFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onLoginSuccess(boolean firstStart, LoginEntry entry) {
        if (shouldRestoreFragments) {
            getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationDrawerFragment.setUp(
            R.id.navigation_drawer,
            (DrawerLayout) findViewById(R.id.drawer_layout));

        if (firstStart || shouldRestoreFragments) {
            onNavigationDrawerItemSelected(NavigationDrawerFragment.PAGE_ASSISTANT_LIST);

            /* Set up the beacon manager */
            ((BeaconsApplication) getApplication()).startTracking();

            checkBluetoothSupport();
        }
    }

    @Override
    public void onLoginFailure() {

    }

    @Override
    public void onCourseSelected() {
        showLogin();
    }
}