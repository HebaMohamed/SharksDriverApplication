package com.sharks.gp.sharkspassengerapplication.myclasses;

/**
 * Created by dell on 6/22/2017.
 */

public class FemaleWarning extends Warning {
    public String status;

    public Long timestamp;
    public double lat;
    public double lng;
//    public int tid;
//    public String datetxt;

    public Driver d;
    public Passenger p;
    public Vehicle v;

    public FemaleWarning(){
        d = new Driver(0);
        p = new Passenger(0);
        v = new Vehicle(0);
    }
}
