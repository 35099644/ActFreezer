package com.adbtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean test = false;
		int pid = 0;
		int[] tids = new int[] {};

		pid = 30952;
		tids = new int[] { 30955, 30956, 30957, 30958 };

		new ServiceCleaner().start();
		
		
		//Process su = Runtime.getRuntime().exec("adb root");
		//new CPULogger().start(); 
		//new GPU_Logger().start();
		
		
		while (test) {

			try {
				Thread.sleep(1000);
				System.out.print(System.currentTimeMillis());
				for (int i = 0; i < tids.length; i++) {
					Process adb = Runtime.getRuntime().exec(
							"adb shell cat /proc/" + pid + "/task/"+tids[i]+"/stat");
					BufferedReader br = new BufferedReader(
							new InputStreamReader(adb.getInputStream()));
					String[] line = br.readLine().split(" ");
					
					System.out.print(",");
					System.out.print(line[38]);

					//try to separate user and sys time
					System.out.print(", utime,");
					System.out.print(line[13]);
					System.out.print(", stime,");
					System.out.print(line[14]);
					System.out.print(", cutime,");
					System.out.print(line[15]);
					System.out.print(", cstime,");
					System.out.print(line[16]);
					
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println();
		}

	}

}
