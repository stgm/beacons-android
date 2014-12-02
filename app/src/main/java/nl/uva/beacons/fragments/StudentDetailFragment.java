package nl.uva.beacons.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.HashMap;

import nl.uva.beacons.R;
import nl.uva.beacons.api.ApiClient;
import nl.uva.beacons.api.BeaconApi;
import nl.uva.beacons.api.CancelableCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class StudentDetailFragment extends BaseFragment {
  private static final String KEY_STUDENT_INFO = "STUDENT_INFO";
  private static final String TAG = StudentDetailFragment.class.getSimpleName();

  public static StudentDetailFragment newInstance(HashMap<String, String> studentInfo) {
    StudentDetailFragment studentDetailFragment = new StudentDetailFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(KEY_STUDENT_INFO, studentInfo);
    studentDetailFragment.setArguments(bundle);
    return studentDetailFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_student_detail, container, false);

    final HashMap<String, String> map = (HashMap<String, String>) getArguments().getSerializable(KEY_STUDENT_INFO);
    TextView studentName = (TextView) v.findViewById(R.id.student_detail_name);
    TextView studentQuestion = (TextView) v.findViewById(R.id.student_detail_question);
    final Button confirmHelpButton = (Button) v.findViewById(R.id.button_confirm_help);

    studentName.setText(map.get(BeaconApi.ATTR_NAME));
    studentQuestion.setText(map.get(BeaconApi.ATTR_QUESTION));
    confirmHelpButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Log.d(TAG, "Clearing help for " + map.get(BeaconApi.ATTR_NAME) + "student id " + map.get(BeaconApi.ATTR_STUDENT_ID));
        confirmHelpButton.setEnabled(false);
        ApiClient.clearHelp(map.get(BeaconApi.ATTR_STUDENT_ID), new CancelableCallback<JsonElement>() {
          @Override
          public void onSuccess(JsonElement jsonElement, Response response) {
            Log.d(TAG, "Cleared help, student id = " + map.get(BeaconApi.ATTR_STUDENT_ID));
          }

          @Override
          public void onFailure(RetrofitError error) {
            Log.d(TAG, "failure: " + error.getMessage());
          }
        });
      }
    });
    return v;
  }

  @Override
  protected String getActionBarTitle() {
    return "Student details";
  }

  @Override
  protected int getHomeButtonMode() {
    return BaseFragment.HOME_BUTTON_BACK;
  }
}
