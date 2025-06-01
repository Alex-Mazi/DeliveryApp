package com.example.deliveryapp.view.FilterActivities;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class PriceFilterActivity extends AppCompatActivity {

    Handler handler;
    List<Store> items;

    ListView listView;
    StoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pricefilter);

        EditText longitudeInput = findViewById(R.id.longitudeInput);
        EditText latitudeInput = findViewById(R.id.latitudeInput);
        AutoCompleteTextView optionButton = findViewById(R.id.categoryAutoComplete);
        ImageButton submitButton = findViewById(R.id.submitButton);
        AppCompatButton selectButton = findViewById(R.id.select);
        AppCompatButton backButton = findViewById(R.id.back);
        listView = findViewById(R.id.list);

        items = new ArrayList<>();
        adapter = new StoreAdapter(PriceFilterActivity.this, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Store clickedStore = (Store) parent.getItemAtPosition(position);
            adapter.setSelectedStore(clickedStore);
            Toast.makeText(PriceFilterActivity.this, "Selected: " + clickedStore.getStoreName(), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(PriceFilterActivity.this, items.size() + " restaurants found!", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(PriceFilterActivity.this, "Failed to retrieve restaurants: Invalid data type.", Toast.LENGTH_SHORT).show();

                        }

                        break;

                    case ClientThread.MESSAGE_NO_RESULTS:

                        items.clear();
                        ((StoreAdapter) listView.getAdapter()).notifyDataSetChanged();
                        Toast.makeText(PriceFilterActivity.this, "No restaurants found.", Toast.LENGTH_SHORT).show();

                        break;

                    case ClientThread.MESSAGE_ERROR:

                        String errorMessage = (msg.obj instanceof String) ? (String) msg.obj : "An unknown error occurred.";
                        Toast.makeText(PriceFilterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();

                        items.clear();
                        ((StoreAdapter) listView.getAdapter()).notifyDataSetChanged();

                        break;

                    default:

                        Toast.makeText(PriceFilterActivity.this, "Received unknown message type.", Toast.LENGTH_SHORT).show();

                        break;

                }

            }

        };

        String[] categories = {"$", "$$", "$$$"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        optionButton.setAdapter(adapter1);

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
                optionButton.setError("Please select a price range");
            } else {
                Toast.makeText(this, "Searching for restaurants...", Toast.LENGTH_SHORT).show();
                Thread clientThread = new Thread(new ClientThread(handler, "192.168.1.90", 5000, longitude,latitude,selectedCategory,"search_price_range"));
                clientThread.start();
            }

        });

        backButton.setOnClickListener(v -> finish());

        selectButton.setOnClickListener(v -> {

            if (!items.isEmpty()) {

                if (adapter.isItemSelected()) {

                    String longitude = longitudeInput.getText().toString();
                    String latitude = latitudeInput.getText().toString();

                    Store selectedStore = adapter.getSelectedStore();

                    Toast.makeText(this, "Opening restaurant: " + selectedStore.getStoreName(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, PurchaseActivity.class);
                    intent.putExtra("selected_store", selectedStore);
                    intent.putExtra("latitude",latitude);
                    intent.putExtra("longitude",longitude);
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
