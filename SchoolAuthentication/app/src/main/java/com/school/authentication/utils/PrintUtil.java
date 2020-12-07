package com.school.authentication.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintUtil {
	public static String printPrefix() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String prefix = sdf.format(new Date()) + "��";
		return prefix;
	}

	public static void printAbout() {
		System.out
				.println("----------------------------------------------------");
		System.out.println("         ���ȣ�LandLeg�� Java�� v2.0 By Coande");
		System.out.println("                  http://coande.github.io");
		System.out.println("                   ��������͵�ʹ�ã�����");
		System.out
				.println("----------------------------------------------------");
	}
}
