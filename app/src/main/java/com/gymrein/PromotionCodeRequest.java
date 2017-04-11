package com.gymrein;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcisneros77 on 4/5/17.
 */

public class PromotionCodeRequest extends StringRequest {
    private static final String promotion_code_request = "https://gymrein.herokuapp.com/api/v1/promotions/validate";
    private Map<String,String> params;
    private String token;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        BuyPackageActivity.setStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        params = new HashMap<>();
        params.put("Authorization","Token token=\"" + token +"\"");

        return params;
    }

    public PromotionCodeRequest(String code,String t, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.POST,promotion_code_request,listener,errorListener);
        params = new HashMap<>();
        params.put("code",code);
        token = t;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }




}
