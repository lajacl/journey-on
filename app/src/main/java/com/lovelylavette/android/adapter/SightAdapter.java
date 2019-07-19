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

import com.amadeus.resources.PointOfInterest;
import com.lovelylavette.android.R;
import com.lovelylavette.android.util.ResponseListener;

import java.util.ArrayList;
import java.util.List;

public class SightAdapter extends RecyclerView.Adapter<SightAdapter.SightHolder> {
    private final String TAG = "Sight Adapter";
    private PointOfInterest[] pointsOfInterest;
    private ResponseListener.Sights listener;
    private Resources resources;
    private List<PointOfInterest> sightsList = new ArrayList<>();
    private List<Integer> last_indices = new ArrayList();

    public class SightHolder extends RecyclerView.ViewHolder {
        CardView offerCard = itemView.findViewById(R.id.offer_card);
        TextView name = itemView.findViewById(R.id.name);
        TextView type = itemView.findViewById(R.id.type);
        TextView tags = itemView.findViewById(R.id.tags);

        SightHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public SightAdapter(PointOfInterest[] pointsOfInterest, ResponseListener.Sights listener, Resources resources) {
        this.pointsOfInterest = pointsOfInterest;
        this.listener = listener;
        this.resources = resources;
    }

    @NonNull
    @Override
    public SightHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_poi_offer, viewGroup, false);

        return new SightHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SightHolder holder, int position) {
        PointOfInterest pointOfInterest = pointsOfInterest[position];
        String delimiter = ", ";

        holder.name.setText(pointOfInterest.getName());
        holder.type.setText(pointOfInterest.getCategory());
        if (pointOfInterest.getTags() != null) {
            holder.tags.setText(TextUtils.join(delimiter, pointOfInterest.getTags()));
        } else {
            holder.tags.setText("NONE");
        }

        if (last_indices.contains(position)) {
            holder.offerCard.setCardBackgroundColor(resources.getColor(R.color.gray));
        } else {
            holder.offerCard.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }

//        holder.pointOfInterest = pointsOfInterest[position];
        holder.offerCard.setOnClickListener(v -> {
            if (!sightsList.contains(pointOfInterest)) {
                last_indices.add(position);
                sightsList.add(pointOfInterest);
                listener.onResponseReceive(sightsList);
            } else {
                last_indices.remove(position);
                sightsList.remove(pointOfInterest);
                listener.onResponseReceive(sightsList);
            }
            notifyDataSetChanged();
        });
    }

    public void updateData(PointOfInterest[] pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return pointsOfInterest.length;
    }
}
