package com.example.sstrial;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    Button logout;
    TextView textUser;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        logout = view.findViewById(R.id.logout);
        textUser = view.findViewById(R.id.user_details);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(requireContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        } else {
            textUser.setText(user.getEmail());
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
