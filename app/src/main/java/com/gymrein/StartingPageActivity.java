package com.gymrein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);

        final Button btn_goTo_login = (Button) findViewById(R.id.btn_login);
        final TextView lnk_register = (TextView) findViewById(R.id.tv_register);

        lnk_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(StartingPageActivity.this,RegisterActivity.class);
                StartingPageActivity.this.startActivity(registerIntent);
            }
        });

        btn_goTo_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(StartingPageActivity.this,LoginActivity.class);
                StartingPageActivity.this.startActivity(loginIntent);
            }
        });

    }
}
