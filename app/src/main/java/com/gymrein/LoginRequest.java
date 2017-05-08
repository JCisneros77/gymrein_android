package com.gymrein;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcisneros77 on 2/17/17.
 */

public class LoginRequest extends StringRequest {

    private static final String login_request_url = "https://gymrein.herokuapp.com/api/v1/sessions/plain";
    private Map<String,String> params;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        LoginActivity.setSuccStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    public LoginRequest(String email,String password,String token, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.POST,login_request_url,listener,errorListener);
        params = new HashMap<>();
        params.put("email",email);
        params.put("password",password);
        params.put("platform","android");
        params.put("device_id",token);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
