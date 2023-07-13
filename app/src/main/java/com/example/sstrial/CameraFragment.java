package com.example.sstrial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.sstrial.ml.Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CameraFragment extends Fragment {

    TextView result, confidence;
    ImageView imageView;
    int imageSize = 224;

    CardView basicMedic, nearbyDoc;

    FirebaseAuth auth;
    FirebaseUser user;

    private String shareResult;

    // Fragment replacement with data share
    private FragmentChangeListener fragmentChangeListener;
    public interface FragmentChangeListener {
        void onFragmentChange(Fragment fragment, String data);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        result = view.findViewById(R.id.result);
        //confidence = view.findViewById(R.id.confidence);
        imageView = view.findViewById(R.id.imageView);
        basicMedic = view.findViewById(R.id.basicMedic);
        nearbyDoc = view.findViewById(R.id.nearbyDoc);


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 1);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }

        nearbyDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentChangeListener != null) {
                    String dataToSend = "Nearby";
                    fragmentChangeListener.onFragmentChange(new MapsFragment(), dataToSend);
                }
            }
        });
        basicMedic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentChangeListener != null) {
                    fragmentChangeListener.onFragmentChange(new MedicFragment(), shareResult);
                }
            }
        });
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmentChangeListener = (FragmentChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentChangeListener");
        }
    }


    @SuppressLint("DefaultLocale")
    public void classifyImage(Bitmap image){
        try {

            //Model model = Model.newInstance(getApplicationContext());
            Model model = Model.newInstance(requireContext());


            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i=0; i<imageSize; i++){
                for (int j=0; j<imageSize; j++){
                    int val = intValues[pixel++]; //RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);


            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Acne and Rosacea", "Alopecia", "Distal Subungual", "Melanoma Skin Cancer Nevi", "Herpes HPV and STD"};

            result.setText(classes[maxPos]);
            shareResult = classes[maxPos];
            /* Confidence level
            String s = "";
            for(int i = 0; i < classes.length; i++){
                s += String.format("" +
                        "%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
            confidence.setText(s);*/

            // releases model resources if no longer used.
            model.close();

            // store in database
            String diseaseResult = classes[maxPos];
            String dateTime = getCurrentDateTime();
            uploadDataToFirebase(image, diseaseResult, dateTime);

        } catch (IOException e) {
            // TODO Handle the exception
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);


            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }


    private void uploadDataToFirebase(Bitmap image, String result, String dateTime) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // upload image
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/"+dateTime+".jpg");
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // image uploaded successfully
            // image to realtime database
            Log.d("Opened "," uploading code ------------------------------------");
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                // getting the image URL
                String imageUrl = uri.toString();

                // store  img URL, result, and datetime to realtime database
                auth = FirebaseAuth.getInstance();
                String Uid = auth.getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("user").child(Uid).child("storage").child(dateTime);

                String entryKey = reference.push().getKey();
                DataModel data = new DataModel(imageUrl, result, dateTime);
                reference.setValue(data)
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(e -> {
                        });
            }).addOnFailureListener(exception -> {
            });
        }).addOnFailureListener(exception -> {
        });

    }
}

