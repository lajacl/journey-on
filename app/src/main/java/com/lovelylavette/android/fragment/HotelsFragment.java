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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amadeus.resources.HotelOffer;
import com.lovelylavette.android.R;
import com.lovelylavette.android.adapter.HotelAdapter;
import com.lovelylavette.android.api.AmadeusApi;
import com.lovelylavette.android.model.Trip;
import com.lovelylavette.android.util.DateUtils;
import com.lovelylavette.android.util.ResponseListener;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HotelsFragment extends Fragment implements ResponseListener.HotelOffer {
    private static final String TAG = "HotelsFragment";
    private static final String ARG_TRIP = "trip";
    private Context context;
    private Trip trip;
    private HotelAdapter hotelAdapter;

    @BindView(R.id.expand)
    ImageView expandFilter;
    @BindView(R.id.filter_card)
    CardView filterCard;
    @BindView(R.id.location)
    TextView locationText;
    @BindView(R.id.date)
    TextView dateText;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.hotel_recycler)
    RecyclerView hotelRecycler;
    @BindView(R.id.search)
    Button searchBtn;
    @BindView(R.id.next_btn)
    Button nextBtn;


    public HotelsFragment() {
    }

    public static HotelsFragment newInstance(Trip trip) {
        HotelsFragment fragment = new HotelsFragment();
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
        View view = inflater.inflate(R.layout.fragment_hotels, container, false);
        ButterKnife.bind(this, view);

        setInitialValues();
        searchBtn.setOnClickListener(v -> getHotels());
        setExpandFilterClickListener();
        setupRecycler();
        checkHotelData();
        setNextBtnOnClickListener();
        return view;
    }

    private void setInitialValues() {
        if (trip.getDestination() != null) {
            locationText.setText(trip.getDestination().getAddress());
        }

        if (trip.getDepartureDate() != null && trip.getReturnDate() != null) {
            trip.setCheckInDate(trip.getDepartureDate());
            if (trip.isRoundTrip()) {
                trip.setCheckOutDate(trip.getReturnDate());
            } else {
                Calendar nextDay = trip.getDepartureDate();
                nextDay.add(Calendar.DAY_OF_MONTH, 1);
                trip.setCheckOutDate(nextDay);
            }
            dateText.setText(DateUtils.getFormattedRange(trip.getDepartureDate(), trip.getReturnDate()));
        }
    }

    private void setupRecycler() {
        hotelRecycler.setHasFixedSize(true);
        hotelRecycler.setLayoutManager(new LinearLayoutManager(context));
        hotelAdapter = new HotelAdapter(new HotelOffer[]{}, this, getResources());
        hotelRecycler.setAdapter(hotelAdapter);
    }

    private void setExpandFilterClickListener() {
        expandFilter.setOnClickListener(v -> {
            if (filterCard.getVisibility() == View.VISIBLE && hotelAdapter.getItemCount() >= 1) {
                filterCard.setVisibility(View.GONE);
                expandFilter.setImageResource(R.drawable.ic_expand_more);
            } else {
                filterCard.setVisibility(View.VISIBLE);
                expandFilter.setImageResource(R.drawable.ic_expand_less);
            }
        });
    }

    private void checkHotelData() {
        if (trip.getDestination() != null && trip.getCheckInDate() != null && trip.getCheckOutDate() != null) {
            searchBtn.setVisibility(View.VISIBLE);
        } else {
            searchBtn.setVisibility(View.GONE);
        }
    }

    private void setNextBtnOnClickListener() {
        nextBtn.setOnClickListener(v -> {
            if (trip.isSightsNeeded()) {
            }
        });
    }

    private void getHotels() {
        searchBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        AmadeusApi.findHotels hotelsCall =
                new AmadeusApi.findHotels();

        hotelsCall.setOnResponseListener(hotelOffers -> {
            progressBar.setVisibility(View.GONE);

            if (hotelOffers == null || hotelOffers.length == 0) {
                hotelAdapter.updateData(new HotelOffer[]{});
                expandFilter.setVisibility(View.GONE);
                Toast.makeText(context, R.string.no_hotels, Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, hotelOffers.length + " Hotel Offers Found");
                filterCard.setVisibility(View.GONE);
                hotelAdapter.updateData(hotelOffers);
                expandFilter.setVisibility(View.VISIBLE);
            }
        });

        hotelsCall.execute(trip);
    }

    @Override
    public void onResponseReceive(HotelOffer hotelOffer) {
        trip.setHotel(hotelOffer);
        nextBtn.setVisibility(View.VISIBLE);
        Toast.makeText(context, "Hotel Selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
