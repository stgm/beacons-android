package nl.uva.beacons.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import nl.uva.beacons.activities.SettingsActivity;
import nl.uva.beacons.adapters.LoginListAdapter;

/**
 * Created by sander on 11/14/14.
 */
public class LoginManagementFragment extends BaseFragment implements LoginListAdapter.OnLoginRemovedListener {
    private static final String TAG = LoginManagementFragment.class.getSimpleName();
    private TextView mTextRole;
    private TextView mTextNumberCourses;
    private LoginListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_management, container, false);

        ListView listView = (ListView) v.findViewById(R.id.manage_login_entry_list);
        mTextRole = (TextView) v.findViewById(R.id.login_management_role);
        mTextNumberCourses = (TextView) v.findViewById(R.id.login_management_number_courses);

        ArrayList<LoginEntry> courseLoginEntries = LoginManager.getCourseLoginEntries(getActivity());
        mAdapter = new LoginListAdapter(getActivity(), courseLoginEntries, ((SettingsActivity) getActivity()), this);

        listView.setAdapter(mAdapter);
        setLoginText(courseLoginEntries);

        v.findViewById(R.id.button_add_new_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SettingsActivity) getActivity()).replaceFragment(new SelectCourseFragment(), true);
            }
        });
        return v;
    }

    private void setLoginText(ArrayList<LoginEntry> courseLoginEntries) {
        int numCourses = courseLoginEntries.size();
        String courses = numCourses == 1 ? " " + getString(R.string.course) : " " + getString(R.string.courses);
        mTextNumberCourses.setText(getString(R.string.currently_logged_in_at) + " " + courseLoginEntries.size() + courses);
        if (courseLoginEntries.size() > 0) {
            mTextRole.setText(getString(R.string.current_role) + ": " + courseLoginEntries.get(0).userRole);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onBackPressed();
        return true;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.manage_logins);
    }

    @Override
    protected int getHomeButtonMode() {
        return BaseFragment.HOME_BUTTON_BACK;
    }

    @Override
    public void onLoginRemoved() {
        setLoginText(mAdapter.getLoginEntries());
    }
}
