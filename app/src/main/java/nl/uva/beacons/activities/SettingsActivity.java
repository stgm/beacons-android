package nl.uva.beacons.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import nl.uva.beacons.R;
import nl.uva.beacons.fragments.SettingsFragment;

/**
 * Created by sander on 11/8/14.
 */
public class SettingsActivity extends ActionBarActivity {
  /* Stored in SharedPreferences course that the user is logged in,
   * saved as key-value pairs:
   * "key_course_uuid_set" -> set of UUIDs
   * uuid -> identifier
   * uuid+"url" => url
   */

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getFragmentManager().beginTransaction().replace(R.id.settings_container, new SettingsFragment()).commit();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

}
