package com.lovelylavette.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovelylavette.android.R;
import com.lovelylavette.android.model.Trip;

public class NeedsFragment extends Fragment {
    private static final String TAG = "NeedsFragment";
    private static final String ARG_TRIP = "trip";
    private Trip trip;


    public NeedsFragment() {
    }

    public static NeedsFragment newInstance(Trip trip) {
        NeedsFragment fragment = new NeedsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, trip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trip = (Trip) getArguments().getSerializable(ARG_TRIP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_needs, container, false);
    }
}
