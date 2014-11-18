package nl.uva.beacons;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

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
    LoginManager.CourseLoginEntry loginEntry = LoginManager.getCurrentEntry(this);
    if (loginEntry != null) {
      if (loginEntry.uuid == null || loginEntry.uuid.isEmpty()) {
        loginEntry.uuid = BeaconTracker.FALLBACK_UUID;
      }
      Identifier mUuid = Identifier.parse(loginEntry.uuid);
      Region region = new Region(BeaconTracker.REGION_ALIAS, mUuid, null, null);
      mRegionBootstrap = new RegionBootstrap(this, region);
    }
  }
}
