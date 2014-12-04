package nl.uva.beacons.adapters;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

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
public class StudentListAdapter extends ArrayAdapter<Map<String, String>> {
  /* TODO: Merge duplicate student entries */

    private static final String TAG = StudentListAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

    public StudentListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView title;
        TextView subTitle;
        final TextView helpIcon;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_student, parent, false);
            title = (TextView) convertView.findViewById(R.id.student_name);
            subTitle = (TextView) convertView.findViewById(R.id.student_details);
            helpIcon = (TextView) convertView.findViewById(R.id.student_help_icon);

            convertView.setTag(R.id.student_name, title);
            convertView.setTag(R.id.student_details, subTitle);
            convertView.setTag(R.id.student_help_icon, helpIcon);
        } else {
            title = (TextView) convertView.getTag(R.id.student_name);
            subTitle = (TextView) convertView.getTag(R.id.student_details);
            helpIcon = (TextView) convertView.getTag(R.id.student_help_icon);
        }

        final Map<String, String> studentInfo = getItem(position);
        title.setText(studentInfo.get(BeaconApi.ATTR_NAME));
        subTitle.setText("Major " + studentInfo.get(BeaconApi.ATTR_LOC_A) + ", Minor "
            + studentInfo.get(BeaconApi.ATTR_LOC_B));

        boolean needsHelp = Boolean.parseBoolean(studentInfo.get(BeaconApi.ATTR_HELP));
        int helpRes = needsHelp ? R.drawable.circle_dark : R.drawable.circle;
        helpIcon.setBackgroundResource(helpRes);

        return convertView;
    }

    @Override
    public void addAll(Collection<? extends Map<String, String>> collection) {
        super.addAll(collection);
        sort(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> lhs, Map<String, String> rhs) {
                boolean lhsNeedsHelp = Boolean.parseBoolean(lhs.get(BeaconApi.ATTR_HELP));
                boolean rhsNeedsHelp = Boolean.parseBoolean(rhs.get(BeaconApi.ATTR_HELP));
                if(lhsNeedsHelp && !rhsNeedsHelp) {
                    return -1;
                } else if (!lhsNeedsHelp && rhsNeedsHelp) {
                    return 1;
                } else {
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
            }
        });
    }

}
