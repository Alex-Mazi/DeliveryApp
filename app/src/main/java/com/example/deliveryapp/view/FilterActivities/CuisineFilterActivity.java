package com.example.deliveryapp.view.FilterActivities;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import static com.example.deliveryapp.util.IPConfig.IP_ADDRESS;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.deliveryapp.R;
import com.example.deliveryapp.util.Store;
import com.example.deliveryapp.util.StoreAdapter;
import com.example.deliveryapp.view.ClientThread;
import com.example.deliveryapp.view.PurchaseActivities.PurchaseActivity;

import java.util.ArrayList;
import java.util.List;

public class CuisineFilterActivity extends AppCompatActivity {

    Handler handler;
    List<Store> items;
    ListView listView;
    StoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cuisinefilter);

        EditText longitudeInput = findViewById(R.id.longitudeInput);
        EditText latitudeInput = findViewById(R.id.latitudeInput);
        EditText optionButton = findViewById(R.id.optionButton);
        ImageButton submitButton = findViewById(R.id.submitButton);
        AppCompatButton selectButton = findViewById(R.id.select);
        AppCompatButton backButton = findViewById(R.id.back);
        listView = findViewById(R.id.list);

        items = new ArrayList<>();
        adapter = new StoreAdapter(CuisineFilterActivity.this, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Store clickedStore = (Store) parent.getItemAtPosition(position);
            adapter.setSelectedStore(clickedStore);
            Toast.makeText(CuisineFilterActivity.this, "Selected: " + clickedStore.getStoreName(), Toast.LENGTH_SHORT).show();

        });

        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {

                super.handleMessage(msg);

                switch (msg.what) {

                    case ClientThread.MESSAGE_SUCCESS:

                        if (msg.obj instanceof List) {

                            List<Store> fetchedItems = (List<Store>) msg.obj;

                            items.clear();
                            items.addAll(fetchedItems);
                            ((StoreAdapter) listView.getAdapter()).notifyDataSetChanged();

                            if (items.size() == 1){
                                Toast.makeText(CuisineFilterActivity.this, "1 restaurant found!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CuisineFilterActivity.this, items.size() + " restaurants found!", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            Toast.makeText(CuisineFilterActivity.this, "Failed to retrieve restaurants: Invalid data type.", Toast.LENGTH_SHORT).show();

                        }

                        break;

                    case ClientThread.MESSAGE_NO_RESULTS:

                        items.clear();
                        ((StoreAdapter) listView.getAdapter()).notifyDataSetChanged();
                        Toast.makeText(CuisineFilterActivity.this, "No restaurants found.", Toast.LENGTH_SHORT).show();

                        break;

                    case ClientThread.MESSAGE_ERROR:

                        String errorMessage = (msg.obj instanceof String) ? (String) msg.obj : "An unknown error occurred.";
                        Toast.makeText(CuisineFilterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();

                        items.clear();
                        ((StoreAdapter) listView.getAdapter()).notifyDataSetChanged();

                        break;

                    case ClientThread.MESSAGE_GENERIC_RESPONSE:

                        String responseMessage = (msg.obj instanceof String) ? (String) msg.obj : "Generic response received.";
                        Toast.makeText(CuisineFilterActivity.this, responseMessage, Toast.LENGTH_SHORT).show();

                        break;

                    default:

                        Toast.makeText(CuisineFilterActivity.this, "Received unknown message type.", Toast.LENGTH_SHORT).show();

                        break;

                }

            }

        };

        submitButton.setOnClickListener(v -> {

            String longitude = longitudeInput.getText().toString();
            String latitude = latitudeInput.getText().toString();
            String selectedCategory = optionButton.getText().toString();

            if (longitude.isEmpty() && latitude.isEmpty()) {
                longitudeInput.setError("Necessary input");
                latitudeInput.setError("Necessary input");
            } else if (latitude.isEmpty()){
                latitudeInput.setError("Necessary input");
            } else if (longitude.isEmpty()){
                longitudeInput.setError("Necessary input");
            } else if (selectedCategory.isEmpty()) {
                optionButton.setError("Please select a cuisine");
            } else {

                Toast.makeText(this, "Searching for restaurants...", Toast.LENGTH_SHORT).show();
                Thread clientThread = new Thread(new ClientThread(handler,IP_ADDRESS, 5000, longitude,latitude,selectedCategory,"search_food_preference"));
                clientThread.start();

            }

        });

        backButton.setOnClickListener(v -> finish());

        selectButton.setOnClickListener(v -> {

            if (!items.isEmpty()) {

                if (adapter.isItemSelected()) {

                    Store selectedStore = adapter.getSelectedStore();

                    Toast.makeText(this, "Opening restaurant: " + selectedStore.getStoreName(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, PurchaseActivity.class);
                    intent.putExtra("selected_store", selectedStore);
                    startActivity(intent);

                } else {

                    Toast.makeText(this, "Please select a restaurant from the list.", Toast.LENGTH_SHORT).show();

                }

            } else {

                Toast.makeText(this, "No restaurants available to select.", Toast.LENGTH_SHORT).show();

            }

        });

    }

}
