package nl.uva.beacons.api;

import com.google.gson.JsonElement;

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

    /* Map attributes */
    static final String ATTR_NAME = "name";
    static final String ATTR_LOC_A = "loca";
    static final String ATTR_LOC_B = "locb";
    static final String ATTR_HELP = "help";
    static final String ATTR_UPDATED = "updated";
    static final String ATTR_QUESTION = "help_question";
    static final String ATTR_STUDENT_ID = "id";
    static final String FIELD_TOKEN = "token";

    @FormUrlEncoded
    @POST("/tracking/tokenized/list_students")
    void getStudentList(@Field(FIELD_TOKEN) String token, Callback<List<Map<String, String>>> callback);

    @FormUrlEncoded
    @POST("/tracking/tokenized/list_assistants")
    void getAssistantList(@Field(FIELD_TOKEN) String token, Callback<List<Map<String, String>>> callback);

    @FormUrlEncoded
    @POST("/tracking/tokenized/ping")
    void submitLocation(@Field(FIELD_TOKEN) String token, @Field(ATTR_LOC_A) int major,
                        @Field(ATTR_LOC_B) int minor, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/tracking/tokenized/gone")
    void submitGone(@Field(FIELD_TOKEN) String token, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/tracking/tokenized/help")
    void askHelp(@Field(FIELD_TOKEN) String token, @Field(ATTR_HELP) boolean help, @Field("help_question") String helpQuestion,
                 Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/tracking/tokenized/clear/{id}")
    void clearHelp(@Field(FIELD_TOKEN) String token, @Path("id") String id, Callback<JsonElement> callback);

}
