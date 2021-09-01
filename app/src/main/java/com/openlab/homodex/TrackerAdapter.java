package com.openlab.homodex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.vision.Tracker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openlab.humanpokedex.R;

import java.util.ArrayList;

public class TrackerAdapter extends RecyclerView.Adapter<TrackerAdapter.TrackerViewHolder> {

    private ArrayList<TrackerLog> TrackerLogss;

    public static class TrackerViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTV, dateTV, areaTV;

        public TrackerViewHolder (View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.trackerlogTimeTV);
            dateTV = itemView.findViewById(R.id.trackerlogDateTV);
            areaTV = itemView.findViewById(R.id.trackerlogAreaTV);
        }
    }
    // private String date, time, description;
    // private Context context;
    // private FirebaseFirestore db;
    // private URL photoStored;

    public TrackerAdapter(ArrayList<TrackerLog> trackerLogs) {
        TrackerLogss = trackerLogs;
        // db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public TrackerAdapter.TrackerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trackerlog_cardview, parent, false);
        // context = v.getContext();
        TrackerViewHolder tvh = new TrackerViewHolder(v);
        return tvh;

        // TrackerAdapter.TrackerViewHolder trackerViewHolder = new TrackerAdapter.TrackerViewHolder(v);
        // return trackerViewHolder;
    }

    @Override
    public void onBindViewHolder(TrackerViewHolder holder, int position) {
        TrackerLog trackerLog = TrackerLogss.get(position);

        // date = trackerLog.getDate();
        // time = trackerLog.getTime();
        // description = trackerLog.getDescription();

        holder.dateTV.setText(trackerLog.getDate());
        holder.timeTV.setText(trackerLog.getTime());
        holder.areaTV.setText(trackerLog.getDescription());

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /*
    public static class TrackerViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTV, timeTV, descriptionTV;

        public TrackerViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTV = itemView.findViewById(R.id.trackerlogDateTV);
            timeTV = itemView.findViewById(R.id.trackerlogTimeTV);
            descriptionTV = itemView.findViewById(R.id.trackerlogArea);
        }
    }
    */
}
