package nl.uva.beacons;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import nl.uva.beacons.api.ApiClient;

/**
 * Created by sander on 11/15/14.
 */
public class LoginManager {
  private static final String TAG = LoginManager.class.getSimpleName();

  public static final String KEY_UUID_TO_TOKEN = "key_uuid_to_token";
  public static final String KEY_UUID_TO_URL = "key_uuid_to_url";
  public static final String KEY_UUID_TO_NAME = "key_uuid_to_name";
  public static final String KEY_UUID_TO_ROLE = "key_uuid_to_role";
  public static final String KEY_UUID_SET = "key_uuid_set";
  public static final String KEY_CURRENT_UUID = "key_current_uuid";

  public static boolean isLoggedIn(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    Set<String> uuids = sp.getStringSet(KEY_UUID_SET, null);
    boolean loggedIn = uuids != null && uuids.size() > 0;
    Log.d(TAG, "Logged in: " + loggedIn);
    return loggedIn;
  }

  public static ArrayList<LoginEntry> getCourseLoginEntries(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    ArrayList<LoginEntry> courseEntries = new ArrayList<LoginEntry>();
    Set<String> uuids = sp.getStringSet(KEY_UUID_SET, null);

    if (uuids == null) {
      return courseEntries;
    }

    for (String uuid : uuids) {
      LoginEntry courseLoginEntry = new LoginEntry();
      courseLoginEntry.uuid = uuid;
      courseLoginEntry.courseName = sp.getString(KEY_UUID_TO_NAME + uuid, null);
      courseLoginEntry.url = sp.getString(KEY_UUID_TO_URL + uuid, null);
      courseLoginEntry.userToken = sp.getString(KEY_UUID_TO_TOKEN + uuid, null);
      courseLoginEntry.userRole = sp.getString(KEY_UUID_TO_ROLE + uuid, null);
      courseEntries.add(courseLoginEntry);
    }
    return courseEntries;
  }

  public static void removeCourseLogin(Context context, LoginEntry login) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    Set<String> loginUuids = getLoginUuids(context);
    String uuid = login.uuid;
    loginUuids.remove(uuid);
    SharedPreferences.Editor edit = sp.edit();
    edit.remove(KEY_UUID_SET);
    edit.remove(KEY_UUID_TO_NAME + uuid);
    edit.remove(KEY_UUID_TO_TOKEN + uuid);
    edit.remove(KEY_UUID_TO_URL + uuid);
    edit.remove(KEY_UUID_TO_ROLE + uuid);
    if (sp.getString(KEY_CURRENT_UUID, "").equals(uuid)) {
      edit.remove(KEY_CURRENT_UUID + uuid);
    }
    edit.putStringSet(KEY_UUID_SET, loginUuids);
    edit.apply();

    ApiClient.removeApiForLogin(login);
  }

  public static LoginEntry getEntryForUuid(Context context, String uuid) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    LoginEntry courseLoginEntry = new LoginEntry();
    courseLoginEntry.uuid = uuid;
    courseLoginEntry.courseName = sp.getString(KEY_UUID_TO_NAME + uuid, null);
    courseLoginEntry.url = sp.getString(KEY_UUID_TO_URL + uuid, null);
    courseLoginEntry.userToken = sp.getString(KEY_UUID_TO_TOKEN + uuid, null);
    courseLoginEntry.userRole = sp.getString(KEY_UUID_TO_ROLE + uuid, null);

    return courseLoginEntry;
  }

  public static void addLoginEntry(Context context, LoginEntry login) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    Set<String> loginUuids = getLoginUuids(context);
    Log.d(TAG, "addLoginEntry, old size: " + loginUuids.size());
    Log.d(TAG, loginUuids.toString());

    Log.d(TAG, "adding uuid: " + login.uuid + ", url: " + login.url);
    loginUuids.add(login.uuid);
    editor.putString(KEY_UUID_TO_URL + login.uuid, login.url);
    editor.putString(KEY_UUID_TO_TOKEN + login.uuid, login.userToken);
    editor.putString(KEY_UUID_TO_NAME + login.uuid, login.courseName);
    editor.putString(KEY_UUID_TO_ROLE + login.uuid, login.userRole);
    editor.putStringSet(KEY_UUID_SET, loginUuids);
    editor.apply();

    ApiClient.addApiForLogin(login);
  }

  /* Return set of (Beacon) UUIDs, one UUID for each course that the user is logged in to.
   */
  private static HashSet<String> getLoginUuids(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    Set<String> uuidSet = sp.getStringSet(KEY_UUID_SET, null);
    HashSet<String> returnSet;
    if (uuidSet == null) {
      returnSet = new HashSet<String>();
    } else {
      returnSet = new HashSet<String>(uuidSet);
    }
    return returnSet;
  }
}
