package nl.uva.beacons.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.Comparator;
import java.util.Map;

import nl.uva.beacons.R;
import nl.uva.beacons.api.BeaconApi;
import nl.uva.beacons.api.BeaconApiClient;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/8/14.
 */
public class StudentListAdapter extends ArrayAdapter<Map<String, String>> {
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
    helpIcon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        helpIcon.setBackgroundResource(R.drawable.circle);
        helpIcon.refreshDrawableState();
        BeaconApiClient.get().clearHelp(studentInfo.get(BeaconApi.ATTR_STUDENT_ID), new CancelableCallback<JsonElement>() {
          @Override
          public void onSuccess(JsonElement jsonElement, Response response) {
            Log.d(TAG, "Cleared help at position + " + position + ", studentid = " + studentInfo.get(BeaconApi.ATTR_STUDENT_ID));
          }

          @Override
          public void onFailure(RetrofitError error) {
            Log.d(TAG, "failure: " + error.getMessage());
          }
        });
      }
    });

    title.setText(studentInfo.get(BeaconApi.ATTR_NAME));
    subTitle.setText("Major " + studentInfo.get(BeaconApi.ATTR_LOC_A) + ", Minor "
        + studentInfo.get(BeaconApi.ATTR_LOC_B) + ", help: " + studentInfo.get(BeaconApi.ATTR_HELP));

    boolean needsHelp = Boolean.parseBoolean(studentInfo.get(BeaconApi.ATTR_HELP));
    int helpRes = needsHelp ? R.drawable.circle_dark : R.drawable.circle;
    helpIcon.setBackgroundResource(helpRes);

    return convertView;
  }

}
