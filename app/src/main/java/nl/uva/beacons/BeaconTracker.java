package nl.uva.beacons;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;

import nl.uva.beacons.api.BeaconApiClient;

/**
 * Created by sander on 11/7/14.
 */
public class BeaconTracker implements MonitorNotifier, RangeNotifier {
  private static final String TAG = BeaconTracker.class.getSimpleName();
  private static final String REGION_ALIAS = "minprog";
  private static final String FALLBACK_UUID = "EBEFD083-70A2-47C8-9837-E7B5634DF524";
  private static final BeaconParser IBEACON_PARSER = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");

  private HashMap<String, Beacon> mDetectedBeacons = new HashMap<String, Beacon>();
  private String mUserToken;
  private Identifier mUuid;
  private BeaconManager mBeaconManager;

  public BeaconTracker(BeaconManager beaconManager, Context context) {
    mBeaconManager = beaconManager;

    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    mUserToken = sp.getString(context.getString(R.string.pref_key_user_token), "");
    mUuid = Identifier.parse(sp.getString(context.getString(R.string.pref_key_proximity_uuid), FALLBACK_UUID));
  }

  public void startTracking() {
    mBeaconManager.setForegroundScanPeriod(5000);
    mBeaconManager.getBeaconParsers().remove(IBEACON_PARSER);
    mBeaconManager.getBeaconParsers().add(IBEACON_PARSER);
    mBeaconManager.setMonitorNotifier(this);
    mBeaconManager.setRangeNotifier(this);

    Region beaconRegion = new Region(REGION_ALIAS, mUuid, null, null);

    try {
      mBeaconManager.startMonitoringBeaconsInRegion(beaconRegion);
    } catch (RemoteException e) {
      Log.e(TAG, "Remote exception: " + e.getMessage());
    }

    try {
      mBeaconManager.startRangingBeaconsInRegion(beaconRegion);
    } catch (RemoteException e) {
      Log.e(TAG, "Remote exception: " + e.getMessage());
    }
  }

  @Override
  public void didEnterRegion(Region region) {
    Log.i(TAG, "I just entered the minprog region.");

  }

  @Override
  public void didExitRegion(Region region) {
    Log.i(TAG, "didExitRegion: submitting...");
    BeaconApiClient.get().submitGone(mUserToken);
  }

  @Override
  public void didDetermineStateForRegion(int state, Region region) {
    Log.i(TAG, "I have just switched from seeing/not seeing beacons, state: " + state);
  }

  @Override
  public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
    if (beacons.size() > 0) {
      /* id1 is Proximity UUID
       * id2 is major
       * id3 is minor
       */
      for (Beacon beacon : beacons) {
        mDetectedBeacons.put(beacon.getId1().toString() + "-" + beacon.getId2().toString() + "-" +  beacon.getId3().toString(), beacon);
      }
      Log.i(TAG, "I see " + mDetectedBeacons.size() + " beacons!");

    }
  }
}
