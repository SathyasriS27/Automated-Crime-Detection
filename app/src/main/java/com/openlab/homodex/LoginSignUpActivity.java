package com.openlab.homodex;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.openlab.homodex.TFLiteFaceRecognition.DetectorActivity;
import com.openlab.humanpokedex.R;
import com.openlab.homodex.TFLiteFaceRecognition.CameraActivity;

public class LoginSignUpActivity extends AppCompatActivity {

    private MaterialButton signIn, signUp, recFace, regFace, devBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        signIn = findViewById(R.id.signInButton);
        signUp = findViewById(R.id.signUpButton);
        recFace = findViewById(R.id.recognizeFaceButton);
        regFace = findViewById(R.id.registerFaceButton);
        devBtn = findViewById(R.id.developerButton);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        recFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizeFace();
            }
        });

        regFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFace();
            }
        });

        devBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                developerAction();
            }
        });
    }

    private void signIn() {
        Intent intent = new Intent(LoginSignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void signUp() {
        Intent intent = new Intent(LoginSignUpActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void recognizeFace() {
        Intent intent = new Intent(LoginSignUpActivity.this, DetectorActivity.class);
        startActivity(intent);
    }

    private void registerFace() {
        Intent intent = new Intent(LoginSignUpActivity.this, RegisterFaceActivity.class);
        startActivity(intent);
    }

    private void developerAction() {
        // Go to requisite activity (changeable)
        Intent intent = new Intent(LoginSignUpActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
