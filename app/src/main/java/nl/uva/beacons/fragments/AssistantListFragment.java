package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import nl.uva.beacons.adapters.AssistantListAdapter;
import nl.uva.beacons.api.BeaconApiClient;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class AssistantListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
  private static final String TAG = AssistantListFragment.class.getSimpleName();
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private AssistantListAdapter mAdapter;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    View v = inflater.inflate(R.layout.fragment_assistant_list, container, false);

    ListView assistantListView = (ListView) v.findViewById(R.id.fragment_assistant_list_view);
    assistantListView.setEmptyView(v.findViewById(R.id.assistants_empty_view));

    mAdapter = new AssistantListAdapter(getActivity());
    assistantListView.setAdapter(mAdapter);

    mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
    mSwipeRefreshLayout.setOnRefreshListener(this);
    mSwipeRefreshLayout.setColorSchemeResources(R.color.material_dark_indigo, R.color.material_primary_indigo, R.color.material_light_indigo);
    loadStudents();

    return v;
  }

  private void loadStudents() {
    String userToken = LoginManager.getCurrentEntry(getActivity()).userToken;
    BeaconApiClient.get().getAssistantList(userToken,
        new CancelableCallback<List<Map<String, String>>>(this) {
          @Override
          public void onSuccess(List<Map<String, String>> assistantList, Response response) {
            Log.d(TAG, "onSuccess: " + assistantList.toString());
            mAdapter.clear();
            mAdapter.addAll(assistantList);
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
}
