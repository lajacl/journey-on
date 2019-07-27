package com.lovelylavette.android.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amadeus.resources.PointOfInterest;
import com.lovelylavette.android.R;
import com.lovelylavette.android.adapter.SightAdapter;
import com.lovelylavette.android.api.AmadeusApi;
import com.lovelylavette.android.model.Trip;
import com.lovelylavette.android.util.ResponseListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SightsFragment extends Fragment implements ResponseListener.Sights {
    private static final String TAG = "SightsFragment";
    private static final String ARG_TRIP = "trip";
    private Context context;
    private Trip trip;
    private SightAdapter sightAdapter;

    @BindView(R.id.expand)
    ImageView expandFilter;
    @BindView(R.id.filter_card)
    CardView filterCard;
    @BindView(R.id.location)
    TextView locationText;
    @BindView(R.id.progress)
    LinearLayout progressBar;
    @BindView(R.id.sight_recycler)
    RecyclerView sightRecycler;
    @BindView(R.id.search)
    Button searchBtn;
    @BindView(R.id.next_btn)
    Button nextBtn;


    public SightsFragment() {
    }

    public static SightsFragment newInstance(Trip trip) {
        SightsFragment fragment = new SightsFragment();
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
        Log.i(TAG, trip.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sights, container, false);
        ButterKnife.bind(this, view);

        setInitialValues();
        searchBtn.setOnClickListener(v -> getSights());
        setExpandFilterClickListener();
        setupRecycler();
        checkSightData();
        setNextBtnOnClickListener();
        return view;
    }

    private void setInitialValues() {
        if (trip.getDestination() != null) {
            locationText.setText(trip.getDestination().getAddress());
        }
    }

    private void setupRecycler() {
        sightRecycler.setHasFixedSize(true);
        sightRecycler.setLayoutManager(new LinearLayoutManager(context));
        sightAdapter = new SightAdapter(new PointOfInterest[]{}, this, getResources());
        sightRecycler.setAdapter(sightAdapter);
    }

    private void setExpandFilterClickListener() {
        expandFilter.setOnClickListener(v -> {
            if (filterCard.getVisibility() == View.VISIBLE && sightAdapter.getItemCount() >= 1) {
                filterCard.setVisibility(View.GONE);
                expandFilter.setImageResource(R.drawable.ic_expand_more);
                searchBtn.setVisibility(View.GONE);
            } else {
                filterCard.setVisibility(View.VISIBLE);
                expandFilter.setImageResource(R.drawable.ic_expand_less);
                searchBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkSightData() {
        if (trip.getDestination() != null) {
            getSights();
        } else {
            searchBtn.setVisibility(View.GONE);
        }
    }

    private void getSights() {
        searchBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        AmadeusApi.findPointsOfInterest sightsCall =
                new AmadeusApi.findPointsOfInterest();

        sightsCall.setOnResponseListener(pointsOfInterest -> {
            progressBar.setVisibility(View.GONE);

            if (pointsOfInterest == null || pointsOfInterest.length == 0) {
                sightAdapter.updateData(new PointOfInterest[]{});
                expandFilter.setVisibility(View.GONE);
                searchBtn.setVisibility(View.VISIBLE);
                Toast.makeText(context, R.string.no_sights, Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, pointsOfInterest.length + " Points of Interest Found");
                filterCard.setVisibility(View.GONE);
                sightAdapter.updateData(pointsOfInterest);
                expandFilter.setVisibility(View.VISIBLE);
            }
        });

        sightsCall.execute(trip.getDestination().getLatLng());
    }

    @Override
    public void onResponseReceive(List<PointOfInterest> pointsOfInterest) {
        if (trip.getSights() == null || pointsOfInterest.size() > trip.getSights().size()) {
            Toast.makeText(context, "Sight Selected", Toast.LENGTH_SHORT).show();
        }
        trip.setSights(pointsOfInterest);
        nextBtn.setVisibility(View.VISIBLE);
    }

    private void setNextBtnOnClickListener() {
        nextBtn.setOnClickListener(v -> {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frag_container, TripFragment.newInstance(trip))
                    .addToBackStack(null).commit();
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
