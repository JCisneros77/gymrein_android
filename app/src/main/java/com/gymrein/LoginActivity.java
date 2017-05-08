package com.gymrein;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static int succStatusCode = 0;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Getting the registration token from the intent
                    token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    //Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if(ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());


                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }


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
                                String id = Integer.toString(userDetails.getInt("id"));
                                String lastNameResponse = userDetails.getString("lastname");
                                String emailResponse = userDetails.getString("email");
                                String phoneResponse = userDetails.getString("phone");
                                int classesResponse = userDetails.getInt("available_classes");
                                String tokenResponse = userDetails.getString("access_token");
                                String avatar_url = userDetails.getString("avatar_url");
                                String dob = userDetails.getString("birth");

                                UserInformation userInfo = new UserInformation(id,nameResponse,dob,emailResponse,lastNameResponse,phoneResponse,classesResponse,tokenResponse,avatar_url);
                                GymReinApp app = (GymReinApp) getApplicationContext();
                                app.setUserInformation(userInfo);

                                Intent classesOfDayIntent = new Intent(LoginActivity.this,ClassesOfDayActivity.class);
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

                LoginRequest registerRequest = new LoginRequest(email,password,token,responseListener,errorListener);
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

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
