package com.gymrein;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static int succStatusCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create variables for all the text fields and buttons.
        // final => because variables won't change

        final EditText tf_email = (EditText) findViewById(R.id.tf_email);
        final EditText tf_password = (EditText) findViewById(R.id.tf_password);
        final Button btn_login = (Button) findViewById(R.id.btn_login);
        final TextView tv_linkRegister =(TextView) findViewById(R.id.tv_linkRegister);

        tv_linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = tf_email.getText().toString();
                final String password = tf_password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("SUCCESSSS");
                        System.out.println(response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean successFlag = jsonResponse.getBoolean("success");

                            if(successFlag){
                                JSONObject userDetails = new JSONObject(jsonResponse.getString("user"));
                                String nameResponse = userDetails.getString("name");
                                String lastNameResponse = userDetails.getString("lastname");
                                String emailResponse = userDetails.getString("email");
                                String phoneResponse = userDetails.getString("phone");
                                //int classesResponse = userDetails.getInt("available_classes");
                                String tokenResponse = userDetails.getString("access_token");

                                Intent userAreaIntent = new Intent(LoginActivity.this,UserAreaActivity.class);
                                userAreaIntent.putExtra("name",nameResponse);
                                userAreaIntent.putExtra("lastname",lastNameResponse);
                                userAreaIntent.putExtra("email",emailResponse);
                                userAreaIntent.putExtra("phone",phoneResponse);
                               // userAreaIntent.putExtra("classes",classesResponse);
                                userAreaIntent.putExtra("token",tokenResponse);

                                LoginActivity.this.startActivity(userAreaIntent);

                            }else{
                                displayMessage("Inicio de Sesión Fallido.");
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
                                case 422:
                                    System.out.println("STATUS 422!!!!!!!");
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

                LoginRequest registerRequest = new LoginRequest(email,password,responseListener,errorListener);
                RequestQueue reqQueue = Volley.newRequestQueue(LoginActivity.this);
                reqQueue.add(registerRequest);
            }
        });
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
        Toast.makeText(LoginActivity.this, toastString, Toast.LENGTH_LONG).show();
    }

    public static void setSuccStatusCode(int s){
        succStatusCode = s;
    }
}