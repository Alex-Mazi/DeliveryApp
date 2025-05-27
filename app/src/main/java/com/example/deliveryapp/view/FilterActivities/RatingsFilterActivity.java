package com.example.deliveryapp.view.FilterActivities;

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
import com.example.deliveryapp.view.ClientThread;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class RatingsFilterActivity extends AppCompatActivity {

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

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
            } else {
                Thread clientThread = new Thread(new ClientThread("192.168.56.1", 5000, longitude,latitude,selectedCategory,"showcase_stores", "Client"));
                clientThread.start();

            }

        });

        backButton.setOnClickListener(v -> finish());

    }

}
