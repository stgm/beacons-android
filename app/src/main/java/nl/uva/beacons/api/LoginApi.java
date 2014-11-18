package nl.uva.beacons.api;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by sander on 11/18/14.
 */
public interface LoginApi {
  /* Response a map of 'role' : student/assistant */
  @FormUrlEncoded
  @POST("/tracking/register")
  void registerUser(@Field("code") String code, Callback<Map<String, String>> callback);

  /* returns one of:
   * role: 'assistant'
   * role: 'student'
   */
  @FormUrlEncoded
  @POST("/tracking/tokenized/identify")
  void identifyUser(@Field("token") String token, Callback<Map<String, String>> callback);
}
