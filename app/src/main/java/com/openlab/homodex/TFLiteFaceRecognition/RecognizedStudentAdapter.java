package com.openlab.homodex.TFLiteFaceRecognition;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.openlab.homodex.StudentOpenCardActivity;
import com.openlab.homodex.TFLiteFaceRecognition.tflite.SimilarityClassifier;
import com.openlab.humanpokedex.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecognizedStudentAdapter extends RecyclerView.Adapter<RecognizedStudentAdapter.RecognizedStudentsViewHolder> {

    private List<SimilarityClassifier.Recognition> recognitionArrayList;
    private String regNo, className, name;
    private URL photoURL;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public RecognizedStudentAdapter(List<SimilarityClassifier.Recognition> recognizedStudents) {
        recognitionArrayList = recognizedStudents;
    }

    @NonNull
    @Override
    public RecognizedStudentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recognized_face_cardview, parent, false);
        context = v.getContext();

        RecognizedStudentAdapter.RecognizedStudentsViewHolder recognizedStudentsViewHolder = new RecognizedStudentAdapter.RecognizedStudentsViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StudentOpenCardActivity.class);
                String transitionName = view.getResources().getString(R.string.transitionAnimation);
                View viewStart = view.findViewById(R.id.recognized_face_cardview);

                intent.putExtra("regNo", regNo);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), viewStart, transitionName);

                ActivityCompat.startActivity(view.getContext(), intent, options.toBundle());
            }
        });

        return recognizedStudentsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecognizedStudentAdapter.RecognizedStudentsViewHolder holder, int position) {
        SimilarityClassifier.Recognition studentRecognition = recognitionArrayList.get(position);

        regNo = studentRecognition.getTitle();

        db.collection("Users").document("Username " + regNo).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<URL> photoStored = new ArrayList<>();
                        photoStored = (ArrayList<URL>) documentSnapshot.get("photoStored");
                        photoURL = photoStored.get(0);
                        name = documentSnapshot.get("Username").toString();
                        className = documentSnapshot.get("Class").toString();
                    }
                });


        StorageReference storageRef = storage.getReferenceFromUrl(photoURL.toString());
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

    public static class RecognizedStudentsViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV, classTV;
        public ImageView photo;

        public RecognizedStudentsViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.nameCardViewStudent);
            classTV = itemView.findViewById(R.id.classCardViewStudent);
            photo = itemView.findViewById(R.id.studentPhoto);
        }
    }
}
