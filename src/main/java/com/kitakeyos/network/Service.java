/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitakeyos.network;

import com.kitakeyos.model.Char;
import com.kitakeyos.model.User;
import com.kitakeyos.xforce.Config;
import com.kitakeyos.xforce.Util;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class Service {

    private Session session;

    public Service(Session session) {
        this.session = session;
    }

    public void setVersionData(HashMap<Integer, String> datas) {
        try {
            Message ms = new Message(Cmd.VERSION_IMAGE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(datas.size());
            for (Map.Entry<Integer, String> e : datas.entrySet()) {
                ds.writeByte(e.getKey());
                ds.writeUTF(e.getValue());
            }
            ds.writeUTF(Config.VERSION_DATA);
            ds.flush();
            session.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setData() {
        try {
            byte[] images = Util.getFile("resources/data/image");
            byte[] parts = Util.getFile("resources/data/part");
            Message ms = new Message(Cmd.GET_DATA);
            DataOutputStream ds = ms.writer();
            ds.writeInt(images.length);
            ds.write(images);
            ds.writeInt(parts.length);
            ds.write(parts);
            ds.flush();
            session.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadEffectAll(int id) {
        try {
            Message ms = new Message(Cmd.LOAD_EFFECT_ALL);
            DataOutputStream ds = ms.writer();
            ds.writeShort(id);
            ds.writeInt(0);
            ds.writeByte(0);
            ds.writeShort(0);
            ds.writeShort(0);
            ds.writeByte(0);
            ds.writeByte(0);
            ds.flush();
            session.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestImage(byte type, int id) {
        try {
            System.out.println("effectServer: " + id);
            byte[] data = Util.getFile(String.format("resources/x%d/effectServer/%d/Big.png", session.zoomLevel, id));
            if (data != null) {
                Message ms = new Message(Cmd.LOAD_CMM_IMAGE);
                DataOutputStream ds = ms.writer();
                ds.writeByte(type);
                ds.writeShort(id);
                ds.writeInt(data.length);
                ds.write(data);
                ds.flush();
                session.sendMessage(ms);
                ms.cleanup();
            }
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadAll(Char _c) {
        try {
            Message ms = new Message(Cmd.ME_LOAD_ALL);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_c.getId());
            ds.writeUTF(_c.getName());
            ds.writeByte(_c.getGender());
            ds.writeInt(_c.getMaxHP());
            ds.writeInt(_c.getHp());
            ds.writeInt(_c.getExp());
            ds.writeInt(_c.getGold());
            ds.writeInt(_c.getLuong());
            ds.writeShort(_c.getHead());
            ds.writeShort(_c.getBody());
            ds.writeShort(_c.getLeg());
            ds.writeShort(_c.getGunID());
            ds.writeShort(_c.getRange());
            ds.writeShort(_c.getDelayShoot());
            ds.writeInt(_c.getNemBom());
            ds.writeInt(_c.getKnife());
            ds.writeInt(_c.getChangeStatus());
            ds.writeInt(_c.getDegenHP());
            ds.writeByte(_c.getLienThanh());
            ds.writeShort(_c.getIdXe());
            if (_c.getIdXe() != -1) {
                ds.writeByte(_c.getLvXe());
                ds.writeInt(_c.getMaxHP());
                ds.writeInt(_c.getHp());
                ds.writeShort(_c.getSpeed());
            }
            ds.writeShort(_c.getIconClan());
            ds.writeShort(_c.getEffKnife());
            ds.flush();
            session.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mapInfo(Char _c) {
        try {
            Message ms = new Message(Cmd.MAP_JOIN);
            DataOutputStream ds = ms.writer();
            ds.writeByte(0);// zoneID
            ds.writeInt(_c.getHp());
            ds.writeInt(_c.getMaxHP());
            ds.writeShort(_c.getX());
            ds.writeShort(_c.getY());
            ds.writeInt(0);// team
            ds.writeInt(0);// medal
            ds.writeByte(0);// mob size
            //mob
            ds.writeByte(0);// obs size
            //obs
            ds.writeByte(0);// player size
            //player

            ds.writeInt(0);// tilemap
            ds.writeByte(0);//null
            ds.writeUTF("đéo có");
            ds.flush();
            session.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openDialog(String text) {
        try {
            Message ms = new Message(Cmd.DIALOG);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.flush();
            session.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loginFail(String text) {
        try {
            Message ms = messageNotLogin(Cmd.LOGIN);
            DataOutputStream ds = ms.writer();
            ds.writeBoolean(true);
            ds.writeUTF(text);
            ds.flush();
            session.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void login(User user) {
        try {
            Message ms = messageNotLogin(Cmd.LOGIN);
            DataOutputStream ds = ms.writer();
            ds.writeBoolean(false);
            ds.writeBoolean(user.isUpdateUser());
            ds.writeBoolean(user.isActive());
            ds.flush();
            session.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Message messageNotLogin(int command) {
        try {
            Message ms = new Message(Cmd.NOT_LOGIN);
            ms.writer().writeByte(command);
            return ms;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Message messageNotMap(int command) {
        try {
            Message ms = new Message(Cmd.NOT_MAP);
            ms.writer().writeByte(command);
            return ms;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Message messageSubCommand(int command) {
        try {
            Message ms = new Message(Cmd.SUB_CMD);
            ms.writer().writeByte(command);
            return ms;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
