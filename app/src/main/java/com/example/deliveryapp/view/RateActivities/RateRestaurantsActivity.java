package com.example.deliveryapp.view.RateActivities;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import static com.example.deliveryapp.util.IPConfig.IP_ADDRESS;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.deliveryapp.R;
import com.example.deliveryapp.util.Store;
import com.example.deliveryapp.util.StoreAdapter;
import com.example.deliveryapp.view.ClientThread;

import android.app.AlertDialog;
import android.widget.RatingBar;

import java.util.ArrayList;
import java.util.List;

public class RateRestaurantsActivity extends AppCompatActivity {

    Handler handler;
    List<Store> items;
    ListView listView;
    StoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_raterestaurants);

        EditText longitudeInput = findViewById(R.id.longitudeInput);
        EditText latitudeInput = findViewById(R.id.latitudeInput);
        ImageButton submitButton = findViewById(R.id.submitButton);
        AppCompatButton selectButton = findViewById(R.id.select);
        AppCompatButton backButton = findViewById(R.id.back);
        listView = findViewById(R.id.list);

        items = new ArrayList<>();
        adapter = new StoreAdapter(RateRestaurantsActivity.this, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Store clickedStore = (Store) parent.getItemAtPosition(position);
            adapter.setSelectedStore(clickedStore);
            Toast.makeText(RateRestaurantsActivity.this, "Selected: " + clickedStore.getStoreName(), Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(RateRestaurantsActivity.this, "1 restaurant found!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RateRestaurantsActivity.this, items.size() + " restaurants found!", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            Toast.makeText(RateRestaurantsActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();

                        }

                        break;

                    case ClientThread.MESSAGE_NO_RESULTS:

                        items.clear();
                        ((StoreAdapter) listView.getAdapter()).notifyDataSetChanged();
                        Toast.makeText(RateRestaurantsActivity.this, "No restaurants found.", Toast.LENGTH_SHORT).show();

                        break;

                    case ClientThread.MESSAGE_ERROR:

                        String errorMessage = (msg.obj instanceof String) ? (String) msg.obj : "An unknown error occurred.";
                        Toast.makeText(RateRestaurantsActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();

                        items.clear();
                        ((StoreAdapter) listView.getAdapter()).notifyDataSetChanged();

                        break;

                    default:

                        Toast.makeText(RateRestaurantsActivity.this, "Received unknown message type.", Toast.LENGTH_SHORT).show();

                        break;

                }

            }

        };

        submitButton.setOnClickListener(v -> {

            String longitude = longitudeInput.getText().toString();
            String latitude = latitudeInput.getText().toString();

            if (longitude.isEmpty() && latitude.isEmpty()) {
                longitudeInput.setError("Necessary input");
                latitudeInput.setError("Necessary input");
            } else if (latitude.isEmpty()){
                latitudeInput.setError("Necessary input");
            } else if (longitude.isEmpty()){
                longitudeInput.setError("Necessary input");
            } else {
                Toast.makeText(this, "Searching for restaurants...", Toast.LENGTH_SHORT).show();
                Thread clientThread = new Thread(new ClientThread(handler,IP_ADDRESS, 5000, longitude,latitude,null,"showcase_stores"));
                clientThread.start();
            }

        });

        backButton.setOnClickListener(v -> finish());

        selectButton.setOnClickListener(v -> {

            if (!items.isEmpty()) {

                if (adapter.isItemSelected()) {
                    showRatingPopup();
                } else {
                    Toast.makeText(this, "Please select a restaurant from the list.", Toast.LENGTH_SHORT).show();
                }

            } else {

                Toast.makeText(this, "No restaurants available to select.", Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void showRatingPopup() {

        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.rating_popup, null);

        RatingBar ratingBar = popupView.findViewById(R.id.ratingBar);

        LayerDrawable starsDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
        Drawable filledStars = starsDrawable.getDrawable(2);
        Drawable wrappedFilledStars = DrawableCompat.wrap(filledStars);
        DrawableCompat.setTint(wrappedFilledStars, Color.parseColor("#EE4216"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(popupView)

                .setPositiveButton("Submit", (dialog, which) -> {

                    float rating = ratingBar.getRating();

                    if (rating == 0) {

                        Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();

                    } else {

                        Store selectedStore = adapter.getSelectedStore();

                        @SuppressLint("DefaultLocale") String selectedRating = "★".repeat((int) rating) + "☆".repeat(5 - (int) rating);
                        Toast.makeText(this, "Submitting rating for: " + selectedStore.getStoreName() + " with rating: " + selectedRating, Toast.LENGTH_LONG).show();

                        String longitude = ((EditText) findViewById(R.id.longitudeInput)).getText().toString();
                        String latitude = ((EditText) findViewById(R.id.latitudeInput)).getText().toString();

                        String preferenceForClientThread = selectedStore.getStoreName() + "_" + selectedRating;

                        new Thread(new ClientThread(handler, IP_ADDRESS, 5000, longitude, latitude, preferenceForClientThread, "rate_store")).start();

                    }

                })

                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())

                .setCancelable(false);

        AlertDialog dialog = builder.create();

        dialog.show();

    }

}

