package nl.uva.beacons.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import nl.uva.beacons.activities.BaseActivity;


/**
 * Created by sander on 11/18/14.
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();
    public static final int HOME_BUTTON_BACK = 1;
    public static final int HOME_BUTTON_DRAWER = 2;

    protected abstract String getActionBarTitle();

    protected abstract int getHomeButtonMode();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setActionBarConfig(getHomeButtonMode(), getActionBarTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getHomeButtonMode() == HOME_BUTTON_BACK) {
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }
}
