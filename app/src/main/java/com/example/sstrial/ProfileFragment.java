package com.example.sstrial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    Button logout;
    TextView emailDetail, nameDetail, phoneDetail, ageDetail;
    FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        logout = view.findViewById(R.id.logout);
        emailDetail = view.findViewById(R.id.emailDetail);
        nameDetail = view.findViewById(R.id.nameDetail);
        phoneDetail = view.findViewById(R.id.phoneDetail);
        ageDetail = view.findViewById(R.id.ageDetail);
        user = auth.getCurrentUser();
        String Uid = auth.getUid();

        if (user == null) {
            Intent intent = new Intent(requireContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        } else {
            emailDetail.setText(user.getEmail());
            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("user").child(Uid);
            dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nameDetail.setText(snapshot.child("name").getValue(String.class));
                    phoneDetail.setText(snapshot.child("phone").getValue(String.class));
                    ageDetail.setText(snapshot.child("age").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), Login.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        return view;
    }
}
