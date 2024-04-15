package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    Button loginButton, passwordReset;

    EditText usurname, password;
    FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);

        usurname = (EditText) findViewById(R.id.usurname_text);
        password = (EditText) findViewById(R.id.registerPassword_text);
        loginButton = (Button) findViewById(R.id.giris_button);
        passwordReset = (Button) findViewById(R.id.passwordReset_button);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = usurname.getText().toString();
                String sifre = password.getText().toString();

                if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(sifre)) {
                    Toast.makeText(Login.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(mail, sifre)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if(user != null && user.isEmailVerified()){


                                    Toast.makeText(Login.this, "Başarıyla giriş yapıldı", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, MainScreen.class);
                                    startActivity(intent);
                                    finish(); // Giriş ekranını kapat
                                    } else {
                                        Toast.makeText(Login.this, "Mailinizi Doğrulayınız", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                } else {
                                    // Giriş başarısız ise kullanıcıya hata mesajı göster
                                    Toast.makeText(Login.this, "Giriş başarısız ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(Login.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Giriş başarısız olursa bu metot çağrılır
                                Toast.makeText(Login.this, "Giriş başarısız. Lütfen bilgilerinizi kontrol ediniz", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, PasswordReset.class);
                startActivity(intent);
            }
        });
    }
}
