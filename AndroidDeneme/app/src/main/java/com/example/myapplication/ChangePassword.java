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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {

    DatabaseReference mRef;
    EditText changeMail, changePassword, changePaswordAgain;
    Button changeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);

        changeMail = (EditText) findViewById(R.id.changeMail);
        changePassword = (EditText) findViewById(R.id.newPassword);
        changePaswordAgain = (EditText) findViewById(R.id.newPasswordAgain);
        changeButton = (Button) findViewById(R.id.chancePasswordButton);

        mRef = FirebaseDatabase.getInstance("https://androidproje-36714-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Kullanicilar");

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = changeMail.getText().toString();
                String newPassword = changePassword.getText().toString();
                String newPasswordAgain = changePaswordAgain.getText().toString();

                if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(newPasswordAgain)) {
                    Toast.makeText(ChangePassword.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                    return;
                }



                if (newPassword.equals(newPasswordAgain)){
                    mRef.orderByChild("Maili").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    String userID = dataSnapshot.getKey();
                                    String oldPassword = dataSnapshot.child("Şifre").getValue(String.class);
                                    if (!oldPassword.equals(newPassword)) {
                                        mRef.child(userID).child("Şifre").setValue(newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ChangePassword.this, "Şifre başarıyla değiştirildi", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            Toast.makeText(ChangePassword.this, "Şifre değiştirilirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user!=null){
                                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ChangePassword.this, "Şifre başarıyla değiştirildi", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ChangePassword.this, "Şifre değiştirilirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(ChangePassword.this, "Yeni şifre eski şifre ile aynı olamaz", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(ChangePassword.this, "Sifreler ayni degil", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
