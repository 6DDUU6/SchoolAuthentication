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

        connection.connect();
        in = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "utf-8"));
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }

        in.close();

        return result;
    }

    public static String doPost(String url, String postData, String[] headers) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(false);
        conn.setDoInput(true);

        for (int i = 0; i < headers.length / 2; i++) {
            conn.setRequestProperty(headers[i * 2], headers[i * 2 + 1]);
        }

        PrintWriter out = new PrintWriter(conn.getOutputStream());

        out.print(postData);
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

    public static String doPost(String url, String param, String cookie) throws Exception {
        String[] headers = new String[]{
                "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36",
                "Content-Type", "application/json",
                "Accept", "*/*",
                "Host", "enet.10000.gd.cn:10001",
                "Cookie", cookie
        };
        return doPost(url, param, headers);
    }

    public static String doPost2(String url, String param) throws Exception {
        String[] headers = new String[]{
                "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36",
                "Content-Type", "application/json",
                "Origin", "http://172.17.18.2:30004",
                "Referer", "http://172.17.18.2:30004/byod/view/byod/template/templatePhone.html",
                "Cookie", "testcookie=yes;"
        };
        return doPost(url, param, headers);
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
}
