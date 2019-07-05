package com.lovelylavette.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovelylavette.android.R;
import com.lovelylavette.android.model.Trip;

public class FlightsFragment extends Fragment {
    private static final String TAG = "FlightsFragment";
    private static final String ARG_TRIP = "trip";
    private Trip trip;


    public FlightsFragment() {
    }

    public static FlightsFragment newInstance(Trip trip) {
        FlightsFragment fragment = new FlightsFragment();
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
            Log.i(TAG, trip.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flights, container, false);
    }
}
