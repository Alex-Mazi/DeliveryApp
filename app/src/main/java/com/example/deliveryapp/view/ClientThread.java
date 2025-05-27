package com.example.deliveryapp.view;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import com.example.deliveryapp.util.*;

/**
 * @author      Alexandra-Maria Mazi || p3220111
 * @author      Christina Perifana   || p3220160
 **/

public class ClientThread implements Runnable{

    private String host;
    private int port;
    private String role;
    private String action;
    private String longitude;
    private String latitude;
    private String preference;
    private final Object lock = new Object();

    public ClientThread(String host, int port, String longitude,String latitude,String preference, String action, String role) {
        this.host = host;
        this.port = port;
        this.preference = preference;
        this.latitude = latitude;
        this.longitude = longitude;
        this.role = role;
        this.action = action;

    }

    public void run() {

        try {

            Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inObj;
            String jobID = UUID.randomUUID().toString();

            try {

                if (this.action.equalsIgnoreCase("showcase_stores")) {

                    try {

                        Socket clientSocket = new Socket(host, port);
                        ObjectOutputStream outObj = new ObjectOutputStream(clientSocket.getOutputStream());
                        outObj.writeObject(new ActionWrapper(longitude + "_" + latitude, action, jobID));
                        outObj.flush();

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                } else if (this.action.equalsIgnoreCase("search_price_range")) {

                    try {

                        Socket clientSocket = new Socket(host, port);
                        ObjectOutputStream outObj = new ObjectOutputStream(clientSocket.getOutputStream());
                        outObj.writeObject(new ActionWrapper(longitude + "_" + latitude + "_" + preference, action, jobID));
                        outObj.flush();


                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                } else if (this.action.equalsIgnoreCase("search_ratings")) {

                    switch (preference) {
                        case "★☆☆☆☆":
                            this.preference = "1";
                            break;
                        case "★★☆☆☆":
                            this.preference = "2";
                            break;
                        case "★★★☆☆":
                            this.preference = "3";
                            break;
                        case "★★★★☆":
                            this.preference = "4";
                            break;
                        default:
                            this.preference = "5";
                            break;
                    }

                    try {

                        Socket clientSocket = new Socket(host, port);
                        ObjectOutputStream outObj = new ObjectOutputStream(clientSocket.getOutputStream());
                        outObj.writeObject(new ActionWrapper(longitude + "_" + latitude + "_" + preference, action, jobID));
                        outObj.flush();


                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                } else if (this.action.equalsIgnoreCase("purchase_product")) {

                    String store = null, product = null;


                    try {

                        Socket clientSocket = new Socket(host, port);
                        ObjectOutputStream outObj = new ObjectOutputStream(clientSocket.getOutputStream());
                        outObj.writeObject(new ActionWrapper(longitude + "_" + latitude + "_" + store + "_" + product, action, jobID));
                        outObj.flush();

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                } else if (this.action.equalsIgnoreCase("rate_store")) {

                    String store = "";

                    try {

                        Socket clientSocket = new Socket(host, port);
                        ObjectOutputStream outObj = new ObjectOutputStream(clientSocket.getOutputStream());
                        outObj.writeObject(new ActionWrapper(longitude + "_" + latitude + "_" + store + "_" + preference, action, jobID));
                        outObj.flush();

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                }

                    /*
                        if (this.action.equalsIgnoreCase("search_food_preference")) {

                            String longitude1, latitude1, preference;

                            longitude1 = String.valueOf(longitude);
                            latitude1 = String.valueOf(latitude);

                            try {

                                Socket clientSocket = new Socket(host, port);
                                ObjectOutputStream outObj = new ObjectOutputStream(clientSocket.getOutputStream());
                                outObj.writeObject(new ActionWrapper(longitude1 + "_" + latitude1 + "_" + preference, action, jobID));
                                outObj.flush();

                                break;

                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                        }
                    */


            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {

            System.out.println("[Client " + role + "] Waiting for server on " + host + ":" + port + "...");

            synchronized (lock) {
                try {
                    lock.wait(500);
                } catch (InterruptedException ignored) {
                }
            }

        }

    }

}
