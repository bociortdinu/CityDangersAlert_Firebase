package com.studioapp.mobileapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Inregistrare2 extends AppCompatActivity {


    private static EditText numeInregistrare;
    private static EditText prenumeInregistrare;
    private static EditText cnpInregistrare;
    private static EditText telefonInregistrare;
    private static EditText adresaInregistrare;
    private static Button buttonSalveazaInregistrare;
    private static ImageView UPLOADimagineProfil;

    private FirebaseFirestore fStore;
    private StorageReference mStorageRef;

    private static final int RESULT_LOAD_IMAGE = 1000;
    private static boolean ok=false;
    private static Uri selectedProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inregistrare2);


        numeInregistrare = (EditText) findViewById(R.id.numeInregistrare);
        prenumeInregistrare = (EditText) findViewById(R.id.prenumeInregistrare);
        cnpInregistrare = (EditText) findViewById(R.id.cnpInregistrare);
        telefonInregistrare = (EditText) findViewById(R.id.telefonInregistrare);
        adresaInregistrare = (EditText) findViewById(R.id.adresaInregistrare);
        buttonSalveazaInregistrare = (Button) findViewById(R.id.buttonSalveazaInregistrare);
        UPLOADimagineProfil = (ImageView) findViewById(R.id.imagineProfilUPDATE);

        fStore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        UPLOADimagineProfil.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });



        buttonSalveazaInregistrare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createNewUserProfile();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!=null)
        {
            Uri selectedImage = data.getData();
            UPLOADimagineProfil.setImageURI(selectedImage);
            selectedProfileImage = selectedImage;
            ok=true;
        }
    }


    public void kill_app()
    {
        finish();
    }

    public void createNewUserProfile()
    {
        if(TextUtils.isEmpty(numeInregistrare.getText().toString())){
            numeInregistrare.setError("Introduceti numele");
            return;
        }
        if(TextUtils.isEmpty(prenumeInregistrare.getText().toString())){
            prenumeInregistrare.setError("Introduceti prenumele");
            return;
        }
        if(TextUtils.isEmpty(cnpInregistrare.getText().toString()) && cnpInregistrare.getText().toString().length()!=13){
            cnpInregistrare.setError("Introduceti un CNP valid");
            return;
        }
        if(TextUtils.isEmpty(adresaInregistrare.getText().toString())){
            adresaInregistrare.setError("Introduceti adresa");
            return;
        }
        if(TextUtils.isEmpty(telefonInregistrare.getText().toString()) && (telefonInregistrare.getText().toString().length()<10 || telefonInregistrare.getText().toString().length()>13 )  ){
            telefonInregistrare.setError("Introduceti un nr. detelefon valid");
            return;
        }

        DocumentReference documentReference = fStore.collection("UserProfile").document(Profil.getInstance().getID());
        Map<String, Object> user = new HashMap<>();
//        Profil.getInstance().getUser();
        user.put("numeUtilizator",numeInregistrare.getText().toString());

        user.put("prenumeUtilizator",prenumeInregistrare.getText().toString());

        user.put("CNP",cnpInregistrare.getText().toString());

        user.put("adresa",adresaInregistrare.getText().toString());

        user.put("adresaEmail",Profil.getInstance().getAdresaEmail());

        user.put("nrTelefon",telefonInregistrare.getText().toString());

        user.put("personalScore",0);

        user.put("interactedReports",Profil.getInstance().interactedReports);

        Profil.getInstance().setUser(user);

        if(selectedProfileImage!=null)
        {
            StorageReference fileRef = mStorageRef.child("images/"+Profil.getInstance().getID()+".jpg");
            fileRef.putFile(selectedProfileImage).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Log.e("Inregistrare2","uploadImageToFirebase : onSuccess");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Inregistrare2","uploadImageToFirebase : onFailure");
                }
            });

        }

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Inregistrare2", "createNewUserProfile : onSuccess" );
                kill_app();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Inregistrare2", "createNewUserProfile : onFailure :" + e.getMessage() );
                kill_app();
            }
        });


    }


}