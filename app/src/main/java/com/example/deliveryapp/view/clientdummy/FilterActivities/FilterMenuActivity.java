package com.example.deliveryapp.view.clientdummy.FilterActivities;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deliveryapp.R;

public class FilterMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filtermenu);

        FrameLayout cuisineButton = findViewById(R.id.Cuisine);
        FrameLayout priceButton = findViewById(R.id.Price);
        FrameLayout ratingsButton = findViewById(R.id.Ratings);

        cuisineButton.setOnClickListener(v -> go("cuisine"));
        priceButton.setOnClickListener(v -> go("price"));
        ratingsButton.setOnClickListener(v -> go("ratings"));

    }

    public void go(String option) {
        Intent intent;
        switch (option) {
            case "cuisine":
                intent = new Intent(this, CuisineFilterActivity.class);
                break;
            case "price":
                intent = new Intent(this, PriceFilterActivity.class);
                break;
            case "ratings":
                intent = new Intent(this, RatingsFilterActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }

}
