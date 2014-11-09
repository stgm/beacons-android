package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;

import java.util.List;

import nl.uva.beacons.tracking.BeaconTracker;
import nl.uva.beacons.activities.MainActivity;
import nl.uva.beacons.R;
import nl.uva.beacons.adapters.BeaconListAdapter;

/**
 * Created by sander on 11/8/14.
 */
public class BeaconListFragment extends Fragment implements BeaconTracker.BeaconListener {
  private BeaconListAdapter mAdapter;
  private ListView mBeaconList;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_beacon_list, container, false);

    mBeaconList = (ListView) v.findViewById(R.id.fragment_beacons_list_view);
    mBeaconList.setEmptyView(v.findViewById(R.id.beacons_empty_view));

    mAdapter = new BeaconListAdapter(getActivity());
    mBeaconList.setAdapter(mAdapter);
    return v;
  }

  @Override
  public void onStart() {
    super.onStart();
    ((MainActivity) getActivity()).getBeaconTracker().setBeaconListener(this);
  }

  @Override
  public void onStop() {
    ((MainActivity) getActivity()).getBeaconTracker().setBeaconListener(null);
    super.onStop();
  }

  @Override
  public void onBeaconsRanged(final List<Beacon> detectedBeacons) {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mAdapter.clear();
        mAdapter.addAll(detectedBeacons);
      }
    });
  }
}
