    package com.openlab.humanpokedex;

import android.content.Context;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.JsonParser;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class FrameAnalyser implements ImageAnalysis.Analyzer {

    private FaceDetectorOptions realTimeOpts = new FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST).build();
    private FaceDetector detector;
    private boolean isProcessing                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            ;
    private ArrayList<Pair<String, Float>> faceList = new ArrayList<>();
    private FaceNetModel faceNetModel;
    private String metricToBeUsed;
    private Context getContext;
    private FaceNetModel model;

    public FrameAnalyser(Context context, BoundingBoxOverlay boundingBoxOverlay) {
        faceList = new ArrayList<>();
        faceNetModel = new FaceNetModel(context);
        isProcessing = AtomicBoolean(false);
        metricToBeUsed = "l2";
        detector = FaceDetection.getClient(realTimeOpts);
        getContext = context;
        model = FaceNetModel(context);
    }

    @Override
    public void analyze(@NonNull ImageProxy image, int rotationDegrees) {
        Bitmap bitmap = toBitmap(image);

        if (isProcessing) {
            return;
        } else {
            isProcessing = true;

            InputImage inputImage = InputImage.fromByteArray(BitmaptoNv21(bitmap), 640, 480, rotationDegrees, InputImage.IMAGE_FORMAT_NV21);
            detector.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                @Override
                public void onSuccess(@NonNull List<Face> faces) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runModel(faces, bitmap);
                        }
                    }).start();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void runModel(List<Face> faces, Bitmap cameraFrameBitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String json = null;
                String path = getContext.getFilesDir() + "faceList.txt";
                try {
                    InputStream is = new FileInputStream(path);
                    int size = is.available();
                    byte[] buffer = new byte[size];

                    is.read(buffer);

                    is.close();

                    json = new String(buffer, "UTF-8");
                } catch (FileNotFoundException e) {
                    Toast.makeText(getContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ArrayList<Pair<String, ArrayList<Float>>> faceList = new ArrayList<>();

                try {
                    JSONObject obj = new JSONObject(json);
                    Iterator<String> it = obj.keys();
                    while (it.hasNext()) {
                        JSONObject temp = obj.getJSONObject(it.next());
                        String name1 = temp.getString("name");
                        JSONArray array = temp.getJSONArray("embeds");

                        ArrayList<Float> embeds = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            Float dec = (Float) array.get(i);
                            embeds.add(dec);
                        }

                        Pair<String, ArrayList<Float>> pair = new Pair<>(name1, embeds);
                        faceList.add(pair);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayList<Prediction> predictions = new ArrayList<>();
                for (Face face : faces) {
                    try {
                        String subject = model.getFaceEmbedding(cameraFrameBitmap, face.getBoundingBox(), true, RecognizeFaceActivity.isRearCameraOn());
                        Map<String, ArrayList<Float>> nameScoreHashMap = new HashMap<>();

                        for (int no = 0; no < faceList.size(); no++) {
                            if (nameScoreHashMap[faceList.get(no).first] == null) {

                            }
                        }
                    }
                }
            }
        })
    }
}
