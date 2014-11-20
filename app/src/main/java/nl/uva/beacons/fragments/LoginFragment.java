package nl.uva.beacons.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

import nl.uva.beacons.LoginEntry;
import nl.uva.beacons.LoginManager;
import nl.uva.beacons.R;
import nl.uva.beacons.activities.BaseActivity;
import nl.uva.beacons.api.ApiClient;
import nl.uva.beacons.api.BeaconApi;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/5/14.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {
  private static final int PAIR_CODE_LENGTH = 4;
  private static final String TAG = LoginFragment.class.getSimpleName();
  private EditText mPinInput;
  private Button mLoginButton;
  private LoginEntry mLoginEntry = new LoginEntry();

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    getFragmentManager().popBackStack();
    return true;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putSerializable("LOGIN", mLoginEntry);
    super.onSaveInstanceState(outState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      if (savedInstanceState.getSerializable("LOGIN") != null) {
        mLoginEntry = (LoginEntry) savedInstanceState.getSerializable("LOGIN");
      }
    }

    ActionBarActivity activity = ((ActionBarActivity) getActivity());
    activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    View v = inflater.inflate(R.layout.fragment_login, container, false);
    TextView textView = (TextView) v.findViewById(R.id.login_chosen_course_name);

    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    String courseName = sp.getString(getString(R.string.pref_key_course_name), "");
    textView.setText(courseName);
    mLoginEntry.courseName = courseName;
    mLoginEntry.url = sp.getString(getString(R.string.pref_key_endpoint_url), "");
    mLoginButton = (Button) v.findViewById(R.id.login_button);
    mLoginButton.setOnClickListener(this);
    mPinInput = (EditText) v.findViewById(R.id.pinInput);
    return v;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.clear();
    inflater.inflate(R.menu.global, menu);
  }

  @Override
  protected String getActionBarTitle() {
    return getString(R.string.log_in);
  }

  @Override
  protected int getHomeButtonMode() {
    return BaseFragment.HOME_BUTTON_BACK;
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
    if (pin != null && pin.length() == PAIR_CODE_LENGTH) {
      ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
          .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

      Log.d(TAG, "Registering user, pin: " + pin);
      mLoginButton.setEnabled(false);
      mLoginButton.setText(R.string.log_in_busy);
      ApiClient.init(getActivity());

      final CancelableCallback<Map<String, String>> identifyCallback = new CancelableCallback<Map<String, String>>(this) {
        @Override
        public void onSuccess(Map<String, String> responseMap, Response response) {
          Log.d(TAG, "onSuccess: " + responseMap.toString() + ", url = " + response.getUrl());
          String userRole = responseMap.get("role");

          if (userRole != null && !userRole.isEmpty()) {
            Log.d(TAG, "Saving response, role: " + responseMap.get("role"));
            Log.d(TAG, "Login success!");
            mLoginEntry.userRole = userRole;

            /* All data received. Finally add login entry */
            // TEMP - FOR TESTING PURPOSES
            // TODO
            if(!mLoginEntry.url.equals("https://prog2.mprog.nl/")) {
              mLoginEntry.uuid += "blabla";
            }

            LoginManager.addLoginEntry(getActivity(), mLoginEntry);
            ((LoginListener)getActivity()).onLoginSuccess(true);
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

          if (userIdToken != null && !userIdToken.isEmpty() && beaconUuid != null && !beaconUuid.isEmpty()) {
            Log.d(TAG, "Saving response token: " + userIdToken);
            Log.d(TAG, "Saving response beacon UUID: " + beaconUuid);
            mLoginEntry.userToken = userIdToken;
            mLoginEntry.uuid = beaconUuid.toLowerCase();

            ApiClient.identifyUser(mLoginEntry.url, userIdToken, identifyCallback);
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
      ApiClient.init(getActivity());
      ApiClient.registerUser(mLoginEntry.url, pin, registerCallback);

    } else {
      // Show input validation error
    }
  }

  private void handleLoginFailure() {
    mLoginButton.setEnabled(true);
    mLoginButton.setText(R.string.log_in);
    ((LoginListener)getActivity()).onLoginFailure();
  }

  public interface LoginListener {
    void onLoginSuccess(boolean startUp);
    void onLoginFailure();
  }

}
