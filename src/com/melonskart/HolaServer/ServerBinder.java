package com.melonskart.HolaServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerBinder {

    private static final String SERVER_INTERFACE = "server";
    private static Registry registry;
    private static  ServerInterface server;

    protected static boolean startServer()  {

        try {
            registry = LocateRegistry.createRegistry(5555);
            server = new ServerImplementation();
            registry.rebind(SERVER_INTERFACE,server);

        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

            return true;

    }

    protected static boolean stopServer()  {

        if (registry != null) {
            try {
                registry.unbind(SERVER_INTERFACE);

                server = null;
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            } catch (NotBoundException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
