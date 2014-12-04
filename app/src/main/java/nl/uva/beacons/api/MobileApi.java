package nl.uva.beacons.api;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by sander on 11/11/14.
 */
public interface MobileApi {
    @GET("/sites.json")
    void getCourses(Callback<Map<String, String>> callback);
}