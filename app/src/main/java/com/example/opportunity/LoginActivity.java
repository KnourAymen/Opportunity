package com.example.opportunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private EditText loginEmailEditText, loginPasswordEditText;
    private Button btnLogin;
    private TextView signUpRedirectText;
    TextView forgotPassword;
    String userEmail;
    ProgressDialog progressDialog;
    long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        loginEmailEditText = findViewById(R.id.login_email_edit_text);
        loginPasswordEditText = findViewById(R.id.login_password_edit_text);
        forgotPassword = findViewById(R.id.forgot_password);
        btnLogin = findViewById(R.id.login_button);
        signUpRedirectText = findViewById(R.id.sign_up_redirect_text);

        progressDialog = new ProgressDialog(this);

        /** ######################################################################################## */

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
                userEmail = loginEmailEditText.getText().toString();
                String userPassword = loginPasswordEditText.getText().toString();

                if (!userEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    if (!userPassword.isEmpty()) {
                        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        verifyEmailAddress();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        progressDialog.dismiss();
                        loginPasswordEditText.setError("Empty fields are not allowed");
                    }
                } else if (userEmail.isEmpty()) {
                    progressDialog.dismiss();
                    loginEmailEditText.setError("Empty fields are not allowed");
                } else {
                    progressDialog.dismiss();
                    loginEmailEditText.setError("Please enter correct email");
                }
            }
        });

        /** ######################################################################################## */

        signUpRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });

        /** ######################################################################################## */

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText emailBox = dialogView.findViewById(R.id.email_box);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();

                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                            Toast.makeText(LoginActivity.this, "Enter your registered email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
    }

    private void verifyEmailAddress() {

        if (firebaseUser != null && !firebaseUser.isEmailVerified()) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Verification email sent, Validate Your Email", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {

            DatabaseReference referenceForUsers = FirebaseDatabase.getInstance().getReference("users");
            referenceForUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    String retrievedUserEmail = snapshot.child(encodeEmail(userEmail)).child("userEmail").getValue(String.class);
                    if (userEmail.equals(retrievedUserEmail)) {
                        String retrievedUserAddress = snapshot.child(encodeEmail(userEmail)).child("userAddress").getValue(String.class);
                        String retrievedUserBirthDay = snapshot.child(encodeEmail(userEmail)).child("userBirthDay").getValue(String.class);
                        String retrievedUserFirstName = snapshot.child(encodeEmail(userEmail)).child("userFirstName").getValue(String.class);
                        String retrievedUserLastName = snapshot.child(encodeEmail(userEmail)).child("userLastName").getValue(String.class);
                        String retrievedUserPassword = snapshot.child(encodeEmail(userEmail)).child("userPassword").getValue(String.class);
                        String retrievedUserPhoneNumber = snapshot.child(encodeEmail(userEmail)).child("userPhoneNumber").getValue(String.class);
                        String retrievedUserSpecialty = snapshot.child(encodeEmail(userEmail)).child("userSpecialty").getValue(String.class);
                        String encodedUserEmail = encodeEmail(retrievedUserEmail);
                        Intent intentToMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                        intentToMainActivity.putExtra("ADDRESS", retrievedUserAddress);
                        intentToMainActivity.putExtra("BIRTH_DAY", retrievedUserBirthDay);
                        intentToMainActivity.putExtra("EMAIL", retrievedUserEmail);
                        intentToMainActivity.putExtra("ENCODED_EMAIL", encodedUserEmail);
                        intentToMainActivity.putExtra("FIRST_NAME", retrievedUserFirstName);
                        intentToMainActivity.putExtra("LAST_NAME", retrievedUserLastName);
                        intentToMainActivity.putExtra("PASSWORD", retrievedUserPassword);
                        intentToMainActivity.putExtra("PHONE_NUMBER", retrievedUserPhoneNumber);
                        intentToMainActivity.putExtra("SPECIALTY", retrievedUserSpecialty);
                        Toast.makeText(LoginActivity.this, "Login Successful m", Toast.LENGTH_SHORT).show();
                        startActivity(intentToMainActivity);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            DatabaseReference referenceForAdministrator = FirebaseDatabase.getInstance().getReference("admin");
            referenceForAdministrator.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    String retrievedAdminEmail = snapshot.child(encodeEmail(userEmail)).child("userEmail").getValue(String.class);

                    if (userEmail.equals(retrievedAdminEmail)) {
                        Intent intentToAdminActivity = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(intentToAdminActivity);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public String encodeEmail(String email) {
        return email.replace(".", ",").replace("@", "_");
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