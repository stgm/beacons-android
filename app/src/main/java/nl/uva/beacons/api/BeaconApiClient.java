package nl.uva.beacons.api;
import android.content.Context;
import android.content.SharedPreferences;

import nl.uva.beacons.MainActivity;
import nl.uva.beacons.R;
import retrofit.RestAdapter;

/**
 * Created by sander on 11/6/14.
 */
public class BeaconApiClient {
  private static BeaconApi api;
  private static final String FALLBACK_URL = "http://apps.mprog.nl/";

  public static void init(Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
    String endpointUrl = sharedPreferences.getString(context.getString(R.string.pref_key_endpoint_url), FALLBACK_URL);
    api = new RestAdapter.Builder().setEndpoint(endpointUrl).build().create(BeaconApi.class);
  }

  private BeaconApiClient() {}
  public static BeaconApi get() {
    return api;
  }
}
