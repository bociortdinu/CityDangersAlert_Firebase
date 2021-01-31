package com.studioapp.mobileapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback{


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Toast.makeText(getContext(), "Map is Ready", Toast.LENGTH_LONG).show();
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                markerInfoLayout.setVisibility(View.INVISIBLE);
                approveReportFloatingActionButton.setVisibility(View.INVISIBLE);
                denyReportFloatingActionButton.setVisibility(View.INVISIBLE);

                if(Profil.getInstance().getID() != null)
                {
                    latLngMarker = latLng;
                    //When clicked on map
                    // Initialize marker options
                    MarkerOptions markerOptions = new MarkerOptions();
                    //Set Position of marker
                    markerOptions.position(latLng);
                    //Set title of marker
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                    //Remove all marker
//                mMap.clear();
                    //Animating to zoom the marker
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    //Add marker on map
                    mMap.addMarker(markerOptions);


                    if(!mStorageAccessPermision){
                        getStorageAccessPermisios();
                    }
                    else{
                        startReportsActivity();
                    }
                    mMap.clear();

                }

            }
        });



//        if(mClusterManager!=null)
//        {
//            mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterMarker>() {
//                @Override
//                public boolean onClusterItemClick(ClusterMarker item) {
//                    Log.e("cluster item","clicked 1 :" + item.getPosition());
//
//
//
//                    double dpPerdegree = 256.0*Math.pow(2, DEFAULT_ZOOM)/170.0;
//                    double screen_height = (double) 670;
//                    double screen_height_30p = 30.0*screen_height/100.0;
//                    double degree_30p = screen_height_30p/dpPerdegree;
//
//
//
//
//                    double latitude = item.getPosition().latitude;
//                    double longitude = item.getPosition().longitude;
//
//                    LatLng latlng = new LatLng(latitude + degree_30p,longitude);
//
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, GoogleMapsFragment.DEFAULT_ZOOM));
//
//                    Log.e("cluster item","clicked 2 :" + latlng);
//                    return true;
//                }
//            });
//
//        }



        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);

            init();

        }

    }

    private static View rootView;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String READ_ACCESS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_ACCESS = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int LOCATION_PERMISSION_REQUEST_CODE=1000;
    private static final int STORAGE_ACCESS_PERMISSION_REQUEST_CODE=2000;
    private static final float DEFAULT_ZOOM = 19f;

//    Thread t1;

    private static LatLng latLngMarker;


    private static AppCompatImageView ic_search;
    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGPS;

    //vars

