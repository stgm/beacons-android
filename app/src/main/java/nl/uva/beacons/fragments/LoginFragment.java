package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Map;

import nl.uva.beacons.R;
import nl.uva.beacons.api.BeaconApiClient;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/5/14.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
  private EditText mPinInput;
  private static final String TAG = LoginFragment.class.getSimpleName();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_login, container, false);
    v.findViewById(R.id.login_button).setOnClickListener(this);
    mPinInput = (EditText)v.findViewById(R.id.pinInput);
    return v;
  }

  @Override
  public void onClick(View v) {

    /* Login button clicked. Try to register the user with the entered pin. */
    String pin = mPinInput.getText().toString();
    if(pin != null && !pin.isEmpty()) {
      Log.d(TAG, "Sending code: " + pin);

      BeaconApiClient.get().registerUser(pin, new CancelableCallback<Map<String, String>>(this) {
        @Override
        public void onSuccess(Map<String, String> stringStringMap, Response response) {
          Log.d(TAG, "onSuccess: " + stringStringMap.toString() + ", url = " + response.getUrl());
        }

        @Override
        public void onFailure(RetrofitError error) {
          Log.d(TAG, "onFailure: " + error.getMessage() + ", url = " + error.getUrl());
        }
      });
    }
  }
}
