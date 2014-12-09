package nl.uva.beacons.interfaces;

import org.altbeacon.beacon.Beacon;

import java.util.List;

/**
 * Created by sander on 12/9/14.
 */
public interface BeaconListener {
    void onBeaconsRanged(List<Beacon> detectedBeacons);
}