package com.gymrein;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class ClassesOfDayActivity extends AppCompatActivity {

    private ArrayList<SideMenuItemModel> menu_dataset;
    private static CustomAdapter menu_Adapter;
    private ListView lv_side_menu;
    private DrawerLayout menu_drawer_layout;

    private ArrayList<ClassItemModel> class_dataset;
    private static ClassItemAdapter class_adapter;
    private ListView lv_classes;
    private DrawerLayout class_drawer_layout;

    private TextView tv_title;
    private RelativeLayout rl_side_bar;
    private ListView lv_classes_of_day;
    private ArrayList<PackageModel> package_dataset;
    private CarouselView carouselView;

    // Save User information
    private GymReinApp app;
    private UserInformation userInfo;
    private RequestQueue queue;
    private static int statusCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_of_day);
        final ImageView iv_qr_code = (ImageView) findViewById(R.id.iv_qr_code);

        carouselView = (CarouselView) findViewById(R.id.cv_packages_available);;

        /* Classes of day */
        lv_classes_of_day = (ListView) findViewById(R.id.lv_classes_of_day);

        /* Side Menu Setup */
        menu_drawer_layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        lv_side_menu = (ListView) findViewById(R.id.lv_side_menu);
        rl_side_bar = (RelativeLayout) findViewById(R.id.activity_side_bar);


        // Get User Info
        app = (GymReinApp) getApplicationContext();
        userInfo = app.getUserInformation();
        queue = Volley.newRequestQueue(ClassesOfDayActivity.this);

        // Add side menu user information
        final TextView tv_user_name = (TextView) findViewById(R.id.tv_user_name_side_menu);
        final TextView tv_user_remaining_classes = (TextView) findViewById(R.id.tv_user_remaining_classes);
        final ImageView img_user_avatar = (ImageView) findViewById(R.id.img_user_avatar);

        tv_user_name.setText(userInfo.getName() + " " + userInfo.getLastname());
        tv_user_remaining_classes.setText("Clases Restantes: " + Integer.toString(userInfo.getClasses()));
        Picasso.with(this).load(userInfo.getAvatar()).into(img_user_avatar);

        // Add Side menu items
        addDrawerItems();
        addClassesOfDay();

        // Set title
        tv_title = (TextView) findViewById(R.id.tv_classes_of_day);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Graduate-Regular.ttf");
        tv_title.setTypeface(custom_font);


        // Side menu listener
        final ImageButton btn_side_menu = (ImageButton) findViewById(R.id.btn_side_menu);
        btn_side_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_drawer_layout.openDrawer(Gravity.LEFT);
            }
        });

        // OnClick Class of Day
        lv_classes_of_day.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        // OnClick Side menu item
        lv_side_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayMessage(menu_dataset.get(position).getName());
                switch (menu_dataset.get(position).getName()){
                    case "Pago":
                    {
                        Intent paymentsIntent = new Intent(ClassesOfDayActivity.this,PaymentActivity.class);
                        ClassesOfDayActivity.this.startActivity(paymentsIntent);

                        break;
                    }


                }

                menu_drawer_layout.addDrawerListener( new DrawerLayout.SimpleDrawerListener(){
                    @Override
                    public void onDrawerClosed(View drawerView){
                        super.onDrawerClosed(drawerView);
                    }
                });
                menu_drawer_layout.closeDrawer(rl_side_bar);
            }
        });
        /* ************************* */

        // Get all packages into package_dataset ArrayList
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    System.out.println(jsonResponse.toString());
                    System.out.println("Status:" + statusCode);

                    if(statusCode == 200 || statusCode == 304){
                        System.out.println("Success 200 o 304!!!!!!");
                        package_dataset = new ArrayList<>();
                        for(int i = 0; i < jsonResponse.length(); ++i){
                            JSONObject jsonObj = jsonResponse.getJSONObject(i);
                            // Get all packages
                            package_dataset.add(new PackageModel(jsonObj.getInt("id"),jsonObj.getString("name"),
                                    jsonObj.getInt("price"),jsonObj.getInt("classes")));
                        }

                        // Carousel View
                        carouselView.setViewListener(viewListener);
                        carouselView.setClickable(true);
                        carouselView.setPageCount(package_dataset.size());

                        // END Caroussel View

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

        PackageRequest packageRequest = new PackageRequest(userInfo.getToken(),responseListener,errorListener);
        packageRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(packageRequest);
        // End of get all packages into ArrayList

        // QR CODE

        try {
            Bitmap bitmap = encodeAsBitmap(userInfo.getToken());
            iv_qr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // End QR CODE


    }
    // Carrousel View
    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {

            View customView = getLayoutInflater().inflate(R.layout.package_item, null);

            TextView tv_package_name = (TextView) customView.findViewById(R.id.tv_package_name);
            TextView tv_package_classes = (TextView) customView.findViewById(R.id.tv_package_classes);
            TextView tv_package_price = (TextView) customView.findViewById(R.id.tv_package_price);
            RelativeLayout rl_package_item = (RelativeLayout) customView.findViewById(R.id.rl_package_item);

            tv_package_name.setText(package_dataset.get(position).getName());
            tv_package_classes.setText(Integer.toString(package_dataset.get(position).getClasses()) + " Clases");
            tv_package_price.setText("$" + Integer.toString(package_dataset.get(position).getPrice()));

            rl_package_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent buyPackageIntent = new Intent(ClassesOfDayActivity.this,BuyPackageActivity.class);
                    buyPackageIntent.putExtra("name",package_dataset.get(carouselView.getCurrentItem()).getName());
                    buyPackageIntent.putExtra("classes",package_dataset.get(carouselView.getCurrentItem()).getClasses());
                    buyPackageIntent.putExtra("price",package_dataset.get(carouselView.getCurrentItem()).getPrice());
                    buyPackageIntent.putExtra("id",Integer.toString(package_dataset.get(carouselView.getCurrentItem()).getID()));
                    ClassesOfDayActivity.this.startActivity(buyPackageIntent);
                }
            });

            carouselView.setIndicatorGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);

            return customView;
        }
    };


    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 500, 500, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    }


    private void addDrawerItems() {
        menu_dataset = new ArrayList<>();
        menu_dataset.add(new SideMenuItemModel("Reservar",R.mipmap.ic_calendar));
        menu_dataset.add(new SideMenuItemModel("Mis Clases",R.mipmap.ic_list));
        menu_dataset.add(new SideMenuItemModel("Pago",R.mipmap.ic_money));
        menu_dataset.add(new SideMenuItemModel("Perfil",R.mipmap.ic_person));

        menu_Adapter = new CustomAdapter(menu_dataset,getApplicationContext(),R.layout.side_menu_item,0);

        lv_side_menu.setAdapter(menu_Adapter);
    }

    private void addClassesOfDay(){
        class_dataset = new ArrayList<>();
        // Get
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    System.out.println(jsonResponse.toString());
                    System.out.println("Status COD:" + statusCode);

                    if(statusCode == 200 || statusCode == 304){
                        System.out.println("Success 200 o 304!!!!!!");
                        package_dataset = new ArrayList<>();
                        for(int i = 0; i < jsonResponse.length(); ++i){
                            JSONObject jsonObj = jsonResponse.getJSONObject(i);
                            String id = jsonObj.getString("id");
                            boolean assisted = jsonObj.getBoolean("assisted");

                            JSONObject class_date = jsonObj.getJSONObject("class_date");
                            String event_id = class_date.getString("event_id");
                            String instructor_id = class_date.getString("instructor_id");
                            String location_id = class_date.getString("location_id");
                            String date = class_date.getString("date");
                            String room = class_date.getString("room");
                            String duration = class_date.getString("duration");
                            String finish = class_date.getString("finish");
                            String limit = class_date.getString("limit");
                            boolean available = class_date.getBoolean("available");
                            String logo_url = class_date.getString("logo_url");

                            JSONObject event = class_date.getJSONObject("event");
                            String event_name = event.getString("name");
                            String event_description = event.getString("description");

                            JSONObject location = class_date.getJSONObject("location");
                            String location_name = location.getString("name");
                            String location_adress = location.getString("address");
                            // Get all packages
                            ClassItemModel newClass = new ClassItemModel(id,assisted,event_id,instructor_id,location_id,date,room,duration,finish,limit,available,logo_url,event_name
                                                                            ,event_description,location_name,location_adress);
                            class_dataset.add(newClass);
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

        Date cDate = new Date();
        String fDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(cDate);
        ClassesOfDayRequest packageRequest = new ClassesOfDayRequest(userInfo.getToken(),fDate,responseListener,errorListener);
        packageRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(packageRequest);

        class_adapter = new ClassItemAdapter(class_dataset,getApplicationContext(),R.layout.class_of_day_item);
        lv_classes_of_day.setAdapter(class_adapter);
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
        Toast.makeText(ClassesOfDayActivity.this, toastString, Toast.LENGTH_LONG).show();
    }

    public static void setStatusCode(int c){
        statusCode = c;
    }
}
