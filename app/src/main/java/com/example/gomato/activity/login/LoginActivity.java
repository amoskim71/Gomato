package com.example.gomato.activity.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gomato.R;
import com.example.gomato.activity.DashboardActivity;
import com.example.gomato.activity.SplashActivity;
import com.example.gomato.common.PrefsData;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private int currentStateId = R.id.start;
    private TextView login_text, sign_up_text, forgot_password;
    private TextInputLayout login_email, login_password;
    private TextInputLayout sign_up_email, sign_up_password, confirm_pwd ;
    private MotionLayout motionLayout;
    PrefsData prefsData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        prefsData = new PrefsData(this);
        login_text = findViewById(R.id.login_text);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        motionLayout = findViewById(R.id.motionLayout);
        sign_up_text = findViewById(R.id.sign_up_text);
        sign_up_email = findViewById(R.id.sign_up_email);
        sign_up_password = findViewById(R.id.sign_up_password);
        confirm_pwd = findViewById(R.id.sign_up_confirm_password);
        login_text.setOnClickListener((view) ->{
            switch(currentStateId) {
                case R.id.start:
                    String email = login_email.getEditText().getText().toString().trim();
                    String password = login_password.getEditText().getText().toString().trim();
                    if (!email.isEmpty() && email.equals("sugandhpatna95@gmail.com")){
                        if (!password.isEmpty() && password.length()>6) {
                            prefsData.setUserLogin(true);
                            startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                        }else
                            Toast.makeText(this, "Password should be greater than 6 character", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Invalid Email Id", Toast.LENGTH_SHORT).show();
                        currentStateId = R.id.end ;
                    }
                default:
                    motionLayout.transitionToStart();
            }
        });

        sign_up_text.setOnClickListener((view)->{
            switch (currentStateId){
                case R.id.end:
                    String email = sign_up_email.getEditText().getText().toString().trim();
                    String password = sign_up_password.getEditText().getText().toString().trim();
                    String confirmPwd = confirm_pwd.getEditText().getText().toString().trim();
                    if (!email.isEmpty() && email.equals("sugandhpatna95@gmail.com")){
                        if (!password.isEmpty() && password.length()>6)
                            startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                        else
                            Toast.makeText(this, "Password should be greater than 6 character", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Invalid Email Id", Toast.LENGTH_SHORT).show();
                        currentStateId = R.id.start ;
                    }
                default:
                    motionLayout.transitionToEnd();
            }
        });

        forgot_password.setOnClickListener((view)->{
            startActivity(new Intent(this,ResetPasswordActivity.class));
        });


        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {            }
            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {            }
            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                currentStateId = currentId;
            }
            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {            }
        });
    }
}