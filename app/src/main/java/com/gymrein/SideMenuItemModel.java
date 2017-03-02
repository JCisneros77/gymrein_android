package com.gymrein;

/**
 * Created by jcisneros77 on 3/1/17.
 */

public class SideMenuItemModel {
    String name;
    int image_path;

    public SideMenuItemModel(String n,int ip){
        this.name = n;
        this.image_path = ip;
    }

    public String getName(){
        return this.name;
    }

    public int getImage_path(){
        return this.image_path;
    }

}
