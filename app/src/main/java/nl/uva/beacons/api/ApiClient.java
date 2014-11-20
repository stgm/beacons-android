package nl.uva.beacons.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uva.beacons.LoginEntry;
import nl.uva.beacons.LoginManager;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by sander on 11/6/14.
 */
public class ApiClient {
  private static final String FALLBACK_URL = "https://prog2.mprog.nl/";
  private static final String MOBILE_URL = "http://mobile.mprog.nl/";
  private static final MobileApi mobileApi = new RestAdapter.Builder().setClient(new OkClient())
      .setEndpoint(MOBILE_URL).build().create(MobileApi.class);

  private static HashMap<LoginEntry, BeaconApi> BEACON_APIS = new HashMap<LoginEntry, BeaconApi>();

  private ApiClient() {
  }

  public static void init(Context context) {
    ArrayList<LoginEntry> loginEntries = LoginManager.getCourseLoginEntries(context);
    RestAdapter.Builder builder = new RestAdapter.Builder().setClient(new OkClient());
    BEACON_APIS.clear();
    for(LoginEntry loginEntry : loginEntries) {
      BEACON_APIS.put(loginEntry, builder.setEndpoint(loginEntry.url).build().create(BeaconApi.class));
    }
  }

  public static void registerUser(String endPointUrl, String code, Callback<Map<String, String>> callback) {
    LoginApi api = new RestAdapter.Builder().setClient(new OkClient()).setEndpoint(endPointUrl).build().create(LoginApi.class);
    api.registerUser(code, callback);
  }

  public static void identifyUser(String endPointUrl, String token, Callback<Map<String, String>> callback) {
    LoginApi api = new RestAdapter.Builder().setClient(new OkClient()).setEndpoint(endPointUrl).build().create(LoginApi.class);
    api.identifyUser(token, callback);
  }

  public static void getStudentList(Callback<List<Map<String, String>>> callback) {
    for(Map.Entry<LoginEntry, BeaconApi> api : BEACON_APIS.entrySet()) {
      api.getValue().getStudentList(api.getKey().userToken, callback);
    }
  }

  public static void getAssistantList(Callback<List<Map<String, String>>> callback) {
    for(Map.Entry<LoginEntry, BeaconApi> api : BEACON_APIS.entrySet()) {
      api.getValue().getAssistantList(api.getKey().userToken, callback);
    }
  }

  public static void submitLocation(LoginEntry loginEntry, int major, int minor, Callback<JsonElement> callback) {
    BEACON_APIS.get(loginEntry).submitLocation(loginEntry.userToken, major, minor, callback);
  }

  public static void submitGone(LoginEntry loginEntry, Callback<JsonElement> callback) {
    BEACON_APIS.get(loginEntry).submitGone(loginEntry.userToken, callback);
  }

  public static void askHelp(LoginEntry loginEntry, boolean help, String helpQuestion, Callback<JsonElement> callback) {
    BEACON_APIS.get(loginEntry).askHelp(loginEntry.userToken, help, helpQuestion, callback);
  }

  public static void clearHelp(String id, Callback<JsonElement> callback) {
    for(Map.Entry<LoginEntry, BeaconApi> api : BEACON_APIS.entrySet()) {
      api.getValue().clearHelp(api.getKey().userToken, id, callback);
    }
  }

  public static void getCourses(Callback<Map<String, String>> callback) {
    mobileApi.getCourses(callback);
  }

}
