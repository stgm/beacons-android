package nl.uva.beacons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Comparator;
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
  public void sort(Comparator<? super Map<String, String>> comparator) {
    super.sort(comparator);
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
    if (needsHelp) {
      helpIcon.setClickable(true);
    } else {
      helpIcon.setClickable(false);
    }

    return convertView;
  }

}
