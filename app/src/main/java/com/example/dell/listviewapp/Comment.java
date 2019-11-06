package com.example.dell.listviewapp;

/**
 * Created by DELL on 08/09/2016.
 */
public class Comment {


    String message;
    String adress;

    public Comment(String m,String a){

        this.message=m;
        this.adress=a;

    }

    public Comment(){


    }
    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
