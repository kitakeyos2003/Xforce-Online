/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitakeyos.network;

/**
 *
 * @author Admin
 */
public interface ISession {

    public abstract boolean isConnected();

    public abstract void setHandler(IMessageHandler messageHandler);
    
    public abstract void setService(Service service);

    public abstract void sendMessage(Message message);

    public abstract void close();

    public abstract void disconnect();
}
