package com.openlab.homodex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openlab.humanpokedex.R;

import java.util.ArrayList;

public class SelectClassActivity extends AppCompatActivity {

    private Spinner deptSpinner, yearSpinner, classSpinner;
    private MaterialButton selectClassBtn;
    private ArrayList<String> years, depts, classes;
    private String yearSelected, deptSelected, classSelected;
    private TextView yearTV, deptTV, classTV;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deptSpinner = findViewById(R.id.selectClassDeptSpinner);
        yearSpinner = findViewById(R.id.selectClassYearSpinner);
        classSpinner = findViewById(R.id.selectClassClassSpinner);
        selectClassBtn = findViewById(R.id.selectClassButton);

        yearTV = findViewById(R.id.selectClassYear);
        deptTV = findViewById(R.id.selectClassDept);
        classTV = findViewById(R.id.selectClassClass);

        years = new ArrayList<>();
        depts = new ArrayList<>();
        classes = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        selectClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish this
                Intent intent = new Intent(SelectClassActivity.this, StudentsClassActivity.class);
                intent.putExtra("className", classSelected);
                intent.putExtra("year", yearSelected);
                intent.putExtra("dept", deptSelected);
                startActivity(intent);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("CampusInfo").document("General").get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                years = (ArrayList<String>) documentSnapshot.get("Years");
                                /*
                                for (Object i : documentSnapshot.getData().values()) {
                                    years.add(i.toString());
                                }
                                */
                            }
                        });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        yearTV.setVisibility(View.VISIBLE);
                        showYearSpinner();
                    }
                });
            }
        });


    }

    private void showYearSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);

        yearSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                yearSelected = parent.getSelectedItem().toString();
                progressBar.setVisibility(View.VISIBLE);
                deptSpinnerBackground();
            }
        });
    }

    private void deptSpinnerBackground() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("CampusInfo").document(yearSelected).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                depts = (ArrayList<String>) documentSnapshot.get("Departments");
                            }
                        });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        deptTV.setVisibility(View.VISIBLE);
                        showDeptSpinner();
                    }
                });
            }
        });
    }

    private void showDeptSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, depts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptSpinner.setAdapter(adapter);

        deptSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deptSelected = parent.getSelectedItem().toString();
                progressBar.setVisibility(View.VISIBLE);
                classSpinnerBackground();
            }
        });
    }

    private void classSpinnerBackground() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("CampusInfo").document(yearSelected).collection("Departments")
                        .document(deptSelected).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                classes = (ArrayList<String>) documentSnapshot.get("Classes");
                            }
                        });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        classTV.setVisibility(View.VISIBLE);
                        showClassSpinner();
                    }
                });
            }
        });
    }

    private void showClassSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(adapter);

        classSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                classSelected = parent.getSelectedItem().toString();
                selectClassBtn.setVisibility(View.VISIBLE);
            }
        });
    }
}
