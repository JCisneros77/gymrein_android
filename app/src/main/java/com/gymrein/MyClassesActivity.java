package com.gymrein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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

public class MyClassesActivity extends AppCompatActivity {

    private ListView lv_classes;
    private ListView lv_waiting_list;
    private ArrayList<ClassItemModel> class_dataset;
    private ArrayList<ClassItemModel> waiting_list_dataset;
    private static ClassItemAdapter class_adapter;
    private static ClassItemAdapter waiting_list_adapter;

    private ImageButton btn_back_to_main;

    // Save User information
    private GymReinApp app;
    private UserInformation userInfo;
    private RequestQueue queue;
    private static int statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);

        lv_classes = (ListView) findViewById(R.id.lv_classes_MyClass);
        lv_waiting_list = (ListView) findViewById(R.id.lv_waiting_list_MyClass);
        btn_back_to_main = (ImageButton) findViewById(R.id.btn_back_to_main_myClass);

        // Get User Info
        app = (GymReinApp) getApplicationContext();
        userInfo = app.getUserInformation();
        queue = Volley.newRequestQueue(MyClassesActivity.this);

        // Back to main
        btn_back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classesOfDayIntent = new Intent(MyClassesActivity.this,ClassesOfDayActivity.class);
                MyClassesActivity.this.startActivity(classesOfDayIntent);
            }
        });


        // Future Classes
        lv_classes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent classDetailsIntent = new Intent(MyClassesActivity.this,BookedClassDetailsActivity.class);
                //displayMessage("ID: " + class_dataset.get(position).getId() + "   Name: " + class_dataset.get(position).getEvent_name());
                classDetailsIntent.putExtra("id",class_dataset.get(position).getId());
                classDetailsIntent.putExtra("resId",class_dataset.get(position).getReservation_id());
                classDetailsIntent.putExtra("coming",0);
                finish();
                MyClassesActivity.this.startActivity(classDetailsIntent);

            }
        });

        // Waiting List
        lv_waiting_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent classDetailsIntent = new Intent(MyClassesActivity.this,ClassInWaitingListActivity.class);
                //displayMessage("ID: " + class_dataset.get(position).getId() + "   Name: " + class_dataset.get(position).getEvent_name());
                classDetailsIntent.putExtra("id",waiting_list_dataset.get(position).getId());
                classDetailsIntent.putExtra("resId",waiting_list_dataset.get(position).getReservation_id());
                finish();
                MyClassesActivity.this.startActivity(classDetailsIntent);

            }
        });

        lv_classes.setAdapter(null);
        lv_waiting_list.setAdapter(null);

        // Fill Classes
        getClasses();

        // Fill Waiting List
        getWaitingList();
    }

    private void getClasses(){
        class_dataset = new ArrayList<>();
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    System.out.println(jsonResponse.toString());
                    System.out.println("Status COD:" + statusCode);

                    if(statusCode == 200 || statusCode == 304){
                        System.out.println("Success 200 o 304!!!!!!");
                        for(int i = 0; i < jsonResponse.length(); ++i){
                            JSONObject classInfo = jsonResponse.getJSONObject(i);
                            String id = classInfo.getString("class_date_id");
                            boolean assisted = classInfo.getBoolean("assisted");
                            String resId = classInfo.getString("id");

                            //boolean assisted = jsonObj.getBoolean("assisted");

                            JSONObject class_date = classInfo.getJSONObject("class_date");
                            //boolean assisted = jsonObj.getBoolean("assisted");
                            String event_id = class_date.getString("event_id");
                            String instructor_id = class_date.getString("instructor_id");
                            String location_id = class_date.getString("location_id");
                            String date = class_date.getString("date");
                            String room = class_date.getString("room");
                            String duration = class_date.getString("duration");
                            String finish = class_date.getString("finish");
                            String limit = class_date.getString("limit");
                            int available = class_date.getInt("available");
                            String logo_url = class_date.getString("logo_url");

                            JSONObject event = class_date.getJSONObject("event");
                            String event_name = event.getString("name");
                            String event_description = event.getString("description");

                            JSONObject location = class_date.getJSONObject("location");
                            String location_name = location.getString("name");
                            String location_address = location.getString("address");
                            // Get all packages
                            ClassItemModel newClass = new ClassItemModel(resId,id,assisted,event_id,instructor_id,location_id,date,room,duration,finish,limit,available,logo_url,event_name
                                    ,event_description,location_name,location_address);
                            class_dataset.add(newClass);
                        }
                        class_adapter = new ClassItemAdapter(class_dataset,getApplicationContext(),R.layout.class_of_day_item);
                        lv_classes.setAdapter(class_adapter);


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


        FutureClassesRequest futureClassesRequest = new FutureClassesRequest(userInfo.getToken(),"reservations",responseListener,errorListener);
        futureClassesRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(futureClassesRequest);
    }

    private void getWaitingList(){
        waiting_list_dataset = new ArrayList<>();
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    System.out.println(jsonResponse.toString());
                    System.out.println("Status WAITING LIST:" + statusCode);

                    if(statusCode == 200 || statusCode == 304){
                        System.out.println("Success 200 o 304!!!!!!");
                        for(int i = 0; i < jsonResponse.length(); ++i){
                            JSONObject classInfo = jsonResponse.getJSONObject(i);
                            String id = classInfo.getString("class_date_id");
                            boolean assisted = false;
                            String resId = classInfo.getString("id");

                            //boolean assisted = jsonObj.getBoolean("assisted");

                            JSONObject class_date = classInfo.getJSONObject("class_date");
                            //boolean assisted = jsonObj.getBoolean("assisted");
                            String event_id = class_date.getString("event_id");
                            String instructor_id = class_date.getString("instructor_id");
                            String location_id = class_date.getString("location_id");
                            String date = class_date.getString("date");
                            String room = class_date.getString("room");
                            String duration = class_date.getString("duration");
                            String finish = class_date.getString("finish");
                            String limit = class_date.getString("limit");
                            int available = class_date.getInt("available");
                            String logo_url = class_date.getString("logo_url");

                            JSONObject event = class_date.getJSONObject("event");
                            String event_name = event.getString("name");
                            String event_description = event.getString("description");

                            JSONObject location = class_date.getJSONObject("location");
                            String location_name = location.getString("name");
                            String location_address = location.getString("address");
                            // Get all packages
                            ClassItemModel newClass = new ClassItemModel(resId,id,assisted,event_id,instructor_id,location_id,date,room,duration,finish,limit,available,logo_url,event_name
                                    ,event_description,location_name,location_address);
                            waiting_list_dataset.add(newClass);
                        }
                        waiting_list_adapter = new ClassItemAdapter(waiting_list_dataset,getApplicationContext(),R.layout.class_of_day_item);
                        lv_waiting_list.setAdapter(waiting_list_adapter);


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


        FutureClassesRequest futureClassesRequest = new FutureClassesRequest(userInfo.getToken(),"waiting_lists",responseListener,errorListener);
        futureClassesRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(futureClassesRequest);
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
        Toast.makeText(MyClassesActivity.this, toastString, Toast.LENGTH_LONG).show();
    }
}
