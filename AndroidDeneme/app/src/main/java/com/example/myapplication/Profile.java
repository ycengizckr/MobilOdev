package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class Profile extends AppCompatActivity {

    EditText pAd, pSAd, pNum, pMail, pSMail, pAcc, pEduT, pEduS;
    FirebaseAuth mAuth;
    public static final int CAMERA_PERM_CODE = 101;
    ImageView selectedImage;
    DatabaseReference mRef;
    Button updateButton, saveButton, photoButton;

    ActivityResultLauncher activityResultLauncher;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance("https://androidproje-36714-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        pAd = (EditText) findViewById(R.id.profileAd);
        pSAd = (EditText) findViewById(R.id.profileSoyad);
        pNum = (EditText) findViewById(R.id.profileNumber);
        pMail = (EditText) findViewById(R.id.profileMail);
        pSMail = (EditText) findViewById(R.id.profileSecondMail);
        pAcc = (EditText) findViewById(R.id.profileAccType);
        pEduT = (EditText) findViewById(R.id.profileEduType);
        pEduS = (EditText) findViewById(R.id.profileEduSemester);

        updateButton = (Button) findViewById(R.id.profileButton);
        saveButton = (Button) findViewById(R.id.profileSaveButton);
        photoButton = (Button) findViewById(R.id.photoButton);

        selectedImage = (ImageView) findViewById(R.id.imageView2);

        userInfo();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if (o.getResultCode() == RESULT_OK && o.getData() != null) {
                    Bundle bundle = o.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    selectedImage.setImageBitmap(bitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userID = user.getUid();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profil_resimleri").child(userID + ".jpg");
                        storageReference.putBytes(imageData)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                mRef.child("Kullanicilar").child(userID).child("profilResimUrl").setValue(uri.toString());
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Profile.this, "Fotoğraf yüklenemedi", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View v) {
                askCameraPermissions();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(Profile.this, "Kullanılabilecek Kamera Bulunamadı", Toast.LENGTH_SHORT).show();
                }
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pAd.setEnabled(true);
                pSAd.setEnabled(true);
                pSMail.setEnabled(true);
                pEduT.setEnabled(true);
                pEduS.setEnabled(true);
                pNum.setEnabled(true);
                updateButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.VISIBLE);
                photoButton.setVisibility(View.VISIBLE);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileUpdate();
                        updateButton.setVisibility(View.VISIBLE);
                        photoButton.setVisibility(View.GONE);
                        saveButton.setVisibility(View.GONE);
                        pAd.setEnabled(false);
                        pSAd.setEnabled(false);
                        pSMail.setEnabled(false);
                        pEduT.setEnabled(false);
                        pEduS.setEnabled(false);
                        pNum.setEnabled(false);
                    }
                });

            }
        });

    }

    private void userInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            DatabaseReference userRef = mRef.child("Kullanicilar").child(userID);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String ad = snapshot.child("Adi").getValue(String.class);
                        String soyad = snapshot.child("SoyAdi").getValue(String.class);
                        String numara = snapshot.child("Telefon").getValue(String.class);
                        String mail = snapshot.child("Maili").getValue(String.class);
                        String sMail = snapshot.child("SecondMail").getValue(String.class);
                        String accType = snapshot.child("AccountType").getValue(String.class);
                        String eduType = snapshot.child("EducationType").getValue(String.class);
                        String eduSemester = snapshot.child("EducationSemester").getValue(String.class);

                        pAd.setText(ad);
                        pSAd.setText(soyad);
                        pNum.setText(numara);
                        pMail.setText(mail);
                        pSMail.setText(sMail);
                        pAcc.setText(accType);
                        pEduT.setText(eduType);
                        pEduS.setText(eduSemester);

                        String profilResimUrl = snapshot.child("profilResimUrl").getValue(String.class);
                        if (profilResimUrl != null) {
                            // Resmi ImageView'a yüklemek için Picasso veya Glide kullanın
                            Picasso.get().load(profilResimUrl).into(selectedImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Profile.this, "Veritabanına erişim hatası: ", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void profileUpdate(){
        FirebaseUser user = mAuth.getCurrentUser();

        String updatedAd = pAd.getText().toString();
        String updatedSAd = pSAd.getText().toString();
        String updatedSMail = pSMail.getText().toString();
        String updatedEduT = pEduT.getText().toString();
        String updatedEduS = pEduS.getText().toString();
        String updateNum = pNum.getText().toString();

        if (user != null){
            String userID = user.getUid();
            DatabaseReference userRef = mRef.child("Kullanicilar").child(userID);
            userRef.child("Adi").setValue(updatedAd);
            userRef.child("SoyAdi").setValue(updatedSAd);
            userRef.child("SecondMail").setValue(updatedSMail);
            userRef.child("EducationType").setValue(updatedEduT);
            userRef.child("EducationSemester").setValue(updatedEduS);
            userRef.child("Telefon").setValue(updateNum);
        }
    }
    private void askCameraPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            } else {
                Toast.makeText(Profile.this, "Kamera izni lazım: ", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
