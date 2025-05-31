package com.example.deliveryapp.util;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.deliveryapp.R;

import java.util.List;

public class StoreAdapter extends ArrayAdapter<Store> {

    private Store selectedStore = null;

    public StoreAdapter(Context context, List<Store> stores) {
        super(context, 0, stores);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Store store = getItem(position);

        ViewHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.store_list_item, parent, false);
            holder = new ViewHolder();
            holder.logoImageView = convertView.findViewById(R.id.storeLogo);
            holder.nameTextView = convertView.findViewById(R.id.storeName);

            holder.descriptionTextView = convertView.findViewById(R.id.storeDescription);
            holder.avgRatingTextView = convertView.findViewById(R.id.storeAvgRating);
            holder.priceRangeTextView = convertView.findViewById(R.id.storePriceRange);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        assert store != null;
        holder.nameTextView.setText(store.getStoreName());

        @SuppressLint("CutPasteId") ImageView logoImageView = convertView.findViewById(R.id.storeLogo);
        @SuppressLint("CutPasteId") TextView nameTextView = convertView.findViewById(R.id.storeName);

        nameTextView.setText(store.getStoreName());

        byte[] logoBytes = store.getStoreLogoBytes();

        if (logoBytes != null && logoBytes.length > 0) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(logoBytes, 0, logoBytes.length);

            if (bitmap != null) {
                logoImageView.setImageBitmap(bitmap);
            } else {
                logoImageView.setImageResource(R.drawable.logo_transparent);
            }

        } else {
            logoImageView.setImageResource(R.drawable.logo_transparent);
        }

        holder.descriptionTextView.setText(String.format("Category: %s", store.getFoodCategory()));
        holder.avgRatingTextView.setText(String.format("Avg Rating: %.1f ★", store.getStars()));
        holder.priceRangeTextView.setText(String.format("Price Range: %s", store.getStorePriceRange()));

        if (store.equals(selectedStore)) {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.box_stroke_color));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        }

        return convertView;

    }


    static class ViewHolder {
        ImageView logoImageView;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView avgRatingTextView;
        TextView priceRangeTextView;
    }

    public void setSelectedStore(Store store) {
        this.selectedStore = store;
        notifyDataSetChanged();
    }

    public Store getSelectedStore() {
        return selectedStore;
    }

    public boolean isItemSelected() {
        return selectedStore != null;
    }

    public void clearSelection() {
        this.selectedStore = null;
        notifyDataSetChanged();
    }

}
