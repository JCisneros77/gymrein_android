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

public class DeletePaymentRequest extends StringRequest {
    private static final String payment_request_url = "https://gymrein.herokuapp.com/api/v1/cards/";
    private Map<String,String> params;
    private String token;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        PaymentDetailActivity.setStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        params = new HashMap<>();
        params.put("Authorization","Token token=\"" + token +"\"");

        return params;
    }

    public DeletePaymentRequest(String idN,String t,Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.DELETE,payment_request_url + idN,listener,errorListener);
        token = t;
    }
}
