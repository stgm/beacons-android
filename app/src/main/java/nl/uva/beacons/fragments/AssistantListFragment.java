package nl.uva.beacons.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import nl.uva.beacons.login.LoginEntry;
import nl.uva.beacons.R;
import nl.uva.beacons.adapters.AssistantListAdapter;
import nl.uva.beacons.api.ApiClient;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class AssistantListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
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
        loadAssistants();

        return v;
    }

    private void loadAssistants() {
        ApiClient.getAssistantList(new CancelableCallback<AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>>>(this) {
            @Override
            public void onSuccess(AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>> assistantList, Response response) {
                Log.d(TAG, "onSuccess: " + assistantList.toString());
                mAdapter.addAndMerge(assistantList);
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
        mAdapter.clear();
        loadAssistants();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.title_section_assistents_list);
    }

    @Override
    protected int getHomeButtonMode() {
        return BaseFragment.HOME_BUTTON_DRAWER;
    }
}
