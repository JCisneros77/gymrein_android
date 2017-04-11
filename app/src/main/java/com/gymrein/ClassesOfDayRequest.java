package com.gymrein;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcisneros77 on 4/10/17.
 */

public class ClassesOfDayRequest extends StringRequest {
    private static final String classes_of_day_url = "https://gymrein.herokuapp.com/api/v1/reservations/find_by_date";
    private Map<String,String> params;
    private Map<String,String> paramsT;
    private String token;

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        ClassesOfDayActivity.setStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        paramsT = new HashMap<>();
        paramsT.put("Authorization","Token token=\"" + token +"\"");

        return paramsT;
    }

    public ClassesOfDayRequest(String t,String date,Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Method.POST,classes_of_day_url,listener,errorListener);
        token = t;
        params = new HashMap<>();
        params.put("date",date);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
