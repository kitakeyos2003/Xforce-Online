/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitakeyos.model;

import lombok.Data;

/**
 *
 * @author Admin
 */
@Data
public class Char {

    private int id;
    private String name;
    private byte gender;
    private int hp;
    private int maxHP;
    private int exp;
    private int gold;
    private int luong;
    private short head, body, leg;
    private short gunID;
    private short range;
    private short delayShoot;
    private int nemBom, knife;
    private int changeStatus;
    private int degenHP;
    private int lienThanh;

    private int idXe;
    private byte lvXe;
    private short speed;

    private short iconClan;
    private short effKnife;

    private short x, y;

}
