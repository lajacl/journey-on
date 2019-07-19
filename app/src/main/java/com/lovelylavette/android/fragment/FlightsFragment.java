package com.lovelylavette.android.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amadeus.resources.FlightOffer;
import com.amadeus.resources.Location;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.lovelylavette.android.R;
import com.lovelylavette.android.adapter.FlightAdapter;
import com.lovelylavette.android.api.AmadeusApi;
import com.lovelylavette.android.api.GooglePlacesApi;
import com.lovelylavette.android.model.Trip;
import com.lovelylavette.android.util.DateUtils;
import com.lovelylavette.android.util.ResponseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class FlightsFragment extends Fragment implements AdapterView.OnItemSelectedListener, ResponseListener.FlightOffer {
    private static final String TAG = "FlightsFragment";
    private static final int AUTOCOMPLETE_ORIGIN_REQUEST_CODE = 2;
    private static final int AUTOCOMPLETE_DESTINATION_REQUEST_CODE = 3;
    private static final int REQUEST_DATE = 0;
    private static final String ARG_TRIP = "trip";
    private static final String SPINNER_PROMPT = "Where?";
    private static final String SELECT_PLACE = "[ SEARCH BY CITY ]";
    private Context context;
    private Trip trip;
    private List<String> originList = new ArrayList<>(Arrays.asList(SPINNER_PROMPT, SELECT_PLACE));
    private List<String> destinationList = new ArrayList<>(Arrays.asList(SPINNER_PROMPT, SELECT_PLACE));
    private ArrayAdapter originAdapter;
    private ArrayAdapter destinationAdapter;
    private FlightAdapter flightAdapter;

    @BindView(R.id.expand)
    ImageView expandFilter;
    @BindView(R.id.filter_card)
    CardView filterCard;
    @BindView(R.id.from_spinner)
    Spinner fromSpinner;
    @BindView(R.id.to_spinner)
    Spinner toSpinner;
    @BindView(R.id.date_text)
    TextView dateTextView;
    @BindView(R.id.search)
    Button searchBtn;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.flight_recycler)
    RecyclerView flightRecycler;
    @BindView(R.id.next_btn)
    Button nextBtn;


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
        } else {
            trip = new Trip();
        }
        Log.i(TAG, trip.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flights, container, false);
        ButterKnife.bind(this, view);
        setupSpinners();
        setupDate();
        searchBtn.setOnClickListener(v -> getFlights());
        setupRecycler();
        setExpandFilterClickListener();
        checkFlightData();
        setNextBtnOnClickListener();
        return view;
    }

    private void setupSpinners() {
        originAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, originList);
        destinationAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, destinationList);

        if (trip != null && trip.getDestination() != null && trip.getDestinationAirport() == null) {
            getAirports(trip.getDestination(), "d");
        }

        originAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destinationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromSpinner.setAdapter(originAdapter);
        toSpinner.setAdapter(destinationAdapter);

        fromSpinner.setOnItemSelectedListener(this);
        toSpinner.setOnItemSelectedListener(this);
    }

    private void setupDate() {
        displayDate();
        dateTextView.setOnClickListener(v -> {
            DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(trip);
            datePickerFragment.setTargetFragment(FlightsFragment.this, REQUEST_DATE);
            getFragmentManager().beginTransaction().replace(R.id.frag_container, datePickerFragment)
                    .addToBackStack(null).commit();
        });
    }

    private void displayDate() {
        if (trip.isRoundTrip() && trip.getDepartureDate() != null && trip.getReturnDate() != null) {
            dateTextView.setText(DateUtils.getFormattedRange(trip.getDepartureDate(), trip.getReturnDate()));
        } else if (!trip.isRoundTrip() && trip.getDepartureDate() != null) {
            dateTextView.setText(DateUtils.getFormattedDate(trip.getDepartureDate()));
        }
    }

    private void setupRecycler() {
        flightRecycler.setHasFixedSize(true);
        flightRecycler.setLayoutManager(new LinearLayoutManager(context));
        flightAdapter = new FlightAdapter(new FlightOffer[]{}, this, getResources());
        flightRecycler.setAdapter(flightAdapter);
    }

    private void setExpandFilterClickListener() {
        expandFilter.setOnClickListener(v -> {
            if (filterCard.getVisibility() == View.VISIBLE && flightAdapter.getItemCount() >= 1) {
                filterCard.setVisibility(View.GONE);
                expandFilter.setImageResource(R.drawable.ic_expand_more);
            } else {
                filterCard.setVisibility(View.VISIBLE);
                expandFilter.setImageResource(R.drawable.ic_expand_less);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String oldAirport;
        if (parent.getId() == R.id.from_spinner && position == parent.getCount() - 1) {
            GooglePlacesApi.showAutocomplete(context, this, AUTOCOMPLETE_ORIGIN_REQUEST_CODE);
        } else if (parent.getId() == R.id.to_spinner && position == parent.getCount() - 1) {
            GooglePlacesApi.showAutocomplete(context, this, AUTOCOMPLETE_DESTINATION_REQUEST_CODE);
        } else if (parent.getId() == R.id.from_spinner && position != parent.getCount() - 1) {
            oldAirport = trip.getOriginAirport();
            trip.setOriginAirport(originList.get(position).split(" - ")[0]);
            if (!trip.getOriginAirport().equals(oldAirport)) {
                checkFlightData();
            }
        } else if (parent.getId() == R.id.to_spinner && position != parent.getCount() - 1) {
            oldAirport = trip.getDestinationAirport();
            trip.setDestinationAirport(destinationList.get(position).split(" - ")[0]);
            if (!trip.getOriginAirport().equals(oldAirport)) {
                checkFlightData();
            }
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
        } else if (requestCode == REQUEST_DATE) {
            trip = (Trip) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            displayDate();
            checkFlightData();
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
        airportsCall.setOnResponseListener(locations -> {

            if (locations == null || locations.length == 0) {
                Toast.makeText(context, R.string.no_airports, Toast.LENGTH_SHORT).show();
                switch (placeType) {
                    case "o":
                        fromSpinner.setSelection(0);
                        break;
                    case "d":
                        toSpinner.setSelection(0);
                        break;
                }
            } else {
                Log.i(TAG, locations.length + " Airports Found");
                updateSpinner(locations, placeType);
            }
        });
        airportsCall.execute(place.getLatLng());

    }

    private void updateSpinner(Location[] airportArray, String placeType) {
        String oldAirport;
        switch (placeType) {
            case "o":
                oldAirport = trip.getOriginAirport();
                addAirports(airportArray, originList, originAdapter, fromSpinner);
                trip.setOriginAirport(airportArray[0].getIataCode());
                if (!trip.getOriginAirport().equals(oldAirport)) {
                    checkFlightData();
                }
                break;
            case "d":
                oldAirport = trip.getDestinationAirport();
                addAirports(airportArray, destinationList, destinationAdapter, toSpinner);
                trip.setDestinationAirport(airportArray[0].getIataCode());
                if (!trip.getDestinationAirport().equals(oldAirport)) {
                    checkFlightData();
                }
                break;
        }
    }

    private void addAirports(Location[] airportArray, List<String> spinnerList, ArrayAdapter adapter, Spinner spinner) {
        spinnerList.clear();
        for (Location airport : airportArray) {
            Log.i(TAG, airport.getIataCode() + " - " + airport.getDetailedName());
            spinnerList.add(airport.getIataCode() + " - " + airport.getDetailedName());
        }
        spinnerList.add(SELECT_PLACE);
        adapter.notifyDataSetChanged();
        spinner.setSelection(0, true);
    }

    private void checkFlightData() {
        if (trip.getOriginAirport() != null && trip.getDestinationAirport() != null &&
                trip.getDepartureDate() != null) {
            searchBtn.setVisibility(View.VISIBLE);
        } else {
            searchBtn.setVisibility(View.GONE);
        }
    }

    private void getFlights() {
        searchBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        AmadeusApi.findLowFareFlights flightsCall =
                new AmadeusApi.findLowFareFlights();

        flightsCall.setOnResponseListener(flightOffers -> {
            progressBar.setVisibility(View.GONE);

            if (flightOffers == null || flightOffers.length == 0) {
                flightAdapter.updateData(new FlightOffer[]{});
                expandFilter.setVisibility(View.GONE);
                Toast.makeText(context, R.string.no_flights, Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, flightOffers.length + " Flight Offers Found");
                filterCard.setVisibility(View.GONE);
                flightAdapter.updateData(flightOffers);
                expandFilter.setVisibility(View.VISIBLE);
            }
        });

        flightsCall.execute(trip);
    }

    @Override
    public void onResponseReceive(FlightOffer flightOffer) {
        trip.setFlight(flightOffer);
        nextBtn.setVisibility(View.VISIBLE);
        Toast.makeText(context, "Flight Selected", Toast.LENGTH_SHORT).show();
    }

    private void setNextBtnOnClickListener() {
        nextBtn.setOnClickListener(v -> {
            if (trip.isHotelNeeded()) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frag_container, HotelsFragment.newInstance(trip))
                    .addToBackStack(null).commit();
            } else if (trip.isSightsNeeded()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.frag_container, SightsFragment.newInstance(trip))
                        .addToBackStack(null).commit();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
