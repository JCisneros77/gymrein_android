package com.gymrein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tv_name;
    private TextView tv_lastname;
    private TextView tv_mail;
    private TextView tv_phone;
    private TextView tv_date_of_birth;
    private TextView tv_gender;

    private ImageView iv_user_photo;
    private ImageButton btn_back_to_main;
    private Button btn_edit_profile;

    private String id;

    // Save User information
    private GymReinApp app;
    private UserInformation userInfo;
    private RequestQueue queue;
    private static int statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tv_name = (TextView) findViewById(R.id.tv_name_userProf);
        tv_lastname = (TextView) findViewById(R.id.tv_lastName_userProf);
        tv_mail = (TextView) findViewById(R.id.tv_email_userProf);
        tv_phone = (TextView) findViewById(R.id.tv_phone_userProf);
        tv_date_of_birth = (TextView) findViewById(R.id.tv_date_of_birth_userProf);
        tv_gender = (TextView) findViewById(R.id.tv_gender_userProf);

        iv_user_photo = (ImageView) findViewById(R.id.btn_profile_picture_userProf);
        btn_back_to_main = (ImageButton) findViewById(R.id.btn_back_to_starting_page_userProf);
        btn_edit_profile = (Button) findViewById(R.id.btn_edit_profile_userProf);

        // Get User Info
        app = (GymReinApp) getApplicationContext();
        userInfo = app.getUserInformation();
        queue = Volley.newRequestQueue(UserProfileActivity.this);

        // Back to starting page
        btn_back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainPageIntent = new Intent(UserProfileActivity.this, ClassesOfDayActivity.class);
                UserProfileActivity.this.startActivity(mainPageIntent);
            }
        });

        // To edit profile
        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainPageIntent = new Intent(UserProfileActivity.this, UserEditProfileActivity.class);
                UserProfileActivity.this.startActivity(mainPageIntent);
            }
        });


        getUserInfo();
    }

    private void getUserInfo(){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    System.out.println(jsonResponse.toString());
                    System.out.println("Status:" + statusCode);

                    if(statusCode == 200 || statusCode == 304){
                        String name = jsonResponse.getString("name");
                        String lastname = jsonResponse.getString("lastname");
                        String email = jsonResponse.getString("email");
                        String phone = jsonResponse.getString("phone");
                        String dob = jsonResponse.getString("birth");
                        String gender = jsonResponse.getString("sex");
                        String avatar_url = jsonResponse.getString("avatar_url");


                        Picasso.with(getApplicationContext()).load(avatar_url).into(iv_user_photo);

                        tv_name.setText(name);
                        tv_lastname.setText(lastname);
                        tv_mail.setText(email);
                        tv_phone.setText(phone);
                        tv_date_of_birth.setText(dob);
                        tv_gender.setText(gender);

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


        UserDetailsRequest userDetailsRequest = new UserDetailsRequest(userInfo.getId(),userInfo.getToken(),responseListener,errorListener);
        userDetailsRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(userDetailsRequest);
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
        Toast.makeText(UserProfileActivity.this, toastString, Toast.LENGTH_LONG).show();
    }

    public static void setStatusCode(int c){
        statusCode = c;
    }
}
