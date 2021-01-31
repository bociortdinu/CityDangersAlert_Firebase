package com.studioapp.mobileapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Profil{

    private String ID;
    private String numeUtilizator;
    private String prenumeUtilizator;
    private String CNP;
    private String adresa;
    private String adresaEmail;
    private String nrTelefon;
    private Uri imagineProfil = null;
    private int personalScore;
    private Map<String, Object> user = new HashMap<>();
    public ArrayList<String> interactedReports = new ArrayList<>();

    private static Profil profil;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public Profil()
    {
        numeUtilizator= "Nume";
        prenumeUtilizator = "Prenume";
        CNP = "0000000000000";
        adresa = "Jud, Oras, Strada, Numar";
        adresaEmail = "exemplu@exemplu.com";
        nrTelefon = "0700000000";
        personalScore = 0;
    }

    public static synchronized Profil getInstance()
    {
        if(profil==null)
        {
            profil = new Profil();
        }
        return profil;
    }

    public void updateProfil()
    {
        Log.e("Profil" ,"updateProfil() : OnStart");

        TextView text;
        text = ProfilFragment.getRootView().findViewById(R.id.numeUtilizator);
        text.setText(numeUtilizator + " " + prenumeUtilizator);

        text = ProfilFragment.getRootView().findViewById(R.id.cnpUtilizator);
        text.setText(CNP);

        text = ProfilFragment.getRootView().findViewById(R.id.adresaUtilizator);
        text.setText(adresa);

        text = ProfilFragment.getRootView().findViewById(R.id.adresaEmailUtilizator);
        text.setText(adresaEmail);

        text = ProfilFragment.getRootView().findViewById(R.id.telefonUtilizator);
        text.setText(nrTelefon);

        text = ProfilFragment.getRootView().findViewById(R.id.personal_score);
        text.setText(String.valueOf(personalScore));

        ImageView imageView;
        imageView = ProfilFragment.getRootView().findViewById(R.id.imagineProfil);
//        Picasso.get().load(imagineProfil).into(imageView);
        Glide.with(imageView.getContext()).load(imagineProfil).into(imageView);

        Log.e("Profil" ,"updateProfil() : OnFinish");

    }

    public void updateProfilDataBase() throws SQLException {  // asta se executa la Editare Profil

        Log.e("Profil" ,"updateProfilDataBase() : OnStart");

        DocumentReference documentReference = db.collection("UserProfile").document(Profil.getInstance().getID());
        Map<String, Object> user = new HashMap<>();
        user.put("numeUtilizator",numeUtilizator);

        user.put("prenumeUtilizator",prenumeUtilizator);

        user.put("CNP",CNP);

        user.put("adresa",adresa);

        user.put("adresaEmail",adresaEmail);

        user.put("nrTelefon",nrTelefon);


        Log.e("Profil" ,"updateProfilDataBase() : uploadProfileImageToFirebase()");

        StorageReference fileRef = mStorageRef.child("images/"+ID+".jpg");

        if(imagineProfil!=null)
        {
            Log.e("Profil" ,"updateProfilDataBase() : StorageReference - imagingeProfil != null");

            fileRef.delete().addOnSuccessListener(new OnSuccessListener() {   // sterg poza din baza de date
                @Override
                public void onSuccess(Object o) {
                    Log.e("Profil","updateProfilDataBase() : deleteImageFromFirebase : onSuccess");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Profil","updateProfilDataBase() : deleteImageFromFirebase : onFailure");
                }
            });



            fileRef.putFile(imagineProfil).addOnSuccessListener(new OnSuccessListener() {  // adaug poza in baza de date
                @Override
                public void onSuccess(Object o) {
                    Log.e("Profil","updateProfilDataBase() : uploadImageToFirebase : onSuccess");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Profil","updateProfilDataBase() : uploadImageToFirebase : onFailure");
                }
            });
        }
        else {
            Log.e("Profil" ,"updateProfilDataBase() : StorageReference - imagingeProfil == null");
        }


        user.put("personalScore",personalScore);

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Profil", "updateProfilDataBase() : updateProfilDataBase : onSuccess" );

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Profil", "updateProfilDataBase() : updateProfilDataBase : onFailure :" + e.getMessage() );
            }
        });


        Log.e("Profil" ,"updateProfilDataBase() : OnFinish");

    }

    public void extractProfilDataFromFirebase() throws SQLException {  // pune din baza de date in atributele clasei


        Log.e("Profil" ,"extractProfilDataFromFirebase() : OnStart");

        db.collection("UserProfile").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("Profil", "extractProfilDataFromFirebase:");

                                Log.e("Profil", document.getId() + " => " + document.getData());

                                if(getID().equals(document.getId()))                            // daca ID-ul existent deja in clasa ( atribuit la "Login" )
                                {
                                    Log.e("A intrat in IF","A intrat in IF");

                                    setNumeUtilizator(document.getData().get("numeUtilizator").toString());
                                    setPrenumeUtilizator(document.getData().get("prenumeUtilizator").toString());
                                    setCNP(document.getData().get("CNP").toString());
                                    setAdresa(document.getData().get("adresa").toString());
                                    setAdresaEmail(document.getData().get("adresaEmail").toString());
                                    setNrTelefon(document.getData().get("nrTelefon").toString());
                                    setPersonalScore(Integer.parseInt(document.getData().get("personalScore").toString()));


                                    Profil.getInstance().interactedReports = (ArrayList<String>) document.get("interactedReports");

                                    Log.e("CITESC DIN BAZA DE DATE" ,"interactedReports = " + Profil.getInstance().interactedReports);


                                    StorageReference fileRef = mStorageRef.child("images/"+ID+".jpg");

                                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.e("Profil" ,"extractProfilDataFromFirebase() : getDownloadUrl() : onSuccess ");
                                            imagineProfil=uri;
//                                            updateProfil();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Profil" ,"extractProfilDataFromFirebase() : getDownloadUrl() : onFailure ");
                                            imagineProfil=null;
                                        }
                                    });

                                }

                            }
                        } else {
                            Log.e("Profil", "Error getting documents.", task.getException());
                        }

