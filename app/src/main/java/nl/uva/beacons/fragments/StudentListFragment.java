package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import nl.uva.beacons.MainActivity;
import nl.uva.beacons.R;
import nl.uva.beacons.api.BeaconApiClient;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class StudentListFragment extends Fragment {
  private static final String TAG = StudentListFragment.class.getSimpleName();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_student_list, container, false);
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    BeaconApiClient.get().getStudentList(sharedPreferences.getString(getString(R.string.pref_key_user_token), ""),
        new CancelableCallback<JSONArray>(this) {
          @Override
          public void onSuccess(JSONArray maps, Response response) {
            Log.d(TAG, "onSuccess: " + maps.toString());
          }

          @Override
          public void onFailure(RetrofitError error) {
            Log.d(TAG, "onFailure: " + error.getMessage());
          }
        });
    return v;
  }
}
