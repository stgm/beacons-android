package nl.uva.beacons.adapters;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uva.beacons.LoginEntry;
import nl.uva.beacons.R;
import nl.uva.beacons.api.BeaconApi;

/**
 * Created by sander on 11/8/14.
 */
public class StudentListAdapter extends ArrayAdapter<List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>>> {
    private static final String TAG = StudentListAdapter.class.getSimpleName();

    /* Map from Student Hash to ArrayList index, for faster merging */
    private HashMap<String, Integer> mHashToIndexMap = new HashMap<>();
    private LayoutInflater mInflater;

    public StudentListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView title;
        TextView subTitle;
        TextView numberOfCoursesText;
        final TextView helpIcon;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_student, parent, false);
            title = (TextView) convertView.findViewById(R.id.student_name);
            subTitle = (TextView) convertView.findViewById(R.id.student_details);
            helpIcon = (TextView) convertView.findViewById(R.id.student_help_icon);
            numberOfCoursesText = (TextView) convertView.findViewById(R.id.student_courses_number);

            convertView.setTag(R.id.student_name, title);
            convertView.setTag(R.id.student_details, subTitle);
            convertView.setTag(R.id.student_help_icon, helpIcon);
            convertView.setTag(R.id.student_courses_number, numberOfCoursesText);
        } else {
            title = (TextView) convertView.getTag(R.id.student_name);
            subTitle = (TextView) convertView.getTag(R.id.student_details);
            helpIcon = (TextView) convertView.getTag(R.id.student_help_icon);
            numberOfCoursesText = (TextView) convertView.getTag(R.id.student_courses_number);
        }

        List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> studentInfo = getItem(position);
        Map<String, String> firstEntry = studentInfo.get(0).getValue();
        title.setText(firstEntry.get(BeaconApi.ATTR_NAME));
        subTitle.setText("Major " + firstEntry.get(BeaconApi.ATTR_LOC_A) + ", Minor " + firstEntry.get(BeaconApi.ATTR_LOC_B));

        String coursesText = "";
        int i = 0;
        for(AbstractMap.SimpleEntry<LoginEntry, Map<String, String>> entry : studentInfo) {
            if(i == 0) {
                coursesText += entry.getKey().courseName;
            } else {
                coursesText += ", " + entry.getKey().courseName;
            }
            i++;
        }
        numberOfCoursesText.setText(getContext().getString(R.string.present_at) + " " + coursesText);

        boolean needsHelp = studentNeedsHelp(studentInfo);
        int helpRes = needsHelp ? R.drawable.circle_dark : R.drawable.circle;
        helpIcon.setBackgroundResource(helpRes);

        return convertView;
    }

    public boolean studentNeedsHelp(List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> studentInfo) {
        for (AbstractMap.SimpleEntry<LoginEntry, Map<String, String>> entry : studentInfo) {
            boolean needsHelp = Boolean.parseBoolean(entry.getValue().get(BeaconApi.ATTR_HELP));
            if (needsHelp) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        super.clear();
        mHashToIndexMap.clear();
    }

    @Override
    public void sort(Comparator<? super List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>>> comparator) {
        setNotifyOnChange(false);
        super.sort(comparator);
        int count = getCount();

        /* Rebuild the hash index after sorting */
        mHashToIndexMap.clear();
        for(int i = 0; i < count; i++) {
            String hash = getItem(i).get(0).getValue().get(BeaconApi.ATTR_ID);
            mHashToIndexMap.put(hash, i);
        }
        setNotifyOnChange(true);
    }
    public synchronized void addAndMerge(AbstractMap.SimpleEntry<LoginEntry, List<Map<String, String>>> apiResult) {
        Log.d(TAG, "addAndMerge");
        List<Map<String, String>> receivedStudents = apiResult.getValue();
        setNotifyOnChange(false);
        for (Map<String, String> student : receivedStudents) {
            String hash = student.get(BeaconApi.ATTR_ID);
            Integer index = mHashToIndexMap.get(hash);
            if (index == null) {
                Log.d(TAG, "New student, name: " + student.get(BeaconApi.ATTR_NAME));
                /* Not in the list, create new entry */
                List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> newItem = new ArrayList<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>>();
                newItem.add(new AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>(apiResult.getKey(), student));
                add(newItem);
                mHashToIndexMap.put(hash, getCount() - 1);
            } else {
                /* Student is already in the list, we need to merge */
                List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> studentListItem = getItem(index);
                studentListItem.add(new AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>(apiResult.getKey(), student));
                Log.d(TAG, "MERGED, student, name: " + student.get(BeaconApi.ATTR_NAME) + ", with: " + studentListItem.get(0).getValue().get(BeaconApi.ATTR_NAME));
            }
        }

        sort(mComparator);
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    private Comparator<List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>>> mComparator
        = new Comparator<List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>>>() {
        @Override
        public int compare(List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> lhs,
                           List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> rhs) {
            boolean lhsNeedsHelp = studentNeedsHelp(lhs);
            boolean rhsNeedsHelp = studentNeedsHelp(rhs);

            if (lhsNeedsHelp && !rhsNeedsHelp) {
                return -1;
            } else if (!lhsNeedsHelp && rhsNeedsHelp) {
                return 1;
            } else {
                String updatedTimeLeft = lhs.get(0).getValue().get(BeaconApi.ATTR_UPDATED);
                String updatedTimeRight = rhs.get(0).getValue().get(BeaconApi.ATTR_UPDATED);
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
    };

}
