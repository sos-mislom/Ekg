package com.example.ext.api;
// #2196F3

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import kotlin.Pair;

public class Ext {
    private final String username;
    private final String password;
    private final String url;
    static String name;
    static String letter_of_class;
    static String num_of_class;
    private String studentId;
    private String uchYear;
    public static Map<Integer, String> sbj_names;
    public static Map<Integer, String> teachers;
    private final Post http;
    private String uchId;
    private String cls;

    public Ext(String username, String password) throws NoSuchAlgorithmException {
        this.url = "http://176.215.5.226:8082/";
        sbj_names = new HashMap<Integer, String>();
        teachers = new HashMap<Integer, String>();
        this.username = username;
        this.password = PasswordToSHA1(password);
        this.http = new Post(this.password, ConvertStringToUnic(this.username));
        try {
            AUTH();
            GET_USER_DATA();
            UCH_YEAR();
            GET_STUDENT_CLASS();
            GET_NECESSARY_DICTS();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean isAlpha(String name) {return name.matches("[a-zA-ZА-Яа-я]+");}

    private String RepeatStr(int i, String str) {
        String result = "";
        for( int u = 0; u < i; u++ ) {
            result = result + str;
        }
        return result;
    }

    public Pair<LocalDate, LocalDate> GET_INTERVAL(boolean is_not_necessary_to_send_30004){
        try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            JSONArray list_of_periods_dates = this.GET_JOURNAL_PERIODS_INFO();
            JSONArray CLASS_PER_SP = this.GET_CLASS_PER_SP();
            ArrayList<String> list_of_periods = new ArrayList<>();
            Map<String, Pair<LocalDate, LocalDate>> list_of_intervals = new TreeMap();
            LocalDate now = LocalDate.now();
            for (int i = 0; i < CLASS_PER_SP.length(); i++) {
                    list_of_periods.add(CLASS_PER_SP.getJSONArray(i).getString(0));
            }
            for (int i = 0; i < list_of_periods_dates.length(); i++) {
                for (String key_of_intervals: list_of_periods) {
                    if (key_of_intervals.equals(list_of_periods_dates.getJSONArray(i).getString(0))) {
                        LocalDate date = LocalDate.parse(GET_DATE(list_of_periods_dates.getJSONArray(i).getJSONArray(1)), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        LocalDate date2 = LocalDate.parse(GET_DATE(list_of_periods_dates.getJSONArray(i).getJSONArray(2)), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        Pair<LocalDate, LocalDate> pair = new Pair<>(date, date2);
                        list_of_intervals.put(list_of_periods_dates.getJSONArray(i).getString(0), pair);
                    }
                }
            }
            for (String index : list_of_intervals.keySet()) {
                if (is_not_necessary_to_send_30004 && !index.equals("360004")){
                    LocalDate t1start = list_of_intervals.get(index).component1();
                    LocalDate t1end = list_of_intervals.get(index).component2();
                    if (now.isAfter(t1start) && now.isBefore(t1end)){
                        return list_of_intervals.get(index);
                    }
                }
                else if (!is_not_necessary_to_send_30004 && index.equals("360004")){
                    return list_of_intervals.get(index);
                }
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String GET_DATE(JSONArray data){
        String day = null;
        try {
            day = String.valueOf(data.get(2));
            String month = String.valueOf((Integer.parseInt(String.valueOf(data.get(1)))+1));
            if (day.length() == 1){ day = "0"+ day; }
            if (month.length() == 1){ month = "0"+ month; }
            String Date = day + "." + month + "." + data.get(0);
            return Date;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return day;
    }

    static String PasswordToSHA1(String passwordToHash) throws NoSuchAlgorithmException {
        MessageDigest message_dig = MessageDigest.getInstance("SHA-1");
        byte[] bytes = message_dig.digest(passwordToHash.getBytes());
        StringBuilder str_bld = new StringBuilder();
        for (byte aByte : bytes) {
            str_bld.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return str_bld.toString();
    }
    private static String ConvertStringToUnic(String input) {
        StringBuilder str_bld = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= 128)
                str_bld.append("\\u").append(String.format("%04X", (int) c));
            else
                str_bld.append(c);
        }

        return str_bld.toString().replace("\\u", "%u");
    }

    private JSONArray RET_JSON_FORMAT(Response r){
        String data = r.toString().replaceAll("new Date\\((([0-9]+,?-?)+)\\)", "[$1]");
        try {
            data = data.replace("\n", "").replace("\r", "").replace("\t", "");
            return new JSONArray(data);
        } catch (JSONException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();

        } return null;
    }

    public void UCH_YEAR() {
        @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        Map<String, String> data = new HashMap<>();
        data.put("currentDate", currentDate);
        try {
            Response r = http.post ( this.url + "act/get_uch_year", data);
            if (r == null) this.uchYear = null;
            JSONArray arr = new JSONArray(r.toString());
            this.uchYear = arr.getJSONArray(0).getString(0);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray AUTH() throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("l", this.username);
        data.put("p", this.password);
        try {
            Response r = http.post(this.url + "login", data);
            JSONArray arr = new JSONArray(r.toString());
            String userId = arr.getJSONArray(0).getString(0);
            this.studentId = arr.getJSONArray(0).getString(6);
            name = arr.getJSONArray(0).getString(5);
            data.clear();
            data.put("uId", userId);
            data.put("act", String.valueOf(1));
            if (http.post(this.url + "auth", data).toString().equals("ok")) {
                return new JSONArray("[ok]");
            }else {
                return new JSONArray("[Ошибка авторизации.");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray GET_MESSAGES() {
        Map<String, String> data = new HashMap<>();
        data.put("uchYear", this.uchYear);
        data.put("student", this.studentId);
        data.put("isGuru", "false");
        try {
            Response r = http.post ( this.url + "act/GET_STUDENT_MESSAGES", data);
            return RET_JSON_FORMAT(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray GET_JOURNAL_PERIODS_INFO(){
        Map<String, String> data = new HashMap<>();
        data.put("uchYear", this.uchYear);
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < 22; i++) {ids.add("36" + RepeatStr( 4 - String.valueOf(i).length(), "0") + i);}
        try {
            Response r = http.post( this.url + "act/GET_JOURNAL_PERIODS_INFO", data, ids);
            return RET_JSON_FORMAT(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray GET_CLASS_PER_SP(){
        Map<String, String> data = new HashMap<>();
        data.put("cls", this.cls);
        try {
            Response r = http.post ( this.url + "act/GET_CLASS_PER_SP", data);
            return RET_JSON_FORMAT(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void GET_USER_DATA() {
        try {
            Response r = http.get( this.url + "act/get_user_data");
            JSONArray arr = new JSONArray(r.toString());
            this.uchId = arr.getJSONArray(0).getString(4);
        } catch (IOException | JSONException | NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void GET_STUDENT_CLASS() {
        @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        Map<String, String> data = new HashMap<>();
        data.put("currentDate", currentDate);
        data.put("uchId", this.uchId);
        data.put("uchYear", this.uchYear);
        data.put("student", this.studentId);
        try {
            Response r = http.post ( this.url + "act/GET_STUDENT_CLASS", data);
            JSONArray arr = new JSONArray(r.toString());
            this.cls = arr.getJSONArray(0).getString(0);
            letter_of_class = arr.getJSONArray(0).getString(2);
            num_of_class = arr.getJSONArray(0).getString(1);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray GET_STUDENT_JOURNAL_DATA() {
        Map<String, String> data = new HashMap<>();
        data.put("cls", this.cls);
        data.put("parallelClasses", "");
        data.put("student", this.studentId);
        try {
            Response r = http.post ( this.url + "act/GET_STUDENT_JOURNAL_DATA", data);
            return RET_JSON_FORMAT(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONObject GET_DAIRY_CLASS_SUBJECTS() {
        Map<String, String> data = new HashMap<>();
        data.put("pClassesIds", "");
        data.put("cls", this.cls);
        try {
            Response r = http.post ( this.url + "act/GET_DAIRY_CLASS_SUBJECTS", data);
            return new JSONObject(r.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONArray GET_STUDENT_DAIRY(String begin_dt, String end_dt) {
        Map<String, String> data = new HashMap<>();
        data.put("cls", this.cls);
        data.put("parallelClasses", "");
        data.put("uchYear", this.uchYear);
        data.put("student", this.studentId);
        data.put("begin_dt", begin_dt);
        data.put("end_dt", end_dt);

        try {
            Response r = http.post ( this.url + "act/GET_STUDENT_DAIRY", data);
            return RET_JSON_FORMAT(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONArray GET_STUDENT_LESSONS(String begin_dt, String end_dt) {
        Map<String, String> data = new HashMap<>();
        data.put("cls", this.cls);
        data.put("parallelClasses", "");
        data.put("uchYear", this.uchYear);
        data.put("student", this.studentId);
        data.put("period_begin", begin_dt);
        data.put("period_end", end_dt);

        try {
            Response r = http.post ( this.url + "act/GET_STUDENT_LESSONS", data);
            return RET_JSON_FORMAT(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONArray GET_STUDENT_GROUPS() {
        Map<String, String> data = new HashMap<>();
        data.put("cls", this.cls);
        data.put("student", this.studentId);
        try {
            Response r = http.post ( this.url + "act/GET_STUDENT_GROUPS", data);
            return new JSONArray(r.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void GET_NECESSARY_DICTS() throws IOException, JSONException {
        Map<String, String> data = new HashMap<>();
        Hashtable edu = new Hashtable();
        data.put("cls", this.cls);
        data.put("parallelClasses", "");
        Response r = http.post ( this.url + "act/GET_DAIRY_CLASS_SUBJECTS", data);
        JSONArray arr = new JSONArray(r.toString());
        for (int i = 0; i < arr.length(); i++) {
            edu.put(arr.getJSONArray(i).getInt(0), arr.getJSONArray(i).getString(1));
        }
        this.sbj_names = edu;

        edu = new Hashtable();
        edu.clear();
        Response g = http.get ( this.url + "act/GET_SUBS_PERSONS_STORE_DATA");
        JSONArray ar = new JSONArray(g.toString());
        for (int i = 0; i < ar.length(); i++) {
            edu.put(ar.getJSONArray(i).getInt(0), ar.getJSONArray(i).getString(1));
        }
        this.teachers = edu;
        edu = new Hashtable();
        edu.clear();
//        Response g2 = http.get ( this.url + "act/GET_PERIODS");
//        JSONArray a = new JSONArray(g2.toString());
//        for (int i = 0; i < a.length(); i++) {
//            edu.put(a.getJSONArray(i).getInt(0), a.getJSONArray(i).getString(1));
//        }
//        this.work_periods = edu;
    }
}
