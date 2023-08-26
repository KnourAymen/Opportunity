package com.example.opportunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class DetailActivity extends AppCompatActivity {

    TextView detailUserFullName, detailUserEmail, detailUserBirthDay, detailUserAddress, detailUserPhoneNumber, detailUserSpecialty, detailUserOfpptAdvantagesComment, detailUserOfpptDisadvantagesComment, detailUserDreamsComment;
    FloatingActionButton deleteButton, generateQrCodeButton;
    ImageView detailUserCvFileImageView, qrImageView;
    String userKey = "";
    String fileUrl;
    Bundle bundle;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private AppCompatActivity avAppCompatActivity;
    String qrCodeData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailUserFullName = findViewById(R.id.detail_user_full_name);
        detailUserCvFileImageView = findViewById(R.id.detail_user_cv_file);
        detailUserEmail = findViewById(R.id.detail_user_email);
        detailUserBirthDay = findViewById(R.id.detail_user_birth_day);
        detailUserAddress = findViewById(R.id.detail_user_address);
        detailUserPhoneNumber = findViewById(R.id.detail_user_phone_number);
        detailUserSpecialty = findViewById(R.id.detail_user_specialty);
        detailUserOfpptAdvantagesComment = findViewById(R.id.detail_user_ofppt_advantages_comment);
        detailUserOfpptDisadvantagesComment = findViewById(R.id.detail_user_ofppt_disadvantages_comment);
        detailUserDreamsComment = findViewById(R.id.detail_user_dreams_comment);
        deleteButton = findViewById(R.id.delete_button);
        generateQrCodeButton = findViewById(R.id.generate_qr_code_button);

        Toolbar toolbar = findViewById(R.id.detail_activity_tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        qrImageView = findViewById(R.id.qr_code_image_view);
        avAppCompatActivity = this;

        bundle = getIntent().getExtras();
        if (bundle != null) {
            userKey = bundle.getString("USER_KEY");
            detailUserAddress.setText(bundle.getString("USER_ADDRESS"));
            detailUserBirthDay.setText(bundle.getString("USER_BIRTH_DAY"));
            detailUserDreamsComment.setText(bundle.getString("USER_DREAMS"));
            detailUserEmail.setText(bundle.getString("USER_EMAIL"));
            detailUserFullName.setText(bundle.getString("USER_FULL_NAME"));
            detailUserOfpptAdvantagesComment.setText(bundle.getString("USER_ADVANTAGES_COMMENT"));
            detailUserOfpptDisadvantagesComment.setText(bundle.getString("USER_DISADVANTAGES_COMMENT"));
            detailUserPhoneNumber.setText(bundle.getString("USER_PHONE_NUMBER"));
            detailUserSpecialty.setText(bundle.getString("USER_SPECIALTY"));

            detailUserCvFileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fileUrl = bundle.getString("USER_FILE_URL");
                    String fileType = "";
                    if (fileUrl.endsWith(".pdf")) {
                        fileType = "application/pdf";
                    } else if (fileUrl.endsWith(".docx")) {
                        fileType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    /** intent.setDataAndType(Uri.parse(fileUrl), fileType); */
                    intent.setType(fileType);
                    intent.setData(Uri.parse(fileUrl));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
            });
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userKey);

                databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        generateQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                qrCodeData = "Full Name : " + detailUserFullName + "\n Birth Day " + detailUserBirthDay + "\n Address " + detailUserAddress + "\nEmail " + detailUserEmail + "\n Phone Number " + detailUserPhoneNumber + "\nSpecialty " + detailUserSpecialty;
                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = Math.min(width, height);
                smallerDimension = smallerDimension * 3 / 4;

                qrgEncoder = new QRGEncoder(qrCodeData, null, QRGContents.Type.TEXT, smallerDimension);
                qrgEncoder.setColorBlack(Color.parseColor("#4c84ff"));
                qrgEncoder.setColorWhite(Color.parseColor("#ffffff"));
                try {
                    bitmap = qrgEncoder.getBitmap();
                    qrImageView.setImageBitmap(bitmap);
                    qrImageView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    String fileName = "qr_code_" + System.currentTimeMillis() + ".jpg";

                    /** Save the QR code Bitmap image to the device's gallery. */
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "FavoriteTrainees");
                    }

                    Uri imageUri;
                    OutputStream outputStream = null;
                    try {
                        ContentResolver resolver = getApplicationContext().getContentResolver();
                        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        outputStream = resolver.openOutputStream(imageUri);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                        /** Notify the system that a new image has been added to the gallery. */
                        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imageUri.toString()}, null, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(avAppCompatActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}