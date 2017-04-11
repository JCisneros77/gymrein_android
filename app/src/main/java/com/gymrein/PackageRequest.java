package com.gymrein;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcisneros77 on 4/2/17.
 */

public class PackageRequest extends StringRequest {

    private static final String package_request_url = "https://gymrein.herokuapp.com/api/v1/packages";
    private Map<String,String> params;
    private String token;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        ClassesOfDayActivity.setStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        params = new HashMap<>();
        params.put("Authorization","Token token=\"" + token +"\"");

        return params;
    }

    public PackageRequest(String t,Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.GET,package_request_url,listener,errorListener);
        token = t;
    }
}
