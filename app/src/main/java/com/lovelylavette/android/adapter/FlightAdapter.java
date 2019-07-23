package com.lovelylavette.android.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amadeus.resources.Airline;
import com.amadeus.resources.FlightOffer;
import com.lovelylavette.android.R;
import com.lovelylavette.android.api.AmadeusApi;
import com.lovelylavette.android.util.ResponseListener;

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
    private ResponseListener.FlightOffer listener;
    private Resources resources;
    private LayoutInflater inflater;
    private HashMap<String, Airline> airlineMap = new HashMap<>();
    private FlightOffer selectedFlight;
    private int last_index = -1;

    public class FlightHolder extends RecyclerView.ViewHolder {
        FlightOffer flightOffer;
        LinearLayout offerLayout = itemView.findViewById(R.id.flight_container);
        CardView offerCard = itemView.findViewById(R.id.flight_offer_card);

        FlightHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public FlightAdapter(FlightOffer[] flightOffers, ResponseListener.FlightOffer listener, Resources resources) {
        this.flightOffers = flightOffers;
        this.listener = listener;
        this.resources = resources;
    }

    @NonNull
    @Override
    public FlightHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_flight_offer, viewGroup, false);

        return new FlightHolder(view);
    }

    @Override
    public void onViewRecycled(@NonNull FlightHolder holder) {
        super.onViewRecycled(holder);
        holder.offerLayout.removeAllViews();
    }

    @Override
    public void onBindViewHolder(@NonNull FlightHolder holder, int position) {
        FlightOffer flightOffer = flightOffers[position];
        FlightOffer.OfferItem offer = flightOffer.getOfferItems()[0];
        FlightOffer.Service[] services = offer.getServices();

        for (int i = 0; i < services.length; i++) {
            FlightOffer.Service service = services[i];
            FlightOffer.FlightSegment segment = service.getSegments()[0].getFlightSegment();

            View serviceView = inflater.inflate(R.layout.item_flight_service, holder.offerLayout, false);
            if (i > 0) {
                serviceView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
            }
            holder.offerLayout.addView(serviceView);

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
            setFlightDuration(segment, duration);
            setNumStops(stops, service.getSegments().length);
        }

        if (position == last_index) {
            holder.offerCard.setCardBackgroundColor(resources.getColor(R.color.gray));
        } else {
            holder.offerCard.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.flightOffer = flightOffers[position];
        holder.offerCard.setOnClickListener(v -> {
            if (selectedFlight == null || !selectedFlight.equals(flightOffer)) {
                selectedFlight = flightOffer;
                listener.onResponseReceive(selectedFlight);
            }

            last_index = position;
            notifyDataSetChanged();
        });
    }

    private void setFlightDuration(FlightOffer.FlightSegment segment, TextView tv) {
        int hours = Integer.parseInt(segment.getDuration().substring(0, 1));
        Log.i(TAG, "Hours: " + hours + " Duration: " + segment.getDuration());

        if (hours > 0) {
            tv.setText(segment.getDuration().replace("DT", "D ").replace("H", "H "));
        } else {
            tv.setText(TextUtils.split(segment.getDuration(), "T")[1].replace("H", "H "));
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

    public void updateData(FlightOffer[] flightOffers) {
        this.flightOffers = flightOffers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return flightOffers.length;
    }
}
