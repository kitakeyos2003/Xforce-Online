/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitakeyos.network;

import java.io.IOException;

/**
 *
 * @author Admin
 */
public class MessageHandler implements IMessageHandler {

    private Session session;

    public MessageHandler(Session session) {
        this.session = session;
    }

    @Override
    public void onMessage(Message message) {
        if (message != null) {
            byte command = message.getCommand();
            try {
                switch (command) {
                    case Cmd.SUB_CMD:
                        messageSubCommand(message);
                        break;
                    case Cmd.NOT_LOGIN:
                        messageNotLogin(message);
                        break;
                    case Cmd.NOT_MAP:
                        this.messageNotMap(message);
                        break;
                        
                    case Cmd.DEVICE_PROFILE:
                        session.setProfile(message);
                        break;
                        
                    case Cmd.REQUEST_IMAGE_MAP:
                        session.requestImageMap(message);
                        break;
                        
                    case Cmd.LOAD_MOB_TEMPLATE:
                        session.requestMobTemplate(message);
                        break;
                        
                    case Cmd.VERSION_IMAGE:
                        session.setVersionData();
                        break;
                        
                    case Cmd.GET_DATA:
                        session.requestData();
                        break;
                        
                    case Cmd.LOAD_EFFECT_ALL:
                        session.loadEffectAll(message);
                        break;
                        
                    case Cmd.LOAD_CMM_IMAGE:
                        session.requestImage(message);
                        break;

                    default:
                        System.out.println(String.format("Client %d: onMessage: %d", session.id, command));
                        break;
                }
            } catch (Exception e) {
                System.out.println(String.format("failed! - onMessage: %d", command));
            }
        }
    }

    public void messageSubCommand(Message mss) throws IOException {
        if (mss != null) {
            byte command = mss.reader().readByte();
            try {
                switch (command) {

                    default:
                        System.out.println(String.format("Client %d: messageSubCommand: %d", session.id, command));
                        break;
                }
            } catch (Exception ex) {
                System.out.println(String.format("failed! - subCommand: %d", command));
            }
        }
    }

    public void messageNotLogin(Message mss) throws IOException {
        if (mss != null) {
            byte command = mss.reader().readByte();
            try {
                switch (command) {
                    
                    case Cmd.LOGIN:
                        session.login(mss);
                        break;

                    default:
                        System.out.println(String.format("Client %d: messageNotLogin: %d", session.id, command));
                        break;
                }
            } catch (Exception ex) {
                System.out.println(String.format("failed! - notLogin: %d", command));
            }
        }
    }

    public void messageNotMap(Message mss) throws IOException {
        if (mss != null) {
            byte command = mss.reader().readByte();
            try {
                switch (command) {

                    default:
                        System.out.println(String.format("Client %d: messageNotMap: %d", session.id, command));
                        break;
                }
            } catch (Exception ex) {
                System.out.println(String.format("failed! - notMap: %d", command));
            }
        }
    }

    @Override
    public void onConnectionFail() {
        System.out.println(String.format("Client %d: Kết nối thất bại!", session.id));
    }

    @Override
    public void onDisconnected() {
        System.out.println(String.format("Client %d: Mất kết nối!", session.id));
    }

    @Override
    public void onConnectOK() {
        System.out.println(String.format("Client %d: Kết nối thành công!", session.id));
    }

}
