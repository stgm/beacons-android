package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nl.uva.beacons.LoginEntry;
import nl.uva.beacons.LoginManager;
import nl.uva.beacons.R;
import nl.uva.beacons.activities.MainActivity;
import nl.uva.beacons.activities.SettingsActivity;
import nl.uva.beacons.adapters.LoginListAdapter;

/**
 * Created by sander on 11/14/14.
 */
public class LoginManagementFragment extends BaseFragment {

  @Nullable
  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_login_management, container, false);

    ListView listView = (ListView) v.findViewById(R.id.manage_login_entry_list);
    ArrayList<LoginEntry> courseLoginEntries = LoginManager.getCourseLoginEntries(getActivity());
    TextView textRole = (TextView)v.findViewById(R.id.login_management_role);
    TextView textNumberCourses = (TextView)v.findViewById(R.id.login_management_number_courses);
    textNumberCourses.setText("Momenteel ingelogd bij " + courseLoginEntries.size() + " vakken");
    if(courseLoginEntries.size() > 0) {
      textRole.setText("Huidige functie: " + courseLoginEntries.get(0).userRole);
    }

    LoginListAdapter adapter = new LoginListAdapter(getActivity(), courseLoginEntries, ((SettingsActivity)getActivity()));
    listView.setAdapter(adapter);

    v.findViewById(R.id.button_add_new_login).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ((SettingsActivity)getActivity()).replaceFragment(new SelectCourseFragment(), true);
      }
    });
    return v;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.d("SANDER", "in loginManagementFragment onOptionsItemSelected");
    getActivity().onBackPressed();
    return true;
  }

  @Override
  protected String getActionBarTitle() {
    return "Beheer aanmeldingen";
  }

  @Override
  protected int getHomeButtonMode() {
    return BaseFragment.HOME_BUTTON_BACK;
  }
}
