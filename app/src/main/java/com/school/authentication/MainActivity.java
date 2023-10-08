package com.school.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.school.authentication.ConfigurationFile;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.school.authentication.Tools;

import org.json.JSONObject;

import com.school.authentication.utils.*;

public class MainActivity extends AppCompatActivity {
    private EditText user, pwd, clientip, loget;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    String username;
    String password;
    String isauto;
    String client;
    String nasip;
    String mac;
    String timestamp;
    String version = "214";
    String secret = "Eshore!@#";
    String iswifi = "4060";
    String cookie;

    String md5String;
    String url;

    int flag = 0;
    int fg = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyStoragePermissions(MainActivity.this);
        String path = getExternalFilesDir("").getAbsolutePath();
        Tools.makeFilePath(path + "/", "index.conf");
        user = (EditText) findViewById(R.id.user);
        pwd = (EditText) findViewById(R.id.pwd);
        clientip = (EditText) findViewById(R.id.clientip);
        loget = (EditText) findViewById(R.id.loget);
        try {
            user.setText(ConfigurationFile.readCfgValue(path + "/index.conf", "record", "user", ""));
            pwd.setText(ConfigurationFile.readCfgValue(path + "/index.conf", "record", "pwd", ""));
            clientip.setText(ConfigurationFile.readCfgValue(path + "/index.conf", "record", "ClientIP", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onclick(final View view) {
        if (fg == 1) { //通过fg判断是否已经有线程启动，避免有人连续按两次（
            return;
        }
        fg = 1;
        //loget.setText("");
        username = user.getText().toString();
        password = pwd.getText().toString();
        client = clientip.getText().toString();
        String path = getExternalFilesDir("").getAbsolutePath();
        try {
            ConfigurationFile.writeCfgValue(path + "/index.conf", "record", "user", username);
            ConfigurationFile.writeCfgValue(path + "/index.conf", "record", "pwd", password);
            ConfigurationFile.writeCfgValue(path + "/index.conf", "record", "ClientIP", client);
        } catch (Exception e) {
            loget.append(e.getMessage() + "\n");
            loget.setSelection(loget.getText().length());
            e.printStackTrace();
            fg = 0;
            return;
        }
        loget.append("登录线程启动...\n");
        loget.setSelection(loget.getText().length());
        new Thread() {
            public void run() {
                addlog("正在检测是否在学校局域网内...\n");
                String url = "http://172.17.18.2:30004/";
                try {
                    int ret = MyHttpUtil.getNetworkCode(url);
                    if (ret == 302 || ret == 200) {
                        addlog("网络连通性测试成功，正在获取重定向地址...\n");
                    } else {
                        addlog("当前没有在学校局域网内，请尝试断开wifi后重连\n");
                        fg = 0;
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    addlog("发生错误：" + e.getMessage() + "\n可能当前没有在学校局域网内，请尝试断开wifi后重连\n");
                    fg = 0;
                    return;
                }

                url = "http://www.qq.com";
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setInstanceFollowRedirects(false);
                    conn.setConnectTimeout(3000);
                    if (conn.getResponseCode() == 200) {
                        //200不是畅通，是需要认证
                        addlog("当前为纯学校wifi，需要登录认证，正在自动认证中...\n");
                        // TODO: 认证
                    } else if (conn.getResponseCode() == 302) {
                        url = conn.getHeaderField("Location");
                        if (url.equals("https://www.qq.com/")) {
                            addlog("当前网络已为畅通状态，无需再次登录\n");
                            fg = 0;
                            return;
                        }
                        addlog("获取到重定向地址为：" + url + "\n");
                    } else {
                        addlog("获取到了非200、302的状态码，登录失败！请检查网络是否正常\n");
                        fg = 0;
                        return;
                    }
                    if (url.contains("172.17.18.2:30004")) {//是否需要认证
                        addlog("需要认证，正在尝试认证中...\n");
                        url = "http://172.17.18.2:30004/byod/byodrs/login/defaultLogin";
                        //TODO: 登录
                        addlog("发包完毕，请重新按登录按钮\n");
                        fg = 0;
                        return;
                    }
                    //3种方式获取clientip
                    Pattern p = Pattern.compile("wlanuserip=(.+?)&wlanacip=(.+)");
                    Matcher m = p.matcher(url);
                    if (m.find()) {
                        client = m.group(1);
                        nasip = m.group(2);
                    } else {
                        String path = getExternalFilesDir("").getAbsolutePath();
                        if (ConfigurationFile.readCfgValue(path + "/index.conf", "record", "clientip", "").equals("")) {
                            String tmp = Tools.getLocalIPAddress(getApplicationContext());
                            Pattern p2 = Pattern.compile("^100.");
                            Matcher m2 = p2.matcher(tmp);
                            if (m2.find()) {
                                client = tmp;
                                nasip = "119.146.175.80";
                            } else {
                                addlog("没有获取到局域网、nasip！\n请检查是否连接了学校网络\n");
                                fg = 0;
                                return;
                            }
                        } else {
                            client = ConfigurationFile.readCfgValue(path + "/index.conf", "record", "clientip", "");
                            addlog("使用默认clientip登录...\n");
                            nasip = "119.146.175.80";
                        }
                    }
                    mac = Tools.getMac(MainActivity.this).replace(":", "-");
                    addlog("获取基本信息成功\nnasip=" + nasip + "\nclientip=" + client + "\nmac=" + mac + "\n" + "正在获取cookie...\n");
                    cookie = MyHttpUtil.getck();
                    addlog("获取cookie为：" + cookie + "\n正在获取验证码...\n");
                    //Log.d("MainActivity", "run: "+cookie);
                    String verifycode = getVerifyCodeString();
                    addlog("获取验证码为：" + verifycode + "，正在登录...\n");
                    String returncode = doLogin(verifycode);
                    addlog("登录返回的信息为：" + returncode + "\n");
                    if (returncode.contains("success")) {
                        onclick3(view);
                        addlog("登录成功！已启动前台服务并维持连接，请保持APP在后台运行\n");
                        fg = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    addlog("发生错误：" + e.getMessage() + "\n请尝试断开wifi后重连\n");
                    fg = 0;
                }
            }
        }.start();
    }

    public void onclick2(View view) {
        loget.append("正在注销中...\n");
        if (client == null || client.equals("")) {
            username = user.getText().toString();
            client = clientip.getText().toString();
            if (client.equals("")) {
                String tmp = Tools.getLocalIPAddress(getApplicationContext());
                Pattern p2 = Pattern.compile("^100.");
                Matcher m2 = p2.matcher(tmp);
                if (m2.find()) {
                    client = tmp;
                    nasip = "119.146.175.80";
                } else {
                    addlog("注销需要您的局域网ip，请手动输入clientip\n");
                    return;
                }
            }
            nasip = "119.146.175.80";
            mac = Tools.getMac(MainActivity.this).replace(":", "-");
        }
        new Thread() {
            public void run() {
                try {
                    Intent stop = new Intent(MainActivity.this, MyService.class);
                    stopService(stop);
                    String returncode = doLogout();
                    addlog("注销返回的信息为：" + returncode + "\n");
                    flag = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    addlog("发生错误：" + e.getMessage() + "\n");
                }
            }
        }.start();
    }

    public String SimpleAuth(String timestamp) {
        return "";
    }

    public String LoginAuth(String timestamp) {
        return "";
    }

    public String getVerifyCodeString() throws Exception {
        url = "http://enet.10000.gd.cn:10001/client/vchallenge";
        timestamp = System.currentTimeMillis() + "";
        md5String = MD5Util.MD5(version + client + nasip + mac + timestamp + secret);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version", version);
        jsonObject.put("username", username);
        jsonObject.put("clientip", client);
        jsonObject.put("nasip", nasip);
        jsonObject.put("mac", mac);
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("authenticator", md5String);

        //Log.d("MainActivity", jsonObject.toString());
        //Log.d("MainActivity", cookie);
        String verifyCodeString = MyHttpUtil.doPost(url, jsonObject.toString(), cookie);

        Log.d("MainActivity", "getVerifyCodeString: " + verifyCodeString);
        JSONObject json = new JSONObject(verifyCodeString);
        verifyCodeString = json.getString("challenge");
        return verifyCodeString;
    }

    public String doLogin(String verifyCode) throws Exception {
        url = "http://enet.10000.gd.cn:10001/client/login";
        timestamp = System.currentTimeMillis() + "";
        md5String = MD5Util.MD5(client + nasip + mac + timestamp + verifyCode + secret);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonObject.put("clientip", client);
        jsonObject.put("nasip", nasip);
        jsonObject.put("mac", mac);
        jsonObject.put("iswifi", iswifi);
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("authenticator", md5String);

        String loginString = MyHttpUtil.doPost(url, jsonObject.toString(), cookie);

        return loginString;
    }

    public String doLogout() throws Exception {
        url = "http://enet.10000.gd.cn:10001/client/logout";
        timestamp = System.currentTimeMillis() + "";
        md5String = MD5Util.MD5(client + nasip + mac + timestamp + secret);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("clientip", client);
        jsonObject.put("nasip", nasip);
        jsonObject.put("mac", mac);
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("authenticator", md5String);

        String verifyCodeString = MyHttpUtil.doPost(url, jsonObject.toString(), cookie);
        return verifyCodeString;
    }

    public String keepConnection() throws Exception {
        timestamp = System.currentTimeMillis() + "";
        md5String = MD5Util.MD5(client + nasip + mac + timestamp + secret);
        url = "http://enet.10000.gd.cn:8001/hbservice/client/active";
        String param = "username=" + username + "&clientip=" + client + "&nasip=" + nasip + "&mac=" + mac + "&timestamp=" + timestamp + "&authenticator="
                + md5String;
        String activeString = MyHttpUtil.doGet(url + "?" + param, "");
        JSONObject json = new JSONObject(activeString);
        activeString = json.getString("resinfo");
        //Log.d("MainActivity", "keepConnection: "+activeString);
        return activeString;
    }

    public void onclick3(View view) {
//        if(flag==1){
//            Toast.makeText(MainActivity.this,"已经启动一个前台服务啦！",Toast.LENGTH_SHORT).show();
//        }
        Intent stop = new Intent(this, MyService.class);//先停止可能存在的服务
        stopService(stop);

        Intent start = new Intent(this, MyService.class);
        //Bundle b = new Bundle();
        start.putExtra("user", username);
        start.putExtra("pwd", password);
        start.putExtra("clientip", client);
        start.putExtra("nasip", nasip);
        start.putExtra("mac", mac);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(start);
        } else {
            startService(start);
        }
        flag = 1;
    }

    public void onclick4(View view) {
        Intent stop = new Intent(this, MyService.class);
        stopService(stop);
        flag = 0;
    }

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addlog(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loget.append(data);
                loget.setSelection(loget.getText().length());
            }
        });
    }
}
