package com.studioapp.mobileapp;

import android.net.Uri;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;


public class Reports {
    private static int reportID;
    private static LatLng geoLocation;
    private static Date date;
    private static Uri image;    // asta trebe rezolvata cumva
    private static String descriptionReport;
    private static String userId;
    private static ReportState reportState;
    private static int reportType;
    private static int credibility=0;


    public enum ReportState{
        Netrimise,
        Trimise,
        Rezolvate
    }

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


    public Reports(LatLng geoLocation, Date data , String descriptionReports, String userId, ReportState state, int type,Uri image)
    {
        Reports.geoLocation = geoLocation;
        Reports.date = data;
        Reports.image = image;
        Reports.descriptionReport = descriptionReports;
        Reports.userId = userId;
        Reports.reportState = state;
        Reports.reportType = type;
        Reports.credibility=0;
    }

    protected void finalize()
    {
//Keep some resource closing operations here
    }


    public int getReportID()
    {
        return reportID;
    }
    public void setReportID(int ID){
        Reports.reportID = ID;
    }


    public LatLng getGeoLocation()
    {
        return geoLocation;
    }
    public void setGeoLocation(LatLng geoLocation){
        Reports.geoLocation = geoLocation;
    }



    public Date getDate(){
        return Reports.date;
    }
    public void setDate(Date date){
        Reports.date = date;
    }



    public Uri getImage()
    {
        return Reports.image;
    }
    public void setImage(Uri image){
        Reports.image= image;
    }



    public String getDescription() {
        return Reports.descriptionReport;
    }
    public void setDescription(String description){
        Reports.descriptionReport = description;
    }


    public String getUserId(){
        return Reports.userId;
    }
    public void setUserId(String userId){
        Reports.userId=userId;
    }



    public String getState()
    {
        return Reports.reportState.toString();
    }
    public void setState_Netrimise(){
        Reports.reportState = ReportState.Netrimise;
    }
    public void setState_Trimise(){
        Reports.reportState = ReportState.Trimise;
    }
    public void setState_Rezolvate(){
        Reports.reportState = ReportState.Rezolvate;
    }



    public String getType(){
        return Type[Reports.reportType];
    }
    public void setType(int type){
        Reports.reportType = type;
    }

    public int getCredibility()
    {
        return credibility;
    }
    public void setCredibility(int Credibility)
    {
        credibility=Credibility;
    }


}
