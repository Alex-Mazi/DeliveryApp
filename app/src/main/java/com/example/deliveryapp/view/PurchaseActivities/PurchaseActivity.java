package com.example.deliveryapp.view.PurchaseActivities;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryapp.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.deliveryapp.util.Product;
import com.example.deliveryapp.util.ProductAdapter;
import com.example.deliveryapp.util.Store;

import java.util.ArrayList;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity {

    private Store receivedStore;
    private String latitude;
    private String longitude;
    private TextView storeNameTextView;
    Handler handler;
    List<Product> items;
    ListView listView;
    ProductAdapter adapter;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        storeNameTextView = findViewById(R.id.store_name);
        AppCompatButton backButton = findViewById(R.id.back);
        AppCompatButton addButton = findViewById(R.id.addToCart);
        listView = findViewById(R.id.list);

        items = new ArrayList<>();
        adapter = new ProductAdapter(PurchaseActivity.this, items);
        listView.setAdapter(adapter);

        if (getIntent().hasExtra("selected_store")) {

            receivedStore = getIntent().getParcelableExtra("selected_store", Store.class);
            longitude = getIntent().getParcelableExtra("longitude", String.class);
            latitude = getIntent().getParcelableExtra("latitude", String.class);

            if (receivedStore != null) {

                storeNameTextView.setText(receivedStore.getStoreName().toUpperCase());

                List<Product> productsFromStore = receivedStore.getProducts();

                if (productsFromStore != null && !productsFromStore.isEmpty()) {

                    items.clear();

                    for (Product p : productsFromStore) {
                        p.setQuantity(0);
                    }

                    items.addAll(productsFromStore);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, productsFromStore.size() + " products loaded!", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "No products found for this store.", Toast.LENGTH_SHORT).show();
                    items.clear();
                    adapter.notifyDataSetChanged();

                }

                backButton.setOnClickListener(v -> finish());

                addButton.setOnClickListener(v -> {

                    List<Product> productsInCart = new ArrayList<>();
                    double totalCost = 0.0;

                    for (Product product : items) {

                        if (product.getQuantity() > 0) {
                            productsInCart.add(product);
                            totalCost += (product.getPrice() * product.getQuantity());
                        }

                    }

                    if (productsInCart.isEmpty()) {

                        Toast.makeText(PurchaseActivity.this, "Your cart is empty. Please add some items.", Toast.LENGTH_SHORT).show();

                    } else {

                        StringBuilder cartSummary = new StringBuilder("Items in cart:\n");

                        for (Product p : productsInCart) {

                            cartSummary.append(p.getProductName())
                                    .append(" x ")
                                    .append(p.getQuantity())
                                    .append(" = $")
                                    .append(String.format("%.2f", p.getPrice() * p.getQuantity()))
                                    .append("\n");
                        }

                        cartSummary.append(String.format("Total: $%.2f", totalCost));

                        Toast.makeText(PurchaseActivity.this, cartSummary.toString(), Toast.LENGTH_LONG).show();

                    }

                });

            } else {

                storeNameTextView.setText("STORE NOT FOUND");
                Toast.makeText(this, "Failed to load store details.", Toast.LENGTH_LONG).show();

            }

        } else {

            storeNameTextView.setText("NO STORE SELECTED");
            Toast.makeText(this, "No store selected to purchase from.", Toast.LENGTH_LONG).show();

        }

    }

}
