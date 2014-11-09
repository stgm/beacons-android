package nl.uva.beacons.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import nl.uva.beacons.R;
import retrofit.RestAdapter;

/**
 * Created by sander on 11/6/14.
 */
public class BeaconApiClient {
  private static final String FALLBACK_URL = "http://apps.mprog.nl/";
  private static BeaconApi api;

  private BeaconApiClient() {
  }

  public static void init(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String endpointUrl = sharedPreferences.getString(context.getString(R.string.pref_key_endpoint_url), FALLBACK_URL);
    api = new RestAdapter.Builder().setEndpoint(endpointUrl).build().create(BeaconApi.class);
  }

  public static BeaconApi get() {
    return api;
  }

  public static final String ATTRIBUTE_NAME = "name";
  public static final String ATTRIBUTE_LOC_A = "loca";
  public static final String ATTRIBUTE_LOC_B = "locb";
  public static final String ATTRIBUTE_HELP = "help";
  public static final String ATTRIBUTE_UPDATED = "updated";

}
