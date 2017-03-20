package com.gymrein;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcisneros77 on 3/13/17.
 */

public class AddPaymentRequest extends JsonObjectRequest {

    private static final String register_request_url = "https://gymrein.herokuapp.com/api/v1/cards";
    private Map<String,String> params;
    private String token;

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        AddPaymentActivity.setStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    public AddPaymentRequest(JSONObject params, String t, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        super(Method.POST,register_request_url,params,listener,errorListener);
        token = t;

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        params = new HashMap<>();
        params.put("Authorization","Token token=\"" + token +"\"");

        return params;
    }
}
