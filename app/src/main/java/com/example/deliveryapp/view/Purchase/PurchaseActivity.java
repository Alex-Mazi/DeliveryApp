package com.example.deliveryapp.view.Purchase;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import static com.example.deliveryapp.util.IPConfig.IP_ADDRESS;

import android.os.Build;
import android.os.Bundle;
import com.example.deliveryapp.R;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deliveryapp.util.Store;

public class PurchaseActivity extends AppCompatActivity {

    private Store receivedStore;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        if (getIntent().hasExtra("selected_store")) {

            receivedStore = getIntent().getParcelableExtra("selected_store", Store.class);
            longitude = getIntent().getParcelableExtra("longitude", String.class);
            latitude = getIntent().getParcelableExtra("latitude", String.class);

            if (receivedStore != null) {



            } else {
                System.out.println("Error: receivedStore is null.");
            }
        } else {
            System.out.println("Error: Intent does not contain 'selected_store' extra.");
        }
        // Handle the case where no store was passed, if necessary
    }

}
