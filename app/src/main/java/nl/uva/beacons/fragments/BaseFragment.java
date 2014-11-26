package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


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
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
    final ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();

    if(actionBar != null) {
      actionBar.setTitle(getActionBarTitle());
      switch (getHomeButtonMode()) {
        case HOME_BUTTON_BACK:
          Log.d(TAG, "HOME_BUTTON_BACK");
          actionBar.setDisplayShowHomeEnabled(true);
          actionBar.setDisplayHomeAsUpEnabled(true);
          break;
        case HOME_BUTTON_DRAWER:
          Log.d(TAG, "HOME_BUTTON_DRAWER");
          break;
        default:
          break;
      }
    }
  }

}
