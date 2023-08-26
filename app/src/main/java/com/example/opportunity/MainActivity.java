package com.example.opportunity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    TextInputEditText userOfpptFirstCommentEditText, userOfpptSecondCommentEditText, userDreamsCommentEditText;
    CustomEditText userCvFileCustomEditText;
    MaterialButton submitBtn;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    String userAddressFromMainActivity, userBirthDayFromMainActivity, userEmailFromMainActivity, userEncodedEmailFromMainActivity, userFirstNameFromMainActivity, userLastNameFromMainActivity, userPasswordFromMainActivity, userPhoneNumberFromMainActivity, userSpecialtyFromMainActivity;
    Uri uri;
    private ActivityResultLauncher<String> filePickerLauncher;
    String fileFormat;
    String theOfpptFirstComment, theOfpptSecondComment, theDreamsComment, theNewOfpptFirstComment, theNewOfpptSecondComment, theNewDreamsComment;
    StorageReference strReference;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    long time = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userOfpptFirstCommentEditText = findViewById(R.id.offpt_advantages_text_input_edit_text);
        userOfpptSecondCommentEditText = findViewById(R.id.offpt_disadvantages_input_edit_text);
        userDreamsCommentEditText = findViewById(R.id.trainee_dreams_input_edit_text);
        userCvFileCustomEditText = findViewById(R.id.select_file_input_edit_text);
        submitBtn = findViewById(R.id.submit_button);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        submitBtn.setEnabled(false);

        Toolbar toolbar = findViewById(R.id.activity_main_tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        /** ######################################################################################## */

        Intent dataFromLoginActivity = getIntent();
        userAddressFromMainActivity = dataFromLoginActivity.getStringExtra("ADDRESS");
        userBirthDayFromMainActivity = dataFromLoginActivity.getStringExtra("BIRTH_DAY");
        userEmailFromMainActivity = dataFromLoginActivity.getStringExtra("EMAIL");
        userEncodedEmailFromMainActivity = dataFromLoginActivity.getStringExtra("ENCODED_EMAIL");
        userFirstNameFromMainActivity = dataFromLoginActivity.getStringExtra("FIRST_NAME");
        userLastNameFromMainActivity = dataFromLoginActivity.getStringExtra("LAST_NAME");
        userPasswordFromMainActivity = dataFromLoginActivity.getStringExtra("PASSWORD");
        userPhoneNumberFromMainActivity = dataFromLoginActivity.getStringExtra("PHONE_NUMBER");
        userSpecialtyFromMainActivity = dataFromLoginActivity.getStringExtra("SPECIALTY");


        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri == null) {
                Toast.makeText(getApplicationContext(), "No file selected", Toast.LENGTH_LONG).show();
                submitBtn.setEnabled(false);
                return;
            }
            String mimeType = getContentResolver().getType(uri);
            if (TextUtils.equals(mimeType, "application/pdf")) {
                fileFormat = "pdf";
                userCvFileCustomEditText.setText(getFileName(uri));
                submitBtn.setEnabled(true);
            } else if (TextUtils.equals(mimeType, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                fileFormat = "docx";
                userCvFileCustomEditText.setText(getFileName(uri));
                submitBtn.setEnabled(true);
            } else {
                Toast.makeText(getApplicationContext(), "We only accept pdf and docx format", Toast.LENGTH_LONG).show();
                userCvFileCustomEditText.setText("");
                submitBtn.setEnabled(false);
            }
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitData(uri, fileFormat);
                }
            });
        });

        userCvFileCustomEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    filePickerLauncher.launch("*/*");
                }
                return false;
            }
        });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void submitData(Uri data, String fileFormat) {

        theOfpptFirstComment = userOfpptFirstCommentEditText.getText().toString().trim();
        theOfpptSecondComment = userOfpptSecondCommentEditText.getText().toString();
        theDreamsComment = userDreamsCommentEditText.getText().toString();

        if (!TextUtils.isEmpty(theOfpptFirstComment)) {
            theNewOfpptFirstComment = theOfpptFirstComment;
        } else {
            theNewOfpptFirstComment = "Nothing";
        }

        if (!TextUtils.isEmpty(theOfpptSecondComment)) {
            theNewOfpptSecondComment = theOfpptSecondComment;
        } else {
            theNewOfpptSecondComment = "Nothing";
        }

        if (!TextUtils.isEmpty(theDreamsComment)) {
            theNewDreamsComment = theDreamsComment;
        } else {
            theNewDreamsComment = "Nothing";
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Data are Loading");
        progressDialog.show();

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_exit);
        AlertDialog dialog = builder.create();


        if (fileFormat.equals("pdf")) {
            strReference = storageReference.child(userSpecialtyFromMainActivity).child(userSpecialtyFromMainActivity + System.currentTimeMillis() + ".pdf");
        } else {
            strReference = storageReference.child(userSpecialtyFromMainActivity).child(userSpecialtyFromMainActivity + System.currentTimeMillis() + ".docx");
        }

        strReference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri = uriTask.getResult();
                        Users users = new Users(userAddressFromMainActivity, userBirthDayFromMainActivity, theNewDreamsComment, userEmailFromMainActivity, userCvFileCustomEditText.getText().toString(), uri.toString(), userFirstNameFromMainActivity, userLastNameFromMainActivity, theNewOfpptFirstComment, theNewOfpptSecondComment, userPasswordFromMainActivity, userPhoneNumberFromMainActivity, userSpecialtyFromMainActivity);
                        databaseReference.child(userEncodedEmailFromMainActivity).setValue(users);
                        progressDialog.dismiss();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Data Uploaded..." + (int) progress + "%");
                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isComplete()) {
                            Toast.makeText(MainActivity.this, "Sending Successful", Toast.LENGTH_LONG).show();
                            dialog.show();
                            dialog.findViewById(R.id.btn_i_hope).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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