package com.example.deliveryapp.view;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import com.example.deliveryapp.util.*;

public class ClientThread implements Runnable {

    private static final String TAG = "ClientThread"; // Tag for logging

    private Handler handler;
    private String host;
    private int port;
    private String action;
    private String longitude;
    private String latitude;
    private String preference; // Used for price range or ratings

    public static final int MESSAGE_SUCCESS = 1;
    public static final int MESSAGE_ERROR = 0;
    public static final int MESSAGE_NO_RESULTS = 2; // For empty list feedback
    public static final int MESSAGE_GENERIC_RESPONSE = 3; // For actions that don't return List<Store>

    public ClientThread(Handler handler, String host, int port, String longitude, String latitude, String preference, String action) {
        this.handler = handler;
        this.host = host;
        this.port = port;
        this.preference = preference;
        this.latitude = latitude;
        this.longitude = longitude;
        this.action = action;
    }

    @Override
    public void run() {

        String jobID = UUID.randomUUID().toString();
        Socket clientSocket = null;
        ObjectOutputStream outObj = null;
        ObjectInputStream inObj = null;

        try {

            clientSocket = new Socket(host, port);
            outObj = new ObjectOutputStream(clientSocket.getOutputStream());
            inObj = new ObjectInputStream(clientSocket.getInputStream());

            ActionWrapper requestWrapper;
            Object receivedResponse;

            switch (this.action.toLowerCase()) {

                case "showcase_stores":

                    requestWrapper = new ActionWrapper(longitude + "_" + latitude, action, jobID);
                    outObj.writeObject(requestWrapper);
                    outObj.flush();

                    receivedResponse = inObj.readObject();
                    handleShowcaseStoresResponse(receivedResponse);

                    break;

                case "search_price_range":

                    requestWrapper = new ActionWrapper(longitude + "_" + latitude + "_" + preference, action, jobID);
                    outObj.writeObject(requestWrapper);
                    outObj.flush();

                    receivedResponse = inObj.readObject();
                    handleGenericResponse(receivedResponse, "Search by price range successful.");

                    break;

                case "search_ratings":

                    String ratingPref = mapRatingPreference(this.preference);
                    requestWrapper = new ActionWrapper(longitude + "_" + latitude + "_" + ratingPref, action, jobID);
                    outObj.writeObject(requestWrapper);
                    outObj.flush();

                    receivedResponse = inObj.readObject();
                    handleGenericResponse(receivedResponse, "Search by ratings successful.");

                    break;

                case "purchase_product":

                    String storeName = "some_store_name";
                    String productName = "some_product_name";

                    requestWrapper = new ActionWrapper(longitude + "_" + latitude + "_" + storeName + "_" + productName, action, jobID);
                    outObj.writeObject(requestWrapper);
                    outObj.flush();

                    receivedResponse = inObj.readObject();
                    handleGenericResponse(receivedResponse, "Product purchase initiated.");

                    break;

                case "rate_store":

                    String storeToRate = "some_store_name"; // You need to get this from UI input
                    requestWrapper = new ActionWrapper(longitude + "_" + latitude + "_" + storeToRate + "_" + preference, action, jobID);
                    outObj.writeObject(requestWrapper);
                    outObj.flush();

                    receivedResponse = inObj.readObject();
                    handleGenericResponse(receivedResponse, "Store rating submitted.");

                    break;

                default:

                    Log.w(TAG, "Unknown action: " + this.action);
                    sendErrorMessage("Unknown action requested.");

                    break;

            }

        } catch (IOException e) {
            Log.e(TAG, "Network or I/O error: " + e.getMessage(), e);
            sendErrorMessage("Network error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Class not found error during deserialization: " + e.getMessage(), e);
            sendErrorMessage("Data error: " + e.getMessage());
        } catch (Exception e) { // Catch any other unexpected exceptions
            Log.e(TAG, "An unexpected error occurred: " + e.getMessage(), e);
            sendErrorMessage("An unexpected error occurred: " + e.getMessage());
        } finally {

            try {
                if (outObj != null) outObj.close();
                if (inObj != null) inObj.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing resources: " + e.getMessage(), e);
            }

        }

    }

