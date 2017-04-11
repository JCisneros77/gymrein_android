package com.gymrein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PaymentActivity extends AppCompatActivity {
    private ListView lv_payment_methods;
    private static CustomAdapter payment_adapter;
    private ArrayList<SideMenuItemModel> payment_dataset;
    private ImageButton btn_back_to_main;
    private static int statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        String deleteMessage = getIntent().getStringExtra("message");
        if (deleteMessage != null)
            displayMessage(deleteMessage);

        lv_payment_methods = (ListView) findViewById(R.id.lv_payment_methods);
        btn_back_to_main = (ImageButton) findViewById(R.id.btn_back_to_main);

        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    System.out.println(jsonResponse.toString());
                    System.out.println("Status:" + statusCode);

                    if(statusCode == 200 || statusCode == 304){
                        System.out.println("Success 200 o 304!!!!!!");
                        payment_dataset = new ArrayList<>();
                        for(int i = 0; i < jsonResponse.length(); ++i){
                            JSONObject jsonObj = jsonResponse.getJSONObject(i);
                            int image = -1;
                            if(jsonObj.getString("brand").equals("visa")){
                                image = R.mipmap.ic_visa;
                            }
                            else if(jsonObj.getString("brand").equals("mastercard")){
                                image = R.mipmap.ic_mastercard;
                            }
                            else if(jsonObj.getString("brand").equals("amex")){
                                image = R.mipmap.ic_amex;
                            }
                            payment_dataset.add(new SideMenuItemModel(jsonObj.getString("number"),image,jsonObj.getString("holder_name"),
                                    jsonObj.getString("brand"),jsonObj.getString("number"),jsonObj.getString("expiration_month"),
                                    jsonObj.getString("expiration_year"),jsonObj.getString("id")));
                        }

                        payment_dataset.add(new SideMenuItemModel("Agregar Tarjeta",R.mipmap.ic_add));
                        payment_adapter = new CustomAdapter(payment_dataset,getApplicationContext(),R.layout.credit_card_item,1);

                        lv_payment_methods.setAdapter(payment_adapter);

                    }
                    else{
                        System.out.println("Error. Status: " + statusCode);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    String json = new String(response.data);
                    System.out.println(json);
                    switch(response.statusCode){
                        case 401:
                            System.out.println("STATUS 401!!!!!!!");
                            System.out.println(json);
                            json = trimMessage(json, "errors");
                            if(json != null) displayMessage(json);
                            break;
                        case 400:
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

        lv_payment_methods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (payment_dataset.get(position).getName()){
                    case "Agregar Tarjeta":
                    {
                        Intent addPaymentIntent = new Intent(PaymentActivity.this,AddPaymentActivity.class);
                        PaymentActivity.this.startActivity(addPaymentIntent);

                        break;
                    }
                    default:{
                        Intent paymentDetailIntent = new Intent(PaymentActivity.this,PaymentDetailActivity.class);
                        paymentDetailIntent.putExtra("name",payment_dataset.get(position).getCardName());
                        paymentDetailIntent.putExtra("brand",payment_dataset.get(position).getCardBrand());
                        paymentDetailIntent.putExtra("exp_month",payment_dataset.get(position).getExpMonth());
                        paymentDetailIntent.putExtra("exp_year",payment_dataset.get(position).getExpYear());
                        paymentDetailIntent.putExtra("number",payment_dataset.get(position).getCard_number());
                        paymentDetailIntent.putExtra("id",payment_dataset.get(position).getId());
                        PaymentActivity.this.startActivity(paymentDetailIntent);
                        break;
                    }


                }
            }
        });
        GymReinApp app = (GymReinApp) getApplicationContext();
        UserInformation userInfo = app.getUserInformation();


        PaymentRequest paymentRequest = new PaymentRequest(userInfo.getToken(),responseListener,errorListener);
        RequestQueue queue = Volley.newRequestQueue(PaymentActivity.this);
        queue.add(paymentRequest);


        btn_back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classesOfDayIntent = new Intent(PaymentActivity.this,ClassesOfDayActivity.class);
                PaymentActivity.this.startActivity(classesOfDayIntent);
            }
        });

    }

    public static int getStatusCode(){
        return statusCode;
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
        Toast.makeText(PaymentActivity.this, toastString, Toast.LENGTH_LONG).show();
    }
}
