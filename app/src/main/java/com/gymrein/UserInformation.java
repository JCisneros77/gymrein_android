package com.gymrein;

/**
 * Created by jcisneros77 on 3/12/17.
 */

public class UserInformation {
    private  String name;
    private  String email;
    private  String lastname;
    private  String phone;
    private  int classes;
    private  String token;
    private String avatar_url;

    public UserInformation(String Name, String Email, String Lastname, String Phone, int Classes, String Token,String Avatar){
        name = Name;
        email = Email;
        lastname = Lastname;
        phone = Phone;
        classes = Classes;
        token = Token;
        avatar_url = Avatar;
    }

    // Get User Information
    public String getName(){
        return name;
    }

    public  String getEmail(){
        return email;
    }

    public String getLastname(){
        return lastname;
    }

    public String getPhone(){
        return phone;
    }

    public int getClasses(){
        return classes;
    }

    public String getToken(){
        return token;
    }

    public String getAvatar(){return avatar_url;}

    public void setClasses(int c) { classes = c;}
}