    private void handleShowcaseStoresResponse(Object receivedObject) {
        if (receivedObject instanceof ActionWrapper) {
            ActionWrapper w = (ActionWrapper) receivedObject;
            if ("final_results".equalsIgnoreCase(w.getAction())) { // Server sends "final_results" for stores
                Object resObj = w.getObject();
                if (resObj instanceof List<?>) {
                    try {
                        List<Store> finalResults = (List<Store>) resObj;
                        Message message = Message.obtain();
                        if (finalResults != null && !finalResults.isEmpty()) {
                            message.what = MESSAGE_SUCCESS;
                            message.obj = finalResults;
                            Log.d(TAG, "Received " + finalResults.size() + " stores.");
                        } else {
                            message.what = MESSAGE_NO_RESULTS;
                            message.obj = null;
                            Log.d(TAG, "No stores found.");
                        }
                        handler.sendMessage(message);
                    } catch (ClassCastException e) {
                        Log.e(TAG, "Received object is a List, but elements are not Store: " + e.getMessage(), e);
                        sendErrorMessage("Received invalid store data.");
                    }
                } else {
                    Log.e(TAG, "Expected List<Store> but received: " + (resObj != null ? resObj.getClass().getName() : "null"));
                    sendErrorMessage("Server response format error.");
                }
            } else {
                Log.e(TAG, "Unexpected action for showcase_stores response: " + w.getAction());
                sendErrorMessage("Unexpected server response.");
            }
        } else {
            Log.e(TAG, "Received non-ActionWrapper object: " + (receivedObject != null ? receivedObject.getClass().getName() : "null"));
            sendErrorMessage("Server sent an unreadable response.");
        }
    }

    // Handles responses for actions that don't return a List<Store>,
    // assuming they return a simple confirmation/error message.
    private void handleGenericResponse(Object receivedObject, String successMessage) {
        if (receivedObject instanceof ActionWrapper) {
            ActionWrapper w = (ActionWrapper) receivedObject;
            // Assuming server sends a simple confirmation or error in ActionWrapper.getObject()
            // You might need to adjust this based on your server's exact response for these actions.
            String serverMessage = w.getObject() instanceof String ? (String) w.getObject() : "Operation completed.";

            if ("success".equalsIgnoreCase(w.getAction())) { // Assuming server sends "success" action for success
                sendMessage(successMessage + " Server says: " + serverMessage);
            } else if ("error".equalsIgnoreCase(w.getAction())) { // Assuming server sends "error" action for failure
                sendErrorMessage("Server error: " + serverMessage);
            } else {
                sendMessage("Server responded: " + serverMessage);
            }
        } else {
            Log.e(TAG, "Received non-ActionWrapper object for generic response: " + (receivedObject != null ? receivedObject.getClass().getName() : "null"));
            sendErrorMessage("Server sent an unreadable response for action.");
        }
    }


    private String mapRatingPreference(String pref) {
        switch (pref) {
            case "★☆☆☆☆": return "1";
            case "★★☆☆☆": return "2";
            case "★★★☆☆": return "3";
            case "★★★★☆": return "4";
            case "★★★★★": // Handle 5 stars if it was the default
            default: return "5"; // Default to 5 stars or a sensible default
        }
    }

    private void sendErrorMessage(String message) {
        Message errorMsg = Message.obtain();
        errorMsg.what = MESSAGE_ERROR;
        errorMsg.obj = message;
        handler.sendMessage(errorMsg);
    }

    private void sendMessage(Object obj) {
        Message msg = Message.obtain();
        msg.what = ClientThread.MESSAGE_GENERIC_RESPONSE;
        msg.obj = obj;
        handler.sendMessage(msg);
    }
}