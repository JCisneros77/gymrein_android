package com.gymrein;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class BookClassActivity extends AppCompatActivity {

    private CalendarView cv_calendar;
    private ListView lv_classes_day;
    private ArrayList<ClassItemModel> class_dataset;
    private static ClassItemAdapter class_adapter;

    // Save User information
    private GymReinApp app;
    private UserInformation userInfo;
    private RequestQueue queue;
    private static int statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_class);

        lv_classes_day = (ListView) findViewById(R.id.lv_classes_day_bc);
        cv_calendar = (CalendarView) findViewById(R.id.cv_calendar_bc);
        final ImageButton btn_back_to_main = (ImageButton) findViewById(R.id.btn_back_to_main_bc);

        TextView tv_title = (TextView) findViewById(R.id.tv_title_bc);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Graduate-Regular.ttf");
        tv_title.setTypeface(custom_font);

        // Back to main
        btn_back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classesOfDayIntent = new Intent(BookClassActivity.this,ClassesOfDayActivity.class);
                BookClassActivity.this.startActivity(classesOfDayIntent);
            }
        });


        // Get User Info
        app = (GymReinApp) getApplicationContext();
        userInfo = app.getUserInformation();
        queue = Volley.newRequestQueue(BookClassActivity.this);


        // Get classes for today
        Calendar now = Calendar.getInstance();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date today = now.getTime();
        final String nowDate = formatter.format(now.getTime());
        String[] separateCurrentDate = nowDate.split("-");
        final String yearStr = separateCurrentDate[0];
        final String monthStr = separateCurrentDate[1];
        final String dayStr = separateCurrentDate[2];
        final int currentYear = Integer.parseInt(yearStr);
        final int currentMonth = Integer.parseInt(monthStr);
        final int currentDay = Integer.parseInt(dayStr);

        addClassesOfDay(currentYear,currentMonth-1,currentDay);

        // Get classes of day picked in calendar
        cv_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                lv_classes_day.setAdapter(null);
                int today = Integer.parseInt(yearStr.concat(monthStr).concat(dayStr));
                String pickedMonth = "";
                String pickedDay = "";
                month+=1;
                System.out.println(month);
                if (month < 10)
                    pickedMonth = "0".concat(Integer.toString(month));
                else
                    pickedMonth = Integer.toString(month);

                if (dayOfMonth < 10)
                    pickedDay = "0".concat(Integer.toString(dayOfMonth));
                else
                    pickedDay = Integer.toString(dayOfMonth);


                int picked = Integer.parseInt(Integer.toString(year).concat(pickedMonth).concat(pickedDay));

                if (picked >= today)
                    addClassesOfDay(year,month-1,dayOfMonth);
                else {
                    addUserBoughtClasses(year, month, dayOfMonth);
                }
            }
        });

        lv_classes_day.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent classDetailsIntent = new Intent(BookClassActivity.this,ClassDetailsActivity.class);
                classDetailsIntent.putExtra("id",class_dataset.get(position).getId());
                classDetailsIntent.putExtra("available",class_dataset.get(position).getAvailable());
                BookClassActivity.this.startActivity(classDetailsIntent);
            }
        });

    }

    private void addClassesOfDay(int year, int month, int dayOfMonth){
        class_dataset = new ArrayList<>();
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    System.out.println(jsonResponse.toString());
                    System.out.println("Status FUTURE CLASSES:" + statusCode);

                    if(statusCode == 200 || statusCode == 304){
                        System.out.println("Success 200 o 304!!!!!!");
                        for(int i = 0; i < jsonResponse.length(); ++i){
                            JSONObject class_date = jsonResponse.getJSONObject(i);
                            String id = class_date.getString("id");
                            //boolean assisted = jsonObj.getBoolean("assisted");
                            String event_id = class_date.getString("event_id");
                            String instructor_id = class_date.getString("instructor_id");
                            String location_id = class_date.getString("location_id");
                            String date = class_date.getString("date");
                            String room = class_date.getString("room");
                            String duration = class_date.getString("duration");
                            //String finish = class_date.getString("finish");
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
                            ClassItemModel newClass = new ClassItemModel(id,false,event_id,instructor_id,location_id,date,room,duration,"0",limit,available,logo_url,event_name
                                    ,event_description,location_name,location_address);
                            class_dataset.add(newClass);
                        }
                        class_adapter = new ClassItemAdapter(class_dataset,getApplicationContext(),R.layout.class_of_day_item,false);
                        lv_classes_day.setAdapter(class_adapter);


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


        String fDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new GregorianCalendar(year, month, dayOfMonth).getTime());
        BookClassRequest bookClassRequest = new BookClassRequest(userInfo.getToken(),fDate,responseListener,errorListener);
        bookClassRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(bookClassRequest);
    }

    private void addUserBoughtClasses(int year, int month, int dayOfMonth){
        class_dataset = new ArrayList<>();
        //
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    System.out.println("CLASSEEESSS DEL DIAAA!!!!!!");
                    System.out.println(jsonResponse.toString());
                    System.out.println("END CLASSEESSS DEL DIAAA!!!!!!");
                    System.out.println("Status COD:" + statusCode);

                    if(statusCode == 200 || statusCode == 304){
                        System.out.println("Success 200 o 304!!!!!!");
                        for(int i = 0; i < jsonResponse.length(); ++i){
                            JSONObject jsonObj = jsonResponse.getJSONObject(i);
                            String id = jsonObj.getString("class_date_id");
                            boolean assisted = jsonObj.getBoolean("assisted");
                            String resId = jsonObj.getString("id");

                            JSONObject class_date = jsonObj.getJSONObject("class_date");
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
                            String location_adress = location.getString("address");
                            // Get all packages
                            ClassItemModel newClass = new ClassItemModel(resId,id,assisted,event_id,instructor_id,location_id,date,room,duration,finish,limit,available,logo_url,event_name
                                    ,event_description,location_name,location_adress);
                            class_dataset.add(newClass);
                            class_adapter = new ClassItemAdapter(class_dataset,getApplicationContext(),R.layout.class_of_day_item,true);
                            lv_classes_day.setAdapter(class_adapter);
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

       // String fDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(cDate);
        String fDate = Integer.toString(dayOfMonth).concat("-").concat(Integer.toString(month)).concat("-").concat(Integer.toString(year));
        ClassesOfDayRequest packageRequest = new ClassesOfDayRequest(userInfo.getToken(),fDate,responseListener,errorListener);
        packageRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(packageRequest);
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
        Toast.makeText(BookClassActivity.this, toastString, Toast.LENGTH_LONG).show();
    }

    public static void setStatusCode(int c){
        statusCode = c;
    }
}
