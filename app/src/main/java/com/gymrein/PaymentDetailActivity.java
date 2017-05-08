package com.gymrein;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetailActivity extends AppCompatActivity {
    private static int statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        
        final TextView tv_card_holder_name = (TextView) findViewById(R.id.tv_card_holder_name);
        final TextView tv_card_number = (TextView) findViewById(R.id.tv_card_number);
        final TextView tv_card_exp_date = (TextView) findViewById(R.id.tv_exp_date);
        final ImageButton btn_back_to_payment = (ImageButton) findViewById(R.id.btn_back_to_payment_list);
        final Button btn_delete_payment = (Button) findViewById(R.id.btn_delete_payment);
        

        final String card_name = getIntent().getStringExtra("name");
        final String card_brand = getIntent().getStringExtra("brand");
        final String card_number = getIntent().getStringExtra("number");
        final String exp_month = getIntent().getStringExtra("exp_month");
        final String exp_year = getIntent().getStringExtra("exp_year");
        final String id = getIntent().getStringExtra("id");

        TextView tv_title = (TextView) findViewById(R.id.tv_payment_details);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Graduate-Regular.ttf");
        tv_title.setTypeface(custom_font);

        
        tv_card_holder_name.setText(card_name);
        tv_card_number.setText("**** " + card_number);
        tv_card_exp_date.setText(exp_month + "/" + exp_year);

        btn_back_to_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent paymentListIntent = new Intent(PaymentDetailActivity.this,PaymentActivity.class);
                PaymentDetailActivity.this.startActivity(paymentListIntent);
            }
        });

        btn_delete_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PaymentDetailActivity.this)
                        .setTitle("Eliminar tarjeta")
                        .setMessage("¿Quiere eliminar esta tarjeta?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                Response.Listener<String> responseListener = new Response.Listener<String>(){
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            System.out.println(jsonResponse.toString());
                                            System.out.println("Status:" + statusCode);

                                            if(statusCode == 200){
                                                System.out.println("STATUS 200. Borrado successful!");
                                                Intent paymentListIntent = new Intent(PaymentDetailActivity.this,PaymentActivity.class);
                                                paymentListIntent.putExtra("message","Se eliminó la tarjeta correctamente.");
                                                PaymentDetailActivity.this.startActivity(paymentListIntent);

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


                                GymReinApp app = (GymReinApp) getApplicationContext();
                                UserInformation userInfo = app.getUserInformation();

                                DeletePaymentRequest deletePaymentRequest = new DeletePaymentRequest(id,userInfo.getToken(),responseListener,errorListener);
                                RequestQueue queue = Volley.newRequestQueue(PaymentDetailActivity.this);
                                queue.add(deletePaymentRequest);
                            }})
                        .setNegativeButton("Cancelar", null).show();
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
        Toast.makeText(PaymentDetailActivity.this, toastString, Toast.LENGTH_LONG).show();
    }


}
