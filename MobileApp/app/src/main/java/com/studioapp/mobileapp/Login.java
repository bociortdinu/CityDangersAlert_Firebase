package com.studioapp.mobileapp;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login extends AppCompatActivity {

    private Button btnLogin;
    private EditText Email;
    private EditText Parola;
    private TextView Inregistrare;
    private TextView Uita_parola;
    private FirebaseAuth mAuth;
    private Button btnStartWithoutLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email=(EditText)findViewById(R.id.emailLogin);
        Parola=(EditText)findViewById(R.id.parolaLogin);
        btnLogin = (Button)findViewById(R.id.buttonSalveaza);
        Inregistrare=(TextView)findViewById(R.id.id_inreg);
        Uita_parola=(TextView)findViewById(R.id.id_uitat);
        btnStartWithoutLogin=(Button)findViewById(R.id.buttonStartWithoutLogin);

        mAuth = FirebaseAuth.getInstance();    // FIREBASE !!!



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuthentificationLogin();   // FIREBASE AUTH
            }
        });
        Inregistrare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Inregistrare.class);
                startActivity(intent);
            }
        });
        Uita_parola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Parola_noua.class);
                startActivity(intent);
            }
        });

        btnStartWithoutLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Profil.getInstance().setID(null);
                startGoogleMapsActivity();

            }
        });

    }


    private void startGoogleMapsActivity()
    {
        Intent intent = new Intent(this,Meniu.class);
        startActivity(intent);
    }


    private void startMeniuActivity()
    {
        try {
            Profil.getInstance().extractProfilDataFromFirebase();    // daca emailul cu care te-ai logat se potriveste cu cel din baza de date atunci extrag datele
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this,Meniu.class);
        startActivity(intent);
    }

    private void FirebaseAuthentificationLogin()
    {
        String email = Email.getText().toString();
        String password = Parola.getText().toString();

        if(TextUtils.isEmpty(email)){
            Email.setError("Introduceti emailul");
            return;
        }
        if(TextUtils.isEmpty(password)){
            Parola.setError("Introduceti o parola");
            return;
        }
        if(password.length() < 6){
            Parola.setError("Introduceti o parola mai lunga (minim 6 caractere)");
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Profil.getInstance().setID(mAuth.getUid());                                            // Aici se introduce ID-ul prima data in clasa!!!
                    Toast.makeText(getApplicationContext(), "Autentificare cu succes!",Toast.LENGTH_SHORT).show();
                    Log.e("Login","Autentificare cu succes!");
                    startMeniuActivity();
                }
                else{
                    Log.e("Login","Error! : "+ task.getException().getMessage());
                    Toast.makeText(getApplicationContext(), "Error! : "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
