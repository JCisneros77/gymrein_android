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

public class BuyPackageRequest extends StringRequest {
    private static final String buy_package_url = "https://gymrein.herokuapp.com/api/v1/user_packages";
    private Map<String,String> params;
    private Map<String,String> paramsT;
    private String token;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        BuyPackageActivity.setStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        paramsT = new HashMap<>();
        paramsT.put("Authorization","Token token=\"" + token +"\"");

        return paramsT;
    }

    public BuyPackageRequest(String t,String pack_id,String card_id,Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.POST,buy_package_url,listener,errorListener);
        token = t;
        params = new HashMap<>();
        params.put("package_id",pack_id);
        params.put("card_id",card_id);
    }

    public BuyPackageRequest(String t,String pack_id,String card_id,String promo_id,Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.POST,buy_package_url,listener,errorListener);
        token = t;
        params = new HashMap<>();
        params.put("package_id",pack_id);
        params.put("promotion_id",promo_id);
        params.put("card_id",card_id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

}
