package com.openlab.humanpokedex;

import android.media.FaceDetector;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;

public class DetectFaceActivity extends AppCompatActivity {

    private com.google.mlkit.vision.face.FaceDetector faceDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE).setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .build();

        faceDetector = FaceDetection.getClient(options);
    }
}
