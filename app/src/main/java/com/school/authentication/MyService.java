package com.school.authentication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.school.authentication.utils.Base64Utils;
import com.school.authentication.utils.MyHttpUtil;
import com.school.authentication.utils.MD5Util;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyService extends Service {
    private static final String TAG = "MyService";
    private static final String ID = "channel_1";
    private static final String NAME = "前台服务";
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

    String addr;

    int flag;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = 0;
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        username = intent.getStringExtra("user");
        password = intent.getStringExtra("pwd");
        client = intent.getStringExtra("clientip");
        nasip = intent.getStringExtra("nasip");
        mac = intent.getStringExtra("mac");
        addr = Tools.getLocalIPAddress(getApplicationContext());
        String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";
        String CHANNEL_ONE_NAME = "CHANNEL_ONE_ID";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager systemService = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            systemService.createNotificationChannel(notificationChannel);
        }

        Notification.Builder builder = new Notification.Builder
                (this.getApplicationContext()); //获取一个Notification构造器
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ONE_ID);
        }
        Intent nfIntent = new Intent(this, MainActivity.class);

        builder.setContentIntent(PendingIntent
                        .getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("校园认证") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("runing中..") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        final Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(110, notification);// 开始前台服务
        flag = 1;
        new Thread() {
            public void run() {
                while (flag == 1) {
                    try {
                        sleep(20000);
                        if (flag == 0) {
                            break;
                        }
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                        Date date = new Date(System.currentTimeMillis());
                        String time = simpleDateFormat.format(date);
                        if (keepConnection().equals("在线") && Tools.getLocalIPAddress(getApplicationContext()).equals(addr)) {
                            setnewtext(time + "维持连接成功！");
                        } else {
                            setnewtext(time + "掉线或网络变化，20s后尝试重登...");
                            relogin(notification);
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        setnewtext("发生错误：" + e.getMessage() + "20s后尝试重新登录");
                        relogin(notification);
                        break;
                    }
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void setnewtext(String text) {
        String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";
        String CHANNEL_ONE_NAME = "CHANNEL_ONE_ID";
        Notification.Builder builder = new Notification.Builder
                (this.getApplicationContext()); //获取一个Notification构造器
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ONE_ID);
        }
        Intent nfIntent = new Intent(this, MainActivity.class);

        builder.setContentIntent(PendingIntent
                        .getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("校园认证") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText(text) // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        final Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(110, notification);// 开始前台服务
    }

    public void relogin(final Notification notification) {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        addr = Tools.getLocalIPAddress(getApplicationContext());
        String url = "http://www.qq.com/";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {

            } else if (conn.getResponseCode() == 302) {
                url = conn.getHeaderField("Location");
                if (url.equals("https://www.qq.com/")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    String time = simpleDateFormat.format(date);
                    setnewtext(time + "网络已为畅通状态");
                    relogin(notification);
                    return;
                }
                if (url.contains("172.17.18.2:30004")) {//是否需要认证
                    setnewtext("需要认证，正在尝试认证中...\n");
                    WebAuth();
                    setnewtext("发包认证完毕，20s后重新获取...\n");
                    relogin(notification);
                    return;
                }
            } else {
                setnewtext("非200、302的状态码，20s后重新尝试登录\n");
                relogin(notification);
                return;
            }
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
                        setnewtext("没有获取到局域网ip！20s后重新尝试\n");
                        new Thread() {
                            public void run() {
                                relogin(notification);
                            }
                        }.start();
                        return;
                    }
                } else {
                    client = ConfigurationFile.readCfgValue(path + "/index.conf", "record", "ClientIP", "");
                    nasip = "119.146.175.80";
                    setnewtext("使用默认clientip登录...\n");
                }
            }
            setnewtext("获取基本信息成功" + client + "正在获取验证码..\n");
            cookie = MyHttpUtil.getck();
            //Log.d("MainActivity", "run: "+cookie);
            String verifycode = getVerifyCodeString();
            setnewtext("获取验证码为：" + verifycode + "，正在登录...\n");
            String returncode = doLogin(verifycode);
            setnewtext("登录返回的信息为：" + returncode + "\n");
            if (returncode.contains("success")) {
                setnewtext("登录成功！循环维持连接中...\n");
                new Thread() {
                    public void run() {
                        while (flag == 1) {
                            try {
                                sleep(20000);
                                if (flag == 0) {
                                    break;
                                }
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                Date date = new Date(System.currentTimeMillis());
                                String time = simpleDateFormat.format(date);
                                if (keepConnection().equals("在线") && Tools.getLocalIPAddress(getApplicationContext()).equals(addr)) {
                                    setnewtext(time + "维持连接成功！");
                                } else {
                                    setnewtext("检测掉线，20s后重登...");
                                    relogin(notification);
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                setnewtext("发生错误：" + e.getMessage() + "尝试重新登录");
                                relogin(notification);
                                break;
                            }
                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setnewtext("发生错误：" + e.getMessage() + "20s后再次尝试");
            relogin(notification);
        }
    }

    public void WebAuth() throws Exception {
        url = "http://172.17.18.2:30004/byod/byodrs/login/defaultLogin";
        HashMap<String, Object> map = new HashMap<String, Object>() {{
            put("userName", username);
            put("userPassword", Base64Utils.encode(password));
            put("serviceSuffixId", "-1");
            put("dynamicPwdAuth", false);
            put("code", "");
            put("codeTime", "");
            put("validateCode", "");
            put("licenseCode", "");
            put("userGroupId", -1);
            put("validationType", 2);
            put("guestManagerId", -1);
            put("shopIdE", null);
            put("wlannasid", null);
        }};
        String data = JSON.toJSONString(map);
        String ret = MyHttpUtil.doPost2(url, data);
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(ret);
        String msg = (String) jsonObject.get("msg");
        setnewtext("登录返回信息：" + msg);
    }

    //谢谢你，冗余侠
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

        Log.d(TAG, "getVerifyCodeString: " + verifyCodeString);
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
        try {
            String activeString = MyHttpUtil.doGet(url + "?" + param, "");
            Log.d(TAG, "keepConnection: " + activeString);
            JSONObject json = new JSONObject(activeString);
            activeString = json.getString("resinfo");
            return activeString;
        } catch (Exception e) {
            return "";
        }
    }
}
