package nl.uva.beacons.api;

import android.content.Context;

import com.google.gson.JsonElement;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uva.beacons.login.LoginEntry;
import nl.uva.beacons.login.LoginManager;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by sander on 11/6/14.
 */
public class ApiClient {
    private static final String MOBILE_URL = "http://mobile.mprog.nl/";
    private static final RestAdapter.Builder apiBuilder = new RestAdapter.Builder().setClient(new OkClient());
    private static final MobileApi mobileApi = apiBuilder.setEndpoint(MOBILE_URL).build().create(MobileApi.class);
    private static final HashMap<LoginEntry, BeaconApi> BEACON_APIS = new HashMap<LoginEntry, BeaconApi>();

    private ApiClient() {
    }

    /* Generate the API interface for each login entry. */
    public static void initEndpoints(Context context) {
        ArrayList<LoginEntry> loginEntries = LoginManager.getCourseLoginEntries(context);
        BEACON_APIS.clear();
        for (LoginEntry loginEntry : loginEntries) {
            addApiForLogin(loginEntry);
        }
    }

    public static void addApiForLogin(LoginEntry entry) {
        BEACON_APIS.put(entry, buildBeaconApi(entry.url));
    }

    public static void removeApiForLogin(LoginEntry entry) {
        BEACON_APIS.remove(entry);
    }

    private static LoginApi buildLoginApi(String endPointUrl) {
        return apiBuilder.setEndpoint(endPointUrl).build().create(LoginApi.class);
    }

    private static BeaconApi buildBeaconApi(String endPointUrl) {
        return apiBuilder.setEndpoint(endPointUrl).build().create(BeaconApi.class);
    }

    public static void getStudentList(final Callback<AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>>> callback) {
        for (final Map.Entry<LoginEntry, BeaconApi> api : BEACON_APIS.entrySet()) {
            api.getValue().getStudentList(api.getKey().userToken, new Callback<List<Map<String, String>>>() {
                @Override
                public void success(List<Map<String, String>> maps, Response response) {
                    /* Save corresponding source (login-entry) for each result */
                    AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>> markedResult =
                        new AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>>(api.getKey(), maps);

                    callback.success(markedResult, response);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.failure(error);
                }
            });
        }
    }

    public static void getAssistantList(final Callback<AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>>> callback) {
        for (final Map.Entry<LoginEntry, BeaconApi> api : BEACON_APIS.entrySet()) {
            api.getValue().getAssistantList(api.getKey().userToken, new Callback<List<Map<String, String>>>() {
                @Override
                public void success(List<Map<String, String>> maps, Response response) {
                   /* Save corresponding source (login-entry) for each result */
                    AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>> markedResult =
                        new AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>>(api.getKey(), maps);
                    callback.success(markedResult, response);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.failure(error);
                }
            });
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
        for (Map.Entry<LoginEntry, BeaconApi> api : BEACON_APIS.entrySet()) {
            api.getValue().clearHelp(api.getKey().userToken, id, callback);
        }
    }

    /* Login API */
    public static void registerUser(String endPointUrl, String code, Callback<Map<String, String>> callback) {
        LoginApi api = buildLoginApi(endPointUrl);
        api.registerUser(code, callback);
    }

    public static void identifyUser(String endPointUrl, String token, Callback<Map<String, String>> callback) {
        LoginApi api = buildLoginApi(endPointUrl);
        api.identifyUser(token, callback);
    }

    public static void getCourses(Callback<Map<String, String>> callback) {
        mobileApi.getCourses(callback);
    }

}
