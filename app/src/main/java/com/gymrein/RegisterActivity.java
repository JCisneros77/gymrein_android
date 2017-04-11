package com.gymrein;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.os.Build.VERSION_CODES.M;

public class RegisterActivity extends AppCompatActivity {

    public static int succStatusCode = 0;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE = 1889;
    private CircularImageView btn_profile_picture;
    private EditText tf_date_of_birth;
    private Calendar myCalendar;
    private String[] genderArray;
    private boolean profile_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Create variables for all the text fields and buttons.

        final ImageButton btn_back_to_starting = (ImageButton) findViewById(R.id.btn_back_to_starting_page);
        final EditText tf_name = (EditText) findViewById(R.id.tf_name);
        final EditText tf_lastName = (EditText) findViewById(R.id.tf_lastName);
        final EditText tf_email = (EditText) findViewById(R.id.tf_email);
        final EditText tf_phone = (EditText) findViewById(R.id.tf_phone);
        tf_date_of_birth = (EditText) findViewById(R.id.tf_date_of_birth);
        final Spinner sp_gender = (Spinner) findViewById(R.id.sp_gender);
        final EditText tf_password = (EditText) findViewById(R.id.tf_password);
        final EditText tf_confirmPassword = (EditText) findViewById(R.id.tf_confirmPassword);
        final CheckBox cb_terms_cond = (CheckBox) findViewById(R.id.cb_terms_cond);
        final TextView tv_terms_cond = (TextView) findViewById(R.id.tv_terms_cond);
        //final WebView wv_terms_cond = (WebView) findViewById(R.id.wv_terms_cond);
        final Button btn_register = (Button) findViewById(R.id.btn_register);
        btn_profile_picture = (CircularImageView) findViewById(R.id.btn_profile_picture);
        profile_pic = false;

        // Back to starting page
        btn_back_to_starting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startingPageIntent = new Intent(RegisterActivity.this, StartingPageActivity.class);
                RegisterActivity.this.startActivity(startingPageIntent);
            }
        });
        // Terms and Conditions
        tv_terms_cond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebView terms_and_conditions_wv = new WebView(RegisterActivity.this);
                terms_and_conditions_wv.setWebViewClient(new WebViewClient());
                terms_and_conditions_wv.loadUrl("http://www.google.com");
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Términos y Condiciones")
                        .setView(terms_and_conditions_wv)
                        .setNeutralButton("Cerrar", null)
                        .show();
            }
        });

        // Birth Date
        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        tf_date_of_birth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Gender Spinner
        genderArray = new String[] {"Masculino","Femenino"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,genderArray);
        sp_gender.setAdapter(genderAdapter);

        // Profile Picture
        btn_profile_picture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu pp_popup_menu =  new PopupMenu(RegisterActivity.this, btn_profile_picture);
                pp_popup_menu.getMenuInflater().inflate(R.menu.profile_picture_menu, pp_popup_menu.getMenu());

                pp_popup_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.choose_picture:
                                if( ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    if (Build.VERSION.SDK_INT >= M) {
                                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);
                                    }
                                }

                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                                break;
                            case R.id.take_picture:
                                if( ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    if (Build.VERSION.SDK_INT >= M) {
                                        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST);
                                    }
                                }
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                break;
                            case R.id.delete_picture:
                                btn_profile_picture.setImageResource(R.mipmap.ic_add_image);
                                profile_pic = false;
                                break;
                        }

                        return false;
                    }
                });
                pp_popup_menu.show();
            }

        });



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_terms_cond.isChecked())
                    displayMessage("Favor de aceptar los términos y condiciones.");
                else {
                    System.out.println("Sending Request...");
                    final String name = tf_name.getText().toString();
                    final String lastName = tf_lastName.getText().toString();
                    final String email = tf_email.getText().toString();
                    final String phone = tf_phone.getText().toString();
                    final String password = tf_password.getText().toString();
                    final String confirmPassword = tf_confirmPassword.getText().toString();
                    String gender = sp_gender.getSelectedItem().toString();

                    // Image
                    String encodedImage = "";
                    if (profile_pic) {
                        Drawable drawable = btn_profile_picture.getDrawable();
                        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] imageInByte = stream.toByteArray();
                        encodedImage = Base64.encodeToString(imageInByte, Base64.DEFAULT);
                    }


                    if (gender.equals("Masculino"))
                        gender = "male";
                    else if (gender.equals("Femenino"))
                        gender = "female";
                    final String birth_date = tf_date_of_birth.getText().toString().replace('/','-');

                    Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            if (succStatusCode == 200) {
                                displayMessage("Registro Exitoso");
                                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(loginIntent);
                            }
                        }
                    };


                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                String json;
                                switch (response.statusCode) {
                                    case 422:
                                        json = new String(response.data);
                                        System.out.println("STATUS 422!!!!!!!");
                                        System.out.println(json);
                                        json = trimMessage(json, "errors");
                                        if (json != null) displayMessage(json);
                                        break;
                                    case 400:
                                        json = new String(response.data);
                                        System.out.println(json);
                                        if (json != null && json.length() > 0)
                                            json = trimMessage(json, "message");
                                        if (json != null && json != "")
                                            displayMessage(json);
                                        break;
                                    default:
                                        System.out.println("Status: " + response.statusCode);
                                        json = new String(response.data);
                                        System.out.println(json);
                                        /*if (json != null && json.length() > 0)
                                            json = trimMessage(json, "message");
                                        if (json != null && json != "")
                                            displayMessage(json);*/
                                        break;
                                }
                            }

                        }

                    };

                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);
                    params.put("lastname", lastName);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("password_confirmation", confirmPassword);
                    params.put("phone", phone);
                    params.put("sex",gender);
                    params.put("birth",birth_date);

                    Map<String, Map<String, String>> userParams = new HashMap<>();
                    userParams.put("user", params);


                    JSONObject userJSON = new JSONObject(userParams);
                    try {
                        userJSON.put("platform","android");
                        if (profile_pic)
                            userJSON.put("avatar",encodedImage);
                        else
                            userJSON.put("avatar",null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    System.out.println(userJSON.toString());
                    RegisterRequest registerRequest = new RegisterRequest(userJSON, responseListener, errorListener);
                    registerRequest.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue reqQueue = Volley.newRequestQueue(RegisterActivity.this);
                    reqQueue.add(registerRequest);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST  && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            btn_profile_picture.setImageBitmap(photo);
            profile_pic = true;
        }else if (requestCode == PICK_IMAGE  && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            btn_profile_picture.setImageBitmap(bitmap);
            profile_pic = true;
        }
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

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tf_date_of_birth.setText(sdf.format(myCalendar.getTime()));
    }

    //Somewhere that has access to a context
    public void displayMessage(String toastString){
        Toast.makeText(RegisterActivity.this, toastString, Toast.LENGTH_LONG).show();
    }

    public static void setSuccStatusCode(int s){
        succStatusCode = s;
    }

}
