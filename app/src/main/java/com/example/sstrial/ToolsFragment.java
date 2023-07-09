package com.example.sstrial;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ToolsFragment extends Fragment {

   /* private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String Uid;

    public ToolsFragment newInstance(String param1, String param2, String Uid) {
        ToolsFragment fragment = new ToolsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.Uid=this.Uid;
        fragment.setArguments(args);
        return fragment;
    }*/

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String Uid = auth.getUid();


        return view;
    }
}