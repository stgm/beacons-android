package nl.uva.beacons.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.uva.beacons.LoginEntry;
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

    public static StudentDetailFragment newInstance(List<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> studentInfo) {
        StudentDetailFragment studentDetailFragment = new StudentDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_STUDENT_INFO, (Serializable) studentInfo);
        studentDetailFragment.setArguments(bundle);
        return studentDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student_detail, container, false);

        TextView studentName = (TextView) v.findViewById(R.id.student_detail_name);
        TextView studentQuestion = (TextView) v.findViewById(R.id.student_detail_question);
        CardView cardQuestionView = (CardView) v.findViewById(R.id.card_view);
        LinearLayout coursesView = (LinearLayout) v.findViewById(R.id.student_detail_courses);
        coursesView.removeAllViews();

        ArrayList<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>> studentInfo
            = (ArrayList<AbstractMap.SimpleEntry<LoginEntry, Map<String, String>>>) getArguments().getSerializable(KEY_STUDENT_INFO);

        Time time = new Time();
        for (AbstractMap.SimpleEntry<LoginEntry, Map<String, String>> entry : studentInfo) {
            View subView = inflater.inflate(R.layout.item_student_course_detail, coursesView, false);

            time.parse3339(entry.getValue().get(BeaconApi.ATTR_UPDATED));
            time.switchTimezone(Time.getCurrentTimezone());
            ((TextView) subView.findViewById(R.id.detail_course_title)).setText(entry.getKey().courseName);
            ((TextView) subView.findViewById(R.id.detail_course_last_updated)).setText(time.format("%c"));
            coursesView.addView(subView);
        }

        final Button confirmHelpButton = (Button) v.findViewById(R.id.button_confirm_help);

        String questionText = null;
        for (AbstractMap.SimpleEntry<LoginEntry, Map<String, String>> map : studentInfo) {
            String q = map.getValue().get(BeaconApi.ATTR_QUESTION);
            if (q != null && !q.isEmpty()) {
                questionText = q;
                break;
            }
        }
        if (questionText != null) {
            studentQuestion.setText(questionText);
        } else {
            cardQuestionView.setVisibility(View.GONE);
        }

        final Map<String, String> map = studentInfo.get(0).getValue();
        studentName.setText(map.get(BeaconApi.ATTR_NAME));

        confirmHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Clearing help for " + map.get(BeaconApi.ATTR_NAME) + "student id " + map.get(BeaconApi.ATTR_ID));
                confirmHelpButton.setEnabled(false);
                ApiClient.clearHelp(map.get(BeaconApi.ATTR_ID), new CancelableCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement jsonElement, Response response) {
                        Log.d(TAG, "Cleared help, student id = " + map.get(BeaconApi.ATTR_ID));
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
        return getString(R.string.student_details);
    }

    @Override
    protected int getHomeButtonMode() {
        return BaseFragment.HOME_BUTTON_BACK;
    }
}
