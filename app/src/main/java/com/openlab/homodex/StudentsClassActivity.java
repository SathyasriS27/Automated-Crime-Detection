package com.openlab.homodex;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openlab.humanpokedex.R;

import java.net.URL;
import java.util.ArrayList;

public class StudentsClassActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView classnameTV;
    private ArrayList<ClassStudents> classStudents;
    private RecyclerView recyclerView;
    private String className, dept, year;
    private ProgressBar progressBar;

    // private RecyclerView.LayoutManager classStudentsLayoutManager;
    private RecyclerView.Adapter classStudentsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_students);

        className = getIntent().getStringExtra("className");
        dept = getIntent().getStringExtra("dept");
        year = getIntent().getStringExtra("year");

        db = FirebaseFirestore.getInstance();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshClassStudents);
        classnameTV = findViewById(R.id.classNameTV);
        recyclerView = findViewById(R.id.class_studentsRecycler);
        classStudents = new ArrayList<ClassStudents>();
        progressBar = findViewById(R.id.class_studentsProgress);

        classnameTV.setText(className);
        showStudents();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showStudents();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showStudents() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                backgroundDBRetrieve();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        runUIThread();
                    }
                });
            }
        }).start();
    }

    private void backgroundDBRetrieve() {
        db.collection("CampusInfo").document(year).collection("Departments").document(dept)
                .collection("Class " + className).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    ArrayList<ClassStudents> classStudents = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String name = documentSnapshot.get("Username").toString();
                        String regNo = documentSnapshot.get("RegNo").toString();
                        ArrayList<URL> photoStoredStr = (ArrayList<URL>) documentSnapshot.get("photoStored");

                        URL photoStored = photoStoredStr.get(0);
                        classStudents.add(new ClassStudents(regNo, name, className, photoStored));

                        /*
                        try {
                            // URI uri = new URI(photoStoredStr);
                            URL photoStored = photoStoredStr.get(0);
                            classStudents.add(new ClassStudents(regNo, name, className, photoStored));
                        } catch (URISyntaxException | MalformedURLException e) {
                            Toast.makeText(StudentsClassActivity.this, "Unable to parse URI.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        */
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentsClassActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void runUIThread() {
        if (classStudents.size() == 0) {
            classnameTV.setText("Empty Class.");
            progressBar.setVisibility(View.GONE);
        } else {
            GridLayoutManager classStudentsLayoutManager = new GridLayoutManager(StudentsClassActivity.this, 3);
            classStudentsAdapter = new StudentsClassAdapter(classStudents);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(classStudentsLayoutManager);
            recyclerView.setAdapter(classStudentsAdapter);
            // swipeDownRefreshTV.setVisibility(View.GONE);
        }
    }
}
