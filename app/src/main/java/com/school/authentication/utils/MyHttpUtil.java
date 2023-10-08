package com.school.authentication.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyHttpUtil {
    // 我写的什么shit，真看不下去
    public static String doGet(String url, String cookie) throws IOException {
        String result = "";
        BufferedReader in = null;

        String urlNameString = url;

        HttpURLConnection connection = (HttpURLConnection) new URL(urlNameString).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36");
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        connection.setRequestProperty("Cookie", cookie);
        // ????????????
        connection.connect();

        // ???? BufferedReader???????????URL?????
        in = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "utf-8"));
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }

        in.close();

        return result;
    }

    public static String doGet2(String url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36");
        conn.setRequestProperty("Accept", "application/signed-exchange");
        conn.setRequestProperty("Accept-Language", "zh-cn");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "utf-8"));

        String line = "", result = "";

        while ((line = in.readLine()) != null) {
            result += line;
        }

        in.close();
        return result;
    }

    public static String doPost(String url, String param, String cookie) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(false);
        conn.setDoInput(true);

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Host", "enet.10000.gd.cn:10001");
        conn.setRequestProperty("Cookie", cookie);

        PrintWriter out = new PrintWriter(conn.getOutputStream());

        out.print(param);
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "utf-8"));

        String line = "", result = "";

        while ((line = in.readLine()) != null) {
            result += line;
        }

        in.close();
        out.close();
        return result;
    }

    public static void doPost2(String url, String param) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(false);
        conn.setDoInput(true);

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36");
        conn.setRequestProperty("Accept", "application/signed-exchange");
        conn.setRequestProperty("Accept-Language", "zh-cn");
        //conn.setRequestProperty("Cookie",cookie);

        PrintWriter out = new PrintWriter(conn.getOutputStream());

        out.print(param);
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "utf-8"));

        String line = "", result = "";

        while ((line = in.readLine()) != null) {
            result += line;
        }

        in.close();
        out.close();
    }

    public static String doPost3(String url, String param, String cookie) throws Exception {//http://172.17.18.2:8080/byod/认证网页
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(false);
        conn.setDoInput(true);

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36");
        conn.setRequestProperty("Cookie", cookie);

        PrintWriter out = new PrintWriter(conn.getOutputStream());

        out.print(param);
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "utf-8"));

        String line = "", result = "";

        while ((line = in.readLine()) != null) {
            result += line;
        }

        in.close();
        out.close();
        return result;
    }

    public static String getRedirectUrl(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString)
                .openConnection();
        conn.setInstanceFollowRedirects(false);
        return conn.getHeaderField("Location");
    }

    public static int getNetworkCode(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(3000);
        return conn.getResponseCode();
    }

    public static String getck() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL("http://enet.10000.gd.cn:10001/advertisement.do?schoolid=1414")
                .openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        return conn.getHeaderField("Set-Cookie");
    }

    public static String getck2() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL("http://172.17.18.2:8080/byod/templatePage/20200318110733542/guestRegister.jsf")
                .openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        return conn.getHeaderField("Set-Cookie");
    }

    public static String getRedirectUrl2(String url, String param, String cookie) throws Exception {//获取http://172.17.18.2:8080/byod/index.xhtml的重定向地址
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(false);
        conn.setDoInput(true);

        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36");
        conn.setRequestProperty("Cookie", cookie);

        PrintWriter out = new PrintWriter(conn.getOutputStream());

        out.print(param);
        out.flush();

        String tmp = conn.getHeaderField("Location");

        out.close();
        return tmp;
    }
}
