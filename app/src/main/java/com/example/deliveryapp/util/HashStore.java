package com.example.deliveryapp.util;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

public class HashStore {

    public HashStore() {}

    public static int getWorkerID(String storeName, int numOfWorkers) {

        int hash = Math.abs(storeName.toLowerCase().hashCode());

        return (hash % numOfWorkers) + 1;

    }

}