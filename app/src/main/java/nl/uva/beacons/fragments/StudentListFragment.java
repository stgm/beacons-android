package nl.uva.beacons.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

import nl.uva.beacons.LoginManager;
import nl.uva.beacons.R;
import nl.uva.beacons.adapters.StudentListAdapter;
import nl.uva.beacons.api.ApiClient;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class StudentListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
  private static final String TAG = StudentListFragment.class.getSimpleName();
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private StudentListAdapter mAdapter;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    View v = inflater.inflate(R.layout.fragment_student_list, container, false);

    ListView studentListView = (ListView) v.findViewById(R.id.fragment_student_list_view);
    studentListView.setEmptyView(v.findViewById(R.id.students_empty_view));

    mAdapter = new StudentListAdapter(getActivity());
    studentListView.setAdapter(mAdapter);

    mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
    mSwipeRefreshLayout.setOnRefreshListener(this);
    mSwipeRefreshLayout.setColorSchemeResources(R.color.material_dark_indigo, R.color.material_primary_indigo, R.color.material_light_indigo);
    loadStudents();

    return v;
  }

  private void loadStudents() {
    String userToken = LoginManager.getCurrentEntry(getActivity()).userToken;
    ApiClient.getStudentList(userToken,
        new CancelableCallback<List<Map<String, String>>>(this) {
          @Override
          public void onSuccess(List<Map<String, String>> studentList, Response response) {
            Log.d(TAG, "onSuccess: " + studentList.toString());
            mAdapter.clear();
            mAdapter.addAll(studentList);
            mSwipeRefreshLayout.setRefreshing(false);
          }

          @Override
          public void onFailure(RetrofitError error) {
            Log.d(TAG, "onFailure: " + error.getMessage());
            mSwipeRefreshLayout.setRefreshing(false);
          }
        });
  }

  @Override
  public void onRefresh() {
    loadStudents();
  }


  @Override
  protected String getActionBarTitle() {
    return getString(R.string.title_section_students_list);
  }

  @Override
  protected int getHomeButtonMode() {
    return 0;
  }
}
