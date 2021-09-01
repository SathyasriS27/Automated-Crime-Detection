package com.openlab.homodex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openlab.humanpokedex.R;

import java.util.HashMap;
import java.util.Map;

public class AddDetailsActivity extends AppCompatActivity {

    private TextInputEditText nameET, classET, phoneET, deptET, yearET, emailET;
    private MaterialButton registerBtn;
    private ProgressBar registerProgress;
    private TextView registerTV;

    private FirebaseFirestore db;
    private Map<String, Object> registerMap;

    private String emailID, name, className, year, dept, phone, regNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extra_details);

        regNo = getIntent().getStringExtra("regNo");

        nameET = findViewById(R.id.extraNameET);
        classET = findViewById(R.id.extraClassET);
        phoneET = findViewById(R.id.extraPhoneET);
        deptET = findViewById(R.id.extraDeptET);
        yearET = findViewById(R.id.extraYearET);
        emailET = findViewById(R.id.extraEmailET);
        registerBtn = findViewById(R.id.extraRegisterBtn);
        registerProgress = findViewById(R.id.extraProgress);
        registerTV = findViewById(R.id.extraTV);

        db = FirebaseFirestore.getInstance();
        registerMap = new HashMap<>();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFace();
            }
        });
    }

    private void retrieveData() {
        name = nameET.getText().toString().trim();
        className = classET.getText().toString().trim();
        year = yearET.getText().toString().trim();
        emailID = emailET.getText().toString().trim();
        dept = deptET.getText().toString().trim();
        phone = phoneET.getText().toString().trim();
    }

    private void registerFace() {
        retrieveData();
        registerBtn.setVisibility(View.GONE);
        registerProgress.setVisibility(View.VISIBLE);
        registerTV.setVisibility(View.VISIBLE);

        registerMap.put("Username", name);
        registerMap.put("RegNo", regNo);
        registerMap.put("Class", className);
        registerMap.put("Year", year);
        registerMap.put("Department", dept);
        registerMap.put("phoneNo", phone);

        db.collection("RegisteredFaces").document("Username " + regNo).set(registerMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddDetailsActivity.this, "Registered face!", Toast.LENGTH_SHORT).show();

                        db.collection("CampusInfo").document(year).collection("Departments").document(dept)
                                .collection("Class " + className).document("Username " + regNo).set(registerMap);

                        registerProgress.setVisibility(View.GONE);
                        Toast.makeText(AddDetailsActivity.this, "Done adding details.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddDetailsActivity.this, LoginSignUpActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddDetailsActivity.this, LoginSignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}