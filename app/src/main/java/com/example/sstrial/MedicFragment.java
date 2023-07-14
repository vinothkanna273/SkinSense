package com.example.sstrial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MedicFragment extends Fragment {

    private TextView resultTxt;
    private String result;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public void setData(String data) {
        this.result = data;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medic, container, false);
        resultTxt = view.findViewById(R.id.dataResult);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String Uid = auth.getUid();
        resultTxt.setText(result);

        LinearLayout linearLayout = view.findViewById(R.id.medicBox);

        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("medicine");

        switch (result) {
            case "Acne and Rosacea":
                fetchMedicineData(dataRef.child("Acne and Rosacea"), linearLayout);
                break;
            case "Alopecia":
                fetchMedicineData(dataRef.child("Alopecia"), linearLayout);
                break;
            case "Distal Subungual":
                fetchMedicineData(dataRef.child("Distal Subungual"), linearLayout);
                break;
            case "Melanoma Skin Cancer Nevi":
                fetchMedicineData(dataRef.child("Melanoma"), linearLayout);
                break;
            case "Herpes HPV and STD":
                fetchMedicineData(dataRef.child("Herpes"), linearLayout);
                break;
        }

        return view;
    }

    private void fetchMedicineData(DatabaseReference dataRef, LinearLayout linearLayout) {
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String medName = dataSnapshot.child("name").getValue(String.class);
                    String medChem = dataSnapshot.child("chemical").getValue(String.class);
                    String medRs = dataSnapshot.child("price").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String link = dataSnapshot.child("link").getValue(String.class);

                    LinearLayout linearLayoutItem = createLinearLayout();
                    linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    linearLayoutItem.setPadding(40, 20, 40, 40);
                    layoutParams.setMargins(50, 20, 50, 20);
                    linearLayoutItem.setLayoutParams(layoutParams);
                    linearLayoutItem.setBackground(getContext().getDrawable(R.drawable.custom_edittext));

                    int desiredWidth = 540;
                    int desiredHeight = 540;
                    ImageView uploadImage = new ImageView(getContext());
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(desiredWidth, desiredHeight);
                    layoutParams1.gravity = Gravity.CENTER;
                    uploadImage.setLayoutParams(layoutParams1);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        loadImageWithGlide(uploadImage, imageUrl);
                    } else {
                        loadImageWithDrawable(uploadImage);
                    }

                    TextView medNameTextView = createTextView(medName, 18, true);
                    TextView medChemTextView = createTextView(medChem, 18);
                    TextView medRsTextView = createTextView(medRs, 22);

                    linearLayoutItem.addView(medNameTextView);
                    linearLayoutItem.addView(medChemTextView);
                    linearLayoutItem.addView(medRsTextView);
                    linearLayoutItem.addView(uploadImage);
                    linearLayoutItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = link;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                        }
                    });


                    linearLayout.addView(linearLayoutItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadImageWithGlide(ImageView imageView, String imageUrl) {
        Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_baseline_image_24)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_baseline_image_24)
                        .error(R.drawable.ic_baseline_image_24))
                .into(imageView);
    }

    private void loadImageWithDrawable(ImageView imageView) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_image_24);
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    private TextView createTextView(String text, int textSize) {
        return createTextView(text, textSize, false);
    }

    private TextView createTextView(String text, int textSize, boolean bold) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(getResources().getColor(R.color.green2));
        if (bold) {
            textView.setTypeface(textView.getTypeface(), android.graphics.Typeface.BOLD);
        }
        return textView;
    }

    private LinearLayout createLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        return linearLayout;
    }
}
