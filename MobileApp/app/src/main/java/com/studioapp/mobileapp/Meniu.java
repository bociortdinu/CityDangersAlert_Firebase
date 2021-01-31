package com.studioapp.mobileapp;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class Meniu extends AppCompatActivity {

    public BottomNavigationView bottomNav;

    private long backPressedTime;

    @Override
    public void onBackPressed() {


        if(backPressedTime + 2000 > System.currentTimeMillis())
        {
            super.onBackPressed();

            if(Profil.getInstance().getID()!=null)
            {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getBaseContext(),"Disconnected",Toast.LENGTH_SHORT).show();
            }

            return;
        }
        else {
            Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meniu);

        bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnNavigationItemSelectedListener(navListener);


        Log.e("Meniu","Profil ID must be null : " + Profil.getInstance().getID());


        Log.e("Meniu","Start Google Maps");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new GoogleMapsFragment()).commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_googlemaps:
                            selectedFragment = new GoogleMapsFragment();
                            break;
                        case R.id.nav_profil:
                            selectedFragment = new ProfilFragment();
                            break;
//                        case R.id.nav_activitati:
//                            selectedFragment = new ActivitatiFragment();
//                            break;
//                        case R.id.nav_recomandari:
//                            selectedFragment = new RecomandariFragment();
//                            break;
                    }

                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

}
