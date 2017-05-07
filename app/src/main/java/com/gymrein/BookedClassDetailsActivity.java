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

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookedClassDetailsActivity extends AppCompatActivity {



    // GUI Elements
    private TextView tv_name;
    private TextView tv_desc;
    private TextView tv_instructor_name;
    private TextView tv_date;
    private TextView tv_time;
    private TextView tv_duration;
    private TextView tv_available;

    private ImageView iv_instructor_photo;
    private ImageButton ib_back_to_main;
    private Button btn_cancel_class;

    // Save User information
    private GymReinApp app;
    private UserInformation userInfo;
    private RequestQueue queue;
    private static int statusCode;

    private String class_id;
    private String reservation_id;
    private int coming_from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        // Get class_id
        class_id = getIntent().getStringExtra("id");
        reservation_id = getIntent().getStringExtra("resId");
        coming_from = getIntent().getIntExtra("coming",-1);

        // Initialize GUI Items
        tv_name = (TextView) findViewById(R.id.tv_class_name_cdtls);
        tv_desc = (TextView) findViewById(R.id.tv_class_desc_cdtls);
        tv_instructor_name = (TextView) findViewById(R.id.tv_instructor_name_cdtls);
        tv_date = (TextView) findViewById(R.id.tv_class_date_cdtls);
        tv_time = (TextView) findViewById(R.id.tv_class_time_cdtls);
        tv_duration = (TextView) findViewById(R.id.tv_class_duration_cdtls);
        tv_available = (TextView) findViewById(R.id.tv_class_available_cdtls);

        iv_instructor_photo = (ImageView) findViewById(R.id.iv_instructor_photo_csdtls);
        ib_back_to_main = (ImageButton) findViewById(R.id.btn_back_to_classes_by_date);
        btn_cancel_class = (Button) findViewById(R.id.btn_book_class_cdtls);

        btn_cancel_class.setText("Cancelar Reservación");


        // Get User Info
        app = (GymReinApp) getApplicationContext();
        userInfo = app.getUserInformation();
        queue = Volley.newRequestQueue(BookedClassDetailsActivity.this);

        // Set back button
        ib_back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coming_from == 0){
                    Intent myClassesIntent = new Intent(BookedClassDetailsActivity.this, MyClassesActivity.class);
                    BookedClassDetailsActivity.this.startActivity(myClassesIntent);
                }
                else {
                    Intent classesOfDayIntent = new Intent(BookedClassDetailsActivity.this, ClassesOfDayActivity.class);
                    BookedClassDetailsActivity.this.startActivity(classesOfDayIntent);
                }
            }
        });

        // Fill GUI items with class details
        getClassDetails();

        // Cancel Class
        btn_cancel_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClass();
                if (coming_from == 0){
                    Intent myClassesIntent = new Intent(BookedClassDetailsActivity.this, MyClassesActivity.class);
                    BookedClassDetailsActivity.this.startActivity(myClassesIntent);
                }
                else {
                    Intent classesByDateIntent = new Intent(BookedClassDetailsActivity.this, ClassesOfDayActivity.class);
                    BookedClassDetailsActivity.this.startActivity(classesByDateIntent);
                }
            }
        });

        /*tv_user_name.setText(userInfo.getName() + " " + userInfo.getLastname());
        tv_user_remaining_classes.setText("Clases Restantes: " + Integer.toString(userInfo.getClasses()));
        Picasso.with(this).load(userInfo.getAvatar()).into(img_user_avatar);*/
    }

    private void getClassDetails(){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    System.out.println(jsonResponse.toString());
                    System.out.println("Status:" + statusCode);

                    if(statusCode == 200 || statusCode == 304){
                        System.out.println("Success 200 o 304!!!!!!");

                        JSONObject event = jsonResponse.getJSONObject("event");
                        String name = event.getString("name");
                        String desc = event.getString("description");

                        JSONObject instructorObj = jsonResponse.getJSONObject("instructor");
                        String instructor_name = instructorObj.getString("name");
                        String instructor_avatar = instructorObj.getString("avatar_url");

                        String date = jsonResponse.getString("date");
                        String duration = jsonResponse.getString("duration");
                        String available = jsonResponse.getString("available");


                        Picasso.with(getApplicationContext()).load(instructor_avatar).into(iv_instructor_photo);
                        tv_name.setText(name);
                        tv_desc.setText(desc);
                        tv_duration.setText(duration.concat(" minutos"));
                        tv_available.setText(available.concat(" lugares disponibles"));
                        tv_instructor_name.setText(instructor_name);

                        SimpleDateFormat toFullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateStr = date.replace("T"," ").replace("Z","");
                        System.out.println("DATE: " + dateStr);
                        Date fullDate = null;
                        try {
                            fullDate = toFullDate.parse(dateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String shortTimeStr = sdf.format(fullDate);
                        System.out.println("TIME: " + shortTimeStr);
                        tv_time.setText(shortTimeStr);

                        String fDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(fullDate);
                        tv_date.setText(fDate);


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
        ClassDetailsRequest classDetailsRequest = new ClassDetailsRequest(class_id,userInfo.getToken(),responseListener,errorListener);
        queue.add(classDetailsRequest);
    }

    private void cancelClass(){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    System.out.println(jsonResponse.toString());
                    System.out.println("Status:" + statusCode);

                    if(statusCode == 200 || statusCode == 304){
                        System.out.println("Success 200 o 304!!!!!!");
                        if (jsonResponse.getBoolean("success")){
                            displayMessage("La clase se canceló exitósamente");
                        }
                        else{
                            displayMessage(jsonResponse.getString("error"));
                        }

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

        CancelClassRequest cancelClassRequest = new CancelClassRequest(reservation_id,userInfo.getToken(),responseListener,errorListener);
        queue.add(cancelClassRequest);
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
        Toast.makeText(BookedClassDetailsActivity.this, toastString, Toast.LENGTH_LONG).show();
    }

    public static void setStatusCode(int c){
        statusCode = c;
    }
}
