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

import nl.uva.beacons.interfaces.BeaconListener;
import nl.uva.beacons.login.LoginEntry;
import nl.uva.beacons.login.LoginManager;
import nl.uva.beacons.api.ApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class BeaconTracker implements MonitorNotifier, RangeNotifier {
    private static final String TAG = BeaconTracker.class.getSimpleName();
    public static final String REGION_ALIAS = "minprog";
    public static final BeaconParser IBEACON_PARSER = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");

    /* Map from UUID -> Nap(Bluetooth Address, BeaconObject)*/
    private HashMap<String, HashMap<String, Beacon>> mRecentBeacons = new HashMap<String, HashMap<String, Beacon>>();

    /* Map from UUID -> (RegionObject) */
    private HashMap<String, Region> mTrackedRegions = new HashMap<String, Region>();

    private BeaconManager mBeaconManager;
    private BeaconListener mBeaconListener;
    private Context mContext;

    public BeaconTracker(BeaconManager beaconManager, Context context) {
        mBeaconManager = beaconManager;
        mContext = context;
    }

    public void start() {
        Log.d(TAG, "start...");
        mBeaconManager.getBeaconParsers().clear();
        mBeaconManager.getBeaconParsers().add(IBEACON_PARSER);
        mBeaconManager.setMonitorNotifier(this);
        mBeaconManager.setRangeNotifier(this);
        initRegions();
    }

    public void stop() {
        Log.d(TAG, "stop...");
        Collection<Region> regions = mTrackedRegions.values();
        for (Region region : regions) {
            try {
                mBeaconManager.stopMonitoringBeaconsInRegion(region);
                mBeaconManager.stopRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mTrackedRegions.clear();
        mRecentBeacons.clear();
    }

    public void removeRegionForLogin(LoginEntry loginEntry) {
        if (mTrackedRegions.containsKey(loginEntry.uuid)) {
            Log.d(TAG, "Removing region for login: " + loginEntry.courseName);
            try {
                mBeaconManager.stopMonitoringBeaconsInRegion(mTrackedRegions.get(loginEntry.uuid));
                mBeaconManager.stopRangingBeaconsInRegion(mTrackedRegions.get(loginEntry.uuid));
                mRecentBeacons.remove(loginEntry.uuid);
                mTrackedRegions.remove(loginEntry.uuid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    public void addRegionForLogin(LoginEntry entry) {
        if (!mTrackedRegions.containsKey(entry.uuid)) {
            Log.d(TAG, "Adding region for login: " + entry.courseName);
            Region beaconRegion = new Region(REGION_ALIAS, Identifier.parse(entry.uuid), null, null);
            mTrackedRegions.put(entry.uuid, beaconRegion);
            try {
                mBeaconManager.startMonitoringBeaconsInRegion(beaconRegion);
                mBeaconManager.startRangingBeaconsInRegion(beaconRegion);
            } catch (RemoteException e) {
                Log.e(TAG, "Remote exception: " + e.getMessage());
            }
        }
    }

    private ArrayList<Beacon> getRecentBeacons() {
        Collection<HashMap<String, Beacon>> recentBeaconList = mRecentBeacons.values();
        ArrayList<Beacon> beacons = new ArrayList<>();
        for (HashMap<String, Beacon> beaconsForUuid : recentBeaconList) {
            Collection<Beacon> beaconsForUuidList = beaconsForUuid.values();
            for (Beacon b : beaconsForUuidList) {
                beacons.add(b);
            }
        }
        return beacons;
    }


    public void initRegions() {
        List<LoginEntry> loginEntries = LoginManager.getCourseLoginEntries(mContext);
        for (LoginEntry entry : loginEntries) {
            addRegionForLogin(entry);
        }
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.i(TAG, "didEnterRegion");
        LoginEntry loginEntry = LoginManager.getEntryForUuid(mContext, region.getId1().toString());
        Log.i(TAG, "Submitting location for uuid = " + loginEntry.uuid + ", " + loginEntry.courseName);
        int major = region.getId2() == null ? 0 : region.getId2().toInt();
        int minor = region.getId3() == null ? 0 : region.getId3().toInt();
        ApiClient.submitLocation(loginEntry, major, minor, submitCallback);
    }

    @Override
    public void didExitRegion(Region region) {
        Log.i(TAG, "didExitRegion: submitting...");
        LoginEntry loginEntry = LoginManager.getEntryForUuid(mContext, region.getId1().toString());

        ApiClient.submitGone(loginEntry, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                Log.d(TAG, "submitGone: success");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "submitGone failure: " + error.getMessage());
            }
        });
        /* Remove the beacons corresponding to this UUID */
        mRecentBeacons.remove(region.getId1().toString().toLowerCase());

        if (mBeaconListener != null) {
            Log.i(TAG, "Sending onBeaconRanged to listener");
            mBeaconListener.onBeaconsRanged(getSortedBeaconList(getRecentBeacons()));
        }
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
                String uuid = beacon.getId1().toString().toLowerCase();
                HashMap<String, Beacon> beaconsWithUuid = mRecentBeacons.get(uuid);
                if (beaconsWithUuid == null) {
                    HashMap<String, Beacon> newMap = new HashMap<>();
                    newMap.put(beacon.getBluetoothAddress(), beacon);
                    mRecentBeacons.put(uuid, newMap);
                } else {
                    beaconsWithUuid.put(beacon.getBluetoothAddress(), beacon);
                }
            }

            List<Beacon> detectedBeacons = getSortedBeaconList(beacons);
            Log.i(TAG, "I just detected " + detectedBeacons.size() + " beacon(s)!");

            if (mBeaconListener != null) {
                Log.i(TAG, "Sending onBeaconRanged to listener");
                mBeaconListener.onBeaconsRanged(getSortedBeaconList(getRecentBeacons()));
            }

            LoginEntry loginEntry = LoginManager.getEntryForUuid(mContext, region.getId1().toString());
            Log.d(TAG, "Submitting location, uuid = " + loginEntry.uuid + ", " + loginEntry.courseName);
            Beacon mostNearbyBeacon = detectedBeacons.get(0);
            int major = mostNearbyBeacon.getId2() != null ? mostNearbyBeacon.getId2().toInt() : region.getId2().toInt();
            int minor = mostNearbyBeacon.getId3() != null ? mostNearbyBeacon.getId3().toInt() : region.getId3().toInt();

            Log.d(TAG, "Sending major, minor: " + major + ", " + minor);
            ApiClient.submitLocation(loginEntry, major, minor, submitCallback);
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

    public void subscribe(BeaconListener beaconListener) {
        mBeaconListener = beaconListener;
        if (mBeaconListener != null && mRecentBeacons.size() > 0) {
            mBeaconListener.onBeaconsRanged(getSortedBeaconList(getRecentBeacons()));
        }
    }

    public void unsubscribe() {
        mBeaconListener = null;
    }

    private Callback<JsonElement> submitCallback = new Callback<JsonElement>() {
        @Override
        public void success(JsonElement jsonElement, Response response) {
            Log.d(TAG, "Location submitted.");
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(TAG, "Error submitting location: " + error.getMessage());
        }
    };

}
