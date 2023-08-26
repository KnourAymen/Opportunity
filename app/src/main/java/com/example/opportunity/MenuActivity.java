package com.example.opportunity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    Button btnLogin, btnSignUp;
    long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnLogin = findViewById(R.id.login_button);
        btnSignUp = findViewById(R.id.sign_up_button);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToLoginActivity = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(intentToLoginActivity);
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToSignUpActivity = new Intent(MenuActivity.this, SignUpActivity.class);
                startActivity(intentToSignUpActivity);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if ((time + 2000) > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Press again", Toast.LENGTH_LONG).show();
        }
        time = System.currentTimeMillis();
    }
}