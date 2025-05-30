package com.example.deliveryapp.util;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Store selectedStore = null;

    public ProductAdapter(Context context, List<Product> products) {
        super(context, 0, products);
    }

}
