package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.JsonElement;

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
    final String userToken = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_key_user_token), "");

    final Button helpButton = (Button) v.findViewById(R.id.button_ask_help);
    helpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "Asking for help...");
        helpButton.setEnabled(false);

        BeaconApiClient.get().askHelp(userToken, true, new Callback<JsonElement>() {
          @Override
          public void success(JsonElement jsonElement, Response response) {
            Log.d(TAG, "Asked help!");
            helpButton.setText("Om hulp gevraagd!");
          }

          @Override
          public void failure(RetrofitError error) {
            helpButton.setEnabled(true);
          }
        });
      }
    });
    return v;
  }
}
