package com.example.opportunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private CustomEditText signUpBirthDayEditText;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private EditText signUpEmailEditText, signUpPasswordEditText, signUpRewritePasswordEditText, signUpFirstNameEditText, signUpLastNameEditText, phoneNumberEditText, signUpAddressEditText;
    private Button btnSignUp;
    private TextView loginRedirectText;
    String[] userSpecialtyItems = {"DEVOAM", "DEVOWFS", "GEOCF", "GEOCM", "GEOOM", "GEORH", "IDOCC", "IDOCS", "IDOSR", "PD"};
    AutoCompleteTextView signUpUserSpecialtyAutoCompleteTextView;
    ArrayAdapter<String> userSpecialtyArrayAdapter;
    String phonePattern;
    Pattern namePattern;
    ProgressDialog progressDialog;
    long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpEmailEditText = findViewById(R.id.sign_up_email_edit_text);
        signUpPasswordEditText = findViewById(R.id.sign_up_password_edit_text);
        signUpRewritePasswordEditText = findViewById(R.id.sign_up_rewrite_password_edit_text);
        signUpFirstNameEditText = findViewById(R.id.sign_up_first_name_edit_text);
        signUpLastNameEditText = findViewById(R.id.sign_up_last_name_edit_text);
        signUpBirthDayEditText = findViewById(R.id.sign_up_birth_day_edit_text);
        signUpAddressEditText = findViewById(R.id.sign_up_address_edit_text);
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        signUpUserSpecialtyAutoCompleteTextView = findViewById(R.id.sign_up_specialty_edit_text);
        btnSignUp = findViewById(R.id.sign_up_button);
        firebaseAuth = FirebaseAuth.getInstance();
        loginRedirectText = findViewById(R.id.login_redirect_text);
        phonePattern = "^(05|06|07|08)[0-9]{8}$";
        namePattern = Pattern.compile("^[a-zA-Z]{3,}$");
        progressDialog = new ProgressDialog(this);

        /** ######################################################################################## */

        userSpecialtyArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, userSpecialtyItems);
        signUpUserSpecialtyAutoCompleteTextView.setAdapter(userSpecialtyArrayAdapter);

        signUpUserSpecialtyAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                signUpUserSpecialtyAutoCompleteTextView.setText(item);
            }
        });

        /** ######################################################################################## */

        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        signUpBirthDayEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    materialDatePicker.show(getSupportFragmentManager(), "Material_Date_Picker");
                    materialDatePicker.setCancelable(false);
                    materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick(Object selection) {
                            signUpBirthDayEditText.setText(materialDatePicker.getHeaderText());
                        }
                    });
                }
                return false;
            }
        });

        /** ######################################################################################## */

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("users");

                String userAddress = signUpAddressEditText.getText().toString().trim();
                String userBirthDay = signUpBirthDayEditText.getText().toString().trim();
                String userFirstName = signUpFirstNameEditText.getText().toString().trim();
                String userLastName = signUpLastNameEditText.getText().toString().trim();
                String userPassword = signUpPasswordEditText.getText().toString().trim();
                String userRewritePassword = signUpRewritePasswordEditText.getText().toString().trim();
                String userPhoneNumber = phoneNumberEditText.getText().toString().trim();
                String userSpecialty = signUpUserSpecialtyAutoCompleteTextView.getText().toString().trim();
                String userEmail = signUpEmailEditText.getText().toString().trim().trim();

                Matcher firstNameMatcher = namePattern.matcher(userFirstName);
                Matcher lastNameMatcher = namePattern.matcher(userLastName);

                if (userEmail.isEmpty()) {
                    signUpEmailEditText.setError("Email cannot be empty");
                    progressDialog.dismiss();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    signUpEmailEditText.setError("Please enter correct email");
                    progressDialog.dismiss();
                    return;
                }
                if (userPassword.isEmpty()) {
                    signUpPasswordEditText.setError("Password cannot be empty");
                    progressDialog.dismiss();
                    return;
                }
                if (!userRewritePassword.equals(userPassword)) {
                    signUpRewritePasswordEditText.setError("Password is not matched");
                    progressDialog.dismiss();
                    return;
                }
                if (userFirstName.isEmpty()) {
                    signUpFirstNameEditText.setError("First name cannot be empty");
                    progressDialog.dismiss();
                    return;
                }
                if (!firstNameMatcher.matches()) {
                    signUpFirstNameEditText.setError("Please enter a valid first name (at least 3 letters, no numbers or special characters)");
                    progressDialog.dismiss();
                    return;
                }
                if (userLastName.isEmpty()) {
                    signUpLastNameEditText.setError("Last name cannot be empty");
                    progressDialog.dismiss();
                    return;
                }
                if (!lastNameMatcher.matches()) {
                    signUpLastNameEditText.setError("Please enter a valid last name (at least 3 letters, no numbers or special characters)");
                    progressDialog.dismiss();
                    return;
                }
                if (userBirthDay.isEmpty()) {
                    signUpBirthDayEditText.setError("Birth day cannot be empty");
                    progressDialog.dismiss();
                    return;
                }
                if (userAddress.isEmpty()) {
                    signUpAddressEditText.setError("Address cannot be empty");
                    progressDialog.dismiss();
                    return;
                }
                if (userPhoneNumber.isEmpty()) {
                    phoneNumberEditText.setError("Phone number cannot be empty");
                    progressDialog.dismiss();
                    return;
                }
                if (!userPhoneNumber.matches(phonePattern)) {
                    phoneNumberEditText.setError("Please enter a valid phone number");
                    progressDialog.dismiss();
                    return;
                }

                if (signUpUserSpecialtyAutoCompleteTextView.getText().toString().isEmpty()) {
                    signUpUserSpecialtyAutoCompleteTextView.setError("Please select an item");
                    progressDialog.dismiss();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Users users = new Users(userAddress, userBirthDay, userEmail, userFirstName, userLastName, userPassword, userPhoneNumber, userSpecialty);
                            databaseReference.child(encodeEmail(userEmail)).setValue(users);
                            Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                            Intent intentToLoginActivity = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intentToLoginActivity);
                            finish();

                        } else {
                            Toast.makeText(SignUpActivity.this, "SignUp Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
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