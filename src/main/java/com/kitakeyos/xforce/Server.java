/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitakeyos.xforce;

import com.kitakeyos.network.Session;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(14445);
            int id = 0;
            System.out.println("Listening port 14445");
            while (true) {
                try {
                    Socket client = server.accept();
                    Session cl = new Session(client, ++id);
                    System.out.println("Accept socket " + cl + " done!");
                } catch (IOException e) {
                    
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
