package com.example.sstrial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.ktx.Firebase;

public class DemoActivity extends AppCompatActivity {

    private EditText input;
    private Button btn, get;
    private DatabaseReference rootdatabaseRef;
    private TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        input = findViewById(R.id.input);
        btn = findViewById(R.id.btn);
        get = findViewById(R.id.get);
        show = findViewById(R.id.show);

        rootdatabaseRef = FirebaseDatabase.getInstance().getReference().child("user");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = input.getText().toString();

                rootdatabaseRef.setValue(data);
            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootdatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        if (datasnapshot.exists()){
                            String data = datasnapshot.getValue().toString();
                            show.setText(data);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}