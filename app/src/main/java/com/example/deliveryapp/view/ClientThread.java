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

    private static final String TAG = "ClientThread";

    private Handler handler;
    private String host;
    private int port;
    private String action;
    private String longitude;
    private String latitude;
    private String preference;

    public static final int MESSAGE_SUCCESS = 1;
    public static final int MESSAGE_ERROR = 0;
    public static final int MESSAGE_NO_RESULTS = 2;
    public static final int MESSAGE_GENERIC_RESPONSE = 3;

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
            outObj.flush();
            inObj = new ObjectInputStream(clientSocket.getInputStream());

            ActionWrapper requestWrapper;
            Object receivedResponse;

            switch (this.action.toLowerCase()) {

                case "showcase_stores":

                    requestWrapper = new ActionWrapper(longitude + "_" + latitude, action, jobID);
                    outObj.writeObject(requestWrapper);
                    outObj.flush();

                    receivedResponse = inObj.readObject();
                    handleStoreListResponse(receivedResponse);

                    break;

                case "search_price_range":

                    requestWrapper = new ActionWrapper(longitude + "_" + latitude + "_" + preference, action, jobID);
                    outObj.writeObject(requestWrapper);
                    outObj.flush();

                    receivedResponse = inObj.readObject();
                    handleStoreListResponse(receivedResponse);

                    break;

                case "search_ratings":

                    requestWrapper = new ActionWrapper(longitude + "_" + latitude + "_" + mapRatingPreference(this.preference), action, jobID);
                    outObj.writeObject(requestWrapper);
                    outObj.flush();

                    receivedResponse = inObj.readObject();
                    handleStoreListResponse(receivedResponse);

                    break;

                case "purchase_product":

                    String storeName = "some_store_name";
                    String productName = "some_product_name";

                    requestWrapper = new ActionWrapper(longitude + "_" + latitude + "_" + storeName + "_" + productName, action, jobID);
                    outObj.writeObject(requestWrapper);
                    outObj.flush();

                    receivedResponse = inObj.readObject();
                    handleStoreListResponse(receivedResponse);

                    break;

                case "rate_store":

                    String[] parts = preference.split("_",2);

                    requestWrapper = new ActionWrapper(longitude + "_" + latitude + "_" + parts[0] + "_" + mapRatingPreference(parts[1]), action, jobID);
                    outObj.writeObject(requestWrapper);
                    outObj.flush();

                    receivedResponse = inObj.readObject();
                    handleStoreListResponse(receivedResponse);

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

    private void handleStoreListResponse(Object receivedObject) {

        if (receivedObject instanceof ActionWrapper) {

            ActionWrapper w = (ActionWrapper) receivedObject;

            if ("final_results".equalsIgnoreCase(w.getAction())) {

                Object resObj = w.getObject();

                if (resObj instanceof List) {

                    try {

                        List<Store> finalResults = (List<Store>) resObj;
                        Message message = Message.obtain();

                        if (finalResults != null && !finalResults.isEmpty()) {

                            message.what = MESSAGE_SUCCESS;
                            message.obj = finalResults;
                            Log.d(TAG, "Received " + finalResults.size() + " stores for search.");

                        } else {

                            message.what = MESSAGE_NO_RESULTS;
                            message.obj = null;
                            Log.d(TAG, "No stores found for search.");

                        }

                        handler.sendMessage(message);

                    } catch (ClassCastException e) {

                        Log.e(TAG, "Received List, but elements are not Store: " + e.getMessage(), e);
                        sendErrorMessage("Received invalid store data for search.");

                    }

                } else {

                    Log.e(TAG, "Server did not send a List<Store> for final_results: " + (resObj != null ? resObj.getClass().getName() : "null"));
                    sendErrorMessage("Server response format error for search: Expected List<Store>.");

                }

            } else if ("error".equalsIgnoreCase(w.getAction())) {

                String errorMessage = (w.getObject() instanceof String) ? (String) w.getObject() : "Server error during search.";
                sendErrorMessage(errorMessage);

            } else {

                Log.e(TAG, "Unexpected action from server for store list search: " + w.getAction());
                sendErrorMessage("Server responded with an unexpected action: " + w.getAction());

            }

        } else {

            Log.e(TAG, "Received non-ActionWrapper object for store list response: " + (receivedObject != null ? receivedObject.getClass().getName() : "null"));
            sendErrorMessage("Server sent an unreadable response object for search.");

        }

    }

    private void handleGenericResponse(Object receivedObject, String successMessage) {

        if (receivedObject instanceof ActionWrapper) {

            ActionWrapper w = (ActionWrapper) receivedObject;
            String serverMessage = w.getObject() instanceof String ? (String) w.getObject() : "Operation completed.";

            if ("success".equalsIgnoreCase(w.getAction())) {
                sendMessage(successMessage + " Server says: " + serverMessage);
            } else if ("error".equalsIgnoreCase(w.getAction())) {
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
            case "★★★★★":
            default: return "5";
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