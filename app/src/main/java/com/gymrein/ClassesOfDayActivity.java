package com.gymrein;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ClassesOfDayActivity extends AppCompatActivity {

    private ArrayList<SideMenuItemModel> menu_dataset;
    private static CustomAdapter menu_Adapter;
    private ListView lv_side_menu;
    private ActionBarDrawerToggle menu_drawer_toggle;
    private DrawerLayout menu_drawer_layout;
    private TextView tv_title;
    private RelativeLayout rl_side_bar;


    // Save User information
    private static String name;
    private static String email;
    private static String lastname;
    private static String phone;
    private static int classes;
    private static String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_of_day);
        /* Side Menu Setup */
        menu_drawer_layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        lv_side_menu = (ListView) findViewById(R.id.lv_side_menu);
        rl_side_bar = (RelativeLayout) findViewById(R.id.activity_side_bar);

        addDrawerItems();

        // Set title
        /*tv_title = (TextView) findViewById(R.id.tv_classes_of_day);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Graduate-Regular.ttf");
        tv_title.setTypeface(custom_font);*/


        final ImageButton btn_side_menu = (ImageButton) findViewById(R.id.btn_side_menu);
        btn_side_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_drawer_layout.openDrawer(Gravity.LEFT);
            }
        });


        lv_side_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayMessage(menu_dataset.get(position).getName());
                switch (menu_dataset.get(position).getName()){
                    case "Pagos":
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
        final TextView tv_welcome = (TextView) findViewById(R.id.tv_classes_of_day);


    }


    private void addDrawerItems() {
        menu_dataset = new ArrayList<>();
        menu_dataset.add(new SideMenuItemModel("Calendario",R.mipmap.ic_calendar));
        menu_dataset.add(new SideMenuItemModel("Mis Clases",R.mipmap.ic_list));
        menu_dataset.add(new SideMenuItemModel("Pagos",R.mipmap.ic_money));
        menu_dataset.add(new SideMenuItemModel("Perfil",R.mipmap.ic_person));

        menu_Adapter = new CustomAdapter(menu_dataset,getApplicationContext(),R.layout.side_menu_item,0);

        lv_side_menu.setAdapter(menu_Adapter);
    }

    //Somewhere that has access to a context
    public void displayMessage(String toastString){
        Toast.makeText(ClassesOfDayActivity.this, toastString, Toast.LENGTH_LONG).show();
    }
}
