package com.sharks.gp.sharkspassengerapplication.myclasses;

/**
 * Created by dell on 2/7/2017.
 */
public class Vehicle {
    public int ID;
    public String Model;
    public String Color;
    public double Longtude;
    public double Attitude;
    public String Outside_working;
    public String Plate_number;

    public Vehicle(int id)
    {
        ID=id;
    }
    public Vehicle(int id,String model,String color,double lngtude,double atitude,String outside_working, String plate_number)
    {
        ID=id;
        Model=model;
        Color=color;
        Longtude=lngtude;
        Attitude=atitude;
        Outside_working=outside_working;
        Plate_number=plate_number;

    }
}
