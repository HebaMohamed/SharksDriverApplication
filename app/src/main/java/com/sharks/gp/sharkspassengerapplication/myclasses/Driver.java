package com.sharks.gp.sharkspassengerapplication.myclasses;

import java.util.ArrayList;

/**
 * Created by dell on 2/7/2017.
 */
public class Driver extends User {

    public String image;//byte array string

    public Driver(int id) {
        this.id = id;
    }

    public Driver(int id, String name, String email, String password, String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public Vehicle vehicle;

    public ArrayList<MgrInstruction> instructions = new ArrayList<MgrInstruction>();

    public ArrayList<Double> restrictedLats = new ArrayList<Double>();
    public ArrayList<Double> restrictedLngs = new ArrayList<Double>();

}