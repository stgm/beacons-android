package nl.uva.beacons.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;

import nl.uva.beacons.R;
import nl.uva.beacons.fragments.NavigationDrawerFragment;

/**
 * Created by sander on 11/18/14.
 */
public abstract class BaseActivity extends ActionBarActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    public static final int HOME_BUTTON_BACK = 1;
    public static final int HOME_BUTTON_DRAWER = 2;

    public abstract ActionBarDrawerToggle getDrawerToggle();

    public abstract NavigationDrawerFragment getNavigationDrawerFragment();

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int transactionAnimation) {
        FragmentManager fragmentManager = getFragmentManager();

        String fragmentClassNameAsTag = fragment.getClass().getName();
        Fragment f = fragmentManager.findFragmentByTag(fragmentClassNameAsTag);
        if (f != null && f.isVisible()) {
      /* This fragment is already active, do nothing */
            Log.d(TAG, "Fragment already added");
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction().setTransition(transactionAnimation);
        if (addToBackStack) {
            ft = ft.addToBackStack(null);
        }
        ft.replace(R.id.container, fragment, fragmentClassNameAsTag).commit();
    }

    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, false);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(fragment, addToBackStack, FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    }

    public void setActionBarConfig(final int mode, String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        ActionBarDrawerToggle toggle = getDrawerToggle();
        NavigationDrawerFragment navigationDrawerFragment = getNavigationDrawerFragment();

        switch (mode) {
            case HOME_BUTTON_BACK:
                Log.d(TAG, "HOME_BUTTON_BACK");
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                if (toggle != null) {
                    toggle.setDrawerIndicatorEnabled(false);
                }
                if (navigationDrawerFragment != null) {
                    navigationDrawerFragment.setDrawerEnabled(false);
                }
                break;
            case HOME_BUTTON_DRAWER:
                Log.d(TAG, "HOME_BUTTON_DRAWER");
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                if (toggle != null) {
                    Log.d(TAG, "Set drawer indicator enabled true");
                    toggle.setDrawerIndicatorEnabled(true);
                }
                if (navigationDrawerFragment != null) {
                    navigationDrawerFragment.setDrawerEnabled(true);
                }
                break;
            default:
                break;
        }
    }

}
