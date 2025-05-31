package com.example.deliveryapp.util;

import java.util.Random;

public class RandomDeliveryMinutes {

    /**
     * Returns a random integer between 20 and 60 (inclusive),
     * that is a multiple of 5.
     * Possible values are: 20, 25, 30, 35, 40, 45, 50, 55, 60.
     *
     * @return A random integer (20, 25, ..., 60).
     */
    public static int getDeliveryMinutes() {

        Random random = new Random();
        int randomIndex = random.nextInt(9);

        // Convert the random index back to the desired value
        return 20 + (randomIndex * 5);
    }
}