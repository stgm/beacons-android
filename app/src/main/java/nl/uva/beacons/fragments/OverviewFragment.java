/*
 * Created by Sander Lijbrink.
 * Copyright (c) 2014 Sander Lijbrink
 */

package nl.uva.beacons.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.uva.beacons.R;

/**
 * Created by sander on 11/2/14.
 */
public class OverviewFragment extends Fragment {
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_main, container, false);
    TextView textView = (TextView)v.findViewById(R.id.section_label);
    textView.setText("Fragment " + getArguments().getInt("POSITION"));
    return v;
  }
}
