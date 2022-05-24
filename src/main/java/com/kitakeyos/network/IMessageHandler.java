/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitakeyos.network;

public interface IMessageHandler {

    public void onMessage(Message message);

    public void onConnectionFail();

    public void onDisconnected();

    public void onConnectOK();
}