//    public static ArrayList<Reports> ReportsArray = new ArrayList<Reports>();

    public static Vector<Reports> ReportsArray = new Vector<Reports>();

    private Boolean mLocationPermissionGranted = false;
    private Boolean mStorageAccessPermision = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ArrayList<MarkerOptions> markersArray = new ArrayList<>();



    private FirebaseFirestore db;
    private StorageReference mStorageRef;


    public String[] Type={
            "Asistenta sociala",
            "Depozitari deseuri",
            "Constructii/Lucrari neautorizate | oraganizare santier",
            "Iluminat public",
            "Parcari neregulamentare",
            "Salubritate",
            "Spatii verzi/Parcuri",
            "Strazi/Alei/Trotuare/Poduri",
            "Taxe si impozite",
            "Semnalizare rutiera",
            "Retele de apa/canalizare (CAS)",
            "Transport public (CTP)",
            "Altele"};



    private RelativeLayout markerInfoLayout;
    private ImageView imagineInfoLayout;
    private TextView tipInfoLayout;
    private TextView dataInfoLayout;
    private TextView desciereInfoLayout;
    private TextView nrConfirmariInfoLayout;

    private FloatingActionButton approveReportFloatingActionButton;
    private FloatingActionButton denyReportFloatingActionButton;




    private ArrayList<Reports.ReportState> state = new ArrayList<>();
    private ArrayList<Integer> type = new ArrayList<>();
    private ArrayList<LatLng> geoLocation = new ArrayList<>();
    private ArrayList<Date> date = new ArrayList<>();
    private ArrayList<String> descriptionReports = new ArrayList<>();
    private ArrayList<String> userId = new ArrayList<>();
    private ArrayList<String> documentIdFB = new ArrayList<>();
    private ArrayList<Integer> credibility = new ArrayList<>();

    private ArrayList<Uri> image = new ArrayList<>();
    private Map<String,Uri> reportImage = new HashMap<>();

    private int nrRapoarte = 0;




    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;


    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();



    private String nowSelectedReportID =null;

    private int nrCredibilityInstant=0;


    private double lastLatitude;
    private double lastLongitude;


    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_googlemaps,container,false);

        markerInfoLayout = (RelativeLayout) rootView.findViewById(R.id.marker_InfoLayout);
        markerInfoLayout.setVisibility(View.INVISIBLE);
        imagineInfoLayout = (ImageView) rootView.findViewById(R.id.imagine_InfoLayout);
        tipInfoLayout = (TextView) rootView.findViewById(R.id.tip_InfoLayout);
        dataInfoLayout = (TextView) rootView.findViewById(R.id.data_InfoLayout);
        desciereInfoLayout = (TextView) rootView.findViewById(R.id.desciere_InfoLayout);
        nrConfirmariInfoLayout = (TextView) rootView.findViewById(R.id.nrConfirmari_InfoLayout);
        nrConfirmariInfoLayout.setText("0");
        approveReportFloatingActionButton =(FloatingActionButton) rootView.findViewById(R.id.approveReport_floatingActionButton);
        denyReportFloatingActionButton =(FloatingActionButton) rootView.findViewById(R.id.denyReport_floatingActionButton);
        approveReportFloatingActionButton.setVisibility(View.INVISIBLE);
        denyReportFloatingActionButton.setVisibility(View.INVISIBLE);


        ic_search = (AppCompatImageView) rootView.findViewById(R.id.ic_search);
        mSearchText = (AutoCompleteTextView) rootView.findViewById(R.id.input_search);
        mGPS = (ImageView) rootView.findViewById(R.id.ic_GPS);

        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        approveReportFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nrCredibilityInstant = Integer.parseInt(nrConfirmariInfoLayout.getText().toString());
                nrCredibilityInstant = nrCredibilityInstant + 1;
                nrConfirmariInfoLayout.setText(String.valueOf(nrCredibilityInstant));

                Profil.getInstance().interactedReports.add(nowSelectedReportID);

                updateReportCredibilityOnDB(nrCredibilityInstant);
                updateInteractedReportsOnDB();

                approveReportFloatingActionButton.setVisibility(View.INVISIBLE);
                denyReportFloatingActionButton.setVisibility(View.INVISIBLE);
            }
        });

        denyReportFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nrCredibilityInstant = Integer.parseInt(nrConfirmariInfoLayout.getText().toString());
                nrCredibilityInstant = nrCredibilityInstant - 1;
                nrConfirmariInfoLayout.setText(String.valueOf(nrCredibilityInstant));

                Profil.getInstance().interactedReports.add(nowSelectedReportID);

                updateReportCredibilityOnDB(nrCredibilityInstant);
                updateInteractedReportsOnDB();

                approveReportFloatingActionButton.setVisibility(View.INVISIBLE);
                denyReportFloatingActionButton.setVisibility(View.INVISIBLE);
            }
        });

        ic_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
            }
        });


        getLocationPermission();
        getStorageAccessPermisios();
        getAllReportsFromDB();

        new LongOperation().execute("");

        return rootView;
    }


    private void updateReportCredibilityOnDB(int credibility)
    {
        // Get a new write batch
        WriteBatch batch = db.batch();

        // Update the credibility
        DocumentReference documentReference = db.collection("QReports").document(nowSelectedReportID);
        batch.update(documentReference, "credibility", credibility);

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("GoogleMapsFragment","updateReportCredibilityOnDB() : onComplete");
            }
        });

    }

    private void updateInteractedReportsOnDB()
    {
        // Get a new write batch
        WriteBatch batch = db.batch();

        // Update the credibility
        DocumentReference documentReference = db.collection("UserProfile").document(Profil.getInstance().getID());
        batch.update(documentReference, "interactedReports", Profil.getInstance().interactedReports);

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("GoogleMapsFragment","updateInteractedReportsOnDB() : onComplete");
            }
        });


    }


    private void displayArrayList()
    {
        Log.e("@@@@@@@@@@@", "displayArrayList() :  OnStart ");
//        int size = ReportsArray.size();
        int i=0;
        for(Reports rap :  ReportsArray)
        {
            Log.e("displayArrayList()"," @@@                                                                           displayArrayList() : ReportsArray.get("+i+").getGeoLocation() = "+ rap.getGeoLocation());
            Log.e("displayArrayList()"," @@@                                                                           displayArrayList() : ReportsArray.get("+i+").getType() = "+rap.getType());
            i++;
        }

    }

    private void setupMarkerArray()
    {

        Log.e("\n\n\n\n\nGoogleMapsFragment", "                         setupMarkerArray() : OnStart");

        int size = ReportsArray.size();
        Log.e("GoogleMapsFragment", "setupMarkerArray() : ReportsArray.size() = " + size);
        int i;
        for (i= 0; i < size ; i++ )
        {
            MarkerOptions marker = new MarkerOptions();

            marker.position(ReportsArray.get(i).getGeoLocation());
            Log.e("GoogleMapsFragment", " setupMarkerArray() : ReportsArray : getGeoLocation("+i+") = "+ ReportsArray.get(i).getGeoLocation());


            marker.title(ReportsArray.get(i).getType());
            Log.e("GoogleMapsFragment", "setupMarkerArray() : ReportsArray : getType("+i+") = "+ ReportsArray.get(i).getType());


            marker.getPosition();
            Log.e("GoogleMapsFragment", " setupMarkerArray() : marker.getPosition("+i+") = " + marker.getPosition());

            markersArray.add(marker);
            Log.e("GoogleMapsFragment", " setupMarkerArray() : marker succesful added " );
        }


        ReportsArray.clear();
        Log.e("GoogleMapsFragment", "setupMarkerArray() : OnFinish");


        Log.e("GoogleMapsFragment", "                                      setupMarkerArray() : OnFinish");
    }


    private void setupMarkerArray2()
    {

        Log.e("\n\n\n\n\nGoogleMapsFragment", "                         setupMarkerArray() : OnStart");



        int i;
        for (i= 0; i < nrRapoarte ; i++ )
        {
            MarkerOptions marker = new MarkerOptions();

            marker.position(geoLocation.get(i));
            Log.e("GoogleMapsFragment", " setupMarkerArray() : marker : getGeoLocation("+i+") = "+ ReportsArray.get(i).getGeoLocation());


            marker.title(Type[type.get(i)]);
            Log.e("GoogleMapsFragment", "setupMarkerArray() : marker : getType("+i+") = "+ Type[type.get(i)]);


            marker.getPosition();
            Log.e("GoogleMapsFragment", " setupMarkerArray() : marker.getPosition("+i+") = " + marker.getPosition());

            markersArray.add(marker);
            Log.e("GoogleMapsFragment", " setupMarkerArray() : marker succesful added " );
        }


        ReportsArray.clear();
        Log.e("GoogleMapsFragment", "setupMarkerArray() : OnFinish");


        Log.e("GoogleMapsFragment", "                                      setupMarkerArray() : OnFinish");
    }


    private void displayAllReportOnGoogleMaps()
    {

        Log.e("GoogleMapsFragment", "displayAllReportOnGoogleMaps() : OnStart");

//        mMap.clear();

        for (int i= 0; i< markersArray.size(); i++ )
        {
            Log.e("GoogleMapsFragment", "displayAllReportOnGoogleMaps() : markersArray[ "+i+" ]" );

            mMap.addMarker(markersArray.get(i));

            Log.e("GoogleMapsFragment", "displayAllReportOnGoogleMaps() : Add marker on map : " + markersArray.get(i));
        }


        Log.e("GoogleMapsFragment", "displayAllReportOnGoogleMaps() : OnFinish");
    }

    private Boolean getAllReportsFromDB()
    {
        Log.e("GoogleMapsFragment", " getAllReportsFromDB() :  OnStart");

        db.collection("QReports")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.e("\nGoogleMapsFragment", " getAllReportsFromDB() : "+document.getId() + " => " + document.getData());

                                HashMap<String, Double> data = (HashMap<String, Double>) document.get("geoLocation");
                                geoLocation.add(new LatLng(data.get("latitude"),data.get("longitude")));
                                Log.e("GoogleMapsFragment", " getAllReportsFromDB() : " + "geoLocation = " + geoLocation.get(nrRapoarte));

                                Timestamp timestamp = (Timestamp) document.getData().get("date");
                                date.add(timestamp.toDate());
                                Log.e("GoogleMapsFragment", " getAllReportsFromDB() : " + "date = " + date.get(nrRapoarte));

                                descriptionReports.add((String) document.getData().get("descriptionReport").toString());
                                Log.e("GoogleMapsFragment", " getAllReportsFromDB() : " + "descriptionReports = " + descriptionReports.get(nrRapoarte));

                                userId.add(document.getData().get("userId").toString());
                                Log.e("GoogleMapsFragment", " getAllReportsFromDB() : " + "userId = " + userId.get(nrRapoarte));


                                switch(document.getData().get("reportState").toString())
                                {
                                    case "Netrimise": state.add(Reports.ReportState.Netrimise);
                                    case "Trimise": state.add(Reports.ReportState.Trimise);
                                    case "Rezolvate": state.add(Reports.ReportState.Rezolvate);
                                }
                                Log.e("GoogleMapsFragment", " getAllReportsFromDB() : " + "reportState = "  + state.get(nrRapoarte).toString());

                                for(int i=0 ; i<Type.length ; i++)
                                {
                                    if(Type[i].equals(document.getData().get("reportType")))
                                    {
                                        type.add(i);
                                        break;
                                    }
                                }
                                Log.e("GoogleMapsFragment", " getAllReportsFromDB() : " + "reportType = " + type.get(nrRapoarte));


                                credibility.add(Integer.parseInt(document.getData().get("credibility").toString()));
                                Log.e("GoogleMapsFragment", " getAllReportsFromDB() : credibility = " + credibility.get(nrRapoarte));


                                Log.e("GoogleMapsFragment", " getAllReportsFromDB() : document.getId() = " + document.getId());

                                documentIdFB.add(document.getId());

                                StorageReference fileRef = mStorageRef.child("reports/"+document.getId()+".jpg");

                                final String documentId = document.getId();

                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.e("GoogleMapsFragment", " getAllReportsFromDB() :  onSuccess " + "image");
                                        image.add(uri);
                                        reportImage.put(documentId,uri);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("GoogleMapsFragment", " getAllReportsFromDB() :  onFailure " + "image");
                                        image.add(null);
                                    }
                                });
                                


                                nrRapoarte++;

                                Log.e("GoogleMapsFragment", " getAllReportsFromDB() :  nrRapoarte " + nrRapoarte);
                            }
                        } else {
                            Log.e("GoogleMapsFragment", " getAllReportsFromDB() : Error getting documents.", task.getException());
                        }
                    }

                });


        Log.e("GoogleMapsFragment", " getAllReportsFromDB() :  OnFinish");
        return true;
    }

    private void addMapMarkers()
    {
        Log.e("MAP", "addMapMarkers(): OnStart");

        if(mMap != null){
            Log.e("MAP", "addMapMarkers(): Map is NOT null");

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mMap);
                Log.e("MAP", "addMapMarkers(): mClusterManager was created");
            }

            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getActivity(),
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
                Log.e("MAP", "addMapMarkers(): mClusterManagerRenderer was created");
            }

            int i=0;
            for(LatLng markerLocation: geoLocation){

                Log.e("MAP", "addMapMarkers: location: " + markerLocation.toString());
                try{
                    String snippet = "";
                    if(documentIdFB.get(i)!=null){
                        snippet = descriptionReports.get(i);
                        Log.e("MAP", "addMapMarkers: documentIdFB!=null: snippet = " + snippet);
                    }
                    else{
                        Log.e("MAP", "addMapMarkers: documentIdFB==null:" + documentIdFB.get(i));
                        snippet = "This is not you!";
                        Log.e("MAP", "addMapMarkers: documentIdFB!=null:" + snippet);
                    }

                    int avatar = R.drawable.raport_altele; // set the default avatar

                    switch(type.get(i))
                    {
                        case 0: avatar = R.drawable.raport_asistenta_sociala;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_asistenta_sociala");
                            break;

                        case 1: avatar = R.drawable.raport_depozitari_deseuri;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_asistenta_sociala");
                            break;

                        case 2: avatar = R.drawable.raport_lucrari_neautorizate;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_lucrari_neautorizate");
                            break;

                        case 3: avatar = R.drawable.raport_iluminat_public;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_iluminat_public");
                            break;

                        case 4: avatar = R.drawable.raport_parcari_neregulamentare;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_parcari_neregulamentare");
                            break;

                        case 5: avatar = R.drawable.raport_salubritate;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_salubritate");
                            break;

                        case 6: avatar = R.drawable.raport_parcuri;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_parcuri");
                            break;

                        case 7: avatar = R.drawable.raport_strazi_alei_trotuare_poduri;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_strazi_alei_trotuare_poduri");
                            break;

                        case 8: avatar = R.drawable.raport_taxe_si_impozite;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_taxe_si_impozite");
                            break;

                        case 9: avatar = R.drawable.raport_semnalizare_rutiera;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_semnalizare_rutiera");
                            break;

                        case 10: avatar = R.drawable.raport_retele_apa_canal;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_retele_apa_canal");
                            break;

                        case 11: avatar = R.drawable.raport_transport_public;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_transport_public");
                            break;

                        case 12: avatar = R.drawable.raport_altele;
                            Log.e("MAP", "addMapMarkers: Tip sesizare : raport_altele");
                            break;

                    }

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            markerLocation,
                            Type[type.get(i)],
                            snippet,
                            avatar,
                            documentIdFB.get(i)
                    );
                    Log.e("MAP", "addMapMarkers:  newClusterMarker :" + newClusterMarker.getTitle());

                    mClusterManager.addItem(newClusterMarker);

                    Log.e("MAP", "addMapMarkers:  newClusterMarker was ADD to mClusterManager ");

                    mClusterMarkers.add(newClusterMarker);

                }catch (NullPointerException e){
                    Log.e("MAP", "addMapMarkers: NullPointerException: " + e.getMessage() );
                }

                i++;

            }
            mClusterManager.cluster();
            Log.e("MAP", "addMapMarkers:   mClusterManager.cluster() ");
