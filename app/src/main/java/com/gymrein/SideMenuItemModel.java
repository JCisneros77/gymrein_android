package com.gymrein;

/**
 * Created by jcisneros77 on 3/1/17.
 */

public class SideMenuItemModel {
    String name;
    int image_path;
    String card_holder_name;
    String card_brand;
    String card_number;
    String exp_month;
    String exp_year;
    String id;

    public SideMenuItemModel(String n,int ip){
        this.name = n;
        this.image_path = ip;
    }

    public SideMenuItemModel(String n,int ip,String c_name,String c_brand,String c_number,String e_month,String e_year,String idN){
        this.name = n;
        this.image_path = ip;
        this.card_holder_name = c_name;
        this.card_brand = c_brand;
        this.card_number = c_number;
        this.exp_month = e_month;
        this.exp_year = e_year;
        this.id = idN;
    }

    public String getName(){
        return this.name;
    }

    public int getImage_path(){
        return this.image_path;
    }

    public String getCardName(){
        return this.card_holder_name;
    }

    public String getCardBrand(){
        return this.card_brand;
    }

    public String getCard_number(){
        return this.card_number;
    }

    public String getExpMonth(){
        return this.exp_month;
    }

    public String getExpYear(){
        return this.exp_year;
    }

    public String getId(){ return this.id; }


}
