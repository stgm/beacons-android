package nl.uva.beacons.api;

import android.content.Context;

import java.util.Map;

import nl.uva.beacons.LoginManager;
import retrofit.Callback;
import retrofit.RestAdapter;

/**
 * Created by sander on 11/6/14.
 */
public class BeaconApiClient {
  private static final String MOBILE_URL = "http://mobile.mprog.nl/";
  private static final MobileApi mobileApi = new RestAdapter.Builder()
      .setEndpoint(MOBILE_URL).build().create(MobileApi.class);

  private static final String FALLBACK_URL = "http://prog2.mprog.nl/";
  private static BeaconApi api;

  private BeaconApiClient() {
  }

  public static void init(Context context) {
    String endpointUrl = LoginManager.getCurrentEndpointUrl(context);
    if (endpointUrl == null || endpointUrl.isEmpty()) {
      endpointUrl = FALLBACK_URL;
    }
    api = new RestAdapter.Builder().setEndpoint(endpointUrl).build().create(BeaconApi.class);
  }

  public static BeaconApi get() {
    return api;
  }

  public static void getCourses(Callback<Map<String, String>> callback) {
    mobileApi.getCourses(callback);
  }

}
