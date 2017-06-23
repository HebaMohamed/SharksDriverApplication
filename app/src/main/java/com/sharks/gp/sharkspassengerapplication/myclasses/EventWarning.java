package com.sharks.gp.sharkspassengerapplication.myclasses;

/**
 * Created by dell on 6/22/2017.
 */

public class EventWarning extends Warning {
    public String text;
//    public int did;
    public Driver d;

//    public String datetxt;
//    public String dname;

    public EventWarning(Driver d){
        this.d=d;
    }
}
