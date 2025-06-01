package com.example.deliveryapp.view.PurchaseActivities;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import static com.example.deliveryapp.util.IPConfig.IP_ADDRESS;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryapp.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.deliveryapp.util.Product;
import com.example.deliveryapp.util.ProductAdapter;
import com.example.deliveryapp.util.PurchaseDetails;
import com.example.deliveryapp.util.Store;
import com.example.deliveryapp.view.ClientThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PurchaseActivity extends AppCompatActivity {

    private Store receivedStore;
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

        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {

                switch (msg.what) {

                    case ClientThread.MESSAGE_SUCCESS:

                        if (msg.obj instanceof String) {

                            Toast.makeText(PurchaseActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(PurchaseActivity.this, "Operation successful!", Toast.LENGTH_SHORT).show();

                        }

                        for (Product p : items) {
                            p.setQuantity(0);
                        }

                        adapter.notifyDataSetChanged();

                        break;

                    case ClientThread.MESSAGE_ERROR:

                        String errorMsg = (String) msg.obj;
                        Toast.makeText(PurchaseActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();

                        break;

                    case ClientThread.MESSAGE_NO_RESULTS:

                        Toast.makeText(PurchaseActivity.this, "No results found.", Toast.LENGTH_SHORT).show();

                        break;

                    case ClientThread.MESSAGE_GENERIC_RESPONSE:

                        if (((String) msg.obj).startsWith("All items")){

                            Intent i = new Intent(PurchaseActivity.this, DeliveryActivity.class);
                            startActivity(i);

                        } else if (((String) msg.obj).startsWith("Some issues occurred")){

                            Pattern pattern = Pattern.compile("Failed to purchase .*?\\. (Only \\d+ available\\.)");
                            Matcher matcher = pattern.matcher((String) msg.obj);

                            while (matcher.find()) {
                                String toastMessage = matcher.group(pattern.flags());
                                Toast.makeText(PurchaseActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(PurchaseActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                        }

                        break;

                }

            }

        };

        if (getIntent().hasExtra("selected_store")) {

            receivedStore = getIntent().getParcelableExtra("selected_store", Store.class);

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

                    Map<String, Integer> productsToPurchaseMap = new HashMap<>();
                    double totalCost = 0.0;

                    for (Product product : items) {

                        if (product.getQuantity() > 0) {
                            productsToPurchaseMap.put(product.getProductName(), product.getQuantity());
                            totalCost += (product.getPrice() * product.getQuantity());
                        }

                    }

                    if (productsToPurchaseMap.isEmpty()) {

                        Toast.makeText(PurchaseActivity.this, "Your cart is empty. Please add some items.", Toast.LENGTH_SHORT).show();

                    } else {

                        StringBuilder cartSummary = new StringBuilder();

                        for (Map.Entry<String, Integer> entry : productsToPurchaseMap.entrySet()) {

                            cartSummary.append("- ")
                                    .append(entry.getKey())
                                    .append("\n");

                        }

                        cartSummary.append(String.format("\nTotal: $%.2f", totalCost));

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PurchaseActivity.this);
                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_cart_summary, null);
                        dialogBuilder.setView(dialogView);

                        TextView cartSummaryTextView = dialogView.findViewById(R.id.dialog_cart_items_summary);
                        cartSummaryTextView.setText(cartSummary.toString().trim());

                        AppCompatButton cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);
                        AppCompatButton buyButton = dialogView.findViewById(R.id.dialog_buy_button);

                        AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.setCancelable(false);

                        cancelButton.setOnClickListener(y -> alertDialog.dismiss());

                        final String finalStoreName = receivedStore.getStoreName();
                        final double finalStoreLatitude = receivedStore.getLatitude();
                        final double finalStoreLongitude = receivedStore.getLongitude();
                        final Map<String, Integer> finalProductsToPurchaseMap = productsToPurchaseMap;

                        buyButton.setOnClickListener(k -> {

                            alertDialog.dismiss();

                            PurchaseDetails purchaseDetails = new PurchaseDetails(
                                    finalStoreLongitude,
                                    finalStoreLatitude,
                                    finalStoreName,
                                    finalProductsToPurchaseMap
                            );

                            Toast.makeText(PurchaseActivity.this, "Processing purchase...", Toast.LENGTH_SHORT).show();

                            new Thread(new ClientThread(handler, IP_ADDRESS, 5000, null, null, purchaseDetails, "purchase_product")).start();

                        });

                        alertDialog.show();

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
