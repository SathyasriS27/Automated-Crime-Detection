package com.openlab.homodex;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openlab.humanpokedex.R;

public class ComplaintOpenCardActivity extends AppCompatActivity {

    private TextView offenceTitleTV, complaintNoTV, offenderNameTV, offenderRegTV, dateTV, timeTV, descriptionTV, complaineeNameTV, complaineeRegNoTV;
    private String offence, offenderName, complaintNo, offenderRegNo, date, time, description, complaineeName, complaineeRegNo;
    private ConstraintLayout layout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complaint_opencard);

        complaintNo = getIntent().getStringExtra("complaintNo");

        db = FirebaseFirestore.getInstance();

        offenceTitleTV = findViewById(R.id.complaint_offenceTV);
        offenderNameTV = findViewById(R.id.complaintOffenderName);
        offenderRegTV = findViewById(R.id.complaintOffenderReg);
        dateTV = findViewById(R.id.complaintDate);
        timeTV = findViewById(R.id.complaintTime);
        descriptionTV = findViewById(R.id.complaintDescription);
        complaineeNameTV = findViewById(R.id.complaineeName);
        complaineeRegNoTV = findViewById(R.id.complaineeReg);
        complaintNoTV = findViewById(R.id.complaintNoTV);
        layout = findViewById(R.id.complaintProgressLayout);

        populateView();
    }

    private void populateView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                backgroundDBRetrieve();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();

        displayPopulatedView();
    }

    private void backgroundDBRetrieve() {
        db.collection("Complaints").document(complaintNo).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                        offenderName = documentSnapshot.get("Username").toString();
                        offenderRegNo = documentSnapshot.get("RegNo").toString();
                        offence = documentSnapshot.get("Offence").toString();
                        date = documentSnapshot.get("Date").toString();
                        time = documentSnapshot.get("Time").toString();
                        description = documentSnapshot.get("Description").toString();
                        complaineeRegNo = documentSnapshot.get("complainerRegNo").toString();
                        complaineeName = documentSnapshot.get("complainerName").toString();
                    }
                });
    }

    private void displayPopulatedView() {
        layout.setVisibility(View.GONE);
        offenceTitleTV.setText(offence);
        offenderNameTV.setText(offenderName);
        complaineeRegNoTV.setText(complaineeRegNo);
        complaineeNameTV.setText(complaineeName);
        offenderRegTV.setText(offenderRegNo);
        dateTV.setText(date);
        timeTV.setText(time);
        descriptionTV.setText(description);
    }
}
