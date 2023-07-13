package com.example.sstrial;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ToolsFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String Uid = auth.getUid();
        LinearLayout linearLayout = view.findViewById(R.id.recordBox);


        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("user").child(Uid).child("storage");
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String dateTime = dataSnapshot.child("dateTime").getValue(String.class);
                    String[] array = dateTime.split(" ");
                    String date = array[0];
                    String time = array[1];
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String result = dataSnapshot.child("result").getValue(String.class);

                    LinearLayout linearLayoutItem = createLinearLayout();
                    linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    linearLayoutItem.setPadding(40,20,40,40);
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
                        Glide.with(requireContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_baseline_image_24) // Default image resource
                                .apply(RequestOptions.placeholderOf(R.drawable.ic_baseline_image_24)
                                        .error(R.drawable.ic_baseline_image_24)
                                        .placeholder(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.yellow2))))
                                .into(uploadImage);

                    } else {
                        // Replace `YourActivity` with the name of your actual Activity class
                        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_image_24 );
                        if (drawable != null) {
                            drawable = DrawableCompat.wrap(drawable);
                            DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), R.color.yellow2));
                            uploadImage.setImageDrawable(drawable);
                        }

                    }

                    TextView dateTextView = createTextView(date, 22);
                    TextView timeTextView = createTextView(time, 22);
                    TextView resultTextView = createTextView(result, 22);

                    linearLayoutItem.addView(dateTextView);
                    linearLayoutItem.addView(timeTextView);
                    linearLayoutItem.addView(resultTextView);
                    linearLayoutItem.addView(uploadImage);

                    // Add LinearLayoutItem to the parent LinearLayout
                    linearLayout.addView(linearLayoutItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
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
