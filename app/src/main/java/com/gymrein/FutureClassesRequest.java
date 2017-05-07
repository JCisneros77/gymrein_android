package com.gymrein;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcisneros77 on 5/7/17.
 */

public class FutureClassesRequest extends StringRequest {

    private static final String future_classes_url = "https://gymrein.herokuapp.com/api/v1/";
    private Map<String,String> params;
    private String token;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        MyClassesActivity.setStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        params = new HashMap<>();
        params.put("Authorization","Token token=\"" + token +"\"");

        return params;
    }

    public FutureClassesRequest(String t,String url,Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.GET,future_classes_url.concat(url).concat("/future"),listener,errorListener);
        token = t;
    }

}
