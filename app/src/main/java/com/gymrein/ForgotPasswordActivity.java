package com.gymrein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private static int succStatusCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        final ImageButton btn_back_to_login = (ImageButton) findViewById(R.id.btn_back_to_login_forgotPsswd);
        final Button btn_new_psswd = (Button) findViewById(R.id.btn_new_password);
        final EditText tf_email = (EditText) findViewById(R.id.tf_email_forgotPsswd);

        btn_back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                ForgotPasswordActivity.this.startActivity(loginIntent);
            }
        });

        btn_new_psswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tf_email.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("SUCCESSSS");
                        System.out.println(response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if(succStatusCode == 200){
                                Boolean success = jsonResponse.getBoolean("success");
                                String message = "";
                                if(success)
                                    message = jsonResponse.getString("message");
                                else
                                    message = jsonResponse.getString("error");

                                displayMessage(message);
                                Intent startingPageIntent = new Intent(ForgotPasswordActivity.this,StartingPageActivity.class);
                                ForgotPasswordActivity.this.startActivity(startingPageIntent);

                            }else{
                                displayMessage("Error al enviar nueva contraseña.");
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

                ForgotPasswordRequest registerRequest = new ForgotPasswordRequest(email,responseListener,errorListener);
                RequestQueue reqQueue = Volley.newRequestQueue(ForgotPasswordActivity.this);
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
        Toast.makeText(ForgotPasswordActivity.this, toastString, Toast.LENGTH_LONG).show();
    }

    public static void setSuccStatusCode(int s){
        succStatusCode = s;
    }
}
