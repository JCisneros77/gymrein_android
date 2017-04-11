package com.gymrein;

/**
 * Created by jcisneros77 on 4/2/17.
 */

public class PackageModel {
    String name;
    int id;
    int price;
    int classes;

    public PackageModel(int idN,String nameN, int priceN, int classesN){
        this.id = idN;
        this.name = nameN;
        this.price = priceN;
        this.classes = classesN;
    }

    public int getID(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public int getPrice(){
        return this.price;
    }

    public int getClasses(){
        return this.classes;
    }
}
