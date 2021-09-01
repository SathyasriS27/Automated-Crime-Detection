package com.openlab.homodex;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openlab.humanpokedex.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentTrackerLogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter trackerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    // private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;
    private String regNo;
    private Map<String, String> trackerLog;
    private ArrayList<TrackerLog> trackerLogArray;
    private TextView trackerLogProgressTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker_log);
        regNo = getIntent().getStringExtra("regNo");
        trackerLog = new HashMap<>();
        trackerLogArray = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.trackerLogProgress);
        // refreshLayout = findViewById(R.id.trackerLogSwipeRefresh);
        recyclerView = findViewById(R.id.trackerLogRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        trackerLogProgressTV = findViewById(R.id.trackerLogProgressTV);
        showTrackerLog();
    }

    private void showTrackerLog() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("Users").document("Username " + regNo).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                        trackerLog = (Map<String, String>) documentSnapshot.get("trackerLog");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentTrackerLogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        for (String logKeys : trackerLog.keySet()) {
            String time = logKeys.split(" ")[1];
            String date = logKeys.split(" ")[0];
            String value = trackerLog.get(logKeys);
            Log.i("trackerLog Map", trackerLog.toString());
            trackerLogArray.add(new TrackerLog(time, date, value));
        }

        if (trackerLogArray.size() == 0) {
            trackerLogProgressTV.setText("No track records.");
            progressBar.setVisibility(View.GONE);
        } else {
            // LinearLayoutManager trackerLayoutManager = new LinearLayoutManager(StudentTrackerLogActivity.this);
            trackerAdapter = new TrackerAdapter(trackerLogArray);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(trackerAdapter);
            // swipeDownRefreshTV.setVisibility(View.GONE);
        }

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                backgroundTracker();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        runTrackerUI();
                    }
                });
            }
        }).start();
        */
    }

    private void backgroundTracker() {
        db.collection("Users").document("Username " + regNo).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                        trackerLog = (Map<String, String>) documentSnapshot.get("trackerLog");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentTrackerLogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        for (String logKeys : trackerLog.keySet()) {
            String time = logKeys.split(" ")[1];
            String date = logKeys.split(" ")[0];
            String value = trackerLog.get(logKeys);
            trackerLogArray.add(new TrackerLog(time, date, value));
        }
    }

    private void runTrackerUI() {
        if (trackerLogArray.size() == 0) {
            trackerLogProgressTV.setText("No track records.");
            progressBar.setVisibility(View.GONE);
        } else {
            LinearLayoutManager trackerLayoutManager = new LinearLayoutManager(StudentTrackerLogActivity.this);
            trackerAdapter = new TrackerAdapter(trackerLogArray);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(trackerLayoutManager);
            recyclerView.setAdapter(trackerAdapter);
            // swipeDownRefreshTV.setVisibility(View.GONE);
        }
    }
}
