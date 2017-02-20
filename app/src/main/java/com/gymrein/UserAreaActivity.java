package com.gymrein;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class UserAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        final EditText tf_email = (EditText) findViewById(R.id.tf_email);
        final EditText tf_name = (EditText) findViewById(R.id.tf_name);
        final EditText tf_lastName = (EditText) findViewById(R.id.tf_lastName);
        final TextView tv_welcome = (TextView) findViewById(R.id.tv_welcome);

        Intent intent = getIntent();
        String name = intent.getStringExtra("nameeeeee");
        String email = intent.getStringExtra("email");
        String lastname = intent.getStringExtra("lastname");
        String phone = intent.getStringExtra("phone");
        //int classes = intent.getIntExtra("classes",0);
        String token = intent.getStringExtra("token");

        tf_email.setText(email);
        tf_name.setText(name);
        tf_lastName.setText(lastname);

    }
}
