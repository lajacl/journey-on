package com.lovelylavette.android.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amadeus.resources.FlightDestination;
import com.lovelylavette.android.R;
import com.lovelylavette.android.adapter.DestinationAdapter;
import com.lovelylavette.android.api.AmadeusApi;
import com.lovelylavette.android.model.User;
import com.lovelylavette.android.util.AppPrefs;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DestinationFragment extends Fragment {
    private static final String TAG = "InspirationFragment";
    private Context context;
    private AppPrefs prefs;
    private User user;
    private DestinationAdapter destinationAdapter;

    @BindView(R.id.destination_header)
    TextView header;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.destination_recycler)
    RecyclerView destinationRecycler;


    public DestinationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_destination, container, false);
        ButterKnife.bind(this, view);
        showUserName();
        setupRecycler();
        getDestinations();
        return view;
    }

    private void showUserName() {
        prefs = new AppPrefs(context);
        user = prefs.getUser();
        if (user != null && user.getName() != null && !user.getName().isEmpty()) {
            header.setText(String.format("Hello %s,\n%s", user.getName(), header.getText()));
        }
    }

    private void setupRecycler() {
        destinationRecycler.setHasFixedSize(true);
        destinationRecycler.setLayoutManager(new LinearLayoutManager(context));
        destinationAdapter = new DestinationAdapter(new FlightDestination[]{}, getResources());
        destinationRecycler.setAdapter(destinationAdapter);
    }

    private void getDestinations() {
        progressBar.setVisibility(View.VISIBLE);
        AmadeusApi.findDestinationFlights destinationsCall =
                new AmadeusApi.findDestinationFlights();

        destinationsCall.setOnResponseListener(destinations -> {
            progressBar.setVisibility(View.GONE);

            if (destinations == null || destinations.length == 0) {
                Toast.makeText(context, R.string.no_destinations, Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, destinations.length + " Destinations Found");
                destinationAdapter.updateData(destinations);
            }
        });
        destinationsCall.execute("ATL");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
