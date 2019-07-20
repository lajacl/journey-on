package com.lovelylavette.android.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amadeus.resources.HotelOffer;
import com.lovelylavette.android.R;
import com.lovelylavette.android.util.ResponseListener;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelHolder> {
    private final String TAG = "Hotel Adapter";
    private HotelOffer[] hotelOffers;
    private ResponseListener.HotelOffer listener;
    private Resources resources;
    private HotelOffer selectedHotel;
    private int last_index = -1;

    public class HotelHolder extends RecyclerView.ViewHolder {
        HotelOffer hotelOffer;
        CardView offerCard = itemView.findViewById(R.id.hotel_offer_card);
        TextView name = itemView.findViewById(R.id.name);
        TextView address = itemView.findViewById(R.id.address);
        TextView price = itemView.findViewById(R.id.price);
        TextView roomType = itemView.findViewById(R.id.room_type);
        TextView amenities = itemView.findViewById(R.id.amenities);

        HotelHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public HotelAdapter(HotelOffer[] hotelOffers, ResponseListener.HotelOffer listener, Resources resources) {
        this.hotelOffers = hotelOffers;
        this.listener = listener;
        this.resources = resources;
    }

    @NonNull
    @Override
    public HotelHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_hotel_offer, viewGroup, false);

        return new HotelHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelHolder holder, int position) {
        HotelOffer hotelOffer = hotelOffers[position];
        String delimiter = ", ";

        holder.name.setText(hotelOffer.getHotel().getName());

        HotelOffer.AddressType address = hotelOffer.getHotel().getAddress();
        StringBuilder addressFull = new StringBuilder();
        addressFull.append(TextUtils.join(delimiter, address.getLines()));
        if (address.getCityName() != null) {
            addressFull.append(delimiter)
                    .append(address.getCityName());
        }
        if (address.getStateCode() != null) {
            addressFull.append(delimiter)
                    .append(address.getStateCode());
        }
        if (address.getCountryCode() != null) {
            addressFull.append(delimiter)
                    .append(address.getCountryCode());
        }
        if (address.getPostalCode() != null) {
            addressFull.append(address.getPostalCode());
        }
        holder.address.setText(addressFull);

        //TODO Show all offers
        holder.price.setText(String.format("$%s", hotelOffer.getOffers()[0].getPrice().getTotal()));

        holder.roomType.setText(hotelOffer.getOffers()[0].getCategory());

        if(hotelOffer.getHotel().getAmenities() != null) {
            holder.amenities.setText(TextUtils.join(delimiter, hotelOffer.getHotel().getAmenities())
                    .toLowerCase().replace("_", " "));
        } else {
            holder.amenities.setText("NONE");
        }

        if (position == last_index) {
            holder.offerCard.setCardBackgroundColor(resources.getColor(R.color.gray));
        } else {
            holder.offerCard.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.hotelOffer = hotelOffers[position];
        holder.offerCard.setOnClickListener(v -> {
            if (selectedHotel == null || !selectedHotel.equals(hotelOffer)) {
                selectedHotel = hotelOffer;
                listener.onResponseReceive(selectedHotel);
            }

            last_index = position;
            notifyDataSetChanged();
        });
    }

    public void updateData(HotelOffer[] hotelOffers) {
        this.hotelOffers = hotelOffers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return hotelOffers.length;
    }
}
