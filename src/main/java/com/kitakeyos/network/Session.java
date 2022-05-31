/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitakeyos.network;

import com.kitakeyos.model.Char;
import com.kitakeyos.model.User;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class Session implements ISession {

    private byte[] key;
    public Socket sc;
    public DataInputStream dis;
    public DataOutputStream dos;
    public int id;
    public IMessageHandler messageHandler;
    protected boolean connected, login;
    private byte curR, curW;
    private final Sender sender;
    private Thread collectorThread;
    private Service service;
    protected Thread sendThread;
    protected String version;
    protected byte device;
    protected byte zoomLevel;
    protected int width;
    protected int height;
    protected String platform;

    public Session(Socket sc, int id) throws IOException {
        this.sc = sc;
        this.id = id;
        this.dis = new DataInputStream(sc.getInputStream());
        this.dos = new DataOutputStream(sc.getOutputStream());
        setHandler(new MessageHandler(this));
        messageHandler.onConnectOK();
        setService(new Service(this));
        sendThread = new Thread(sender = new Sender());
        collectorThread = new Thread(new MessageCollector());
        collectorThread.start();
    }

    @Override
    public void setService(Service service) {
        this.service = service;
    }

    public void setProfile(Message mss) {
        try {
            this.width = mss.reader().readShort();
            this.height = mss.reader().readShort();
            this.platform = mss.reader().readUTF();
            this.version = mss.reader().readUTF();
            this.zoomLevel = mss.reader().readByte();
            this.device = mss.reader().readByte();
            System.out.println(String.format("w: %d - h: %d - platform: %s - version: %s - zoom: %d - device: %d", width, height, platform, version, zoomLevel, device));
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestImageMap(Message mss) {
        try {
            int size = mss.reader().readShort();
            System.out.println("size: " + size);
            for (int i = 0; i < size; i++) {
                int mapID = mss.reader().readShort();
                System.out.println("mapID: " + mapID);
            }
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestMobTemplate(Message mss) {
        try {
            short mobID = mss.reader().readShort();
            System.out.println("mobID: " + mobID);
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestData() {
        service.setData();
    }

    public void loadEffectAll(Message mss) {
        try {
            short id = mss.reader().readShort();
            service.loadEffectAll(id);
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestImage(Message mss) {
        try {
            byte type = mss.reader().readByte();
            short id = mss.reader().readShort();
            service.requestImage(type, id);
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setVersionData() {
        File file = new File(String.format("resources/x%d/big/", zoomLevel));
        File[] files = file.listFiles();
        HashMap<Integer, String> maps = new HashMap<>();
        for (File f : files) {
            String name = f.getName();
            name = name.replaceAll("Big", "");
            name = name.replaceAll(".png", "");
            int id = Integer.parseInt(name);
            String version = String.valueOf(f.length());
            maps.put(id, name);
        }
        service.setVersionData(maps);
    }

    public void login(Message mss) {
        try {
            String username = mss.reader().readUTF();
            String password = mss.reader().readUTF();
            System.out.println(String.format("%s logging..", username));
            User user = new User(username, password);
            int t = user.login();
            if (t == User.LOGIN_SUCCESS) {
                service.login(user);
                Char _c = new Char();
                _c.setId(1);
                _c.setName("admin");
                _c.setGender((byte) 0);
                _c.setMaxHP(10000);
                _c.setHp(10000);
                _c.setExp(10000);
                _c.setGold(1000000);
                _c.setLuong(1000);
                _c.setHead((short) 0);
                _c.setBody((short) 1);
                _c.setLeg((short) 2);
                _c.setGunID((short) 0);
                _c.setRange((short) 100);
                _c.setDelayShoot((short) 10);
                _c.setNemBom(0);
                _c.setKnife(0);
                _c.setChangeStatus(0);
                _c.setDegenHP(0);
                _c.setLienThanh(0);
                _c.setIdXe(-1);
                _c.setIconClan((short) 0);
                _c.setEffKnife((byte) 0);
                _c.setX((short) 50);
                _c.setY((short) 10);
                service.loadAll(_c);
                service.mapInfo(_c);

            } else if (t == User.LOGIN_INCORRECT) {
                service.loginFail("Tài khoản hoặc mật khẩu không chính xác!");
            }
        } catch (IOException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void setHandler(IMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void sendMessage(Message message) {
        sender.addMessage(message);
    }

    protected synchronized void doSendMessage(Message m) throws IOException {
        if (m == null) {
            return;
        }
        byte[] data = m.getData();
        byte b = m.getCommand();
        if (connected) {
            dos.writeByte(writeKey(b));
        } else {
            dos.writeByte(b);
        }
        if (data != null) {
            int size = data.length;
            if (connected) {
                if (b == Cmd.REQUEST_IMAGE_MAP || b == Cmd.ATTACK || b == Cmd.LOAD_MOB_TEMPLATE || b == Cmd.LOAD_OBS_TEMPLATE || b == Cmd.LOAD_EFFECT_ALL || b == Cmd.FRIEND_LIST) {
                    int num = writeKey((byte) (size >> 24));
                    dos.writeByte((byte) num);
                    int num2 = writeKey((byte) (size >> 16));
                    dos.writeByte((byte) num2);
                    int num3 = writeKey((byte) (size >> 8));
                    dos.writeByte((byte) num3);
                    int num4 = writeKey((byte) (size & 255));
                    dos.writeByte((byte) num4);
                } else {
                    int num3 = writeKey((byte) (size >> 8));
                    dos.writeByte((byte) num3);
                    int num4 = writeKey((byte) (size & 255));
                    dos.writeByte((byte) num4);
                }
            } else {
                if (b == Cmd.REQUEST_IMAGE_MAP || b == Cmd.ATTACK || b == Cmd.LOAD_MOB_TEMPLATE || b == Cmd.LOAD_OBS_TEMPLATE || b == Cmd.LOAD_EFFECT_ALL || b == Cmd.FRIEND_LIST) {
                    dos.writeInt(size);
                } else {
                    dos.writeByte(size & 256);
                    dos.writeByte(size & 255);
                }
            }
            if (connected) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = writeKey(data[i]);
                }
            }
            dos.write(data);
        }
        dos.flush();
        m.cleanup();
    }

    private byte readKey(byte b) {
        byte b2 = curR;
        curR = (byte) (b2 + 1);
        byte result = (byte) ((key[(int) b2] & 255) ^ ((int) b & 255));
        if (curR >= key.length) {
            curR = (byte) (curR % key.length);
        }
        return result;
    }

    private byte writeKey(byte b) {
        byte b2 = curW;
        curW = (byte) (b2 + 1);
        byte result = (byte) ((key[(int) b2] & 255) ^ ((int) b & 255));
        if (curW >= key.length) {
            curW = (byte) (curW % key.length);
        }
        return result;
    }

    @Override
    public void close() {
        try {
            if (isConnected()) {
                messageHandler.onDisconnected();
            }
            cleanNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        if (sc != null && sc.isConnected()) {
            try {
                sc.close();
            } catch (IOException ex) {
                //logger.error("failed!", ex);
            }
        }
    }

    private void cleanNetwork() {
        curR = 0;
        curW = 0;
        connected = false;
        login = false;
        try {
            if (sc != null) {
                sc.close();
                sc = null;
            }
            if (dos != null) {
                dos.close();
                dos = null;
            }
            if (dis != null) {
                dis.close();
                dis = null;
            }
            if (sendThread != null && sendThread.isAlive()) {
                sendThread.stop();
                sendThread = null;
            }
            if (collectorThread != null && collectorThread.isAlive()) {
                collectorThread.stop();
                collectorThread = null;
            }
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Client " + this.id;
    }

    public void generateKey(int size) {
        this.key = "kitakeyos".getBytes();
    }

    public void sendKey() throws IOException {
        if (connected) {
            return;
        }
        Message ms = new Message(Cmd.GET_SESSION_ID);
        DataOutputStream ds = ms.writer();
        ds.writeByte(key.length);
        ds.writeByte(key[0]);
        for (int i = 1; i < key.length; i++) {
            ds.writeByte(key[i] ^ key[i - 1]);
        }
        ds.flush();
        doSendMessage(ms);
        ms.cleanup();
        connected = true;
        sendThread.start();
    }

    private class Sender implements Runnable {

        private final ArrayList<Message> sendingMessage;

        public Sender() {
            sendingMessage = new ArrayList<>();
        }

        public void addMessage(Message message) {
            sendingMessage.add(message);
        }

        @Override
        public void run() {
            try {
                while (isConnected()) {
                    while (sendingMessage.size() > 0) {
                        Message m = sendingMessage.get(0);
                        //ServerManager.log("Send mss " + m.getCommand() + " to " + Session.this.toString());
                        doSendMessage(m);
                        sendingMessage.remove(0);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    class MessageCollector implements Runnable {

        @Override
        public void run() {
            while (!sc.isClosed() && dis != null) {
                try {
                    Message message = readMessage();
                    try {
                        if (message != null) {
                            if (message.getCommand() == Cmd.GET_SESSION_ID) {
                                generateKey(10);
                                sendKey();
                            } else {
                                messageHandler.onMessage(message);
                            }
                        } else {
                            break;
                        }
                    } finally {
                        message.cleanup();
                    }
                } catch (Exception e) {
                    break;
                }
            }
            close();
        }

        private Message readMessage() throws IOException {
            // read message command
            byte cmd = dis.readByte();
            if (connected) {
                cmd = readKey(cmd);
            }
            // read size of data
            int size;
            if (connected) {
                byte b1 = dis.readByte();
                byte b2 = dis.readByte();
                size = (readKey(b1) & 0xff) << 8 | readKey(b2) & 0xff;
            } else {
                size = dis.readShort();
            }
            byte data[] = new byte[size];
            int len = 0;
            int byteRead = 0;
            while (len != -1 && byteRead < size) {
                len = dis.read(data, byteRead, size - byteRead);
                if (len > 0) {
                    byteRead += len;
                }
            }
            if (connected) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = readKey(data[i]);
                }
            }

            Message msg = new Message(cmd, data);
            return msg;
        }
    }

}
