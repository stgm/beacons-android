package nl.uva.beacons.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;

import java.util.List;

import nl.uva.beacons.BeaconsApplication;
import nl.uva.beacons.R;
import nl.uva.beacons.adapters.BeaconListAdapter;
import nl.uva.beacons.tracking.BeaconTracker;

/**
 * Created by sander on 11/8/14.
 */
public class BeaconListFragment extends BaseFragment implements BeaconTracker.BeaconListener {
    private BeaconListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_beacon_list, container, false);

        ListView beaconList = (ListView) v.findViewById(R.id.fragment_beacons_list_view);
        beaconList.setEmptyView(v.findViewById(R.id.beacons_empty_view));

        mAdapter = new BeaconListAdapter(getActivity());
        beaconList.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.updateCourseMap(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        ((BeaconsApplication) getActivity().getApplication()).getBeaconTracker().setBeaconListener(this);
    }

    @Override
    public void onStop() {
        ((BeaconsApplication) getActivity().getApplication()).getBeaconTracker().setBeaconListener(null);
        super.onStop();
    }

    @Override
    public void onBeaconsRanged(final List<Beacon> detectedBeacons) {
        if (getActivity() != null && isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.clear();
                    mAdapter.addAll(detectedBeacons);
                }
            });
        }
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.title_section_scan_beacons);
    }

    @Override
    protected int getHomeButtonMode() {
        return BaseFragment.HOME_BUTTON_DRAWER;
    }
}
