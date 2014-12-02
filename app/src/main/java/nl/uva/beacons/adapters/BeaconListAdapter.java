package nl.uva.beacons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import nl.uva.beacons.R;

/**
 * Created by sander on 11/8/14.
 */
public class BeaconListAdapter extends ArrayAdapter<Beacon> {
  private LayoutInflater mInflater;

  public BeaconListAdapter(Context context) {
    super(context, 0);
    mInflater = LayoutInflater.from(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView title;
    TextView subTitle;
    TextView estimatedDistance;
    TextView beaconUuid;

    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.list_item_beacon, parent, false);
      title = (TextView) convertView.findViewById(R.id.beacon_title);
      subTitle = (TextView) convertView.findViewById(R.id.beacon_subtitle);
      estimatedDistance = (TextView) convertView.findViewById(R.id.beacon_estimated_distance);
      beaconUuid = (TextView)convertView.findViewById(R.id.beacon_uuid);

      convertView.setTag(R.id.beacon_title, title);
      convertView.setTag(R.id.beacon_subtitle, subTitle);
      convertView.setTag(R.id.beacon_estimated_distance, estimatedDistance);
      beaconUuid.setTag(R.id.beacon_uuid, beaconUuid);
    } else {
      title = (TextView) convertView.getTag(R.id.beacon_title);
      subTitle = (TextView) convertView.getTag(R.id.beacon_subtitle);
      estimatedDistance = (TextView) convertView.getTag(R.id.beacon_estimated_distance);
      beaconUuid = (TextView)convertView.getTag(R.id.beacon_uuid);
    }

    Beacon b = getItem(position);
    title.setText(b.getBluetoothName() + " : " + b.getBluetoothAddress());
    subTitle.setText("Major " + b.getId2().toString() + ",  Minor " + b.getId3().toString() + ", RSSI " + b.getRssi());
    estimatedDistance.setText(String.format("%.1f", b.getDistance()) + "m");
    beaconUuid.setText(b.getId1().toString());
    return convertView;
  }
}
