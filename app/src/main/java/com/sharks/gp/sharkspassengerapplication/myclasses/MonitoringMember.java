package com.sharks.gp.sharkspassengerapplication.myclasses;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dell on 6/22/2017.
 */

public class MonitoringMember extends User {
    public String position;
    public String gender;
    public String lastlogin_time;
    public String account_state;

    public MonitoringMember(){}

    public MonitoringMember(int id, String name, String position, String gender, long lastlogin, String account_state){
        this.name=name;
        this.id=id;
        this.position=position;
        this.gender=gender;
        this.account_state = account_state;

        Date lastdate = new Date(lastlogin);
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        lastlogin_time = formatter.format(lastdate);
    }

}
