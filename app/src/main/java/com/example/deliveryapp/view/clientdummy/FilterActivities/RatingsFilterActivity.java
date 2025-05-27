package com.example.deliveryapp.view.clientdummy.FilterActivities;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.deliveryapp.R;

public class RatingsFilterActivity extends AppCompatActivity {

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
        ListView listView = findViewById(R.id.list);

        String[] categories = {"★☆☆☆☆", "★★☆☆☆", "★★★☆☆", "★★★★☆", "★★★★★"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        optionButton.setAdapter(adapter);

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
            }

        });

        backButton.setOnClickListener(v -> finish());

    }

}
