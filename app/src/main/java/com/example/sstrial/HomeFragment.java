package com.example.sstrial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    TextView homeName;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String Uid = auth.getUid();
        homeName =view.findViewById(R.id.homeName);
        if (user == null) {
            Intent intent = new Intent(requireContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        } else {
            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("user").child(Uid);
            dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    homeName.setText(snapshot.child("name").getValue(String.class));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return view;
    }
}