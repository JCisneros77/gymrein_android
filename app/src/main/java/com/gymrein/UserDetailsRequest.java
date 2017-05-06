package com.gymrein;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcisneros77 on 5/6/17.
 */

public class UserDetailsRequest extends StringRequest {
    private static final String user_details_url = "https://gymrein.herokuapp.com/api/v1/users/";

    private Map<String,String> paramsT;
    private String token;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        UserProfileActivity.setStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        paramsT = new HashMap<>();
        paramsT.put("Authorization","Token token=\"" + token +"\"");

        return paramsT;
    }

    public UserDetailsRequest(String id,String t,Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.GET,user_details_url.concat(id),listener,errorListener);
        token = t;
    }

}