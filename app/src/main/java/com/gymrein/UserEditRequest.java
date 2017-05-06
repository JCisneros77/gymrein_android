package com.gymrein;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcisneros77 on 5/6/17.
 */

public class UserEditRequest extends JsonObjectRequest {

    private static final String update_user_url = "https://gymrein.herokuapp.com/api/v1/users";
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
        UserEditProfileActivity.setSuccStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    public UserEditRequest(JSONObject params,String t, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        super(Method.PATCH,update_user_url,params,listener,errorListener);
        token = t;
    }
}