package nl.uva.beacons.api;
import retrofit.RestAdapter;

/**
 * Created by sander on 11/6/14.
 */
public class BeaconApiClient {
  private static final String BASE_URL = "http://apps.mprog.nl/";
  private static BeaconApi api = new RestAdapter.Builder().setEndpoint(BASE_URL).build().create(BeaconApi.class);

  private BeaconApiClient() {}

  public static BeaconApi get() {
    return api;
  }
}