//            setCameraView();



            if(mClusterManager!=null)
            {
                mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterMarker>() {
                    @Override
                    public boolean onClusterItemClick(ClusterMarker item) {
                        Log.e("cluster item","clicked 1 :" + item.getPosition());

                        approveReportFloatingActionButton.setVisibility(View.INVISIBLE);
                        denyReportFloatingActionButton.setVisibility(View.INVISIBLE);

                        markerInfoLayout.setVisibility(View.VISIBLE);

                        double distanceInKM  = distanceInKmBetweenEarthCoordinates(lastLatitude,lastLongitude,item.getPosition().latitude,item.getPosition().longitude);

                        Log.e("GoogleMapsFragment","distanceInKM : " + distanceInKM);

                        if(Profil.getInstance().getID()!=null && !Profil.getInstance().interactedReports.contains(item.getMarkerId()))
                        {
                            if(distanceInKM < 0.05)
                            {
                                approveReportFloatingActionButton.setVisibility(View.VISIBLE);
                                denyReportFloatingActionButton.setVisibility(View.VISIBLE);
                                approveReportFloatingActionButton.bringToFront();
                                approveReportFloatingActionButton.bringToFront();

                                Log.e("GoogleMapsFragment","distanceInKM < 0.05");
                            }
                        }

                        nowSelectedReportID = item.getMarkerId();


                        int i;
                        for(i=0;i<nrRapoarte;i++)
                        {
                            if(documentIdFB.get(i).equals(item.getMarkerId()))
                            {
//                                image.get(i)
                                nrCredibilityInstant = credibility.get(i);

                                Glide.with(getContext()).load(reportImage.get(item.getMarkerId())).into(imagineInfoLayout);
                                tipInfoLayout.setText(item.getTitle());
                                desciereInfoLayout.setText(item.getSnippet());
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm");
                                dataInfoLayout.setText(dateFormat.format(date.get(i)));
                                nrConfirmariInfoLayout.setText(String.valueOf(nrCredibilityInstant));
                            }
                        }


                        double dpPerdegree = 256.0*Math.pow(2, DEFAULT_ZOOM)/170.0;
                        double screen_height = (double) 670;
                        double screen_height_30p = 30.0*screen_height/100.0;
                        double degree_30p = screen_height_30p/dpPerdegree;



                        double latitude = item.getPosition().latitude;
                        double longitude = item.getPosition().longitude;

                        LatLng latlng = new LatLng(latitude + degree_30p,longitude);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, GoogleMapsFragment.DEFAULT_ZOOM));

                        Log.e("cluster item","clicked 2 :" + latlng);
                        return true;
                    }
                });

            }


        }
        else{
            Log.e("MAP", "addMapMarkers(): Map is null");
        }

        Log.e("MAP", "addMapMarkers(): OnFinish");
    }

    private void initMap(){
        Log.e("MAP", "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_maps);

        mapFragment.getMapAsync(GoogleMapsFragment.this);

    }

    private void init(){
        Log.e("MAP" , "init: initializing ");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });
        mGPS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("MAP" , "onClick: clicked gps icon");
                markerInfoLayout.setVisibility(View.INVISIBLE);
                approveReportFloatingActionButton.setVisibility(View.INVISIBLE);
                denyReportFloatingActionButton.setVisibility(View.INVISIBLE);
                getLocationPermission();
            }
        });
    }

    private void geoLocate() {
        Log.e("MAP" , "geoLocate: geolocateing ");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch(IOException e){
            Log.e("MAP","geoLocate: IOException: " + e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.e("MAP" , "geoLocate: found a location: "+ address.toString());

//            Toast.makeText(getContext(),address.toString(),Toast.LENGTH_LONG).show();
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()), address.getAddressLine(0));
        }
    }

    private void getDeviceLocation(){
        Log.e("MAP","getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try{
            if(mLocationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener(){
                    @Override
                    public void onComplete(@NonNull final Task task) {

                        if(task.isSuccessful() && task != null)
                        {
                            Log.e("MAP", "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            Log.e("MAP", "task.getResult(): " + task.getResult());

                            if(task.getResult()!=null)
                            {
                                Log.e("MAP", "task.getResult() != null");
                            }
                            else{
                                Log.e("MAP", "task.getResult() == null");
                            }

                            if(currentLocation!=null)
                            {
                                Log.e("MAP", "onComplete: currentLocation != null");
                                LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                                lastLatitude = currentLocation.getLatitude();
                                lastLongitude = currentLocation.getLongitude();

                                moveCamera(latLng, "My location");
                            }
                            else {
                                Log.e("MAP", "onComplete: currentLocation == null");
                            }

                        }
                        else{
                            Log.e("MAP", "onComplete: current location is null");
                            Toast.makeText(getContext(),"unable to get current location",Toast.LENGTH_LONG).show();
                        }


                    }
                });

            }

        }catch(SecurityException e)
        {
            Log.e("MAP","getDeviceLocation:  SecurityException: " + e.getMessage() );
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        mStorageAccessPermision = false;

        Log.e("MAP", "onRequestPermissionsResult: getting location");

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.e("MAP", "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.e("MAP", "onRequestPermissionsResult: getting granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                    Log.e("MAP","INIT MAP");
                }
            }
            case STORAGE_ACCESS_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mStorageAccessPermision = false;
                            Log.e("MAP", "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.e("MAP", "onRequestPermissionsResult: getting granted");
                    mStorageAccessPermision = true;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        addMapMarkers();
    }


    public static LatLng getLatLngFromMarker()
    {
        return latLngMarker;
    }

    public void makeReports()
    {
        try{

            Log.e("\n\n\n\n\nGoogleMapsFragment","                                      makeReports() : OnStart");

            for(int i=0; i<nrRapoarte; i++)
            {
                Reports rap = null;

                Log.e("GoogleMapsFragment","makeReports() : geoLocation.get("+i+") : " + geoLocation.get(i));

                Log.e("GoogleMapsFragment","makeReports() : date.get("+i+") : " + date.get(i));

                Log.e("GoogleMapsFragment","makeReports() : descriptionReports.get("+i+") : " + descriptionReports.get(i));

                Log.e("GoogleMapsFragment","makeReports() : userId.get("+i+") : " + userId.get(i));

                Log.e("GoogleMapsFragment","makeReports() : type.get("+i+") : " + type.get(i));

                Log.e("GoogleMapsFragment","makeReports() : image.get("+i+") : " + image.get(i));


                rap = new Reports(geoLocation.get(i),
                        date.get(i),
                        descriptionReports.get(i),
                        userId.get(i),
                        state.get(i),
                        type.get(i),
                        image.get(i));

                Log.e("GoogleMapsFragment","makeReports() : Reports rap "+i+": was created : " + rap.getGeoLocation());

                ReportsArray.add(new Reports(geoLocation.get(i),
                        date.get(i),
                        descriptionReports.get(i),
                        userId.get(i),
                        state.get(i),
                        type.get(i),
                        image.get(i)));



                try{
                    Log.e("GoogleMapsFragment","rap WAS NOT finalize() : " + rap.getGeoLocation() );
                }catch(Exception e)
                {
                    Log.e("GoogleMapsFragment","rap was finalize() : " + e.getMessage() );
                }

                Log.e("GoogleMapsFragment","628 makeReports() : ReportsArray.size() : " + ReportsArray.size());


                displayArrayList();
            }


            Log.e("GoogleMapsFragment","                                      makeReports() : OnFinish\n\n\n\n\n");

        }catch(Exception e)
        {
            Log.e("GoogleMapsFragment","Error on makeReports() : " + e.getMessage());
        }

    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            for(int i=0;i<5;i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
//            TextView txt = (TextView) findViewById(R.id.output);
//            txt.setText("Executed");
            Log.e("GoogleMapsFragment","LongOperation : AsyncTask : doInBackground() -" + "Executed");
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("GoogleMapsFragment","LongOperation : AsyncTask : doInBackground() -" + "onPostExecute : ");

//            makeReports();

//            displayArrayList();

//            setupMarkerArray();

//            setupMarkerArray2();

            getStorageAccessPermisios();

            addMapMarkers();


//            displayAllReportOnGoogleMaps();
        }
        @Override
        protected void onPreExecute() {
            Log.e("GoogleMapsFragment","LongOperation : AsyncTask : doInBackground() -" + "onPreExecute");
//            getAllReportsFromDB();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.e("GoogleMapsFragment","LongOperation : AsyncTask : doInBackground() -" + "onProgressUpdate");

        }
    }

    private void getLocationPermission(){
        Log.e("MAP", "getLocationPermission: getting location");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ActivityCompat.checkSelfPermission(getContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.e("MAP", "getLocationPermission: if 1");
            if (ActivityCompat.checkSelfPermission(getContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.e("MAP", "getLocationPermission: if 2");
                //set a boolean
                mLocationPermissionGranted = true;
                initMap();
                getStorageAccessPermisios();
            }
            else{
                Log.e("MAP", "getLocationPermission: else 1");
                requestPermissions(permissions,LOCATION_PERMISSION_REQUEST_CODE);
                getStorageAccessPermisios();
            }
        }
        else{
            Log.e("MAP", "getLocationPermission: else 2");
            requestPermissions(permissions,LOCATION_PERMISSION_REQUEST_CODE);
            getStorageAccessPermisios();
        }
        Log.e("MAP", "getLocationPermission: end");
    }

    private void getStorageAccessPermisios()
    {
        String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(ActivityCompat.checkSelfPermission(getContext(),READ_ACCESS) == PackageManager.PERMISSION_GRANTED) {
            Log.e("MAP", "getStoragePermissions: if 1");
            if (ActivityCompat.checkSelfPermission(getContext(), WRITE_ACCESS) == PackageManager.PERMISSION_GRANTED) {
                Log.e("MAP", "getStoragePermissions: if 2");
                //set a boolean
                mStorageAccessPermision = true;
            }
            else{
                Log.e("MAP", "getStoragePermissions: else 1");
                requestPermissions(permissionsStorage,STORAGE_ACCESS_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            Log.e("MAP", "getStoragePermissions: else 2");
            requestPermissions(permissionsStorage,STORAGE_ACCESS_PERMISSION_REQUEST_CODE);
        }
        Log.e("MAP", "getStoragePermissions: end");
    }

    private void moveCamera(LatLng latLng, String title){
        Log.e("MAP","moveCamera: moving the camera to:  lat: "+ latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, GoogleMapsFragment.DEFAULT_ZOOM));
        Log.e("MAP", "Am trecut 1");

        if(!title.equals("My location")){
            Log.e("MAP", "Am trecut 2");
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            Log.e("MAP", "Am trecut 3");
            mMap.addMarker(options);
            Log.e("MAP", "Am trecut 4");
        }
        else {
            Log.e("MAP", "Am trecut 6");
        }

        Log.e("MAP", "Am trecut 7");

    }

    public void startReportsActivity()
    {
        Intent intent = new Intent(getContext(), ReportsActivity.class);
        startActivity(intent);
    }

    private double distanceInKmBetweenEarthCoordinates(double lat1, double lon1, double lat2, double lon2)
    {
        double earthRadiusKm = 6371;

        double dLat = degreesToRadians(lat2-lat1);
        double dLon = degreesToRadians(lon2-lon1);

        double mlat1 = degreesToRadians(lat1);
        double mlat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(mlat1) * Math.cos(mlat2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadiusKm * c;
    }

    private double degreesToRadians(double degrees)
    {
        return degrees * Math.PI / 180;
    }

}


