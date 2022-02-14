package com.example.ext;
// #2196F3

import android.annotation.SuppressLint;
import android.util.Log;

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
    private String url;
    private String userId;
    private String studentId;
    private String uchYear;
    public static Map<Integer, String> sbj_names;
    public static Map<Integer, String> teachers;
    private Post p;
    private String uchId;
    private String cls;
    private static Map<Integer, String> work_pheriods;

    public Ext(String username, String password) throws NoSuchAlgorithmException {
        this.url = "http://176.215.5.226:8082/";
        this.sbj_names = new HashMap<Integer, String>();
        this.work_pheriods = new HashMap<Integer, String>();
        this.teachers = new HashMap<Integer, String>();
        this.username = username;
        this.password = PasswordToSHA1(password);
        this.p = new Post(this.password, ConvertStringToUnic(this.username));
        try {
            AUTH();
            GET_USER_DATA();
            UCH_YEAR();
            GET_STUDENT_CLASS();
            GET_NESSESERY_DICTS();
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

    public Pair<LocalDate, LocalDate> GET_INTERVAL(){
        try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            JSONArray list_of_periods_dates = this.GET_JOURNAL_PERIODS_INFO();
            JSONArray CLASS_PER_SP = this.GET_CLASS_PER_SP();
            ArrayList<Integer> list_of_periods = new ArrayList<>();
            Map<Integer, Pair<LocalDate, LocalDate>> list_of_intervals = new TreeMap();
            LocalDate now = LocalDate.now();
            for (int i = 0; i < CLASS_PER_SP.length(); i++) {
                    list_of_periods.add(CLASS_PER_SP.getJSONArray(i).getInt(0));
            }
            for (int i = 0; i < list_of_periods_dates.length(); i++) {
                if(list_of_periods.contains(list_of_periods_dates.getJSONArray(i).getInt(0)));
                LocalDate date = LocalDate.parse(GET_DATE(list_of_periods_dates.getJSONArray(i).getJSONArray(1)), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                LocalDate date2 = LocalDate.parse(GET_DATE(list_of_periods_dates.getJSONArray(i).getJSONArray(2)), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                Pair<LocalDate, LocalDate> pair = new Pair<>(date, date2);
                list_of_intervals.put(list_of_periods_dates.getJSONArray(i).getInt(0), pair);
            }
            for (int index : list_of_intervals.keySet()) {
                if (index != 360004){
                    LocalDate t1start = list_of_intervals.get(index).component1();
                    LocalDate t1end = list_of_intervals.get(index).component2();
                    if (now.isAfter(t1start) && now.isBefore(t1end)){
                        return list_of_intervals.get(index);
                    }
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

    private static String PasswordToSHA1(String passwordToHash) throws NoSuchAlgorithmException {
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
            boolean is_open = false;
            data = data.replace("\\n", "").replace("\r", "").replace("\t", "");
            for (int i = 0; i < data.length()-3; i++) {
                if (data.charAt(i) == '"' && !is_open){
                    is_open = true;
                }
                else if (data.charAt(i+1) == ',' && data.charAt(i) == '"' || (!isAlpha(String.valueOf(data.charAt(i+3))) && data.charAt(i+3) == 'n')){
                    is_open = false;
                }
                else if (is_open && data.charAt(i) == '"' && !(data.charAt(i) == '"' && data.charAt(i+1) == '"')){
                    data = data.substring(0, i++) + "$" + data.substring(i);
                }
            }
            data = data.replace("\n", "");
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
            Response r = p.post ( this.url + "act/get_uch_year", data);
            if (r == null) this.uchYear = null;
            JSONArray arr = new JSONArray(r.toString());
            this.uchYear = arr.getJSONArray(0).getString(0);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public Object AUTH() throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("l", this.username);
        data.put("p", this.password);
        try {
            Response r = p.post(this.url + "login", data);
            if (r == null) return new JSONObject("{'error': 'Неправильный логин и/или пароль.'}");
            JSONArray arr = new JSONArray(r.toString());
            this.userId = arr.getJSONArray(0).getString(0);
            this.studentId = arr.getJSONArray(0).getString(6);
            data.clear();
            data.put("uId", this.userId);
            data.put("act", String.valueOf(1));
            if (p.post(this.url + "auth", data).toString().equals("ok")) {
                return new JSONObject("{'success': 'ok'}");
            }else {
                return new JSONObject("{'error': 'Ошибка авторизации.'}");
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
            Response r = p.post ( this.url + "act/GET_STUDENT_MESSAGES", data);
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
        data.put("ids", ids.toString());
        try {
            Response r = p.post ( this.url + "act/GET_JOURNAL_PERIODS_INFO", data);
            Log.e("asd", r.toString());
            if (r.toString().equals("[]")){
                Response rr = new Response("[[360000, new Date(2021,8,1,0,0,0,0), new Date(2021,9,31,0,0,0,0)],[360000, new Date(2021,8,1,0,0,0,0), new Date(2021,9,31,0,0,0,0)],[360000, new Date(2021,8,1,0,0,0,0), new Date(2021,9,31,0,0,0,0)],[360002, new Date(2022,0,10,0,0,0,0), new Date(2022,2,19,0,0,0,0)],[360003, new Date(2022,2,28,0,0,0,0), new Date(2022,4,31,0,0,0,0)],[360004, new Date(2021,8,1,0,0,0,0), new Date(2022,4,31,0,0,0,0)],[360012, new Date(2022,0,10,0,0,0,0), new Date(2022,4,31,0,0,0,0)]]", 400);
                return RET_JSON_FORMAT(rr);
            }
            return new JSONArray(r.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private JSONArray GET_CLASS_PER_SP(){
        Map<String, String> data = new HashMap<>();
        data.put("cls", this.cls);
        try {
            Response r = p.post ( this.url + "act/GET_CLASS_PER_SP", data);
            return RET_JSON_FORMAT(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void GET_USER_DATA() {
        try {
            Response r = p.get( this.url + "act/get_user_data");
            JSONArray arr = new JSONArray(r.toString());
            this.uchId = arr.getJSONArray(0).getString(4);
        } catch (IOException | JSONException e) {
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
            Response r = p.post ( this.url + "act/GET_STUDENT_CLASS", data);
            JSONArray arr = new JSONArray(r.toString());
            this.cls = arr.getJSONArray(0).getString(0);
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
            Response r = p.post ( this.url + "act/GET_STUDENT_JOURNAL_DATA", data);
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
            Response r = p.post ( this.url + "act/GET_DAIRY_CLASS_SUBJECTS", data);
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
            Response r = p.post ( this.url + "act/GET_STUDENT_DAIRY", data);
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
            Response r = p.post ( this.url + "act/GET_STUDENT_GROUPS", data);
            return new JSONArray(r.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void GET_NESSESERY_DICTS() throws IOException, JSONException {
        Map<String, String> data = new HashMap<>();
        Hashtable edu = new Hashtable();
        data.put("cls", this.cls);
        data.put("parallelClasses", "");
        Response r = p.post ( this.url + "act/GET_DAIRY_CLASS_SUBJECTS", data);
        JSONArray arr = new JSONArray(r.toString());
        for (int i = 0; i < arr.length(); i++) {
            edu.put(arr.getJSONArray(i).getInt(0), arr.getJSONArray(i).getString(1));
        }
        this.sbj_names = edu;

        edu = new Hashtable();
        edu.clear();
        Response g = p.get ( this.url + "act/GET_SUBS_PERSONS_STORE_DATA");
        JSONArray ar = new JSONArray(g.toString());
        for (int i = 0; i < ar.length(); i++) {
            edu.put(ar.getJSONArray(i).getInt(0), ar.getJSONArray(i).getString(1));
        }
        this.teachers = edu;
        edu = new Hashtable();
        edu.clear();
        Response g2 = p.get ( this.url + "act/GET_PERIODS");
        JSONArray a = new JSONArray(g2.toString());
        for (int i = 0; i < a.length(); i++) {
            edu.put(a.getJSONArray(i).getInt(0), a.getJSONArray(i).getString(1));
        }
        this.work_pheriods = edu;
    }
}
