package com.studioapp.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Inregistrare extends AppCompatActivity {

    //widgets
    private static Button Next;
    private static ImageButton Back;

    //vars
    private static EditText email;
    private static EditText parola;
    private static EditText confirmaParola;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inregistrare);
        Toast.makeText(getApplicationContext(), "Inregistrare",Toast.LENGTH_SHORT).show();

        Next=(Button)findViewById(R.id.buttonNext);
        Back =(ImageButton)findViewById(R.id.btn_back);

        email = (EditText) findViewById(R.id.emailInregistrare);
        parola = (EditText) findViewById(R.id.parolaInregistrare);
        confirmaParola = (EditText) findViewById(R.id.confirmaParolaInregistrare);

        mAuth = FirebaseAuth.getInstance();

//        if(mAuth.getCurrentUser() != null){ // daca userul este deja logat(nu e cazul momentan)
//
//        }

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseRegister1();
                kill_app();

            }
        });
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

    public void startInregistrare2Activity()
    {
        Intent intent = new Intent(this,Inregistrare2.class);
        startActivity(intent);
    }


    private void FirebaseRegister1(){
        String newEmail = email.getText().toString();
        String newPassword = parola.getText().toString();
        String newPasswordConfirmation = confirmaParola.getText().toString();

        if(TextUtils.isEmpty(newEmail)){
            email.setError("Introduceti emailul");
            return;
        }
        if(TextUtils.isEmpty(newPassword)){
            parola.setError("Introduceti o parola");
            return;
        }
        if(newPassword.length() < 6){
            parola.setError("Introduceti o parola mai lunga (minim 6 caractere)");
            return;
        }
        if(!newPassword.equals(newPasswordConfirmation)){
            confirmaParola.setError("Parola nu este identica!");
            return;
        }

        mAuth.createUserWithEmailAndPassword(newEmail,newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Profil.getInstance().setID(mAuth.getCurrentUser().getUid());
                    Profil.getInstance().setAdresaEmail(email.getText().toString()); //setam aici adresa de email in profil pentru ca nu putem la inregistrare2
                    startInregistrare2Activity();
                    Log.e("Inregistrare1","Inregistrare1 efectuata cu succes");
                }
                else{
                    Log.e("Inregistrare1","Error! : " + task.getException().getMessage());
                    Toast.makeText(getApplicationContext(), "Error! : " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
