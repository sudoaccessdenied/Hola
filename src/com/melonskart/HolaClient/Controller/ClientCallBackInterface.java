package com.melonskart.HolaClient.Controller;

import com.mongodb.BasicDBObject;

import javax.swing.text.Document;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

public interface ClientCallBackInterface extends Remote {
    long getMobile() throws RemoteException;
    boolean notifyChanges(Long from, String msg, int messageId, Date date) throws  RemoteException;

    boolean messageAlert(Long from, String msg, int messageId, Date date) throws RemoteException;
    // function to to be call by server
//    String getMobile() throws RemoteException;
//    String getTo() throws  RemoteException;
//    void notifyChanges(Vector msg) throws RemoteException;
}
