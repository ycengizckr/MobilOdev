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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PasswordReset extends AppCompatActivity {

    Button resetButton;
    EditText resetMail, resetID;
    DatabaseReference mRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpassword);

        resetButton = (Button) findViewById(R.id.passReset);
        resetMail = (EditText) findViewById(R.id.resetMail);
        resetID = (EditText) findViewById(R.id.resetID);
        mRef = FirebaseDatabase.getInstance("https://androidproje-36714-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Kullanicilar");

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = resetMail.getText().toString();
                String userID = resetID.getText().toString();

                if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(userID)) {
                    Toast.makeText(PasswordReset.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                    return;
                }

                mRef.orderByChild("Maili").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            String fUserID = snapshot1.child("Numarasi").getValue(String.class);
                            if(fUserID.equals(userID)) {
                                FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(), "Şifre sıfırlama e-postası gönderildi. Lütfen e-posta adresinizi kontrol edin.", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(PasswordReset.this, "E-posta gönderilemedi: " , Toast.LENGTH_SHORT).show();
                                                }
                                                }

                                        });
                                return;
                            }
                        }
                        Toast.makeText(PasswordReset.this, "Girdiğiniz bilgilere uygun bir kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PasswordReset.this, "Veritabanı hatası: ", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }
}
