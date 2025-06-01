package com.example.deliveryapp.util;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import java.util.Random;

public class RandomDeliveryMinutes {

    public static int getDeliveryMinutes() {

        Random random = new Random();
        int randomIndex = random.nextInt(9);

        return 20 + (randomIndex * 5);

    }

}