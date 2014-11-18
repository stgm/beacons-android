package nl.uva.beacons.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private static final HashMap<String, BeaconApi> BEACON_APIS = new HashMap<String, BeaconApi>();

  private ApiClient() {
  }

  public static void init(Context context) {
    ArrayList<LoginManager.CourseLoginEntry> loginEntries = LoginManager.getCourseLoginEntries(context);
    RestAdapter.Builder builder = new RestAdapter.Builder().setClient(new OkClient());
    for(LoginManager.CourseLoginEntry loginEntry : loginEntries) {
      BEACON_APIS.put(loginEntry.uuid, builder.setEndpoint(loginEntry.url).build().create(BeaconApi.class));
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

  public static void getStudentList(String token, Callback<List<Map<String, String>>> callback) {
    for(Map.Entry<String, BeaconApi> api : BEACON_APIS.entrySet()) {
      api.getValue().getStudentList(token, callback);
    }
  }

  public static void getAssistantList(String token, Callback<List<Map<String, String>>> callback) {
    for(Map.Entry<String, BeaconApi> api : BEACON_APIS.entrySet()) {
      api.getValue().getAssistantList(token, callback);
    }
  }

  public static void submitLocation(String token, int major, int minor, Callback<JsonElement> callback) {
    for(Map.Entry<String, BeaconApi> api : BEACON_APIS.entrySet()) {
      api.getValue().submitLocation(token, major, minor, callback);
    }
  }

  public static void submitGone(String token, Callback<JsonElement> callback) {
    for(Map.Entry<String, BeaconApi> api : BEACON_APIS.entrySet()) {
      api.getValue().submitGone(token, callback);
    }
  }

  public static void askHelp(String token, boolean help, String helpQuestion, Callback<JsonElement> callback) {
    for(Map.Entry<String, BeaconApi> api : BEACON_APIS.entrySet()) {
      api.getValue().askHelp(token, help, helpQuestion, callback);
    }
  }

  public static void clearHelp(String token, String id, Callback<JsonElement> callback) {
    for(Map.Entry<String, BeaconApi> api : BEACON_APIS.entrySet()) {
      api.getValue().clearHelp(token, id, callback);
    }
  }

  public static void getCourses(Callback<Map<String, String>> callback) {
    mobileApi.getCourses(callback);
  }

}
