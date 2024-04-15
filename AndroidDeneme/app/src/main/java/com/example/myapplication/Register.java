package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Register extends AppCompatActivity {

    Button registerButton;
    EditText usurname, password, name, surname, id;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference mReference;

    HashMap<String, String> mData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerscreen);

        usurname = (EditText) findViewById(R.id.registerMail_text);
        password = (EditText) findViewById(R.id.registerPassword_text);
        name = (EditText) findViewById(R.id.registerName_text);
        surname = (EditText) findViewById(R.id.registerSName_text);
        id = (EditText) findViewById(R.id.registerID_text);

        registerButton = (Button) findViewById(R.id.register_button);

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance("https://androidproje-36714-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = usurname.getText().toString();
                String sifre = password.getText().toString();
                String nameText = name.getText().toString();
                String surnameText = surname.getText().toString();
                String userID = id.getText().toString();
                String sMail = "ikinci mail";
                String EduType = "Eğitim Düzeyi";
                String EduSemester = "Eğitim Yarıyılı";
                String phoneNum = "Telefon Numarası";
                String AccType;


                if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(sifre)) {
                    Toast.makeText(Register.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isOnlyLetters(nameText)) {
                    name.setError("Geçerli bir ad giriniz");
                    return;
                }

                if (!isOnlyLetters(surnameText)) {
                    surname.setError("Geçerli bir soyad giriniz");
                    return;
                }

                if (sifre.length() < 6) {
                    // Şifre 6 karakterden kısa ise kullanıcıya uyarı ver
                    Toast.makeText(Register.this, "Şifre en az 6 karakter olmalıdır", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mail.endsWith("@std.yildiz.edu.tr") || mail.endsWith("@gmail.com")) {
                    AccType = "Öğrenci";
                } else if (mail.endsWith("@yildiz.edu.tr")) {
                    AccType = "Akademisyen";
                } else {
                    Toast.makeText(Register.this, "Geçerli bir e-posta adresi giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(mail, sifre)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    mUser = mAuth.getCurrentUser();
                                    mData = new HashMap<>();
                                    mData.put("Adi", nameText);
                                    mData.put("SoyAdi", surnameText);
                                    mData.put("Numarasi", userID);
                                    mData.put("Maili", mail);
                                    mData.put("Şifre" ,sifre);
                                    mData.put("KullaniciId", mUser.getUid());
                                    mData.put("SecondMail", sMail);
                                    mData.put("AccountType", AccType);
                                    mData.put("EducationType", EduType);
                                    mData.put("EducationSemester", EduSemester);
                                    mData.put("Telefon", phoneNum);

                                    mReference.child("Kullanicilar").child(mUser.getUid())
                                                    .setValue(mData)
                                                            .addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){

                                                                    } else {
                                                                        Toast.makeText(Register.this, "Olmadi", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                    sendEmailVerification();
                                } else {
                                    Toast.makeText(Register.this, "Kayit basarisiz", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void sendEmailVerification(){

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null && !user.isEmailVerified()){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "E-posta doğrulama linki gönderildi. Lütfen e-posta adresinizi kontrol edin.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(Register.this, "E-posta doğrulama linki gönderilemedi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean isOnlyLetters(String text) {
        return text.matches("^[\\p{L}]+$");
    }

}
