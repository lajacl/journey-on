package com.lovelylavette.android.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amadeus.resources.FlightDestination;
import com.lovelylavette.android.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.DestinationHolder> {
    private final String TAG = "Destination Adapter";
    private FlightDestination[] destinations;
    private Resources resources;
    private int lastIndex = -1;

    public class DestinationHolder extends RecyclerView.ViewHolder {
        CardView offerCard = itemView.findViewById(R.id.destination_offer_card);
        TextView price = itemView.findViewById(R.id.price);
        TextView flightPath = itemView.findViewById(R.id.flight_path);
        TextView dates = itemView.findViewById(R.id.dates);

        DestinationHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public DestinationAdapter(FlightDestination[] destinations, Resources resources) {
        this.destinations = destinations;
        this.resources = resources;
    }

    @NonNull
    @Override
    public DestinationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_destination, viewGroup, false);

        return new DestinationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationHolder holder, int position) {
        FlightDestination destination = destinations[position];

        NumberFormat currencyFormatter = new DecimalFormat("0.00");
        holder.price.setText(String.format("$%s", currencyFormatter.format(destination.getPrice().getTotal())));

        holder.flightPath.setText(String.format("%s to %s", destination.getOrigin(), destination.getDestination()));

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
        holder.dates.setText(String.format("%s - %s", sdf.format(destination.getDepartureDate()),
                sdf.format(destination.getReturnDate())));

        if (lastIndex == position) {
            holder.offerCard.setCardBackgroundColor(resources.getColor(R.color.gray));
        } else {
            holder.offerCard.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.offerCard.setOnClickListener(v -> {
            lastIndex = position;
            notifyDataSetChanged();
        });
    }

    public void updateData(FlightDestination[] destinations) {
        this.destinations = destinations;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return destinations.length;
    }
}
