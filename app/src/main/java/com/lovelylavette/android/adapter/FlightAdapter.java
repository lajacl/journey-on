package com.lovelylavette.android.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amadeus.resources.Airline;
import com.amadeus.resources.FlightOffer;
import com.lovelylavette.android.R;
import com.lovelylavette.android.api.AmadeusApi;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightHolder> {
    private final String TAG = "Flight Adapter";
    private FlightOffer[] flightOffers;
    private HashMap<String, Airline> airlineMap = new HashMap<>();

    static class FlightHolder extends RecyclerView.ViewHolder {TextView carrier = itemView.findViewById(R.id.carrier);
        TextView flightNum = itemView.findViewById(R.id.flight_num);
        TextView price = itemView.findViewById(R.id.price);
        TextView travelClass = itemView.findViewById(R.id.travel_class);
        TextView airports = itemView.findViewById(R.id.airports);
        TextView time = itemView.findViewById(R.id.time);
        TextView duration = itemView.findViewById(R.id.duration);
        TextView stops = itemView.findViewById(R.id.stops);

        FlightHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public FlightAdapter(FlightOffer[] flightOffers) {
        this.flightOffers = flightOffers;
    }

    @NonNull
    @Override
    public FlightHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_flight_offer, viewGroup, false);

        return new FlightHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightHolder holder, int i) {
        FlightOffer.OfferItem offer = flightOffers[i].getOfferItems()[0];
        FlightOffer.FlightSegment segment = offer.getServices()[0].getSegments()[0].getFlightSegment();

        setAirlineName(holder.carrier, segment.getCarrierCode());
        holder.flightNum.setText(String.format("%s %s", segment.getCarrierCode(), segment.getNumber()));
        setPrice(holder.price, offer);
        setTravelClass(holder.travelClass, offer);
        setAirports(holder.airports, offer);
        setFlightTimes(holder.time, segment);
        holder.duration.setText(TextUtils.split(segment.getDuration(), "T")[1].replace("H", "H "));
        setNumStops(holder.stops, offer);
    }

    private void setAirlineName(TextView tv, String carrierCode) {
        if (airlineMap.containsKey(carrierCode)) {
//            Log.i(TAG, "Airline Found in Map");
            tv.setText(airlineMap.get(carrierCode).getBusinessName());
        } else {
            AmadeusApi.findAirlines airlinesCall =
                    new AmadeusApi.findAirlines();
            airlinesCall.setOnResponseListener(airlines -> {
                if (airlines == null || airlines.length == 0) {
                    Log.i(TAG, "Failed to get airline results");
                } else {
                    airlineMap.put(carrierCode, airlines[0]);
//                    Log.i(TAG, airlineMap.size() + " Airlines in Map: " + airlineMap.keySet().toString());
                    tv.setText(airlineMap.get(carrierCode).getBusinessName());
                }
            });
            airlinesCall.execute(carrierCode);
        }
    }

    private void setPrice(TextView tv, FlightOffer.OfferItem offer) {
        NumberFormat currencyFormatter = new DecimalFormat("0.00");
        String price = currencyFormatter.format(offer.getPricePerAdult().getTotal() + offer.getPricePerAdult().getTotalTaxes());
        tv.setText(String.format(Locale.getDefault(), "$%s", price));
    }

    private void setTravelClass(TextView tv, FlightOffer.OfferItem offer) {
        String travelClass = offer.getServices()[0].getSegments()[0].getPricingDetailPerAdult().getTravelClass();
        tv.setText(String.format("%s%s", travelClass.substring(0, 1), travelClass.substring(1).toLowerCase()));
    }

    private void setAirports(TextView tv, FlightOffer.OfferItem offer) {
        FlightOffer.Segment[] segments = offer.getServices()[0].getSegments();
        String airports = "";
        int i = 1;

        for(FlightOffer.Segment stop : offer.getServices()[0].getSegments()) {
            airports = airports.concat(stop.getFlightSegment().getDeparture().getIataCode() + " > ");

            if(i++ == segments.length) {
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

    private void setNumStops(TextView tv, FlightOffer.OfferItem offer) {
        int num_stops = offer.getServices()[0].getSegments().length;
        if (num_stops == 1) {
            tv.setText(R.string.nonstop);
        } else {
            tv.setText(String.format("%s Stop(s)", num_stops - 1));
        }
    }

    public void updateData(FlightOffer[] flightOffers) {
        this.flightOffers = flightOffers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return flightOffers.length;
    }
}
