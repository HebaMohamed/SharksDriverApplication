package com.sharks.gp.sharkspassengerapplication.myclasses;

/**
 * Created by dell on 2/10/2017.
 */

public class TalkMessage {
    public String msgFlag;// p for passenger, d for driver
    public String msg;

    public TalkMessage(String msgFlag, String msg){
        this.msgFlag=msgFlag;
        this.msg=msg;
    }
}
