package com.example.ext;
// #2196F3
import android.annotation.SuppressLint;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class Ext {
    private final String username;
    private final String password;
    private String url;
    private int studentId;
    Dictionary<String, Integer> teachers = new Hashtable<>();

    public Ext(String username, String password) throws NoSuchAlgorithmException {
        this.url = "http://176.215.5.226:8082/";
        this.username = ConvertStringToUnic(username);
        this.password = PasswordToSHA1(password);


    }
    public String UCH_YEAR() throws NoSuchAlgorithmException {
        @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        Map<String, String> data = new Hashtable<>();
        data.put("currentDate", currentDate);
        try {
            return Post.post( this.url + "act/get_uch_year", data, this.username, this.password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentDate;
    }
    public static String PasswordToSHA1(String passwordToHash) throws NoSuchAlgorithmException {
        MessageDigest message_dig = MessageDigest.getInstance("SHA-1");
        byte[] bytes = message_dig.digest(passwordToHash.getBytes());
        StringBuilder str_bld = new StringBuilder();
        for (byte aByte : bytes) {
            str_bld.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return str_bld.toString();
    }
    public static String ConvertStringToUnic(String input) {
        StringBuilder str_bld = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= 128)
                str_bld.append("\\u").append(String.format("%04X", (int) c));
            else
                str_bld.append(c);
        }

        return str_bld.toString().replace("\\u", "%u");
    }
}