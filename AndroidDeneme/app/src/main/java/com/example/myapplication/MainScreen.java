package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainScreen extends AppCompatActivity {

    String[] kategoriler = {"Profil arama", "Bilgilerim", "Şifre Değiştir"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen);

        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainScreen.this, android.R.layout.simple_list_item_1, kategoriler);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = kategoriler[position];
                switch (selected){
                    case "Profil arama":
                        Intent searchIntent = new Intent(MainScreen.this, SearchPerson.class);
                        startActivity(searchIntent);
                        break;

                    case "Bilgilerim":
                        Intent profileIntent = new Intent(MainScreen.this, Profile.class);
                        startActivity(profileIntent);
                        break;

                    case "Şifre Değiştir":
                        Intent chancePassword = new Intent(MainScreen.this, ChangePassword.class);
                        startActivity(chancePassword);
                        break;
                }
            }
        });

    }
}
