package com.studioapp.mobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class Parola_noua extends AppCompatActivity{
    private Button Trimite;
    private ImageButton Back;
    private EditText emailReset;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parola_noua);

//        mAuth = FirebaseAuth.getInstance();

        Trimite = (Button) findViewById(R.id.buttonTrimite);

        Back = findViewById(R.id.btn_back);

        emailReset = (EditText) findViewById(R.id.email_reset);

//        Trimite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String resetEmail = emailReset.getText().toString();
//                mAuth.sendPasswordResetEmail(resetEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(getApplicationContext(), "Link-ul a fost trimis!", Toast.LENGTH_SHORT).show();
//                        kill_app();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), "Error! Link-ul nu a putut fi trimis! : " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
//        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kill_app();
            }
        });
    }

    public void kill_app()
    {
        finish();
    }
}
