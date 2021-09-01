package com.openlab.homodex;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openlab.humanpokedex.R;
import com.openlab.homodex.TFLiteFaceRecognition.DetectorActivity;

public class HomeActivity extends AppCompatActivity {

    private MaterialButton signOutButton, trackerLogButton, complaintLogButton, trafficButton, recognizeFaceButton, identifyCriminalButton, findStudentButton, registerFaceButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    String regNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        signOutButton = findViewById(R.id.logOutButton);
        trackerLogButton = findViewById(R.id.trackerLogButton);
        complaintLogButton = findViewById(R.id.complaintLogButton);
        trafficButton = findViewById(R.id.trafficButton);
        recognizeFaceButton = findViewById(R.id.recognizeFaceButton);
        identifyCriminalButton = findViewById(R.id.identifyCriminalsButton);
        findStudentButton = findViewById(R.id.findStudentButton);
        registerFaceButton = findViewById(R.id.registerFaceButton);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("emailID", user.getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        regNo = documentSnapshot.get("RegNo").toString();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new MaterialAlertDialogBuilder(HomeActivity.this)
                        .setTitle("Signing out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();
                                Toast.makeText(HomeActivity.this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HomeActivity.this, LoginSignUpActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);
                            }
                        }).setNegativeButton("Cancel", null)
                        .show();
            }
        });

        trackerLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackerLog();
            }
        });

        complaintLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complaintLog();
            }
        });

        recognizeFaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizeFace();
            }
        });

        identifyCriminalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifyCriminal();
            }
        });

        findStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findStudent();
            }
        });

        registerFaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFace();
            }
        });
    }

    private void trackerLog() {
        Intent intent = new Intent(HomeActivity.this, StudentTrackerLogActivity.class);
        intent.putExtra("regNo", regNo);
        startActivity(intent);
    }

    private void complaintLog() {
        Intent intent = new Intent(HomeActivity.this, ComplaintLogActivity.class);
        intent.putExtra("regNo", regNo);
        startActivity(intent);
    }

    private void recognizeFace() {
        Intent intent = new Intent(HomeActivity.this, DetectorActivity.class);
        startActivity(intent);
    }

    private void identifyCriminal() {
        // Finish this
    }

    private void findStudent() {
        Intent intent = new Intent(HomeActivity.this, SelectClassActivity.class);
        startActivity(intent);
    }

    private void registerFace() {
        Intent intent = new Intent(HomeActivity.this, RegisterFaceActivity.class);
        intent.putExtra("registerFlag", 1);
        startActivity(intent);
    }
}
