package com.lovelylavette.android.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.amadeus.resources.Location;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.lovelylavette.android.R;
import com.lovelylavette.android.api.AmadeusApi;
import com.lovelylavette.android.api.GooglePlacesApi;
import com.lovelylavette.android.model.Trip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class FlightsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "FlightsFragment";
    private static final int AUTOCOMPLETE_ORIGIN_REQUEST_CODE = 2;
    private static final int AUTOCOMPLETE_DESTINATION_REQUEST_CODE = 3;
    private static final String ARG_TRIP = "trip";
    private static final String SPINNER_PROMPT = "Where?";
    private static final String SELECT_PLACE = "[ SEARCH BY CITY ]";
    private Context context;
    private Trip trip;
    private List<String> originList = new ArrayList<>(Arrays.asList(SPINNER_PROMPT, SELECT_PLACE));
    private List<String> destinationList = new ArrayList<>(Arrays.asList(SPINNER_PROMPT, SELECT_PLACE));
    private ArrayAdapter originAdapter;
    private ArrayAdapter destinationAdapter;

    @BindView(R.id.from_spinner)
    Spinner fromSpinner;
    @BindView(R.id.to_spinner)
    Spinner toSpinner;


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
        View view = inflater.inflate(R.layout.fragment_flights, container, false);
        ButterKnife.bind(this, view);
        setupSpinners();
        return view;
    }

    private void setupSpinners() {
        originAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, originList);
        destinationAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, destinationList);

        if(trip.getDestination() != null) {
            getAirports(trip.getDestination(), "d");
        }

        originAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destinationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromSpinner.setAdapter(originAdapter);
        toSpinner.setAdapter(destinationAdapter);

        fromSpinner.setOnItemSelectedListener(this);
        toSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.from_spinner && position == parent.getCount() - 1) {
            GooglePlacesApi.showAutocomplete(context, this, AUTOCOMPLETE_ORIGIN_REQUEST_CODE);
        } else if (parent.getId() == R.id.to_spinner && position == parent.getCount() - 1) {
            GooglePlacesApi.showAutocomplete(context, this, AUTOCOMPLETE_DESTINATION_REQUEST_CODE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_ORIGIN_REQUEST_CODE) {
            processOriginResponse(resultCode, data);
        } else if (requestCode == AUTOCOMPLETE_DESTINATION_REQUEST_CODE) {
            processDestinationResponse(resultCode, data);
        }
    }

    private void processOriginResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place origin = Autocomplete.getPlaceFromIntent(data);
            trip.setOrigin(origin);
            Log.i(TAG, "Selected Origin: " + origin.toString());
            getAirports(origin, "o");
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
            fromSpinner.setSelection(0, true);
        }
    }

    private void processDestinationResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place destination = Autocomplete.getPlaceFromIntent(data);
            trip.setDestination(destination);
            Log.i(TAG, "Selected Destination: " + destination.toString());
            getAirports(destination, "d");
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
            toSpinner.setSelection(0, true);
        }
    }

    private void getAirports(Place place, String placeType) {
        AmadeusApi.findNearestRelevantAirports airportsCall =
                new AmadeusApi.findNearestRelevantAirports();
        airportsCall.setOnResponseListener(locations -> updateSpinner(locations, placeType));
        airportsCall.execute(place.getLatLng());
    }

    private void updateSpinner(Location[] airportArray, String placeType) {
        if(airportArray != null && airportArray.length >= 1) {
            switch(placeType) {
                case "o":
                    addAirports(airportArray, originList, fromSpinner, originAdapter);
                    break;
                case "d":
                    addAirports(airportArray, destinationList, toSpinner, destinationAdapter);
                    break;
            }
        }
    }

    private void addAirports(Location[] airportArray, List<String> spinnerList, Spinner spinner, ArrayAdapter adapter) {
        spinnerList.clear();
        adapter.notifyDataSetChanged();
        for(Location airport : airportArray) {
            Log.i(TAG, airport.getIataCode() + " - " + airport.getDetailedName());
            spinnerList.add(airport.getIataCode() + " - " + airport.getDetailedName());
        }
        spinnerList.add(SELECT_PLACE);
        adapter.notifyDataSetChanged();
        spinner.setSelection(0, true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
