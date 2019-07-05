package com.lovelylavette.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.lovelylavette.android.R;
import com.lovelylavette.android.model.Trip;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NeedsFragment extends Fragment {
    private static final String TAG = "NeedsFragment";
    private static final String ARG_TRIP = "trip";
    private Trip trip;

    @BindView(R.id.flight_cb)
    CheckBox flightCheckBox;
    @BindView(R.id.hotel_cb)
    CheckBox hotelCheckBox;
    @BindView(R.id.sights_cb)
    CheckBox sightsCheckBox;
    @BindView(R.id.card_nav)
    LinearLayout cardNav;
    @BindView(R.id.next_link)
    LinearLayout nextLink;


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
        View view = inflater.inflate(R.layout.fragment_needs, container, false);
        ButterKnife.bind(this, view);
        addCheckedChangeListeners();
        addNextOnClickListener();
        return view;
    }

    private void addCheckedChangeListeners() {
        CompoundButton.OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked) -> {
            switch (buttonView.getId()) {
                case R.id.flight_cb:
                    trip.setFlightNeeded(isChecked);
                    break;
                case R.id.hotel_cb:
                    trip.setHotelNeeded(isChecked);
                    break;
                case R.id.sights_cb:
                    trip.setSightsNeeded(isChecked);
                    break;
            }

            if(flightCheckBox.isChecked() || hotelCheckBox.isChecked() || sightsCheckBox.isChecked()
                && cardNav.getVisibility() == View.GONE) {
                cardNav.setVisibility(View.VISIBLE);
            } else if(cardNav.getVisibility() == View.VISIBLE) {
                cardNav.setVisibility(View.GONE);
            }

            Log.i(TAG, trip.toString());
        };

        flightCheckBox.setOnCheckedChangeListener(checkedChangeListener);
        hotelCheckBox.setOnCheckedChangeListener(checkedChangeListener);
        sightsCheckBox.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void addNextOnClickListener() {
        nextLink.setOnClickListener(v -> getFragmentManager().beginTransaction()
                .replace(R.id.frag_container, FlightsFragment.newInstance(trip))
                .addToBackStack(null).commit());
    }
}
