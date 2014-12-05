package nl.uva.beacons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uva.beacons.LoginEntry;
import nl.uva.beacons.LoginManager;
import nl.uva.beacons.R;

/**
 * Created by sander on 11/8/14.
 */
public class BeaconListAdapter extends ArrayAdapter<Beacon> {
    private LayoutInflater mInflater;
    private HashMap<String, LoginEntry> mMap = new HashMap<String, LoginEntry>();

    public BeaconListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    public void updateCourses(Context context) {
        mMap.clear();
        ArrayList<LoginEntry> loginEntries = LoginManager.getCourseLoginEntries(context);
        for (LoginEntry entry : loginEntries) {
            mMap.put(entry.uuid, entry);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView courseTitle;
        TextView title;
        TextView subTitle;
        TextView estimatedDistance;
        TextView uuid;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_beacon, parent, false);
            title = (TextView) convertView.findViewById(R.id.beacon_title);
            subTitle = (TextView) convertView.findViewById(R.id.beacon_subtitle);
            estimatedDistance = (TextView) convertView.findViewById(R.id.beacon_estimated_distance);
            uuid = (TextView) convertView.findViewById(R.id.beacon_uuid);
            courseTitle = (TextView) convertView.findViewById(R.id.beacon_corresponding_course_title);

            convertView.setTag(R.id.beacon_title, title);
            convertView.setTag(R.id.beacon_subtitle, subTitle);
            convertView.setTag(R.id.beacon_estimated_distance, estimatedDistance);
            convertView.setTag(R.id.beacon_uuid, uuid);
            convertView.setTag(R.id.beacon_corresponding_course_title, courseTitle);
        } else {
            title = (TextView) convertView.getTag(R.id.beacon_title);
            subTitle = (TextView) convertView.getTag(R.id.beacon_subtitle);
            estimatedDistance = (TextView) convertView.getTag(R.id.beacon_estimated_distance);
            uuid = (TextView) convertView.getTag(R.id.beacon_uuid);
            courseTitle = (TextView) convertView.getTag(R.id.beacon_corresponding_course_title);
        }

        Beacon b = getItem(position);
        title.setText(b.getBluetoothName() + " : " + b.getBluetoothAddress());
        subTitle.setText("Major " + b.getId2().toString() + ",  Minor " + b.getId3().toString() + ", RSSI " + b.getRssi());
        estimatedDistance.setText(String.format("%.1f", b.getDistance()) + "m");
        uuid.setText(b.getId1().toString());
        LoginEntry loginEntry = mMap.get(b.getId1().toString());
        if (loginEntry != null) {
            courseTitle.setText(loginEntry.courseName);
        } else {
            courseTitle.setText(getContext().getString(R.string.beacon_list_item_not_logged_in));
        }

        return convertView;
    }
}
