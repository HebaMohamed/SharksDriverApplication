package com.sharks.gp.sharkspassengerapplication.myclasses;

/**
 * Created by dell on 2/7/2017.
 */
public class Passenger extends User {

    public String gender;
    public int age;
    public int relative_phones;
    public String language;

    public Passenger(int id){
        this.id=id;
    }
    public Passenger(int id,String pass,String name,String gender,int age,int phone,int R_phone,String language,String email)
    {
        this.id=id;
        this.password=pass;
        this.name=name;
        this.gender=gender;
        this.age=age;
        this.phone=phone;
        this.relative_phones=R_phone;
        this.language=language;
        this.email=email;


    }


}
