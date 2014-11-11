package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Map;

import nl.uva.beacons.R;
import nl.uva.beacons.activities.MainActivity;
import nl.uva.beacons.adapters.CourseListAdapter;
import nl.uva.beacons.api.BeaconApiClient;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/11/14.
 */
public class SelectCourseFragment extends Fragment implements AdapterView.OnItemClickListener {
  private static final String TAG = SelectCourseFragment.class.getSimpleName();
  private CourseListAdapter mAdapter;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ActionBarActivity activity = ((ActionBarActivity) getActivity());
    activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    View v = inflater.inflate(R.layout.fragment_select_course, container, false);
    final ListView courseList = (ListView) v.findViewById(R.id.course_list_view);
    mAdapter = new CourseListAdapter(getActivity());
    courseList.setAdapter(mAdapter);
    courseList.setOnItemClickListener(this);

    BeaconApiClient.getCourses(new CancelableCallback<Map<String, String>>(this) {
      @Override
      public void onSuccess(Map<String, String> coursesMap, Response response) {
        Log.d(TAG, "onSuccess: " + coursesMap);
        coursesMap.entrySet();
        mAdapter.clear();
        mAdapter.addAll(coursesMap.entrySet());
      }

      @Override
      public void onFailure(RetrofitError error) {

      }
    });
    return v;
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    Map.Entry<String, String> courseEntry = mAdapter.getItem(i);
    Log.d(TAG, "Course selected: " + courseEntry.getKey() + ", " + courseEntry.getValue());

    MainActivity mainActivity = (MainActivity) getActivity();
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);
    sp.edit().putString(getString(R.string.pref_key_course_name), courseEntry.getKey())
        .putString(getString(R.string.pref_key_endpoint_url), courseEntry.getValue()).apply();
    mainActivity.showLogin();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.clear();
    inflater.inflate(R.menu.global, menu);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

}
