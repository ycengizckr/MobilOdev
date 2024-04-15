package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchPerson extends AppCompatActivity {

    EditText searchName;
    Button searchButton;
    DatabaseReference mRef;
    RecyclerView recyclerView;
    private List<HashMap<String, String>> userList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchperson);

        searchName = (EditText) findViewById(R.id.searchName);
        searchButton = (Button) findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.recyclerView);

        mRef = FirebaseDatabase.getInstance("https://androidproje-36714-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        userList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(null);
                String name = searchName.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(SearchPerson.this, "Lütfen isim girin", Toast.LENGTH_SHORT).show();
                    return;
                }

                Search(name);
            }
        });
    }

    private void Search(String name) {
        mRef.child("Kullanicilar").orderByChild("Adi").equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            HashMap<String, String> userData = (HashMap<String, String>) dataSnapshot.getValue();

                            if (userData != null) {
                                String adi = userData.get("Adi");
                                String profilResimUrl = userData.get("profilResimUrl");
                                String AccountType = userData.get("AccountType");
                                String SoyAdi = userData.get("SoyAdi");
                                String Telefon = userData.get("Telefon");
                                String Maili = userData.get("Maili");
                                String SecondMail = userData.get("SecondMail");
                                String EducationType = userData.get("EducationType");
                                String EducationSemester = userData.get("EducationSemester");


                                // Sadece adı ve profil resmini kullanıcı listesine ekle
                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("Adi", adi);
                                userMap.put("profilResimUrl", profilResimUrl);
                                userMap.put("AccountType", AccountType);
                                userMap.put("SoyAdi", SoyAdi);
                                userMap.put("Telefon", Telefon);
                                userMap.put("Maili", Maili);
                                userMap.put("SecondMail", SecondMail);
                                userMap.put("EducationType", EducationType);
                                userMap.put("EducationSemester", EducationSemester);
                                userList.add(userMap);
                            }
                        }
                        if (userList.isEmpty()) {
                            Toast.makeText(SearchPerson.this, "Sonuç bulunamadı", Toast.LENGTH_SHORT).show();
                        } else {
                            // RecyclerView'i güncelle
                            SearchAdapter searchAdapter = new SearchAdapter(userList);
                            recyclerView.setAdapter(searchAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchPerson.this, "Veritabanı erişim hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name, txt_sname, txt_AccType, txt_Phone, txt_mail, txt_smail, txt_eduType, txt_eduSemester;
        ImageView image;

        public SearchViewHolder(View itemView) {
            super(itemView);

            this.txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            this.image = (ImageView) itemView.findViewById(R.id.image);
            this.txt_sname= (TextView) itemView.findViewById(R.id.txt_surname);
            this.txt_AccType = (TextView) itemView.findViewById(R.id.txt_account_level);
            this.txt_Phone = (TextView) itemView.findViewById(R.id.txt_phone_number);
            this.txt_mail = (TextView) itemView.findViewById(R.id.txt_email);
            this.txt_smail = (TextView) itemView.findViewById(R.id.txt_second_email);
            this.txt_eduType = (TextView) itemView.findViewById(R.id.txt_education_level);
            this.txt_eduSemester = (TextView) itemView.findViewById(R.id.txt_education_semester);
        }

    }

    class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private List<HashMap<String, String>> userList;

        public SearchAdapter(List<HashMap<String, String>> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
            return new SearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            HashMap<String, String> currentItem = userList.get(position);

            String adi = currentItem.get("Adi");
            String sname = currentItem.get("SoyAdi");
            String profilResimUrl = currentItem.get("profilResimUrl");
            String AccountType = currentItem.get("AccountType");
            String mail = currentItem.get("Maili");
            String smail = currentItem.get("SecondMail");
            String eduType = currentItem.get("EducationType");
            String eduSemester = currentItem.get("EducationSemester");
            String phone = currentItem.get("Telefon");


            holder.txt_name.setText(adi);
            holder.txt_AccType.setText(AccountType);
            holder.txt_sname.setText(sname);
            holder.txt_mail.setText(mail);
            holder.txt_smail.setText(smail);
            holder.txt_eduSemester.setText(eduSemester);
            holder.txt_eduType.setText(eduType);
            holder.txt_Phone.setText(phone);

            Picasso.get().load(profilResimUrl).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }
    }
}

