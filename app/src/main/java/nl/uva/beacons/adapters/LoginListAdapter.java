package nl.uva.beacons.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nl.uva.beacons.BeaconsApplication;
import nl.uva.beacons.LoginEntry;
import nl.uva.beacons.LoginManager;
import nl.uva.beacons.R;
import nl.uva.beacons.activities.SettingsActivity;
import nl.uva.beacons.api.ApiClient;

/**
 * Created by sander on 11/18/14.
 */
public class LoginListAdapter extends ArrayAdapter<LoginEntry> {
  private List<LoginEntry> mEntries;
  private static final String TAG = LoginListAdapter.class.getSimpleName();
  private SettingsActivity mActivity;

  private LayoutInflater mLayoutInflater;

  public LoginListAdapter(Context context, List<LoginEntry> objects, SettingsActivity settingsActivity) {
    super(context, 0, objects);
    mActivity = settingsActivity;
    mEntries = objects;
    mLayoutInflater = LayoutInflater.from(context);
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    TextView loginCourseName;
    TextView loginCourseUrl;
    TextView buttonLogout;

    if(convertView == null) {
      convertView = mLayoutInflater.inflate(R.layout.list_item_login_entry, parent, false);
      loginCourseName = (TextView)convertView.findViewById(R.id.login_course_name);
      loginCourseUrl = (TextView)convertView.findViewById(R.id.login_detail);
      buttonLogout = (TextView)convertView.findViewById(R.id.course_log_out_button);
      convertView.setTag(R.id.login_course_name, loginCourseName);
      convertView.setTag(R.id.login_detail, loginCourseUrl);
      convertView.setTag(R.id.course_log_out_button, buttonLogout);
    } else {
      loginCourseName = (TextView) convertView.getTag(R.id.login_course_name);
      loginCourseUrl = (TextView)convertView.getTag(R.id.login_detail);
      buttonLogout = (TextView)convertView.getTag(R.id.course_log_out_button);
    }

    LoginEntry entry = getItem(position);
    loginCourseName.setText(entry.courseName);
    loginCourseUrl.setText(entry.url);

    buttonLogout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "onClick logout " + position);
        showConfirmationDialog(getItem(position), position);
      }
    });

    return  convertView;
  }

  private void showConfirmationDialog(final LoginEntry entry, final int position) {
    new AlertDialog.Builder(getContext()).setTitle("Bevestiging")
        .setMessage("Weet je zeker dat je je voor " + entry.courseName + " " + "wil afmelden?").setNegativeButton("Nee", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
      }
    }).setPositiveButton("Ja", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        LoginManager.removeCourseLogin(getContext(), entry.uuid);
        mEntries.remove(position);
        notifyDataSetChanged();
        if (mEntries.size() == 0) {
          mActivity.logOut();
        } else {
          /* Re-initialize the api client */
          ApiClient.init(mActivity);
          ((BeaconsApplication) mActivity.getApplication()).getBeaconTracker().initRegions();
        }
      }
    }).create().show();
  }

}