//                        updateProfil();
                    }
                });

        Log.e("Profil" ,"extractProfilDataFromFirebase() : OnFinish");

    }


    public Connection conectionclass()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;

        String USERNAME = "dinu@mobileapp-testserver";
        String PASSWORD = "Descarcare1";
        String URL = "jdbc:jtds:sqlserver://datc2020.database.windows.net:1433;DatabaseName=DATC;user=admindatc@datc2020;password=Proiect2020;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
       // String URL = "jdbc:jtds:sqlserver://lilycord.database.windows.net:1433;DatabaseName=lilycord-db;user=lilycord@lilycord;password=London10;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            Log.e("CONNECT","PASS 1");

            ConnectionURL=URL;
            connection = DriverManager.getConnection(ConnectionURL);
            Log.e("CONNECT","PASS 2");

        }
        catch (SQLException e)
        {
            Log.e("error here 1 : ", e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2: ", e.getLocalizedMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3: ", e.getLocalizedMessage());
        }

        return connection;
    }



    public String getID()
    {
        return ID;
    }
    public void setID(String id)
    {
        ID = id;
    }

    public String getNumeUtilizator()
    {
        return numeUtilizator;
    }
    public void setNumeUtilizator(String nume)
    {
        numeUtilizator=nume;
    }

    public String getPrenumeUtilizator()
    {
        return prenumeUtilizator;
    }
    public void setPrenumeUtilizator(String prenume)
    {
        prenumeUtilizator=prenume;
    }

    public String getCNP()
    {
        return CNP;
    }
    public void setCNP(String cnp)
    {
        CNP=cnp;
    }

    public String getAdresa()
    {
        return adresa;
    }
    public void setAdresa(String adr)
    {
        adresa=adr;
    }

    public String getAdresaEmail()
    {
        return adresaEmail;
    }
    public void setAdresaEmail(String email)
    {
        adresaEmail=email;
    }

    public String getNrTelefon()
    {
        return nrTelefon;
    }
    public void setNrTelefon(String telefon)
    {
        nrTelefon=telefon;
    }

    public int getPersonalScore(){
        return personalScore;
    }

    public void setPersonalScore(int Score) {
        personalScore = Score;
    }

    public Uri getImagineProfil()
    {
        return imagineProfil;
    }
    public void setImagineProfil(Uri uri)
    {
        imagineProfil = uri;
    }

    public Map<String, Object> getUser()
    {
        return user;
    }
    public void setUser(Map<String, Object> User)
    {
        user = User;
    }

}
