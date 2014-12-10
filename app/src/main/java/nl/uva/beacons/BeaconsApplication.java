package nl.uva.beacons;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;

import nl.uva.beacons.api.ApiClient;
import nl.uva.beacons.tracking.BeaconTracker;

/**
 * Created by sander on 11/12/14.
 */
public class BeaconsApplication extends Application implements BeaconConsumer {
    private final BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
    private boolean mShouldRequestBluetooth = true;

    @Override
    public void onCreate() {
        super.onCreate();
        ApiClient.initEndpoints(this);
    }

    public void startTracking() {
        BeaconTracker beaconTracker = BeaconTracker.getInstance();
        if (beaconTracker != null && !beaconTracker.isStarted()) {
            BeaconTracker.getInstance().start();
        } else if (!mBeaconManager.isBound(this)) {
            mBeaconManager.bind(this);
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        /* Initialize the global (singleton) instance of the BeaconTracker with the context */
        BeaconTracker.init(mBeaconManager, this);
    }

    public boolean shouldRequestBluetooth() {
        if (mShouldRequestBluetooth) {
            mShouldRequestBluetooth = false;
            return true;
        } else {
            return false;
        }
    }

}
