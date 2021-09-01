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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openlab.humanpokedex.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText nameET, regNoET, classET, yearET, deptET, emailET, phoneET, passwordET;
    private MaterialButton signUpBtn;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private ProgressBar signupProgress;
    private TextView signupTV;

    private String emailID, password, name, regNo, className, year, dept, phone;
    private Map<String, Object> userSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        nameET = findViewById(R.id.signupNameET);
        regNoET = findViewById(R.id.signupRegNoET);
        classET = findViewById(R.id.signupClassET);
        yearET = findViewById(R.id.signupYearET);
        emailET = findViewById(R.id.signupEmailET);
        deptET = findViewById(R.id.signupDeptET);
        phoneET = findViewById(R.id.signupPhoneET);
        passwordET = findViewById(R.id.signupPasswordET);
        signUpBtn = findViewById(R.id.signupSignUpBtn);

        signupProgress = findViewById(R.id.signupProgress);
        signupTV = findViewById(R.id.signupTV);

        userSignUp = new HashMap();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        signUpBtn.setVisibility(View.GONE);
        signupProgress.setVisibility(View.VISIBLE);
        signupTV.setVisibility(View.VISIBLE);
        retrieveData();
        Map<String, String> trackerLog = new HashMap<>();
        ArrayList<String> complaintLog = new ArrayList<>();

        userSignUp.put("Username", name);
        userSignUp.put("RegNo", regNo);
        userSignUp.put("Class", className);
        userSignUp.put("Year", year);
        userSignUp.put("Department", dept);
        userSignUp.put("emailID", emailID);
        userSignUp.put("phoneNo", phone);
        userSignUp.put("password", password);
        userSignUp.put("trackerLog", trackerLog);
        userSignUp.put("complaintLog", complaintLog);


        firebaseAuth.createUserWithEmailAndPassword(emailID, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        db.collection("Users").document("Username " + regNo)
                                .set(userSignUp).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SignUpActivity.this, "Signed you up. Proceeding...", Toast.LENGTH_SHORT).show();
                                db.collection("CampusInfo").document(year).collection("Departments").document(dept)
                                        .collection("Class " + className).document("Username " + regNo).set(userSignUp);
                                updateUI();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        db.collection("RegisteredFaces").document("Username " + regNo).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String photoURL = documentSnapshot.get("photoStored").toString();
                            db.collection("Users").document("Username " + regNo).update("photoStored", photoURL)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity.this, "Face registered.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, "An unexpected error has occurred. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Intent intent = new Intent(SignUpActivity.this, RegisterFaceActivity.class);
                            intent.putExtra("registeredFlag", 1);
                            startActivity(intent);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveData() {
        name = nameET.getText().toString().trim();
        regNo = regNoET.getText().toString().trim();
        className = classET.getText().toString().trim();
        year = yearET.getText().toString().trim();
        emailID = emailET.getText().toString().trim();
        dept = deptET.getText().toString().trim();
        phone = phoneET.getText().toString().trim();
        password = passwordET.getText().toString().trim();
    }
}
