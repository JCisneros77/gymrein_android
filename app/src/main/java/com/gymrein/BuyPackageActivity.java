package com.gymrein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuyPackageActivity extends AppCompatActivity {

    private TextView tv_package_classes;
    private TextView tv_package_name;
    private TextView tv_total_amount;
    private ImageButton btn_back_to_main;
    private Button btn_buy_package;
    private EditText tb_promo_code;
    private Spinner sp_credit_cards;
    private static int statusCode;
    private String promo_id;
    private boolean promo_id_flag;
    private List<String> credit_cards;
    private ArrayList<SideMenuItemModel> payment_dataset;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_package);

        tv_package_classes = (TextView) findViewById(R.id.tv_package_classes_bp);
        tv_package_name = (TextView) findViewById(R.id.tv_package_name_bp);
        tv_total_amount = (TextView) findViewById(R.id.tv_total_amount);
        btn_back_to_main = (ImageButton) findViewById(R.id.btn_back_to_main_buy_package);
        btn_buy_package = (Button) findViewById(R.id.btn_buy_package);
        tb_promo_code = (EditText) findViewById(R.id.tb_promo_code_bp);
        sp_credit_cards = (Spinner) findViewById(R.id.sp_credit_card_bp);

        final String package_name = getIntent().getStringExtra("name");
        final int package_price = getIntent().getIntExtra("price",0);
        final int package_classes = getIntent().getIntExtra("classes",0);
        final String package_id = getIntent().getStringExtra("id");
        credit_cards = new ArrayList<String>();

        promo_id_flag = false;

        queue = Volley.newRequestQueue(BuyPackageActivity.this);
        // User Info
        final GymReinApp app = (GymReinApp) getApplicationContext();
        final UserInformation userInfo = app.getUserInformation();

        // Back to main
        btn_back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classesOfDayIntent = new Intent(BuyPackageActivity.this,ClassesOfDayActivity.class);
                BuyPackageActivity.this.startActivity(classesOfDayIntent);
            }
        });

        // Set package info
        tv_package_classes.setText("Clases: " + Integer.toString(package_classes));
        tv_total_amount.setText("$" + Integer.toString(package_price));
        tv_package_name.setText(package_name);

        // Set credit cards
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    System.out.println(jsonResponse.toString());
                    System.out.println("Status CC:" + statusCode);

                    if(statusCode == 200){
                        System.out.println("Success 200 Credit Cards");
                        payment_dataset = new ArrayList<>();

                        for(int i = 0; i < jsonResponse.length(); ++i){
                            JSONObject jsonObj = jsonResponse.getJSONObject(i);
                            System.out.println(jsonObj.toString());
                            credit_cards.add(jsonObj.getString("number"));
                            System.out.println(credit_cards);
                            payment_dataset.add(new SideMenuItemModel(jsonObj.getString("number"),0,jsonObj.getString("holder_name"),
                                    jsonObj.getString("brand"),jsonObj.getString("number"),jsonObj.getString("expiration_month"),
                                    jsonObj.getString("expiration_year"),jsonObj.getString("id")));
                        }

                        // Gender Spinner
                        ArrayAdapter<String> creditCardAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,credit_cards);
                        creditCardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_credit_cards.setAdapter(creditCardAdapter);
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

        PaymentPackageRequest paymentRequest = new PaymentPackageRequest(userInfo.getToken(),responseListener,errorListener);
        paymentRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(paymentRequest);

        // Set promo code listener
        tb_promo_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                System.out.println(jsonObj.toString());
                                System.out.println("Status Promo:" + statusCode);

                                if (statusCode == 200) {
                                    System.out.println("Success 200 PROMO");
                                    if (jsonObj.has("errors")) {
                                        promo_id_flag = false;
                                        tv_total_amount.setText("$" + Integer.toString(package_price));
                                        displayMessage("Código de promoción invalido.");
                                    } else {
                                        promo_id_flag = true;
                                        promo_id = jsonObj.getString("id");
                                        String promo_code = jsonObj.getString("code");
                                        String promo_type = jsonObj.getString("promotion_type");
                                        String promo_expiration = jsonObj.getString("expiration");
                                        int promo_amount = jsonObj.getInt("amount");
                                        // Adjust the amount depending on the promotion
                                        if (promo_type.equals("percentage")) {
                                            tv_total_amount.setText("$" + Integer.toString(Math.round((float) package_price * (1 - ((float)promo_amount / 100)))));
                                        } else if (promo_type.equals("quantity")) {
                                            tv_total_amount.setText("$" + Integer.toString(package_price - promo_amount));
                                        } else {
                                            displayMessage("Unknown error.");
                                        }

                                        displayMessage("Promoción aplicada");
                                    }
                                    //}
                                } else {
                                    tv_total_amount.setText("$" + Integer.toString(package_price));
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
                            if (response != null && response.data != null) {
                                String json = new String(response.data);
                                System.out.println(json);
                                switch (response.statusCode) {
                                    case 401:
                                        System.out.println("STATUS 401!!!!!!!");
                                        System.out.println(json);
                                        json = trimMessage(json, "errors");
                                        if (json != null) displayMessage(json);
                                        break;
                                    case 400:
                                        System.out.println(json.length());
                                        System.out.println(json);
                                        if (json != null && json.length() > 0)
                                            json = trimMessage(json, "message");
                                        if (json != null && json != "")
                                            displayMessage(json);
                                        break;
                                }
                            }
                        }
                    };

                    String code = tb_promo_code.getText().toString();
                    System.out.println(code);
                    PromotionCodeRequest promoCodeRequest = new PromotionCodeRequest(code, userInfo.getToken(), responseListener, errorListener);
                    promoCodeRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(promoCodeRequest);
                }
            }
        });
        // Buy package
        btn_buy_package.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            System.out.println(jsonObj.toString());
                            System.out.println("Status Buy:" + statusCode);

                            if (statusCode == 200) {
                                System.out.println("Success 200 BUY");
                                displayMessage("Paquete comprado con exito.");
                                JSONObject userObj = jsonObj.getJSONObject("user");
                                userInfo.setClasses(userObj.getInt("available_classes"));

                                Intent classesOfDayIntent = new Intent(BuyPackageActivity.this,ClassesOfDayActivity.class);
                                BuyPackageActivity.this.startActivity(classesOfDayIntent);

                            } else {
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
                        if (response != null && response.data != null) {
                            String json = new String(response.data);
                            System.out.println(json);
                            switch (response.statusCode) {
                                case 401:
                                    System.out.println("STATUS 401!!!!!!!");
                                    System.out.println(json);
                                    json = trimMessage(json, "errors");
                                    if (json != null) displayMessage(json);
                                    break;
                                case 400:
                                    System.out.println(json.length());
                                    System.out.println(json);
                                    if (json != null && json.length() > 0)
                                        json = trimMessage(json, "message");
                                    if (json != null && json != "")
                                        displayMessage(json);
                                    break;
                            }
                        }
                    }
                };

                String card_id = payment_dataset.get(sp_credit_cards.getSelectedItemPosition()).getId();
                BuyPackageRequest buyPackageRequest;
                System.out.println("FLAG:" + promo_id_flag);
                System.out.println("Package:" + package_id);
                System.out.println("Card:" + card_id);
                System.out.println("PROMO CODE:" + promo_id);
                if (promo_id_flag)
                    buyPackageRequest = new BuyPackageRequest(userInfo.getToken(),package_id,card_id,promo_id,responseListener, errorListener);
                else
                    buyPackageRequest = new BuyPackageRequest(userInfo.getToken(),package_id,card_id, responseListener, errorListener);

                buyPackageRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(buyPackageRequest);
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
        Toast.makeText(BuyPackageActivity.this, toastString, Toast.LENGTH_LONG).show();
    }

    private void setPromoCode(String id){ promo_id = id; }
}
