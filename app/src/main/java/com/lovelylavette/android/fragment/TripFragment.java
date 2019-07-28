package com.lovelylavette.android.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amadeus.resources.Airline;
import com.amadeus.resources.FlightOffer;
import com.amadeus.resources.HotelOffer;
import com.amadeus.resources.PointOfInterest;
import com.lovelylavette.android.R;
import com.lovelylavette.android.api.AmadeusApi;
import com.lovelylavette.android.model.Trip;
import com.lovelylavette.android.util.DateUtils;
import com.lovelylavette.android.util.TripPrefs;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class TripFragment extends Fragment {
    private static final String TAG = "TripFragment";
    private static final String ARG_TRIP = "trip";
    private static final String DELIMITER = ", ";
    private Context context;
    private static TripPrefs tripPrefs;
    private Trip trip;
    private HashMap<String, Airline> airlineMap = new HashMap<>();

    @BindView(R.id.flight_label)
    TextView flightLabel;
    @BindView(R.id.flight_offer_card)
    CardView flightCard;
    @BindView(R.id.flight_container)
    LinearLayout flightContainer;
    @BindView(R.id.flight_dates)
    TextView flightDates;
    @BindView(R.id.hotel_label)
    TextView hotelLabel;
    @BindView(R.id.hotel_offer_card)
    CardView hotelCard;
    @BindView(R.id.hotel_dates)
    TextView hotelDates;
    @BindView(R.id.name)
    TextView hotelName;
    @BindView(R.id.address)
    TextView hotelAddress;
    @BindView(R.id.price)
    TextView hotelPrice;
    @BindView(R.id.room_type)
    TextView hotelRoomType;
    @BindView(R.id.amenities)
    TextView hotelAmenities;
    @BindView(R.id.sights_label)
    TextView sightsLabel;
    @BindView(R.id.sights_ll)
    LinearLayout sightsLayout;
    @BindView(R.id.save_btn)
    Button saveBtn;


    public TripFragment() {
    }

    public static TripFragment newInstance(Trip trip) {
        TripFragment fragment = new TripFragment();
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
        View view = inflater.inflate(R.layout.fragment_trip, container, false);
        ButterKnife.bind(this, view);
        tripPrefs = new TripPrefs(context);
        setSaveBtnOnClickListener();
        showTripData();
        return view;
    }

    private void setSaveBtnOnClickListener() {
        saveBtn.setOnClickListener(v -> {
            trip.setSaveDate(Calendar.getInstance());
            tripPrefs.saveTrip(trip);
            Toast.makeText(context, R.string.trip_saved, Toast.LENGTH_SHORT).show();
            saveBtn.setVisibility(GONE);
        });
    }

    private void showTripData() {
        if (trip.getFlight() != null) {
            showFlight();
        } else {
            flightLabel.setVisibility(GONE);
            flightCard.setVisibility(GONE);
        }

        if (trip.getHotel() != null) {
            showHotel();
        } else {
            hotelLabel.setVisibility(GONE);
            hotelCard.setVisibility(GONE);
        }

        if (trip.getSights() != null && trip.getSights().size() > 0) {
            showSights();
        } else {
            sightsLabel.setVisibility(GONE);
            sightsLayout.setVisibility(GONE);
        }
    }

    private void showFlight() {
        FlightOffer.OfferItem offer = trip.getFlight().getOfferItems()[0];
        FlightOffer.Service[] services = offer.getServices();

        if (services.length > 1) {
            flightDates.setText(String.format("%s\n", DateUtils.getFormattedRange(trip.getDepartureDate(), trip.getReturnDate())));
        } else {
            flightDates.setText(String.format("%s\n", DateUtils.getFormattedDate(trip.getDepartureDate())));
        }

        for (int i = 0; i < services.length; i++) {
            FlightOffer.Service service = services[i];
            FlightOffer.FlightSegment segment = service.getSegments()[0].getFlightSegment();

            View serviceView = getLayoutInflater().inflate(R.layout.item_flight_service, flightContainer, false);
            if (i > 0) {
                serviceView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
            }
            flightContainer.addView(serviceView);

            TextView price = serviceView.findViewById(R.id.price);
            TextView carrier = serviceView.findViewById(R.id.carrier);
            TextView flightNum = serviceView.findViewById(R.id.flight_num);
            TextView travelClass = serviceView.findViewById(R.id.travel_class);
            TextView airports = serviceView.findViewById(R.id.airports);
            TextView time = serviceView.findViewById(R.id.time);
            TextView duration = serviceView.findViewById(R.id.duration);
            TextView stops = serviceView.findViewById(R.id.stops);

            setPrice(price, offer, i == 0);
            setAirlineName(carrier, segment.getCarrierCode());
            flightNum.setText(String.format("%s %s", segment.getCarrierCode(), segment.getNumber()));
            setTravelClass(travelClass, service.getSegments()[0]);
            setAirports(airports, service);
            setFlightTimes(time, segment);
            duration.setText(TextUtils.split(segment.getDuration(), "T")[1].replace("H", "H "));
            setNumStops(stops, service.getSegments().length);
        }
    }

    private void setPrice(TextView tv, FlightOffer.OfferItem offer, boolean isFirst) {
        if (isFirst) {
            NumberFormat currencyFormatter = new DecimalFormat("0.00");
            String price = currencyFormatter.format(offer.getPrice().getTotal() + offer.getPrice().getTotalTaxes());
            tv.setText(String.format(Locale.getDefault(), "$%s", price));
        }
    }

    private void setAirlineName(TextView tv, String carrierCode) {
        if (airlineMap.containsKey(carrierCode)) {
            tv.setText(airlineMap.get(carrierCode).getBusinessName());
        } else {
            AmadeusApi.findAirlines airlinesCall =
                    new AmadeusApi.findAirlines();
            airlinesCall.setOnResponseListener(airlines -> {
                if (airlines == null || airlines.length == 0) {
                    Log.i(TAG, "Failed to get airline");
                } else {
                    airlineMap.put(carrierCode, airlines[0]);
                    tv.setText(airlineMap.get(carrierCode).getBusinessName());
                }
            });
            airlinesCall.execute(carrierCode);
        }
    }

    private void setTravelClass(TextView tv, FlightOffer.Segment segment) {
        String travelClass = segment.getPricingDetailPerAdult().getTravelClass();
        tv.setText(String.format("%s%s", travelClass.substring(0, 1), travelClass.substring(1).toLowerCase()));
    }

    private void setAirports(TextView tv, FlightOffer.Service service) {
        FlightOffer.Segment[] segments = service.getSegments();
        String airports = "";
        int i = 1;

        for (FlightOffer.Segment stop : service.getSegments()) {
            airports = airports.concat(stop.getFlightSegment().getDeparture().getIataCode() + " > ");

            if (i++ == segments.length) {
                airports = airports.concat(stop.getFlightSegment().getArrival().getIataCode());
            }
        }
        tv.setText(airports);
    }

    private void setFlightTimes(TextView tv, FlightOffer.FlightSegment segment) {
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma", Locale.getDefault());

        Date departDate = parseFormat.parse(segment.getDeparture().getAt().replace("T", " "), new ParsePosition(0));
        String departTime = timeFormat.format(departDate);

        Date arriveDate = parseFormat.parse(segment.getArrival().getAt().replace("T", " "), new ParsePosition(0));
        String arriveTime = timeFormat.format(arriveDate);

        tv.setText(String.format("%s - %s", departTime, arriveTime));
    }

    private void setNumStops(TextView tv, int num_segments) {
        int stops = num_segments - 1;

        if (stops == 0) {
            tv.setText(R.string.nonstop);
        } else if (stops == 1) {
            tv.setText(String.format("%s Stop", stops));
        } else {
            tv.setText(String.format("%s Stop(s)", stops));
        }
    }

    private void showHotel() {
        HotelOffer hotelOffer = trip.getHotel();
        hotelDates.setText(String.format("%s\n", DateUtils.getFormattedRange(trip.getCheckInDate(), trip.getCheckOutDate())));

        HotelOffer.AddressType address = hotelOffer.getHotel().getAddress();
        StringBuilder addressFull = new StringBuilder();
        addressFull.append(TextUtils.join(DELIMITER, address.getLines()));
        if (address.getCityName() != null) {
            addressFull.append(DELIMITER)
                    .append(address.getCityName());
        }
        if (address.getStateCode() != null) {
            addressFull.append(DELIMITER)
                    .append(address.getStateCode());
        }
        if (address.getCountryCode() != null) {
            addressFull.append(DELIMITER)
                    .append(address.getCountryCode());
        }
        if (address.getPostalCode() != null) {
            addressFull.append(address.getPostalCode());
        }

        hotelName.setText(hotelOffer.getHotel().getName());
        hotelAddress.setText(addressFull);
        hotelPrice.setText(String.format("$%s", hotelOffer.getOffers()[0].getPrice().getTotal()));
        hotelRoomType.setText(hotelOffer.getOffers()[0].getCategory());

        if (hotelOffer.getHotel().getAmenities() != null) {
            hotelAmenities.setText(TextUtils.join(DELIMITER, hotelOffer.getHotel().getAmenities())
                    .toLowerCase().replace("_", " "));
        } else {
            hotelAmenities.setText("NONE");
        }
    }

    private void showSights() {
        for (PointOfInterest pointOfInterest : trip.getSights()) {
            View poiCard = getLayoutInflater().inflate(R.layout.item_poi_offer, sightsLayout, false);

            TextView poiName = poiCard.findViewById(R.id.name);
            TextView poiType = poiCard.findViewById(R.id.type);
            TextView poiTags = poiCard.findViewById(R.id.tags);

            poiName.setText(pointOfInterest.getName());
            poiType.setText(pointOfInterest.getCategory());
            if (pointOfInterest.getTags() != null) {
                poiTags.setText(TextUtils.join(DELIMITER, pointOfInterest.getTags()));
            } else {
                poiTags.setText("NONE");
            }

            sightsLayout.addView(poiCard);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
