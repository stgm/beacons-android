package nl.uva.beacons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nl.uva.beacons.LoginEntry;
import nl.uva.beacons.R;

/**
 * Created by sander on 11/26/14.
 */
public class HelpCourseListAdapter extends ArrayAdapter<LoginEntry> {
    private LayoutInflater mLayoutInflater;

    public HelpCourseListAdapter(Context context, List<LoginEntry> objects) {
        super(context, 0, objects);
        mLayoutInflater = LayoutInflater.from(context);
    }

    public HelpCourseListAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView title;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_course_help, parent, false);
            title = (TextView) convertView.findViewById(R.id.course_help_title);
            convertView.setTag(R.id.course_help_title, title);
        } else {
            title = (TextView) convertView.getTag(R.id.course_help_title);
        }

        LoginEntry loginEntry = getItem(position);
        title.setText(loginEntry.courseName);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
