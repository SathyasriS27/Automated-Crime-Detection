package com.openlab.homodex;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.common.util.concurrent.ListenableFuture;
import com.openlab.humanpokedex.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterFaceActivity extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSIONS = 1001;
    // private String REQUIRED_PERMISSIONS = "android.permission.CAMERA";
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private Executor executor = Executors.newSingleThreadExecutor();
    private PreviewView registerPreview;
    private MaterialButton doneButton, registerNameBtn;
    private TextView instructionsText, registerTitle;
    private int flag = 0, photoCount = 0, capturePic = 0, registeredFlag = 0;
    private StorageReference storageReference;
    private ImageCapture imageCapture;
    private TextInputEditText registerFaceName;
    private String name;
    private static Uri capturedImageUri;
    private ArrayList<Uri> imageURIs;
    private int count = 0;
    private FirebaseFirestore db;
    private ProgressBar registerProgress;
    private ConstraintLayout registerFaceLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_face);

        registeredFlag = getIntent().getIntExtra("registeredFlag", 0);

        registerPreview = findViewById(R.id.registerFacePreview);
        doneButton = findViewById(R.id.registerFaceDoneBtn);
        registerNameBtn = findViewById(R.id.registerFaceBtn);
        instructionsText = findViewById(R.id.registerInstructions);
        registerFaceName = findViewById(R.id.registerFaceNameET);
        registerProgress = findViewById(R.id.registerProgress);
        registerFaceLayout = findViewById(R.id.registerFaceLayout);
        registerTitle = findViewById(R.id.registerFaceTitle);
        db = FirebaseFirestore.getInstance();

        registerNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = registerFaceName.getText().toString();
                registerTitle.setVisibility(View.GONE);
                registerFaceName.setVisibility(View.GONE);
                registerNameBtn.setVisibility(View.GONE);
                registerFaceLayout.setVisibility(View.VISIBLE);
            }
        });

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        storageReference = FirebaseStorage.getInstance().getReference();
        imageURIs = new ArrayList<>();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++flag;
                changeCapture();
            }
        });
    }

    private boolean allPermissionsGranted() {
        if (ContextCompat.checkSelfPermission(RegisterFaceActivity.this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permission not granted to start camera.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterFaceActivity.this, LoginSignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                    Toast.makeText(RegisterFaceActivity.this, "Can't find a camera for preview.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterFaceActivity.this, LoginSignUpActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        }, ContextCompat.getMainExecutor(this));

    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(registerPreview.getSurfaceProvider());
        // registerPreview.setImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);
        preview.setSurfaceProvider(registerPreview.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, imageCapture);

    }

    private void changeCapture() {
        switch (flag) {
            case 0:
                doneButton.setText("Next");
                doneButton.setVisibility(View.GONE);
                registerPreview.setVisibility(View.VISIBLE);
                String textLine_11 = "Hitting the \"Next\" button will capture your photos periodically. Try not to switch between ";
                String textLine_12 = "front and back cameras during the process. Follow the instructions.\n";
                String text1 = textLine_11 + textLine_12;
                instructionsText.setText(text1);
            case 1:
                doneButton.setText("Done, next step");
                doneButton.setVisibility(View.GONE);
                String textLine_21 = "Turn your head slowly from side to side, with a neutral face.\n";
                String textLine_22 = "Take 10 seconds to move from one side to the other.\n";
                String text2 = textLine_21 + textLine_22;
                instructionsText.setText(text2);
                capturePic = 1;
                captureImagesPeriodically();
            case 2:
                doneButton.setText("Done, next step");
                doneButton.setVisibility(View.GONE);
                String textLine_31 = "Turn your head slowly from side to side, with a smile.\n";
                String textLine_32 = "Take 10 seconds to move from one side to the other.\n";
                String text3 = textLine_31 + textLine_32;
                instructionsText.setText(text3);
                capturePic = 1;
                captureImagesPeriodically();
            case 3:
                doneButton.setText("Done, finish registration");
                doneButton.setVisibility(View.GONE);
                String textLine_41 = "Hold your phone in front of your face and stretch your hand.\n";
                String textLine_42 = "Move the camera towards and away from your face slowly.\n";
                String text4 = textLine_41 + textLine_42;
                instructionsText.setText(text4);
                capturePic = 1;
                captureImagesPeriodically();
            case 4:
                AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                        .setMessage("Your face has been registered.")
                        .setTitle("Thanks!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(RegisterFaceActivity.this, LoginSignUpActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                /*
                                OkHttpClient httpClient = new OkHttpClient();
                                String url = "https://45812.wayscript.io/";
                                try {
                                    String response = runRequest(url, httpClient);
                                    /*
                                    Intent intent = new Intent(RegisterFaceActivity.this, LoginSignUpActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                } catch (IOException e) {
                                    Toast.makeText(RegisterFaceActivity.this, "IO Exception.", Toast.LENGTH_SHORT).show();
                                }
                                */
                            }
                        }).create();
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                // Toast.makeText(this, "Thanks! Your face has been registered.", Toast.LENGTH_SHORT).show();
        }
    }

    private String runRequest(String URL, OkHttpClient client) throws IOException {
        Request request = new Request.Builder().url(URL).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private void captureImagesPeriodically() {
        while (capturePic == 1) {
            Log.i("INSIDE_CAPTUREPERIOD", "Inside captureImagesPeriodically");
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            File file = new File(Environment.getDataDirectory(), mDateFormat.format(new Date()) + ".jpg");
            Log.i("INSIDE_CAPTUREPERIOD_1", "Created new JPEG");

            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
            Log.i("INSIDE_CAPTUREPERIOD_2", "Capturing images.");
            imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("INSIDE_CAPTUREPERIOD_3", "Captured image.");
                            // Changes here
                            Toast.makeText(RegisterFaceActivity.this, "Image Captured: " + photoCount, Toast.LENGTH_SHORT).show();
                            photoCount++;
                            capturedImageUri = Uri.fromFile(file);
                            imageURIs.add(capturedImageUri);
                        }
                    });
                }

                @Override
                public void onError(@NonNull ImageCaptureException error) {
                    Toast.makeText(RegisterFaceActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (photoCount == 10) {
                RegisterAsyncTask asyncTask = new RegisterAsyncTask(this);
                asyncTask.execute(imageURIs);

            }
        }
    }

    // AsyncTask Inner Class
    private static class RegisterAsyncTask extends AsyncTask<ArrayList<Uri>, Integer, Integer> {
        private WeakReference<RegisterFaceActivity> weakReference;

        RegisterAsyncTask (RegisterFaceActivity activity) {
            weakReference = new WeakReference<RegisterFaceActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            RegisterFaceActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.instructionsText.setText("Please wait, uploading your photos...");
            activity.registerProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            RegisterFaceActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.capturePic = 0;
            activity.instructionsText.setText("Done! You can proceed to the next step.\n");
            activity.photoCount = 0;
            activity.registerProgress.setVisibility(View.GONE);
            activity.doneButton.setVisibility(View.VISIBLE);
            Toast.makeText(activity, "Uploaded "+ activity.count + "images.", Toast.LENGTH_SHORT).show();
            activity.name = activity.registerFaceName.getText().toString().trim();

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("RegNo", activity.name);

            if (activity.registeredFlag == 1) {
                Intent intent = new Intent(activity, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
            } else {
                activity.db.collection("RegisteredFaces").document("Username " + activity.name).set(userMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                activity.db.collection("New Users").document("Username " + activity.name).set(userMap)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                Intent intent = new Intent(activity, AddDetailsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("regNo", activity.name);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        @Override
        protected Integer doInBackground(ArrayList<Uri>... Uris) {

            RegisterFaceActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }

            StorageReference registerFacesRef = activity.storageReference.child("New Datasets/" + activity.name);
            StorageReference photoFacesRef = activity.storageReference.child("Photos/" + activity.name);
            ArrayList<Uri> passedArray = new ArrayList<>();
            passedArray = Uris[0];

            capturedImageUri = passedArray.get(0);
            UploadTask uploadTask1 = photoFacesRef.putFile(capturedImageUri);

            for (int i = 0; i < Uris.length; i++) {

                capturedImageUri = passedArray.get(i);
                UploadTask uploadTask = registerFacesRef.putFile(capturedImageUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // finish this
                        activity.count++;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, LoginSignUpActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                    }
                });
            }

            return null;
        }
    }
}