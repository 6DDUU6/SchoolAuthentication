package com.school.authentication.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

public class AddressUtil {
	public static String getLocalMac(InetAddress ia) throws SocketException {
		// TODO Auto-generated method stub
		//��ȡ��������ȡ��ַ
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<mac.length; i++) {
			if(i!=0) {
				sb.append("-");
			}
			//�ֽ�ת��Ϊ����
			int temp = mac[i]&0xff;
			String str = Integer.toHexString(temp);
			if(str.length()==1) {
				sb.append("0"+str);
			}else {
				sb.append(str);
			}
		}
		return sb.toString().toUpperCase();
	}
}
