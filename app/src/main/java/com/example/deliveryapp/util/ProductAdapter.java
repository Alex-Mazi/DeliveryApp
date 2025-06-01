package com.example.deliveryapp.util;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import android.annotation.SuppressLint;
import android.content.Context;
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

        holder.minusButton.setOnClickListener(v -> {

            int currentQuantity = product.getQuantity();

            if (currentQuantity > 0) {

                product.setQuantity(0);
                notifyDataSetChanged();

            }

        });

        holder.plusButton.setOnClickListener(v -> {

            int currentQuantity = product.getQuantity();

            if (currentQuantity == 0) {
                product.setQuantity(1);
                notifyDataSetChanged();
            }

        });

        if (product.getQuantity() == 0) {

            holder.minusButton.setEnabled(false);
            holder.plusButton.setEnabled(true);
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

        } else {

            holder.minusButton.setEnabled(true);
            holder.plusButton.setEnabled(false);
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.box_stroke_color));

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
