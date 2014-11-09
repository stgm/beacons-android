package nl.uva.beacons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Map;

import nl.uva.beacons.R;
import nl.uva.beacons.api.BeaconApiClient;

/**
 * Created by sander on 11/8/14.
 */
public class StudentListAdapter extends ArrayAdapter<Map<String, String>> {
  private LayoutInflater mInflater;

  public StudentListAdapter(Context context) {
    super(context, 0);
    mInflater = LayoutInflater.from(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView title;
    TextView subTitle;

    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.list_item_student, parent, false);
      title = (TextView) convertView.findViewById(R.id.student_name);
      subTitle = (TextView) convertView.findViewById(R.id.student_details);

      convertView.setTag(R.id.student_name, title);
      convertView.setTag(R.id.student_details, subTitle);
    } else {
      title = (TextView) convertView.getTag(R.id.student_name);
      subTitle = (TextView) convertView.getTag(R.id.student_details);
    }

    Map<String, String> studentInfo = getItem(position);
    title.setText(studentInfo.get(BeaconApiClient.ATTRIBUTE_NAME));
    subTitle.setText("Major " + studentInfo.get(BeaconApiClient.ATTRIBUTE_LOC_A) + ", Minor "
        + studentInfo.get(BeaconApiClient.ATTRIBUTE_LOC_B) + ", help: " + studentInfo.get(BeaconApiClient.ATTRIBUTE_HELP));
    return convertView;
  }
}
