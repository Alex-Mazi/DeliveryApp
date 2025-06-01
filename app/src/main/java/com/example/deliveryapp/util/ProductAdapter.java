package com.example.deliveryapp.util;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.deliveryapp.R;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    private static final int COLOR_GREY_TEXT = Color.parseColor("#A0A0A0");
    private static final int COLOR_DISABLED_BUTTON_BLUE = Color.parseColor("#ADD8E6");
    private static final int COLOR_DEFAULT_PRODUCT_NAME = Color.parseColor("#0F0000");
    private static final int COLOR_DEFAULT_PRODUCT_PRICE = Color.parseColor("#555555");
    private static final int COLOR_ACTIVE_BUTTON_BLUE = Color.parseColor("#87CEEB");

    public ProductAdapter(Context context, List<Product> products) {
        super(context, 0, products);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Product product = getItem(position);

        ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_list_item, parent, false);
            holder = new ViewHolder();
            holder.productNameTextView = convertView.findViewById(R.id.productName);
            holder.productPriceTextView = convertView.findViewById(R.id.productPrice);
            holder.minusButton = convertView.findViewById(R.id.minusButton);
            holder.quantityTextView = convertView.findViewById(R.id.quantityTextView);
            holder.plusButton = convertView.findViewById(R.id.plusButton);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        assert product != null;

        holder.productNameTextView.setText(product.getProductName());
        holder.productPriceTextView.setText(String.format("Price: $%.2f", product.getPrice()));
        holder.quantityTextView.setText(String.valueOf(product.getQuantity()));

        if (!product.isAvailable()) {

            holder.minusButton.setEnabled(false);
            holder.plusButton.setEnabled(false);
            convertView.setClickable(false);
            convertView.setFocusable(false);

            holder.productNameTextView.setTextColor(COLOR_GREY_TEXT);
            holder.productPriceTextView.setTextColor(COLOR_GREY_TEXT);
            holder.quantityTextView.setTextColor(COLOR_GREY_TEXT);

            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

            holder.minusButton.setBackgroundTintList(ColorStateList.valueOf(COLOR_DISABLED_BUTTON_BLUE));
            holder.plusButton.setBackgroundTintList(ColorStateList.valueOf(COLOR_DISABLED_BUTTON_BLUE));

        } else {

            convertView.setClickable(true);
            convertView.setFocusable(true);

            holder.productNameTextView.setTextColor(COLOR_DEFAULT_PRODUCT_NAME);
            holder.productPriceTextView.setTextColor(COLOR_DEFAULT_PRODUCT_PRICE);
            holder.quantityTextView.setTextColor(Color.BLACK);

            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

            holder.minusButton.setBackgroundTintList(ColorStateList.valueOf(COLOR_ACTIVE_BUTTON_BLUE));
            holder.plusButton.setBackgroundTintList(ColorStateList.valueOf(COLOR_ACTIVE_BUTTON_BLUE));

            holder.minusButton.setEnabled(product.getQuantity() != 0);
            holder.plusButton.setEnabled(true);

        }

        if (product.isAvailable()) {

            holder.minusButton.setOnClickListener(v -> {

                int currentQuantity = product.getQuantity();

                if (currentQuantity > 0) {
                    product.setQuantity(currentQuantity - 1);
                    notifyDataSetChanged();
                }

            });

            holder.plusButton.setOnClickListener(v -> {

                int currentQuantity = product.getQuantity();

                product.setQuantity(currentQuantity + 1);

                notifyDataSetChanged();

            });

        } else {

            holder.minusButton.setOnClickListener(null);
            holder.plusButton.setOnClickListener(null);

        }

        return convertView;

    }

    static class ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;
        ImageButton minusButton;
        TextView quantityTextView;
        ImageButton plusButton;
    }

}
