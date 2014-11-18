package nl.uva.beacons.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import nl.uva.beacons.R;

/**
 * Created by sander on 11/18/14.
 */
public class BaseActivity extends ActionBarActivity {
  public void replaceFragment(Fragment fragment, boolean addToBackStack) {
    FragmentManager fragmentManager = getFragmentManager();

    String fragmentClassNameAsTag = fragment.getClass().getName();
    Fragment f = fragmentManager.findFragmentByTag(fragmentClassNameAsTag);
    if (f != null && f.isAdded()) {
      /* This fragment is already active, do nothing */
      return;
    }
    FragmentTransaction ft = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    if(addToBackStack) {
      ft = ft.addToBackStack(null);
    }
    ft.replace(R.id.container, fragment, fragmentClassNameAsTag).commit();
  }

  public void replaceFragment(Fragment fragment) {
    replaceFragment(fragment, false);
  }
}
