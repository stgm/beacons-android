package nl.uva.beacons;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Set;

import nl.uva.beacons.activities.MainActivity;
import nl.uva.beacons.tracking.BeaconTracker;

/**
 * Created by sander on 11/12/14.
 */
public class BeaconsApplication extends Application implements BootstrapNotifier {
  private static final String TAG = BeaconsApplication.class.getSimpleName();
  private BackgroundPowerSaver mBackgroundPowerSaver;
  private RegionBootstrap mRegionBootstrap;
  private BeaconManager mBeaconManager;

  @Override
  public void onCreate() {
    super.onCreate();
    mBackgroundPowerSaver = new BackgroundPowerSaver(this);
    mBeaconManager = BeaconManager.getInstanceForApplication(this);
    mBeaconManager.getBeaconParsers().add(BeaconTracker.IBEACON_PARSER);
  }

  @Override
  public void didEnterRegion(Region region) {
    Log.d(TAG, "didEnterRegion: " + region.toString());
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  @Override
  public void didExitRegion(Region region) {

  }

  @Override
  public void didDetermineStateForRegion(int i, Region region) {

  }

  public void initBackgroundScanning() {
    Log.d(TAG, "initBackgroundScanning");
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    Identifier mUuid = Identifier.parse(sp.getString(getString(R.string.pref_key_proximity_uuid), BeaconTracker.FALLBACK_UUID));
    Region region = new Region(BeaconTracker.REGION_ALIAS, mUuid, null , null);
    mRegionBootstrap = new RegionBootstrap(this, region);
  }





}
