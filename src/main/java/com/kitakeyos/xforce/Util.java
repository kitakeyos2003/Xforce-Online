/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitakeyos.xforce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author Admin
 */
public class Util {

    public static byte[] getFile(String url) {
        try {
            File file = new File(url);
            if (file.exists()) {
                return Files.readAllBytes(file.toPath());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
