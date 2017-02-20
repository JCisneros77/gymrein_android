package com.gymrein;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;


/**
 * Created by jcisneros77 on 2/16/17.
 */

public class RegisterRequest extends JsonObjectRequest {

    private static final String register_request_url = "https://gymrein.herokuapp.com/api/v1/users";

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        RegisterActivity.setSuccStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    public RegisterRequest(JSONObject params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        super(Method.POST,register_request_url,params,listener,errorListener);
    }
}
