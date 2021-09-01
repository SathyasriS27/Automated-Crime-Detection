package com.openlab.homodex;

import android.os.Bundle;
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

public class ComplaintLogActivity extends AppCompatActivity {

    private RecyclerView.Adapter complaintAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;
    private String regNo, date, time, offence;
    private ArrayList<String> complaintLog;
    private ArrayList<ComplaintLog> complaintLogArray;
    private TextView complaintLogProgressTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complaint_log);

        regNo = getIntent().getStringExtra("regNo");

        recyclerView = findViewById(R.id.complaintLogRecyclerView);
        progressBar = findViewById(R.id.complaintProgressBar);
        // refreshLayout = findViewById(R.id.complaintLogSwipeRefresh);

        complaintLogArray = new ArrayList<>();
        complaintLogProgressTV = findViewById(R.id.complaintLogProgressTV);

        db = FirebaseFirestore.getInstance();
        showComplaints();

    }

    private void showComplaints() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Users").document("Username " + regNo).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                        complaintLog = (ArrayList<String>) documentSnapshot.get("ComplaintLog");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ComplaintLogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        for (String complaintNo : complaintLog) {
            db.collection("Complaints").document(complaintNo).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                            date = documentSnapshot.get("Date").toString();
                            time = documentSnapshot.get("Time").toString();
                            offence = documentSnapshot.get("Offence").toString();

                            complaintLogArray.add(new ComplaintLog(date, time, offence, complaintNo));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ComplaintLogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        updateUI();

        /*
         new Thread(new Runnable() {
             @Override
             public void run() {
                 backgroundDataRetrieval();
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         progressBar.setVisibility(View.VISIBLE);
                     }
                 });
             }
         }).start();

         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 updateUI();
             }
         });
         */
    }

    private void backgroundDataRetrieval() {
        db.collection("Users").document("Username " + regNo).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                        complaintLog = (ArrayList<String>) documentSnapshot.get("ComplaintLog");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ComplaintLogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        for (String complaintNo : complaintLog) {
            db.collection("Complaints").document(complaintNo).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                            date = documentSnapshot.get("Date").toString();
                            time = documentSnapshot.get("Time").toString();
                            offence = documentSnapshot.get("Offence").toString();

                            complaintLogArray.add(new ComplaintLog(date, time, offence, complaintNo));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ComplaintLogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUI() {
        if (complaintLogArray.size() == 0) {
            complaintLogProgressTV.setText("Empty Class.");
            progressBar.setVisibility(View.GONE);
        } else {
            LinearLayoutManager complaintsLayoutManager = new LinearLayoutManager(ComplaintLogActivity.this);
            complaintAdapter = new ComplaintAdapter(complaintLogArray);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(complaintsLayoutManager);
            recyclerView.setAdapter(complaintAdapter);
            // swipeDownRefreshTV.setVisibility(View.GONE);
        }
    }
}
