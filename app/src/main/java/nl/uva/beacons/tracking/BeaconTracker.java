package nl.uva.beacons.tracking;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.JsonElement;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import nl.uva.beacons.LoginManager;
import nl.uva.beacons.api.BeaconApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class BeaconTracker implements MonitorNotifier, RangeNotifier {
  private static final String TAG = BeaconTracker.class.getSimpleName();
  public static final String REGION_ALIAS = "minprog";
  public static final long FOREGROUND_SCAN_PERIOD = 5000;
  public static final String FALLBACK_UUID = "EBEFD083-70A2-47C8-9837-E7B5634DF524";
  public static final BeaconParser IBEACON_PARSER = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");

  private HashMap<String, Beacon> mRecentBeacons = new HashMap<String, Beacon>();
  private String mUserToken;
  private Identifier mUuid;
  private BeaconManager mBeaconManager;
  private BeaconListener mBeaconListener;

  public BeaconTracker(BeaconManager beaconManager, Context context) {
    mBeaconManager = beaconManager;

    LoginManager.CourseLoginEntry loginEntry = LoginManager.getCurrentEntry(context);
    mUserToken = loginEntry.userToken;
    mUuid = Identifier.parse(loginEntry.uuid);
    Log.d(TAG, "uuid = " + mUuid);
  }

  public void start() {
    Log.d(TAG, "start...");
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

  public void stop() {
    Region beaconRegion = new Region(REGION_ALIAS, mUuid, null, null);
    try {
      mBeaconManager.stopMonitoringBeaconsInRegion(beaconRegion);
      mBeaconManager.stopRangingBeaconsInRegion(beaconRegion);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void didEnterRegion(Region region) {
    Log.i(TAG, "I just entered the minprog region.");
  }

  @Override
  public void didExitRegion(Region region) {
    Log.i(TAG, "didExitRegion: submitting...");
    BeaconApiClient.get().submitGone(mUserToken, new Callback<JsonElement>() {
      @Override
      public void success(JsonElement jsonElement, Response response) {
        Log.d(TAG, "Submit didExitRegion: success");
      }

      @Override
      public void failure(RetrofitError error) {

      }
    });
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
        mRecentBeacons.put(beacon.getBluetoothAddress(), beacon);
      }

      List<Beacon> detectedBeacons = getSortedBeaconList(beacons);
      Log.i(TAG, "I just detected " + detectedBeacons.size() + " beacons!");

      if (mBeaconListener != null) {
        Log.i(TAG, "Sending onBeaconRanged to listener");
        mBeaconListener.onBeaconsRanged(getSortedBeaconList(mRecentBeacons.values()));
      }

      Log.d(TAG, "Submitting location...");
      Beacon mostNearbyBeacon = detectedBeacons.get(0);
      BeaconApiClient.get().submitLocation(mUserToken, mostNearbyBeacon.getId2().toInt(), mostNearbyBeacon.getId3().toInt(),
          new Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
              Log.d(TAG, "Location submitted.");
            }

            @Override
            public void failure(RetrofitError error) {
              Log.d(TAG, "Error submitting location: " + error.getMessage());
            }
          });
    }
  }

  private List<Beacon> getSortedBeaconList(Collection<Beacon> beaconCollection) {
    List<Beacon> beacons = new ArrayList<Beacon>(beaconCollection);
    Collections.sort(beacons, new Comparator<Beacon>() {
      @Override
      public int compare(Beacon beacon, Beacon beacon2) {
        return Double.compare(beacon.getDistance(), beacon2.getDistance());
      }
    });
    return beacons;
  }

  public void setBeaconListener(BeaconListener beaconListener) {
    mBeaconListener = beaconListener;
    if (mBeaconListener != null && mRecentBeacons.size() > 0) {
      mBeaconListener.onBeaconsRanged(getSortedBeaconList(mRecentBeacons.values()));
    }
  }

  public interface BeaconListener {
    void onBeaconsRanged(List<Beacon> detectedBeacons);
  }
}
