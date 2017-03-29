package com.gymrein;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        final TextView tv_linkForgotPassword =(TextView) findViewById(R.id.tv_linkForgotPassword);
        final ImageButton ib_back_to_starting = (ImageButton) findViewById(R.id.btn_back_to_starting_page_login);

        tf_email.setText("cesar.millan06@gmail.com");
        tf_password.setText("12345678");

        ib_back_to_starting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startingPageIntent = new Intent(LoginActivity.this,StartingPageActivity.class);
                LoginActivity.this.startActivity(startingPageIntent);
            }
        });

        tv_linkForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPassIntent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                LoginActivity.this.startActivity(forgotPassIntent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = tf_email.getText().toString();
                final String password = tf_password.getText().toString();
                System.out.println("hola");
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

                                UserInformation userInfo = new UserInformation(nameResponse,emailResponse,lastNameResponse,phoneResponse,0,tokenResponse);
                                GymReinApp app = (GymReinApp) getApplicationContext();
                                app.setUserInformation(userInfo);

                                Intent classesOfDayIntent = new Intent(LoginActivity.this,ClassesOfDayActivity.class);
                                /*classesOfDayIntent.putExtra("name",nameResponse);
                                classesOfDayIntent.putExtra("lastname",lastNameResponse);
                                classesOfDayIntent.putExtra("email",emailResponse);
                                classesOfDayIntent.putExtra("phone",phoneResponse);
                               // userAreaIntent.putExtra("classes",classesResponse);
                                classesOfDayIntent.putExtra("token",tokenResponse);*/

                                LoginActivity.this.startActivity(classesOfDayIntent);

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
