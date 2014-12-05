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
import java.util.Collection;
import java.util.Collections;
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
public class AssistantListAdapter extends ArrayAdapter<List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>>> {
    private static final String TAG = AssistantListAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

    /* Map from Hash to ArrayList index, for faster merging */
    private HashMap<String, Integer> mHashToIndexMap = new HashMap<>();

    public AssistantListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView title;
        TextView subTitle;
        TextView numberOfCoursesText;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_assistant, parent, false);
            title = (TextView) convertView.findViewById(R.id.assistant_name);
            subTitle = (TextView) convertView.findViewById(R.id.assistant_details);
            numberOfCoursesText = (TextView)convertView.findViewById(R.id.assistant_courses_number);

            convertView.setTag(R.id.assistant_name, title);
            convertView.setTag(R.id.assistant_details, subTitle);
            convertView.setTag(R.id.assistant_courses_number, numberOfCoursesText);

        } else {
            title = (TextView) convertView.getTag(R.id.assistant_name);
            subTitle = (TextView) convertView.getTag(R.id.assistant_details);
            numberOfCoursesText = (TextView)convertView.getTag(R.id.assistant_courses_number);
        }

        List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> assistantInfo = getItem(position);
        Map<String, String> firstEntry = assistantInfo.get(0).getValue();
        title.setText(firstEntry.get(BeaconApi.ATTR_NAME));
        subTitle.setText("Major " + firstEntry.get(BeaconApi.ATTR_LOC_A) + ", Minor " + firstEntry.get(BeaconApi.ATTR_LOC_B));

        String coursesText = "";
        int i = 0;
        for(AbstractMap.SimpleEntry<LoginEntry, Map<String, String>> entry : assistantInfo) {
            if(i == 0) {
                coursesText += entry.getKey().courseName;
            } else {
                coursesText += ", " + entry.getKey().courseName;
            }
            i++;
        }
        numberOfCoursesText.setText(getContext().getString(R.string.present_at) + " " + coursesText);
        return convertView;
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
        List<Map<String, String>> receivedAssistants = apiResult.getValue();
        setNotifyOnChange(false);
        for (Map<String, String> assistant : receivedAssistants) {
            String hash = assistant.get(BeaconApi.ATTR_ID);
            Integer index = mHashToIndexMap.get(hash);
            if (index == null) {
                Log.d(TAG, "New assistant, name: " + assistant.get(BeaconApi.ATTR_NAME));
                /* Not in the list, create new entry */
                List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> newItem = new ArrayList<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>>();
                newItem.add(new AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>(apiResult.getKey(), assistant));
                add(newItem);
                mHashToIndexMap.put(hash, getCount() - 1);
            } else {
                /* Student is already in the list, we need to merge */
                List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> assistantListItem = getItem(index);
                assistantListItem.add(new AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>(apiResult.getKey(), assistant));
                Log.d(TAG, "MERGED, assistant, name: " + assistant.get(BeaconApi.ATTR_NAME) + ", with: " + assistantListItem.get(0).getValue().get(BeaconApi.ATTR_NAME));

                /* Sort the login entries by latest updated time */
                Collections.sort(assistantListItem, StudentListAdapter.TIME_COMPARATOR);
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
            return StudentListAdapter.TIME_COMPARATOR.compare(lhs.get(0), rhs.get(0));
        }

    };
}
