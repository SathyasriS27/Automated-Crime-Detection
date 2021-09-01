package com.openlab.homodex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.openlab.humanpokedex.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StudentsClassAdapter extends RecyclerView.Adapter<StudentsClassAdapter.StudentsClassViewHolder> {

    private ArrayList<ClassStudents> ClassStudentss;
    private String name, className, regNo;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private URL photosURL;
    // private URL photoStored;

    public StudentsClassAdapter(ArrayList<ClassStudents> classStudents) {
        ClassStudentss = classStudents;
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public StudentsClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_cardview, parent, false);
        context = v.getContext();

        StudentsClassViewHolder studentsClassViewHolder = new StudentsClassViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StudentOpenCardActivity.class);
                String transitionName = view.getResources().getString(R.string.transitionAnimation);
                View viewStart = view.findViewById(R.id.student_cardview);

                intent.putExtra("regNo", regNo);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), viewStart, transitionName);

                ActivityCompat.startActivity(view.getContext(), intent, options.toBundle());
            }
        });

        return studentsClassViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentsClassViewHolder holder, int position) {
        ClassStudents classStudent = ClassStudentss.get(position);

        name = classStudent.getStudentName();
        regNo = classStudent.getRegNo();
        photosURL = classStudent.getPhotosURL();

        holder.nameTV.setText(name);

        db = FirebaseFirestore.getInstance();
        /*
        db.collection("Users").document("Username " + regNo).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String s = documentSnapshot.get("photoStored").toString();
                        try {
                            URI uri = new URI(s);
                            photoStored = uri.toURL();
                        } catch (URISyntaxException | MalformedURLException e) {
                            Toast.makeText(context, "String cannot be parsed to URI.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        */

        StorageReference storageRef = storage.getReferenceFromUrl(photosURL.toString());
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> listItems = listResult.getItems();
                StorageReference realRef = listItems.get(0);

                Glide.with(context).load(realRef).into(holder.photo);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class StudentsClassViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV;
        public ImageView photo;

        public StudentsClassViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.nameCardViewStudent);
            photo = itemView.findViewById(R.id.studentPhoto);
        }
    }
}
