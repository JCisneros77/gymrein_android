package com.gymrein;

import android.app.Application;

/**
 * Created by jcisneros77 on 3/12/17.
 */

public class GymReinApp extends Application {
    private UserInformation obj;

    public UserInformation getUserInformation(){
        return obj;
    }

    public void setUserInformation(UserInformation o){
        obj = o;
    }

    public void logout(){obj = null;}
}
