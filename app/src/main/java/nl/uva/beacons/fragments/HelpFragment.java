package nl.uva.beacons.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.gson.JsonElement;

import java.util.List;

import nl.uva.beacons.LoginEntry;
import nl.uva.beacons.LoginManager;
import nl.uva.beacons.R;
import nl.uva.beacons.adapters.HelpCourseListAdapter;
import nl.uva.beacons.api.ApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class HelpFragment extends BaseFragment {
  private Spinner mSpinner;
  private static final String TAG = HelpFragment.class.getSimpleName();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_ask_help, container, false);

    final EditText editText = (EditText) v.findViewById(R.id.input_ask_help_text);
    final Button helpButton = (Button) v.findViewById(R.id.button_ask_help);

    /* Todo spinner options */
    final List<LoginEntry> loginEntries = LoginManager.getCourseLoginEntries(getActivity());
    final HelpCourseListAdapter courseListAdapter = new HelpCourseListAdapter(getActivity(), loginEntries);

    mSpinner = (Spinner)v.findViewById(R.id.help_course_spinner);
    mSpinner.setAdapter(courseListAdapter);
    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Selected: " + ((LoginEntry)mSpinner.getSelectedItem()).courseName);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    helpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "Asking for help, course: " + ((LoginEntry)mSpinner.getSelectedItem()).courseName);
        helpButton.setEnabled(false);
        String message = editText.getText().toString();
        ApiClient.askHelp((LoginEntry)mSpinner.getSelectedItem(), true, message, new Callback<JsonElement>() {
          @Override
          public void success(JsonElement jsonElement, Response response) {
            Log.d(TAG, "Asked help!");
            helpButton.setText("Om hulp gevraagd!");
          }

          @Override
          public void failure(RetrofitError error) {
            Log.d(TAG, "error: " + error.getMessage());
            helpButton.setEnabled(true);
          }
        });
      }
    });
    return v;
  }

  @Override
  protected String getActionBarTitle() {
    return getString(R.string.title_section_help);
  }

  @Override
  protected int getHomeButtonMode() {
    return 0;
  }
}
