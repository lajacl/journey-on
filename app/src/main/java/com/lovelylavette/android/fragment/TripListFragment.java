package com.lovelylavette.android.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lovelylavette.android.R;
import com.lovelylavette.android.util.TripPrefs;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripListFragment extends Fragment {
    private static final String TAG = "TripListFragment";
    private static final String ARG_TRIP = "trip";
    private static final String DELIMITER = ", ";
    private Context context;
    private static TripPrefs tripPrefs;

    @BindView(R.id.trip_message)
    TextView message;


    public TripListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_list, container, false);
        ButterKnife.bind(this, view);
        showMessage();
        return view;
    }

    private void showMessage() {
        tripPrefs = new TripPrefs(context);
        List tripList = tripPrefs.getTrips();
        if(tripList == null || tripList.isEmpty()) {
            message.setText(R.string.no_trips);
        } else {
            message.setText(String.format(Locale.getDefault(), "You have %d saved trips\n\n%s", tripList.size(), message.getText()));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
