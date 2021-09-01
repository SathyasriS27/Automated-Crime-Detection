package com.openlab.homodex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.openlab.humanpokedex.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StudentOpenCardActivity extends AppCompatActivity {

    private ImageView studentImage;
    private TextView nameET, regNoET, classET, yearET, deptET, phoneNoET, emailIDET;
    private MaterialButton trackerLog, complaintLog;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String name, regNo, className, year, dept, phoneNo, emailID;
    private ArrayList<URL> photosURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_opencard);

        regNo = getIntent().getStringExtra("regNo");

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        nameET = findViewById(R.id.studentName);
        regNoET = findViewById(R.id.studentRegNo);
        classET = findViewById(R.id.studentClass);
        yearET = findViewById(R.id.studentYear);
        deptET = findViewById(R.id.studentDept);
        phoneNoET = findViewById(R.id.studentPhoneNo);
        emailIDET = findViewById(R.id.studentEmail);
        trackerLog = findViewById(R.id.trackerLogBtn);
        complaintLog = findViewById(R.id.complaintsLogBtn);
        studentImage = findViewById(R.id.studentImage);

        complaintLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewComplaintLog();
            }
        });

        trackerLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewTrackerLog();
            }
        });

        fillElements();

    }

    private void fillElements() {
        db.collection("Users").document("Username " + regNo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                name = documentSnapshot.get("Username").toString();
                className = documentSnapshot.get("Class").toString();
                year = documentSnapshot.get("Year").toString();
                dept = documentSnapshot.get("Department").toString();
                emailID = documentSnapshot.get("emailID").toString();
                phoneNo = documentSnapshot.get("phoneNo").toString();
                photosURL = (ArrayList<URL>) documentSnapshot.get("photoStored");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentOpenCardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        nameET.setText(name);
        classET.setText(className);
        yearET.setText(year);
        deptET.setText(year);
        emailIDET.setText(emailID);
        phoneNoET.setText(phoneNo);

        URL photoStored = photosURL.get(0);

        StorageReference storageRef = storage.getReferenceFromUrl(photoStored.toString());
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> listItems = listResult.getItems();
                StorageReference realRef = listItems.get(0);

                Glide.with(StudentOpenCardActivity.this).load(realRef).into(studentImage);
            }
        });
    }

    private void viewComplaintLog() {
        Intent intent = new Intent(StudentOpenCardActivity.this, ComplaintLogActivity.class);
        intent.putExtra("regNo", regNo);
        startActivity(intent);
    }

    private void viewTrackerLog() {
        Intent intent = new Intent(StudentOpenCardActivity.this, StudentTrackerLogActivity.class);
        intent.putExtra("regNo", regNo);
        startActivity(intent);
    }
}
