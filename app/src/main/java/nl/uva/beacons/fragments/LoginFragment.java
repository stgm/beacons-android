package nl.uva.beacons.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Map;

import nl.uva.beacons.MainActivity;
import nl.uva.beacons.R;
import nl.uva.beacons.api.BeaconApiClient;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/5/14.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
  private static final int PAIR_CODE_LENGTH = 4;
  private EditText mPinInput;
  private Button mLoginButton;
  private static final String TAG = LoginFragment.class.getSimpleName();
  private LoginListener mLoginListener;
  private Spinner mCourseSpinner;
  private String[] mUrlEntries;

  public static LoginFragment newInstance(LoginListener loginListener) {
    LoginFragment loginFragment = new LoginFragment();
    loginFragment.setLoginListener(loginListener);
    return loginFragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
    ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    mUrlEntries = getResources().getStringArray(R.array.spinner_course_url_values);

    View v = inflater.inflate(R.layout.fragment_login, container, false);
    mCourseSpinner = (Spinner)v.findViewById(R.id.spinner_select_course);
    mCourseSpinner.setSelection(0);
    mCourseSpinner.setOnItemSelectedListener(this);

    mLoginButton = (Button) v.findViewById(R.id.login_button);
    mLoginButton.setOnClickListener(this);
    mPinInput = (EditText)v.findViewById(R.id.pinInput);
    return v;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.clear();
    inflater.inflate(R.menu.global, menu);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onClick(View v) {

    /* Login button clicked. Try to register the user with the entered pin. */
    final String pin = mPinInput.getText().toString();
    if(pin != null && pin.length() == PAIR_CODE_LENGTH) {
      ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
          .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

      Log.d(TAG, "Registering user, pin: " + pin);
      mLoginButton.setEnabled(false);
      mLoginButton.setText(R.string.log_in_busy);

      final CancelableCallback<Map<String, String>> identifyCallback = new CancelableCallback<Map<String, String>>(this) {
        @Override
        public void onSuccess(Map<String, String> responseMap, Response response) {
          Log.d(TAG, "onSuccess: " + responseMap.toString() + ", url = " + response.getUrl());
          String userRole = responseMap.get("role");

          if(userRole != null && !userRole.isEmpty()) {
            Log.d(TAG, "Saving response, role: " + responseMap.get("role"));
            Log.d(TAG, "Login success!");
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MainActivity.KEY_SHARED_PREFS, Context.MODE_PRIVATE).edit();
            editor.putString(getString(R.string.pref_key_user_role), userRole).apply();
            mLoginListener.onLoginSuccess();
          } else {
            handleLoginFailure();
          }
        }

        @Override
        public void onFailure(RetrofitError error) {
          Log.d(TAG, "onFailure: " + error.getMessage() + ", url = " + error.getUrl());
          handleLoginFailure();
        }
      };

      CancelableCallback<Map<String, String>> registerCallback = new CancelableCallback<Map<String, String>>(this) {
        @Override
        public void onSuccess(Map<String, String> responseMap, Response response) {
          Log.d(TAG, "onSuccess: " + responseMap.toString() + ", url = " + response.getUrl());

          String userIdToken = responseMap.get("token");
          String beaconUuid = responseMap.get("beacon_id");

          if(userIdToken != null && !userIdToken.isEmpty() && beaconUuid != null && !beaconUuid.isEmpty()) {
            Log.d(TAG, "Saving response token: " + userIdToken);
            Log.d(TAG, "Saving response beacon UUID: " + beaconUuid);
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MainActivity.KEY_SHARED_PREFS, Context.MODE_PRIVATE).edit();
            editor.putString(getString(R.string.pref_key_user_token), userIdToken).apply();
            editor.putString(getString(R.string.pref_key_proximity_uuid), beaconUuid).apply();
            BeaconApiClient.get().identifyUser(userIdToken, identifyCallback);
          } else {
            handleLoginFailure();
          }
        }

        @Override
        public void onFailure(RetrofitError error) {
          Log.d(TAG, "onFailure: " + error.getMessage() + ", url = " + error.getUrl());
          handleLoginFailure();
        }
      };
      BeaconApiClient.init(getActivity());
      BeaconApiClient.get().registerUser(pin, registerCallback);

    } else {
      // Show input validation error
    }
  }

  private void handleLoginFailure() {
    mLoginButton.setEnabled(true);
    mLoginButton.setText(R.string.log_in);
    mLoginListener.onLoginFailure();
  }

  public void setLoginListener(LoginListener loginListener) {
    mLoginListener = loginListener;
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    Log.d(TAG, "onItemSelected: " + i);
    Log.d(TAG, mUrlEntries[i]);
    SharedPreferences sp = getActivity().getSharedPreferences(MainActivity.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
    sp.edit().putString(getString(R.string.pref_key_endpoint_url), mUrlEntries[i]).apply();
  }

  @Override
  public void onNothingSelected(AdapterView<?> adapterView) {

  }

  public interface LoginListener {
    void onLoginSuccess();
    void onLoginFailure();
  }

}
