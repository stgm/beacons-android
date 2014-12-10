package nl.uva.beacons.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uva.beacons.login.LoginEntry;
import nl.uva.beacons.R;
import nl.uva.beacons.activities.MainActivity;
import nl.uva.beacons.adapters.StudentListAdapter;
import nl.uva.beacons.api.ApiClient;
import nl.uva.beacons.api.BeaconApi;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class StudentListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
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
        studentListView.setOnItemClickListener(this);

        mAdapter = new StudentListAdapter(getActivity());
        studentListView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.material_dark_indigo, R.color.material_primary_indigo, R.color.material_light_indigo);
        loadStudents();
        return v;
    }

    private void loadStudents() {
        ApiClient.getStudentList(new CancelableCallback<AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>>>(this) {
            @Override
            public void onSuccess(AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>> studentList, Response response) {
                Log.d(TAG, "onSuccess: " + studentList.toString());
                mAdapter.addAndMerge(studentList);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(RetrofitError error) {
                Log.d(TAG, "onFailure: " + error.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /* Only for testing purposes. This method loads 100 dummy student items in the ListView */
    private void loadTestData() {
        LoginEntry fakeLoginEntry = new LoginEntry();
        fakeLoginEntry.courseName = "Blablabla";
        fakeLoginEntry.uuid = "uuid ";
        fakeLoginEntry.url = "http://....";
        fakeLoginEntry.userRole = "fake";
        fakeLoginEntry.userToken = "token!";
        ArrayList<Map<String, String>> fakeMap = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(BeaconApi.ATTR_QUESTION, "My question " + i);
            hashMap.put(BeaconApi.ATTR_UPDATED, new Time().format3339(true));
            hashMap.put(BeaconApi.ATTR_ID, "blabla" + i);
            hashMap.put(BeaconApi.ATTR_NAME, "Name " + i);
            hashMap.put(BeaconApi.ATTR_HELP, i % 2 == 0 ? "true" : "false");
            hashMap.put(BeaconApi.ATTR_LOC_A, "0");
            hashMap.put(BeaconApi.ATTR_LOC_B, "" + i);
            fakeMap.add(hashMap);
        }
        AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>> fakeData
            = new AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>>(fakeLoginEntry, fakeMap);

        mAdapter.addAndMerge(fakeData);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        mAdapter.clear();
        loadStudents();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.title_section_students_list);
    }

    @Override
    protected int getHomeButtonMode() {
        return BaseFragment.HOME_BUTTON_DRAWER;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> clickedStudent = mAdapter.getItem(position);
        ((MainActivity) getActivity()).replaceFragment(StudentDetailFragment.newInstance(clickedStudent),
            true, FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    }
}
