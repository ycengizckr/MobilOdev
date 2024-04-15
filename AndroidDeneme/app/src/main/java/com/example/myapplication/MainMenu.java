package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;

public class MainMenu extends AppCompatActivity {

    Button login, register;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginorregister);
        login = (Button) findViewById(R.id.girisYap_button);
        register = (Button) findViewById(R.id.kayitOl_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(true);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(false);
            }
        });
    }


    public void openActivity(boolean secim) {
        if (secim) {
            Intent intent = new Intent(MainMenu.this, Login.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainMenu.this, Register.class);
            startActivity(intent);
        }
    }
}
