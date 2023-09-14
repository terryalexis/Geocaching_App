package com.example.geocache.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.geocache.MapsActivity;
import com.example.geocache.R;
import com.example.geocache.models.Geocache;

import java.util.List;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Geocache> geocaches;

    public MyItemRecyclerViewAdapter(List<Geocache> items) {
        geocaches = items;
    }

    @NonNull
    @Override
    public MyItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_my_places, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyItemRecyclerViewAdapter.ViewHolder holder, int position) {
        Geocache geocache = geocaches.get(position);
        TextView geocacheLocationView = holder.itemView.findViewById(R.id.geocacheLocation);
        TextView geocacheTimestampView = holder.itemView.findViewById(R.id.geocacheTimestamp);
        geocacheLocationView.setText("(" + geocache.getLatitude() + ", " + geocache.getLongitude() + ")");
        geocacheTimestampView.setText(geocache.getTimestamp());


//        Bundle bundle = new Bundle();
//        bundle.putString("location", "(" + geocacheLocation.getLatitude() + ", " + geocacheLocation.getLongitude() + ")");
//        bundle.putString("id", geocacheLocation.getId());
//
//        MapsActivity activity = (MapsActivity) holder.itemView.getContext();
//        Fragment geocacheFragment = new GeocacheFragment();
//        noteFragment.setArguments(bundle);

//        holder.itemView.findViewById(R.id.geocacheCard).setOnClickListener((view) -> {
//            MapsActivity activity = (MapsActivity) holder.itemView.getContext();
//            activity.getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.myPlacesFragment, createGeocacheFragment)
//                    .setReorderingAllowed(true)
//                    .commit();
//        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return geocaches.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}