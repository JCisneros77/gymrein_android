package com.gymrein;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcisneros77 on 3/26/17.
 */

public class ForgotPasswordRequest extends StringRequest {
    private static final String login_request_url = "https://gymrein.herokuapp.com/api/v1/users/send_password_recovery";
    private Map<String,String> params;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        ForgotPasswordActivity.setSuccStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    public ForgotPasswordRequest(String email, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.POST,login_request_url,listener,errorListener);
        params = new HashMap<>();
        params.put("email",email);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
