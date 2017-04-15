package com.netlab.servicelogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServiceCleaner extends Thread {

	public void run() {
		readProcess();
	}

	private void readProcess() {
		Process reading_process = null;
		String ps_line = null;

		try {
			reading_process = Runtime.getRuntime().exec(
					new String[] { "ps" });
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				reading_process.getInputStream()));

		try {
			System.out.println("ServiceLogger start to read ps");
			while ((ps_line = br.readLine()) != null) {
				System.out.println(ps_line);
				String[] line = ps_line.split("\\s+");
				//System.out.println(ps_line.split("\\s+").length);
				if(line.length==9)
				{
					System.out.println("pid = " + line[1] + " name = " + line[8]);
					if(!line[0].contains("system")&&!line[0].contains("root"))
					{
					reading_process = Runtime.getRuntime().exec(
							new String[] {"am force-stop " + line[8].split(":")[0]});
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
