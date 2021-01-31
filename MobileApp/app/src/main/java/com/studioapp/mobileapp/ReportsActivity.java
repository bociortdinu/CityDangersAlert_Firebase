package com.studioapp.mobileapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.sql.PreparedStatement;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {


    private static TextView dateAndTime;
    private static Spinner tipSizare;
    private static TextInputEditText descriereSesizare;
    private static Button SalveazaSesizarea;
    private static ImageButton takeAPhoto;
    private static ImageButton browseAPhoto;
    private static FloatingActionButton deleteSetImage1;
    private static FloatingActionButton deleteSetImage2;


    //var
    private static final int RESULT_LOAD_IMAGE = 1000;
    private static final int REQUEST_IMAGE_CAPTURE = 2000;

    public static boolean ReportComplete = false;
    public Date currentTime = Calendar.getInstance().getTime();
    public Reports newReport;
    public Uri selectedBrowseImage = null;
    public Uri selectedTakeImage = null;

    public FirebaseFirestore db;
    private StorageReference mStorageRef;


    private String documentReferenceID ;

    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        dateAndTime = (TextView) findViewById(R.id.date_sesizare);
        dateAndTime.setText(currentTime.toString());

        tipSizare = (Spinner) findViewById(R.id.tip_sizare);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ReportsActivity.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.tip_sizare));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipSizare.setAdapter(myAdapter);
        tipSizare.setSelection(0);
        descriereSesizare = (TextInputEditText) findViewById(R.id.descriere_sesizare);
        SalveazaSesizarea = (Button) findViewById(R.id.salveazaSesizarea);

        takeAPhoto = (ImageButton) findViewById(R.id.imageButton_takeAPhoto);
        browseAPhoto = (ImageButton) findViewById(R.id.imageButton_browseAPhoto);
        takeAPhoto.setEnabled(true);
        browseAPhoto.setEnabled(true);


        deleteSetImage1 = (FloatingActionButton) findViewById(R.id.delete_SetImage1);
        deleteSetImage2 = (FloatingActionButton) findViewById(R.id.delete_SetImage2);

        deleteSetImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeAPhoto.setEnabled(true);
                browseAPhoto.setEnabled(true);

                deleteSetImage1.setVisibility(View.INVISIBLE);

                takeAPhoto.setImageURI(null);
                selectedTakeImage= null;
                File file = new File(String.valueOf(selectedTakeImage));
                file.delete();

                Log.e("ReportsActivity","deleteSetImage1 :  onClick : selectedTakeImage = " + selectedTakeImage);
            }
        });

        deleteSetImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAPhoto.setEnabled(true);
                browseAPhoto.setEnabled(true);

                deleteSetImage2.setVisibility(View.INVISIBLE);

                selectedBrowseImage = null;
                browseAPhoto.setImageURI(null);
                File file = new File(String.valueOf(selectedBrowseImage));
                file.delete();

                Log.e("ReportsActivity","deleteSetImage2 :  onClick : selectedBrowseImage = " + selectedBrowseImage);
            }
        });

        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        browseAPhoto.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        takeAPhoto.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                // create Intent to take a picture and return control to the calling application
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Create a File reference for future access
                photoFile = getPhotoFileUri("photo.jpg");

                Uri fileProvider = FileProvider.getUriForFile(ReportsActivity.this, "com.codepath.fileprovider", photoFile);


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }
            }
        });

        SalveazaSesizarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(createReport())
                {
                    try {
                        if(addReportToDB())
                        {
                            Toast.makeText(ReportsActivity.this, "Raportul a fost adaugat in baza de date", Toast.LENGTH_LONG).show();
                            ReportComplete= true;
                            kill_activity();
                        }
                        else {
                            Toast.makeText(ReportsActivity.this, "Raportul nu a fost adaugat", Toast.LENGTH_LONG).show();
                            kill_activity();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(ReportsActivity.this, "Reportul nu a putut fi creat", Toast.LENGTH_LONG).show();
                    Log.e("createReport","Reportul nu a putut fi creat");
//                    kill_activity();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("ReportsActivity", "onActivityResult() : OnStart");

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!=null)
        {
            Uri selectedImage = data.getData();
            Log.e("ReportsActivity", "browseAPhoto : onActivityResult() : browseAPhotoURI = " + selectedImage);
            browseAPhoto.setImageURI(selectedImage);
            selectedBrowseImage = selectedImage;
            takeAPhoto.setEnabled(false);

            deleteSetImage2.setVisibility(View.VISIBLE);

        }
        else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Log.e("O intrat pe if","O intrat pe ifu de facut poza!!!!!");

            deleteSetImage1.setVisibility(View.VISIBLE);

            // by this point we have the camera photo on disk
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            takeAPhoto.setImageBitmap(takenImage);


//            selectedTakeImage = Uri.fromFile(getPhotoFileUri("photo.jpg"));

            selectedTakeImage = getImageUri(this,takenImage);

            Log.e("ReportsActivity", "takeAPhoto : onActivityResult() : takeAPhotoURI = " + selectedTakeImage);


            browseAPhoto.setEnabled(false);
        }


        Log.e("ReportsActivity", "onActivityResult() : OnFinish");
    }

    private boolean createReport()
    {


        Log.e("ReportActivity","createReport() : OnStart");
        if(tipSizare.getSelectedItemId() > 0)
            if (descriereSesizare.getText().toString().length() > 15){

                if(selectedBrowseImage!= null)
                {

                    Log.e("ReportActivity","createReport() : selectedBrowseImage = "+ selectedBrowseImage );

                    Log.e("ReportActivity","createReport() : tipSizare.getSelectedItemId() = "+ tipSizare.getSelectedItemId() );

                    newReport = new Reports(GoogleMapsFragment.getLatLngFromMarker(),
                            currentTime,
                            descriereSesizare.getText().toString(),
                            Profil.getInstance().getID(),
                            Reports.ReportState.Netrimise,
                            ((int)tipSizare.getSelectedItemId()),
                            selectedBrowseImage);
                }
                else if(selectedTakeImage!= null)
                {
                    Log.e("ReportActivity","createReport() : selectedTakeImage = "+ selectedTakeImage );

                    Log.e("ReportActivity","createReport() : tipSizare.getSelectedItemId() = "+ tipSizare.getSelectedItemId() );

                    newReport = new Reports(GoogleMapsFragment.getLatLngFromMarker(),
                            currentTime,
                            descriereSesizare.getText().toString(),
                            Profil.getInstance().getID(),
                            Reports.ReportState.Trimise,
                            ((int)tipSizare.getSelectedItemId()-1),   // -1 pentru ca in arrayul Type din clasa Report
                            selectedTakeImage);
                }
                else{
                    Log.e("ReportActivity","createReport() :  selectedImage is null" );

                    Toast.makeText(ReportsActivity.this, "Faceti/Selectati o poza", Toast.LENGTH_LONG).show();
                    return false;
                }


                Toast.makeText(ReportsActivity.this, "Raport creat cu succes", Toast.LENGTH_LONG).show();

                Log.e("ReportActivity","createReport() : OnFinish   return true");
                return true;
            }
            else{

                Log.e("ReportActivity","createReport() : descriereSesizare < 15" );
                Toast.makeText(ReportsActivity.this, "Descrierea nu are destule caractere ("+descriereSesizare.getText().toString().length()+")", Toast.LENGTH_LONG).show();
                return false;
            }
        else{

            Log.e("ReportActivity","createReport() : tipSizare.getSelectedItemId() == 0" );

            Toast.makeText(ReportsActivity.this, "Alegeti un tip de sesizare", Toast.LENGTH_LONG).show();
            return false;
        }


    }

    private boolean addReportToDB() throws SQLException {
        Log.e("ReportsActivity", "addReportToDB() : OnStart");

        Map<String, Object> report = new HashMap<>();
        report.put("userId", Profil.getInstance().getID());
        report.put("date", newReport.getDate());
        report.put("geoLocation", newReport.getGeoLocation());
        report.put("reportState", newReport.getState());
        report.put("reportType", newReport.getType());
        report.put("descriptionReport", newReport.getDescription());
        report.put("credibility",0);



        db.collection("QReports")
                .add(report)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e("ReportsActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
                        documentReferenceID = documentReference.getId();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReportsActivity", "Error adding document", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.e("ReportsActivity", "OnCompleteListener()");

                        if(task.isSuccessful()) {

                            Log.e("ReportsActivity", "OnCompleteListener() : task isSuccessful");

                            StorageReference riversRef = mStorageRef.child("reports/"+ documentReferenceID +".jpg");

                            if(newReport.getImage()!=null)
                            {
                                Log.e("ReportsActivity", " newReport.getImage()!=null");

                                riversRef.putFile(newReport.getImage())
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // Get a URL to the uploaded content
                                                Log.e("ReportsActivity", " StorageReference : putFile  onSuccess");

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle unsuccessful uploads
                                                Log.e("ReportsActivity", " StorageReference : putFile  onFailures");
                                            }
                                        });
                            }
                            else{
                                Log.e("ReportsActivity", " newReport.getImage() == null");
                            }


                        }

                    }
                });





        Log.e("ReportsActivity", "addReportToDB() : OnFinish");

        return true;
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ReportsActivity");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("ReportsActivity", " getPhotoFileUri() : failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void kill_activity(){
        finish();
    }

}