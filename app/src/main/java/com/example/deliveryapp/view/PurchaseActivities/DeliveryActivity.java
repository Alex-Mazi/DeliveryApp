package com.example.deliveryapp.view.PurchaseActivities;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.deliveryapp.R;
import com.example.deliveryapp.util.RandomDeliveryMinutes;

public class DeliveryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        AppCompatButton backButton = findViewById(R.id.back);
        TextView delivery = findViewById(R.id.deliveryMessageCombined);

        int minutes = RandomDeliveryMinutes.getDeliveryMinutes();

        String fullDeliveryMessage = getString(R.string.delivery_message_with_minutes, minutes);
        delivery.setText(fullDeliveryMessage);

        backButton.setOnClickListener(v -> finish());

    }

}
