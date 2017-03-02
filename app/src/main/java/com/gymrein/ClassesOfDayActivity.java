package com.gymrein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ClassesOfDayActivity extends AppCompatActivity {

    private ArrayList<SideMenuItemModel> menu_dataset;
    private static CustomAdapter menu_Adapter;
    private ListView lv_side_menu;
    private ActionBarDrawerToggle menu_drawer_toggle;
    private DrawerLayout menu_drawer_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_of_day);
        /* Side Menu Setup */
        menu_drawer_layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        lv_side_menu = (ListView) findViewById(R.id.lv_side_menu);

        addDrawerItems();

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


                menu_drawer_layout.addDrawerListener( new DrawerLayout.SimpleDrawerListener(){
                    @Override
                    public void onDrawerClosed(View drawerView){
                        super.onDrawerClosed(drawerView);
                    }
                });
                menu_drawer_layout.closeDrawer(lv_side_menu);
            }
        });
        /* ************************* */
        final TextView tv_welcome = (TextView) findViewById(R.id.tv_classes_of_day);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String lastname = intent.getStringExtra("lastname");
        String phone = intent.getStringExtra("phone");
        //int classes = intent.getIntExtra("classes",0);
        String token = intent.getStringExtra("token");


    }

    private void addDrawerItems() {
        menu_dataset = new ArrayList<>();
        menu_dataset.add(new SideMenuItemModel("Calendario",R.mipmap.ic_back_button));
        menu_dataset.add(new SideMenuItemModel("Mis Clases",R.mipmap.lock));
        menu_dataset.add(new SideMenuItemModel("Pago",R.mipmap.mail));
        menu_dataset.add(new SideMenuItemModel("Perfil",R.mipmap.ic_back_button));

        menu_Adapter = new CustomAdapter(menu_dataset,getApplicationContext());

        lv_side_menu.setAdapter(menu_Adapter);
    }

    //Somewhere that has access to a context
    public void displayMessage(String toastString){
        Toast.makeText(ClassesOfDayActivity.this, toastString, Toast.LENGTH_LONG).show();
    }
}
