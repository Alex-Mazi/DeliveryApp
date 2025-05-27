package com.example.deliveryapp;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deliveryapp.view.clientdummy.ClientMenuActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button clientButton = findViewById(R.id.ClientLogin);

        clientButton.setOnClickListener(v -> login("client"));
    }

    public void login(String userType) {
        Intent intent;
        if (userType.equals("client")) {
            intent = new Intent(this, ClientMenuActivity.class);
        } else {
            return;
        }
        startActivity(intent);
    }

}