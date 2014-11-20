package nl.uva.beacons.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonElement;

import java.util.List;

import nl.uva.beacons.LoginEntry;
import nl.uva.beacons.LoginManager;
import nl.uva.beacons.R;
import nl.uva.beacons.api.ApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class HelpFragment extends BaseFragment {
  private static final String TAG = HelpFragment.class.getSimpleName();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_ask_help, container, false);

    final EditText editText = (EditText) v.findViewById(R.id.input_ask_help_text);
    final Button helpButton = (Button) v.findViewById(R.id.button_ask_help);

    /* Todo spinner options */
    final List<LoginEntry> loginEntry = LoginManager.getCourseLoginEntries(getActivity());

    helpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "Asking for help...");
        helpButton.setEnabled(false);
        String message = editText.getText().toString();
        ApiClient.askHelp(loginEntry.get(0), true, message, new Callback<JsonElement>() {
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
