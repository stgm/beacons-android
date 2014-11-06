package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.uva.beacons.R;

/**
 * Created by sander on 11/5/14.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_login, container, false);
    v.findViewById(R.id.login_button).setOnClickListener(this);
    return v;
  }

  @Override
  public void onClick(View v) {
    /* Login button clicked */
  }
}
