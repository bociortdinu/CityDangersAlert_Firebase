package com.studioapp.mobileapp;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.Attributes;

public class RecomandariFragment extends Fragment {

    private static View rootView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<RecomandareItem> recomandareItemArrayList = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recomandari,container,false);


        recyclerView = rootView.findViewById(R.id.recycleview_recomandari);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new RecomandariItemAdapter(recomandareItemArrayList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

//        try {
//            getAndPreviwRecomandation();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

//
//        swipeRefreshLayout = rootView.findViewById(R.id.recomandari_swipeRefresh);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//                try {
//                    getAndPreviwRecomandation();
//                    recyclerView.setAdapter(mAdapter);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

        return rootView;
    }



    private void getAndPreviwRecomandation() throws SQLException {
        Statement sql;
        sql = Profil.getInstance().conectionclass().createStatement();

        Log.e("Recomandation","getAndPreviwRecomandation");
        ResultSet rs;

        rs = sql.executeQuery("select * from [dbo].[UserRecomandation]");

        recomandareItemArrayList.clear();

        while (rs.next())
        {
            if(String.valueOf(rs.getInt("id")).equals(Profil.getInstance().getID()))
            {
                String titlu = rs.getString("titleRecomandation");
                String recomandarea = rs.getString("recomandation");
                recomandareItemArrayList.add(new RecomandareItem(R.drawable.ic_add_alert_black_24dp,titlu,recomandarea));
                Log.e("Primeste","Primeste titlu: " + titlu);
                Log.e("Primeste","Primeste recomandarea: "+recomandarea);
            }
        }
        Profil.getInstance().conectionclass().close();
    }


}
