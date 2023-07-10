package com.example.sstrial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetDetailsActivity extends AppCompatActivity {

    EditText name, age, phoneNo;
    Button enter;
    DatabaseReference rootdatabaseRef;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details);
        name = findViewById(R.id.name);
        age = findViewById((R.id.age));
        phoneNo = findViewById(R.id.phoneNo);
        enter = findViewById(R.id.enterDetail);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String Uid = auth.getUid();

        rootdatabaseRef = FirebaseDatabase.getInstance().getReference().child("user").child(Uid);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameValue = name.getText().toString().trim();
                String ageValue = age.getText().toString().trim();
                String phoneNoValue = phoneNo.getText().toString().trim();

                if (TextUtils.isEmpty(nameValue)) {
                    Toast.makeText(GetDetailsActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(ageValue)) {
                    Toast.makeText(GetDetailsActivity.this, "Enter Age", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phoneNoValue)) {
                    Toast.makeText(GetDetailsActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                rootdatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rootdatabaseRef.child("name").setValue(nameValue);
                        rootdatabaseRef.child("age").setValue(ageValue);
                        rootdatabaseRef.child("phone").setValue(phoneNoValue);
                        rootdatabaseRef.child("email").setValue(user.getEmail());
                        Toast.makeText(GetDetailsActivity.this, "Details Saved",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}
