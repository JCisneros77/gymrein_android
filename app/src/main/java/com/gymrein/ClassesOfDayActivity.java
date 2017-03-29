package com.gymrein;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class ClassesOfDayActivity extends AppCompatActivity {

    private ArrayList<SideMenuItemModel> menu_dataset;
    private static CustomAdapter menu_Adapter;
    private ListView lv_side_menu;
    private ActionBarDrawerToggle menu_drawer_toggle;
    private DrawerLayout menu_drawer_layout;
    private TextView tv_title;
    private ListView lv_classes_of_day;

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
        final ImageView iv_qr_code = (ImageView) findViewById(R.id.iv_qr_code);

        CarouselView carouselView;
        final int[] sampleImages = {R.mipmap.ic_back_button,R.mipmap.ic_launcher,R.mipmap.icon,R.mipmap.mail};

        /* Classes of day */
        lv_classes_of_day = (ListView) findViewById(R.id.lv_classes_of_day);

        /* Side Menu Setup */
        menu_drawer_layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        lv_side_menu = (ListView) findViewById(R.id.lv_side_menu);


        addDrawerItems();

        // Set title
        tv_title = (TextView) findViewById(R.id.tv_classes_of_day);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Graduate-Regular.ttf");
        tv_title.setTypeface(custom_font);


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
                menu_drawer_layout.closeDrawer(lv_side_menu);
            }
        });
        /* ************************* */
        final TextView tv_welcome = (TextView) findViewById(R.id.tv_classes_of_day);

        // QR CODE
        GymReinApp app = (GymReinApp) getApplicationContext();
        UserInformation userInfo = app.getUserInformation();

        try {
            Bitmap bitmap = encodeAsBitmap(userInfo.getToken());
            iv_qr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // Carousel View
        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(sampleImages[position]);
            }
        };

        carouselView = (CarouselView) findViewById(R.id.cv_packages_available);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);


    }



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
        menu_dataset.add(new SideMenuItemModel("Calendario",R.mipmap.ic_back_button));
        menu_dataset.add(new SideMenuItemModel("Mis Clases",R.mipmap.lock));
        menu_dataset.add(new SideMenuItemModel("Pagos",R.mipmap.mail));
        menu_dataset.add(new SideMenuItemModel("Perfil",R.mipmap.ic_back_button));

        menu_Adapter = new CustomAdapter(menu_dataset,getApplicationContext(),R.layout.side_menu_item,0);

        lv_side_menu.setAdapter(menu_Adapter);
    }

    //Somewhere that has access to a context
    public void displayMessage(String toastString){
        Toast.makeText(ClassesOfDayActivity.this, toastString, Toast.LENGTH_LONG).show();
    }
}
