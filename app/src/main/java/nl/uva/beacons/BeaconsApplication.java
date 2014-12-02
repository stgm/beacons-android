package nl.uva.beacons;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.List;

import nl.uva.beacons.activities.MainActivity;
import nl.uva.beacons.tracking.BeaconTracker;

/**
 * Created by sander on 11/12/14.
 */
public class BeaconsApplication extends Application implements BeaconConsumer {
  private static final String TAG = BeaconsApplication.class.getSimpleName();
  private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
  private BeaconTracker mBeaconTracker;
  private boolean mShouldRequestBluetooth = true;
  private boolean mStarted = false;

  @Override
  public void onCreate() {
    super.onCreate();
    mBeaconManager = BeaconManager.getInstanceForApplication(this);
    mBeaconManager.getBeaconParsers().add(BeaconTracker.IBEACON_PARSER);
  }

  private void initDefaultSettings() {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    long scanPeriod = Long.parseLong(sp.getString(getString(R.string.pref_title_scan_interval), "5000"));
    Log.d(TAG, "initDefaultSettings, scanPeriod = " + scanPeriod);
    setScanPeriod(scanPeriod);
  }

  public void setScanPeriod(long period) {
    Log.d(TAG, "Set scan period: " + period);
    mBeaconManager.setBackgroundScanPeriod(period);
    mBeaconManager.setForegroundScanPeriod(period);
    try {
      mBeaconManager.updateScanPeriods();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  public void startTracking() {
    if(!mBeaconManager.isBound(this)) {
      mBeaconManager.bind(this);
    } else {
      mBeaconTracker.start();
    }
  }

  public void stopTracking() {
    if(mBeaconTracker != null) {
      mBeaconTracker.stop();
    }
  }

  public BeaconTracker getBeaconTracker() {
    return mBeaconTracker;
  }

  @Override
  public void onBeaconServiceConnect() {
    if(!mStarted) {
      mStarted = true;
      initDefaultSettings();
      mBeaconTracker = new BeaconTracker(mBeaconManager, this);
    }
    mBeaconTracker.start();
  }

  public boolean shouldRequestBluetooth() {
    if(mShouldRequestBluetooth) {
      mShouldRequestBluetooth = false;
      return true;
    } else {
      return false;
    }
  }

}
