package nl.uva.beacons.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.JsonElement;

import java.util.List;

import nl.uva.beacons.login.LoginEntry;
import nl.uva.beacons.login.LoginManager;
import nl.uva.beacons.R;
import nl.uva.beacons.adapters.HelpCourseListAdapter;
import nl.uva.beacons.api.ApiClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/7/14.
 */
public class HelpFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Spinner mSpinner;
    private Button mHelpButton;
    private HelpCourseListAdapter mAdapter;
    private EditText mHelpText;
    private static final String TAG = HelpFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ask_help, container, false);

        mHelpText = (EditText) v.findViewById(R.id.input_ask_help_text);
        mHelpButton = (Button) v.findViewById(R.id.button_ask_help);

        mAdapter = new HelpCourseListAdapter(getActivity());

        mSpinner = (Spinner) v.findViewById(R.id.help_course_spinner);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(this);

        mHelpButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Refresh login entries */
        List<LoginEntry> loginEntries = LoginManager.getCourseLoginEntries(getActivity());
        mAdapter.clear();
        mAdapter.addAll(loginEntries);
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.title_section_help);
    }

    @Override
    protected int getHomeButtonMode() {
        return BaseFragment.HOME_BUTTON_DRAWER;
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Selected: " + ((LoginEntry) mSpinner.getSelectedItem()).courseName);
                mHelpButton.setEnabled(true);
                mHelpButton.setText(getString(R.string.ask_for_help));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Asking for help, course: " + ((LoginEntry) mSpinner.getSelectedItem()).courseName);
        mHelpButton.setEnabled(false);
        String message = mHelpText.getText().toString();
        ApiClient.askHelp((LoginEntry) mSpinner.getSelectedItem(), true, message, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                Log.d(TAG, "Asked help!");
                mHelpButton.setText(getString(R.string.asked_for_help));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "error: " + error.getMessage());
                mHelpButton.setEnabled(true);
            }
        });
    }
}
