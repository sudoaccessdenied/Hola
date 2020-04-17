package com.melonskart.HolaServer;

import com.melonskart.HolaClient.Controller.ClientCallBackInterface;
import org.bson.Document;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface ServerInterface extends Remote {

    String getName(long mobile) throws RemoteException;
    boolean registorClient(ClientCallBackInterface client) throws RemoteException;
    boolean unRegistorClient(ClientCallBackInterface clientCallBackInterface) throws RemoteException;
    boolean createUser(long mobile,String name,String password,int otp) throws RemoteException;
    boolean sendOTP(long mobile) throws RemoteException;
    boolean loginUser(long mobile, String password) throws  RemoteException;
    HashMap<Long, String> friend(long mobile) throws RemoteException;
    Document messages(long from, long mobile) throws RemoteException;

    String addContacts(long currentUser, long userToAdd) throws RemoteException;
    boolean sendMessage(long from, long to, String msg) throws RemoteException;
    boolean checkOnline(long to) throws  RemoteException;

}
