package nl.uva.beacons.adapters;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import nl.uva.beacons.R;
import nl.uva.beacons.api.BeaconApi;

/**
 * Created by sander on 11/8/14.
 */
public class AssistantListAdapter extends ArrayAdapter<Map<String, String>> {
    /* TODO: Merge duplicate assistant entries */

    private LayoutInflater mInflater;
    public AssistantListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView title;
        TextView subTitle;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_assistant, parent, false);
            title = (TextView) convertView.findViewById(R.id.assistant_name);
            subTitle = (TextView) convertView.findViewById(R.id.assistant_details);

            convertView.setTag(R.id.assistant_name, title);
            convertView.setTag(R.id.assistant_details, subTitle);
        } else {
            title = (TextView) convertView.getTag(R.id.assistant_name);
            subTitle = (TextView) convertView.getTag(R.id.assistant_details);
        }

        Map<String, String> assistantInfo = getItem(position);
        title.setText(assistantInfo.get(BeaconApi.ATTR_NAME));
        subTitle.setText("Major " + assistantInfo.get(BeaconApi.ATTR_LOC_A) + ", Minor "
            + assistantInfo.get(BeaconApi.ATTR_LOC_B));
        return convertView;
    }

    @Override
    public void addAll(Collection<? extends Map<String, String>> collection) {
        super.addAll(collection);
        sort(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> lhs, Map<String, String> rhs) {
                String updatedTimeLeft = lhs.get(BeaconApi.ATTR_UPDATED);
                String updatedTimeRight = rhs.get(BeaconApi.ATTR_UPDATED);
                if (updatedTimeLeft == null || updatedTimeRight == null ||
                    updatedTimeLeft.equals("null") || updatedTimeLeft.equals("null")) {
                    return 0;
                }

                Time lhsDate = new Time();
                lhsDate.parse3339(updatedTimeLeft);
                Time rhsDate = new Time();
                rhsDate.parse3339(updatedTimeRight);
                return Time.compare(rhsDate, lhsDate);
            }
        });
    }
}
