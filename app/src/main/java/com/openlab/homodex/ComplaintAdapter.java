package com.openlab.homodex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.openlab.humanpokedex.R;

import java.util.ArrayList;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {

    private ArrayList<ComplaintLog> ComplaintLogss;
    private String date, time, offence, complaintNo;
    private Context context;
    private FirebaseFirestore db;
    // private URL photoStored;

    public ComplaintAdapter(ArrayList<ComplaintLog> complaintLogs) {
        ComplaintLogss = complaintLogs;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ComplaintAdapter.ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trackerlog_cardview, parent, false);
        context = v.getContext();

        ComplaintAdapter.ComplaintViewHolder complaintViewHolder = new ComplaintAdapter.ComplaintViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ComplaintOpenCardActivity.class);
                String transitionName = v.getResources().getString(R.string.transitionAnimation);
                View viewStart = v.findViewById(R.id.complaintlog_cardview);

                intent.putExtra("complaintNo", complaintNo);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) v.getContext(), viewStart, transitionName);

                ActivityCompat.startActivity(v.getContext(), intent, options.toBundle());
            }
        });
        return complaintViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintAdapter.ComplaintViewHolder holder, int position) {
        ComplaintLog complaintLog = ComplaintLogss.get(position);

        date = complaintLog.getDate();
        time = complaintLog.getTime();
        offence = complaintLog.getOffence();
        complaintNo = complaintLog.getComplaintNo();

        holder.dateTV.setText(date);
        holder.timeTV.setText(time);
        holder.offenceTV.setText(offence);
        holder.complaintNoTV.setText(complaintNo);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTV, timeTV, offenceTV, complaintNoTV;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTV = itemView.findViewById(R.id.complaintlogDateTV);
            timeTV = itemView.findViewById(R.id.complaintlogTimeTV);
            offenceTV = itemView.findViewById(R.id.complaintlogOffence);
            complaintNoTV = itemView.findViewById(R.id.complaintlogComplaintNoTV);
        }
    }

}
