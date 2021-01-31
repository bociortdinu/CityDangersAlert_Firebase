package com.studioapp.mobileapp;

import android.util.Log;

public class RecomandareItem {
    private int mImageResource;
    private String recomandare_linia1;
    private String recomandare_linia2;

    public RecomandareItem(int ImageResource, String recomandare_ln1, String recomandare_ln2)
    {
        mImageResource = ImageResource;
        recomandare_linia1=recomandare_ln1;
        recomandare_linia2=recomandare_ln2;
        Log.e("Constructor","RecomandareItem");
    }

    public int getmImageResource() {
        return mImageResource;
    }
    public String getRecomandare_linia1()
    {
        return  recomandare_linia1;
    }
    public String getRecomandare_linia2()
    {
        return recomandare_linia2;
    }
}
