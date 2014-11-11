package nl.uva.beacons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Map;

import nl.uva.beacons.R;

/**
 * Created by sander on 11/11/14.
 */
public class CourseListAdapter extends ArrayAdapter<Map.Entry<String, String>> {
  private LayoutInflater mLayoutInflater;

  public CourseListAdapter(Context context) {
    super(context, 0);
    mLayoutInflater = LayoutInflater.from(context);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    TextView title;
    TextView url;

    if (convertView == null) {
      convertView = mLayoutInflater.inflate(R.layout.list_item_course, parent, false);
      title = (TextView) convertView.findViewById(R.id.course_name);
      url = (TextView) convertView.findViewById(R.id.course_details);
      convertView.setTag(R.id.course_name, title);
      convertView.setTag(R.id.course_details, url);
    } else {
      title = (TextView) convertView.getTag(R.id.course_name);
      url = (TextView) convertView.getTag(R.id.course_details);
    }
    Map.Entry courseItem = getItem(position);
    title.setText((String) courseItem.getKey());
    url.setText((String) courseItem.getValue());

    return convertView;
  }
}
