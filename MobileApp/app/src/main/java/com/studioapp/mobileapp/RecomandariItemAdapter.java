package com.studioapp.mobileapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecomandariItemAdapter extends RecyclerView.Adapter<RecomandariItemAdapter.RecomandariViewHolder> {

    public ArrayList<RecomandareItem> mRecomandariList;

    public static class RecomandariViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public RecomandariViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.recomandare_imagine);
            mTextView1 = itemView.findViewById(R.id.recomandare_linia1);
            mTextView2 = itemView.findViewById(R.id.recomandare_linia2);
            Log.e("RecomandariViewHolder","RecomandariViewHolder : Am ajuns aici");
        }
    }

    public RecomandariItemAdapter(ArrayList<RecomandareItem> recomandareItemArrayList){
        mRecomandariList = recomandareItemArrayList;
    }

    @NonNull
    @Override
    public RecomandariViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recomandare_item,parent,false);
        Log.e("RecomandariViewHolder","onCreateViewHolder : Am ajuns aici");
        return new RecomandariViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecomandariViewHolder holder, int position) {
        RecomandareItem currentItem = mRecomandariList.get(position);

        holder.imageView.setImageResource(currentItem.getmImageResource());
        Log.e("BindViewHolder","IMG:"+currentItem.getmImageResource());
        holder.mTextView1.setText(currentItem.getRecomandare_linia1());
        Log.e("BindViewHolder","IMG:"+currentItem.getRecomandare_linia1());
        holder.mTextView2.setText(currentItem.getRecomandare_linia2());
        Log.e("BindViewHolder","IMG:"+currentItem.getRecomandare_linia2());
    }

    @Override
    public int getItemCount() {
        Log.e("getItemCount","Am ajuns aici");
        return mRecomandariList.size();
    }
}
