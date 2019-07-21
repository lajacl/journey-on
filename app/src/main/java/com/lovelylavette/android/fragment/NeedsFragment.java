package com.lovelylavette.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.lovelylavette.android.R;
import com.lovelylavette.android.model.CoSpace;
import com.lovelylavette.android.model.Trip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NeedsFragment extends Fragment {
    private static final String TAG = "NeedsFragment";
    private static final String ARG_TRIP = "trip";
    private Trip trip;
    private List<CoSpace> coSpaceList = new ArrayList<>();

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
    @BindView(R.id.travel_reason_rg)
    RadioGroup reasonsRadio;


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
        } else {
            trip = new Trip();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_needs, container, false);
        ButterKnife.bind(this, view);
        addCheckedChangeListeners();
        addReasonsCheckedListener();
        addNextOnClickListener();
        return view;
    }

    private void addReasonsCheckedListener() {
        reasonsRadio.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.work_radio:
                    trip.setTravelReason("work");
            }
        });
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

            if (flightCheckBox.isChecked() || hotelCheckBox.isChecked()) {
                cardNav.setVisibility(View.VISIBLE);
                sightsCheckBox.setEnabled(true);
            } else {
                cardNav.setVisibility(View.GONE);
                sightsCheckBox.setEnabled(false);
            }

            Log.i(TAG, trip.toString());
        };

        flightCheckBox.setOnCheckedChangeListener(checkedChangeListener);
        hotelCheckBox.setOnCheckedChangeListener(checkedChangeListener);
        sightsCheckBox.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void addNextOnClickListener() {
        nextLink.setOnClickListener(v -> {
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            if (trip.isFlightNeeded()) {
                ft.replace(R.id.frag_container, FlightsFragment.newInstance(trip))
                        .addToBackStack(null).commit();
            } else if (trip.isHotelNeeded()) {
                ft.replace(R.id.frag_container, HotelsFragment.newInstance(trip))
                        .addToBackStack(null).commit();
            } else if (trip.isSightsNeeded()) {
                ft.replace(R.id.frag_container, SightsFragment.newInstance(trip))
                        .addToBackStack(null).commit();
            } else {
                ft.replace(R.id.frag_container, TripFragment.newInstance(trip))
                        .addToBackStack(null).commit();
            }
        });
    }
}
