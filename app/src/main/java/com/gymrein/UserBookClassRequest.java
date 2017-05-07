package com.gymrein;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcisneros77 on 5/5/17.
 */

public class UserBookClassRequest extends JsonObjectRequest {

    private static final String book_class_url = "https://gymrein.herokuapp.com/api/v1/";
    private Map<String,String> paramsT;
    private String token;

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        paramsT = new HashMap<>();
        paramsT.put("Authorization","Token token=\"" + token +"\"");

        return paramsT;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        ClassDetailsActivity.setStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    public UserBookClassRequest(JSONObject params,String t,String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        super(Method.POST,book_class_url.concat(url),params,listener,errorListener);
        token = t;
    }
}