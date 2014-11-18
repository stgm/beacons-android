package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonElement;

import nl.uva.beacons.LoginManager;
import nl.uva.beacons.R;
import nl.uva.beacons.api.BeaconApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class HelpFragment extends Fragment {
  private static final String TAG = HelpFragment.class.getSimpleName();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_ask_help, container, false);

    final EditText editText = (EditText) v.findViewById(R.id.input_ask_help_text);
    final String userToken = LoginManager.getCurrentEntry(getActivity()).userToken;
    final Button helpButton = (Button) v.findViewById(R.id.button_ask_help);

    helpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "Asking for help...");
        helpButton.setEnabled(false);
        String message = editText.getText().toString();
        BeaconApiClient.get().askHelp(userToken, true, message, new Callback<JsonElement>() {
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
}
