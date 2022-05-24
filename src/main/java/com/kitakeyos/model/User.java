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
public class User {

    public static final byte LOGIN_SUCCESS = 0;
    public static final byte LOGIN_INCORRECT = 1;
    public static final byte USERNAME_EMPTY = 2;
    public static final byte PASSWORD_EMPTY = 3;

    private String username;
    private String password;
    private boolean isUpdateUser;
    private boolean isActive;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int login() {
        if (username.isEmpty()) {
            return USERNAME_EMPTY;
        }
        if (password.isEmpty()) {
            return PASSWORD_EMPTY;
        }
        if (username.equals("kitakeyos") && password.equals("12345")) {
            isUpdateUser = false;
            isActive = true;
            return LOGIN_SUCCESS;
        } else {
            return LOGIN_INCORRECT;
        }
    }
}
