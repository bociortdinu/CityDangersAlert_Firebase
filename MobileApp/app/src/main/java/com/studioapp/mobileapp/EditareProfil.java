package com.studioapp.mobileapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.SQLException;


public class EditareProfil extends AppCompatActivity {

    private FloatingActionButton btnSalvare;
    private ImageView UPLOADimagineProfil = null;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static boolean ok=false;
    private Uri selectedProfileImage=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editare_profil);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        btnSalvare = findViewById(R.id.butonSalvareProfil);
        UPLOADimagineProfil = findViewById(R.id.imagineProfil);
        ok = false;

        UPLOADimagineProfil.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        btnSalvare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    salveazaProfil();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar1_menu2,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.settings_back:
                kill_app();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void kill_app()
    {
        finish();
    }

    private void salveazaProfil() throws SQLException {

        Log.e("EditareProfil","salveazaProfil() : OnStart");

        EditText editText;
        boolean OK = false;

        editText = findViewById(R.id.editNume);
        if (!editText.getText().toString().equals("")) {
            Profil.getInstance().setNumeUtilizator(editText.getText().toString());
            OK=true;
        }

        editText = findViewById(R.id.editPrenume);
        if (!editText.getText().toString().equals("")) {
            Profil.getInstance().setPrenumeUtilizator(editText.getText().toString());
            OK=true;
        }

        editText = findViewById(R.id.editAdresa);
        if (!editText.getText().toString().equals("")) {
            Profil.getInstance().setAdresa(editText.getText().toString());
            OK=true;
        }

        editText = findViewById(R.id.editEmail);
        if (!editText.getText().toString().equals("")) {
            Profil.getInstance().setAdresaEmail(editText.getText().toString());
            OK=true;
        }

        editText = findViewById(R.id.editTelefon);
        if (!editText.getText().toString().equals("")) {
            Profil.getInstance().setNrTelefon(editText.getText().toString());
            OK=true;
        }

        if(ok)
            Profil.getInstance().setImagineProfil(selectedProfileImage);

        Profil.getInstance().updateProfil();

        if(OK || ok)

            Profil.getInstance().updateProfilDataBase();


        Log.e("EditareProfil","salveazaProfil() : OnFinish");
    }
}
