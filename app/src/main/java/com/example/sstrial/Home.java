package com.example.sstrial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.sstrial.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.ktx.Firebase;

public class Home extends AppCompatActivity implements CameraFragment.FragmentChangeListener  {

    ActivityHomeBinding binding;
    FloatingActionButton click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String uid = FirebaseAuth.getInstance().getUid();
        replaceFragment(new HomeFragment());
        click = findViewById(R.id.CameraClick);
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.tools:
                    replaceFragment(new ToolsFragment());
                    break;
                case R.id.blog:
                    replaceFragment(new MapsFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        });

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new CameraFragment());
            }
        });
    }

    // set fragment in home activity
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    // Fragment replace with data share
    @Override
    public void onFragmentChange(Fragment fragment, String data) {
        if (fragment instanceof MedicFragment) {
            ((MedicFragment) fragment).setData(data);
        }
        replaceFragment(fragment);
    }



}
