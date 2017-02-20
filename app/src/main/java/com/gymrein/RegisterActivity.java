package com.gymrein;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {

    public static int succStatusCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Create variables for all the text fields and buttons.

        final EditText tf_name = (EditText) findViewById(R.id.tf_name);
        final EditText tf_lastName = (EditText) findViewById(R.id.tf_lastName);
        final EditText tf_email = (EditText) findViewById(R.id.tf_email);
        final EditText tf_phone = (EditText) findViewById(R.id.tf_phone);
        final EditText tf_password = (EditText) findViewById(R.id.tf_password);
        final EditText tf_confirmPassword = (EditText) findViewById(R.id.tf_confirmPassword);
        final Button btn_register = (Button) findViewById(R.id.btn_register);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = tf_name.getText().toString();
                final String lastName = tf_lastName.getText().toString();
                final String email = tf_email.getText().toString();
                final String phone = tf_phone.getText().toString();
                final String password = tf_password.getText().toString();
                final String confirmPassword = tf_confirmPassword.getText().toString();

                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                            /*String jsonS = response.toString();
                            System.out.println("SUCCESSS!!!!");
                            System.out.println(jsonS);
                            System.out.println("STATUS CODE:");
                            System.out.println(succStatusCode);*/
                            if(succStatusCode == 200){
                                displayMessage("Registro Exitoso");
                                Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                                RegisterActivity.this.startActivity(loginIntent);
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
                            }
                        }

                    }

                };

                Map<String, String> params = new HashMap<>();
                params.put("name",name);
                params.put("name",name);
                params.put("lastname",lastName);
                params.put("email",email);
                params.put("password",password);
                params.put("password_confirmation",confirmPassword);
                params.put("phone",phone);

                Map<String,Map<String,String>> userParams = new HashMap<>();
                userParams.put("user",params);
                JSONObject userJSON = new JSONObject(userParams);

                RegisterRequest registerRequest = new RegisterRequest(userJSON,responseListener,errorListener);
                RequestQueue reqQueue = Volley.newRequestQueue(RegisterActivity.this);
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
        Toast.makeText(RegisterActivity.this, toastString, Toast.LENGTH_LONG).show();
    }

    public static void setSuccStatusCode(int s){
        succStatusCode = s;
    }

}
