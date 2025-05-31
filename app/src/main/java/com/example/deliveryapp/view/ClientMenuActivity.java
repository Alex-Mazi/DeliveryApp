package com.example.deliveryapp.view;

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
import com.example.deliveryapp.view.FilterActivities.FilterMenuActivity;
import com.example.deliveryapp.view.FindActivities.FindRestaurantsActivity;
import com.example.deliveryapp.view.RateActivities.RateRestaurantsActivity;

public class ClientMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        FrameLayout findButton = findViewById(R.id.Finder);
        FrameLayout filterButton = findViewById(R.id.Filter);
        FrameLayout rateButton = findViewById(R.id.Rater);

        findButton.setOnClickListener(v -> go("find"));
        filterButton.setOnClickListener(v -> go("filter"));
        rateButton.setOnClickListener(v -> go("rate"));

    }

    public void go(String option) {

        Intent intent;

        switch (option) {
            case "find":
                intent = new Intent(this, FindRestaurantsActivity.class);
                break;
            case "filter":
                intent = new Intent(this, FilterMenuActivity.class);
                break;
            case "rate":
                intent = new Intent(this, RateRestaurantsActivity.class);
                break;
            default:
                return;
        }

        startActivity(intent);

    }

}
