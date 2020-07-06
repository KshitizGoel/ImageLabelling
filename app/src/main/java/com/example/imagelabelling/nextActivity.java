package com.example.imagelabelling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class nextActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 123;
    ImageView imageView ;
    TextView Details ;
    Button UploadingTheImage , GettingTheDetails ;
    Bitmap imageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        imageView = findViewById(R.id.imageToBeCollected);
        Details = findViewById(R.id.detailsFromTheModel);
        UploadingTheImage = findViewById(R.id.upload);
        GettingTheDetails = findViewById(R.id.detaislFromTheImage);


        UploadingTheImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });


        GettingTheDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Details.setText("");
                detectingProtectedOrExposed();
//                AutoMLImageLabelerLocalModel localModel =
//                        new AutoMLImageLabelerLocalModel.Builder()
//                                .setAssetFilePath("model/manifest.json")
//                                // or .setAbsoluteFilePath(absolute file path to manifest file)
//                                .build();
//
//                AutoMLImageLabelerOptions autoMLImageLabelerOptions =
//                        new AutoMLImageLabelerOptions.Builder(localModel)
//                                .setConfidenceThreshold(0.7f)  // Evaluate your model in the Firebase console
//                                // to determine an appropriate value.
//                                .build();
//                ImageLabeler labeler = ImageLabeling.getClient(autoMLImageLabelerOptions);
            }
        });
    }

    //==============================================================Taking picture from the camera===================================================================
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    //==============================================================Picture taken from the camera is now being sent to ML model to distinguish between Exposed or Masked. ===================================================================


    private void detectingProtectedOrExposed() {

        InputImage image = InputImage.fromBitmap(imageBitmap , 0);
        AutoMLImageLabelerLocalModel localModel =
                        new AutoMLImageLabelerLocalModel.Builder()
                                .setAssetFilePath("model/manifest.json")
                                // or .setAbsoluteFilePath(absolute file path to manifest file)
                                .build();

                AutoMLImageLabelerOptions autoMLImageLabelerOptions =
                        new AutoMLImageLabelerOptions.Builder(localModel)
                                .setConfidenceThreshold(0.7f)  // Evaluate your model in the Firebase console
                                // to determine an appropriate value.
                                .build();
                ImageLabeler labeler = ImageLabeling.getClient(autoMLImageLabelerOptions);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            float confidence = label.getConfidence();
                            Details.append(text + " " + "\n\n" + confidence*100 + "%" );

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(nextActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


    //==============================================================Picture taken from the camera is now being sent to ===================================================================


}