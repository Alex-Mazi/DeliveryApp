package com.example.deliveryapp.view.FilterActivities;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

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

import java.util.ArrayList;
import java.util.List;

public class RatingsFilterActivity extends AppCompatActivity {

    Handler handler;
    List<Store> items;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ratingsfilter);

        EditText longitudeInput = findViewById(R.id.longitudeInput);
        EditText latitudeInput = findViewById(R.id.latitudeInput);
        AutoCompleteTextView optionButton = findViewById(R.id.categoryAutoComplete);
        ImageButton submitButton = findViewById(R.id.submitButton);
        AppCompatButton selectButton = findViewById(R.id.select);
        AppCompatButton backButton = findViewById(R.id.back);
        listView = findViewById(R.id.list);

        items = new ArrayList<>();
        StoreAdapter adapter = new StoreAdapter(RatingsFilterActivity.this, items);
        listView.setAdapter(adapter);

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
                            Toast.makeText(RatingsFilterActivity.this, items.size() + " restaurants found!", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(RatingsFilterActivity.this, "Failed to retrieve restaurants: Invalid data type.", Toast.LENGTH_SHORT).show();

                        }

                        break;

                    case ClientThread.MESSAGE_NO_RESULTS:

                        items.clear();
                        ((StoreAdapter) listView.getAdapter()).notifyDataSetChanged();
                        Toast.makeText(RatingsFilterActivity.this, "No restaurants found.", Toast.LENGTH_SHORT).show();

                        break;

                    case ClientThread.MESSAGE_ERROR:

                        String errorMessage = (msg.obj instanceof String) ? (String) msg.obj : "An unknown error occurred.";
                        Toast.makeText(RatingsFilterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();

                        items.clear();
                        ((StoreAdapter) listView.getAdapter()).notifyDataSetChanged();

                        break;


                    case ClientThread.MESSAGE_GENERIC_RESPONSE:

                        String responseMessage = (msg.obj instanceof String) ? (String) msg.obj : "Generic response received.";
                        Toast.makeText(RatingsFilterActivity.this, responseMessage, Toast.LENGTH_SHORT).show();

                        break;

                    default:

                        Toast.makeText(RatingsFilterActivity.this, "Received unknown message type.", Toast.LENGTH_SHORT).show();

                        break;

                }

            }

        };

        String[] categories = {"★☆☆☆☆", "★★☆☆☆", "★★★☆☆", "★★★★☆", "★★★★★"};
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
                optionButton.setError("Please select a rating");
            } else {
                Toast.makeText(this, "Searching for restaurants...", Toast.LENGTH_SHORT).show();
                Thread clientThread = new Thread(new ClientThread(handler,"172.20.10.2", 5000, longitude,latitude,selectedCategory,"search_ratings"));
                clientThread.start();
            }

        });

        backButton.setOnClickListener(v -> finish());

    }

}
