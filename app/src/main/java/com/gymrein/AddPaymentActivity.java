package com.gymrein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddPaymentActivity extends AppCompatActivity {
    private static int statusCode = -1;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        final ImageButton btn_back_to_payment = (ImageButton) findViewById(R.id.btn_back_to_payment);
        final Button btn_save_card = (Button) findViewById(R.id.btn_save_card);
        final EditText tf_card_holder_name= (EditText) findViewById(R.id.tf_card_holder_name);
        final EditText tf_card_number = (EditText) findViewById(R.id.tf_card_number);
        final EditText tf_card_exp_month = (EditText) findViewById(R.id.tf_card_exp_moth);
        final EditText tf_card_exp_year = (EditText) findViewById(R.id.tf_card_exp_year);
        final EditText tf_card_cvv = (EditText) findViewById(R.id.tf_card_cvv);

        btn_back_to_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent paymentIntent = new Intent(AddPaymentActivity.this,PaymentActivity.class);
                AddPaymentActivity.this.startActivity(paymentIntent);
            }
        });

        tf_card_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (count <= tf_card_number.getText().toString().length()
                        &&(tf_card_number.getText().toString().length()==4
                        ||tf_card_number.getText().toString().length()==9
                        ||tf_card_number.getText().toString().length()==14)){
                    tf_card_number.setText(tf_card_number.getText().toString()+" ");
                    int pos = tf_card_number.getText().length();
                    tf_card_number.setSelection(pos);
                }else if (count >= tf_card_number.getText().toString().length()
                        &&(tf_card_number.getText().toString().length()==4
                        ||tf_card_number.getText().toString().length()==9
                        ||tf_card_number.getText().toString().length()==14)){
                    tf_card_number.setText(tf_card_number.getText().toString().substring(0,tf_card_number.getText().toString().length()-1));
                    int pos = tf_card_number.getText().length();
                    tf_card_number.setSelection(pos);
                }
                count = tf_card_number.getText().toString().length();
            }
        });

        btn_save_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String card_holder_name = tf_card_holder_name.getText().toString();
                final String card_number = tf_card_number.getText().toString().replace(" ", "");;
                final String card_exp_month = tf_card_exp_month.getText().toString();
                final String card_exp_year = tf_card_exp_year.getText().toString();
                final String card_cvv = tf_card_cvv.getText().toString();

                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(statusCode == 200){
                            displayMessage("Registro Exitoso");
                            Intent paymentIntent = new Intent(AddPaymentActivity.this,PaymentActivity.class);
                            AddPaymentActivity.this.startActivity(paymentIntent);
                        }
                    }
                };


                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            String json;
                            switch(response.statusCode){
                                case 422:
                                    json = new String(response.data);
                                    System.out.println("STATUS 422!!!!!!!");
                                    System.out.println(json);
                                    json = trimMessage(json, "errors");
                                    if(json != null) displayMessage(json);
                                    break;
                                case 400:
                                    json = new String(response.data);
                                    System.out.println(json.length());
                                    System.out.println(json);
                                    if (json != null && json.length() > 0)
                                        json = trimMessage(json, "message");
                                    if(json != null && json != "")
                                        displayMessage(json);
                                    break;

                                default:
                                    json = new String(response.data);
                                    System.out.println(json.length());
                                    System.out.println(json);
                                    if (json != null && json.length() > 0)
                                        json = trimMessage(json, "message");
                                    if(json != null && json != "")
                                        displayMessage(json);
                                    break;
                            }
                        }

                    }

                };

                String card_brand;

                if (card_number.matches("^4[0-9]{6,}$")){
                    // Visa
                    card_brand = "visa";
                } else if (card_number.matches("^5[1-5][0-9]{5,}|222[1-9][0-9]{3,}|22[3-9][0-9]{4,}|2[3-6][0-9]{5,}|27[01][0-9]{4,}|2720[0-9]{3,}$")){
                    // Mastercard
                    card_brand = "mastercard";
                } else if (card_number.matches("^3[47][0-9]{5,}$")){
                    // American Express
                    card_brand = "american express";
                }
                else{
                    card_brand = "error";
                }

                Map<String, String> params = new HashMap<>();
                params.put("number",card_number);
                params.put("brand",card_brand);
                params.put("expiration_month",card_exp_month);
                params.put("expiration_year",card_exp_year);
                params.put("holder_name",card_holder_name);
                //params.put("cvv",card_cvv);

                Map<String,Map<String,String>> userParams = new HashMap<>();
                userParams.put("card",params);
                JSONObject userJSON = new JSONObject(userParams);
                displayMessage(userParams.toString());

                GymReinApp app = (GymReinApp) getApplicationContext();
                UserInformation userInfo = app.getUserInformation();

                AddPaymentRequest addPaymentRequest = new AddPaymentRequest(userJSON,userInfo.getToken(),responseListener,errorListener);
                RequestQueue reqQueue = Volley.newRequestQueue(AddPaymentActivity.this);
                reqQueue.add(addPaymentRequest);
            }
        });


    }

    public static void setStatusCode(int c){
        statusCode = c;
    }

    public String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    //Somewhere that has access to a context
    public void displayMessage(String toastString){
        Toast.makeText(AddPaymentActivity.this, toastString, Toast.LENGTH_LONG).show();
    }


}
