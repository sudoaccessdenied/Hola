package com.melonskart.HolaClient;

import com.melonskart.HolaServer.ServerImplementation;
import com.melonskart.HolaServer.ServerInterface;



import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientBinder {

    private static ServerInterface server;

    private static final String SERVER_INTERFACE = "server";

    private static String host = "localhost";


    // first call this method to connect
    public static void connectToServer() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host, 5555);
        server = (ServerInterface) registry.lookup(SERVER_INTERFACE);

    }

    public   static ServerInterface getInstance() {
        return server;
    }
    public static void setHost(String lhost){ host = lhost;}

}
