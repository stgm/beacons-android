package nl.uva.beacons.api;
import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by sander on 11/6/14.
 */
public interface BeaconApi {
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

  /* Response list or map?:
 * [["Martijn Stegeman",1,2,true,"2014-10-09T17:21:29.954+02:00"]]
 * attributes: name, major, minor, needs_help, last_seen
 */
  @FormUrlEncoded
  @POST("/tracking/tokenized/list_students")
  void getStudentList(@Field("token") String token, CancelableCallback<JSONArray> callback);

  @FormUrlEncoded
  @POST("/tracking/tokenized/list_assistants")
  void getAssistantList(@Field("token") String token, CancelableCallback<JSONArray> callback);

  @FormUrlEncoded
  @POST("/tracking/tokenized/ping")
  void submitLocation(@Field("token") String token, @Field("loca") int major,  @Field("locb") int minor);

  @FormUrlEncoded
  @POST("/tracking/tokenized/gone")
  void submitGone(@Field("token") String token);

  @FormUrlEncoded
  @POST("/tracking/tokenized/help")
  void askHelp(@Field("token") String token, @Field("help") boolean help);

}
