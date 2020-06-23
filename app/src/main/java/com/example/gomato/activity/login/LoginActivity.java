package com.example.gomato.activity.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.gomato.R;

public class LoginActivity extends AppCompatActivity {
    private int currentStateId = R.id.start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}