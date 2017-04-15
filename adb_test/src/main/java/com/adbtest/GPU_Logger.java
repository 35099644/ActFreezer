package com.adbtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GPU_Logger extends Thread {

	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		String freq = readGPU_freq();
		String load = readGPU_load();
		System.out.println("" + System.currentTimeMillis() + "," + freq + ","
				+ load);
		}
	}

	/**
	 * Read current GPU load
	 * 
	 * @return
	 */
	private String readGPU_freq() {
		// get gpu frequency
		String GPU_freq = null;

		java.lang.Process gpu_freq = null;
		try {

			
			
				gpu_freq = Runtime.getRuntime().exec(
						new String[] { "adb", "shell","su","-c",
								"cat " + "/sys/class/kgsl/kgsl-3d0/gpuclk" });
				BufferedReader gpu_freq_br = new BufferedReader(
						new InputStreamReader(gpu_freq.getInputStream()));
				GPU_freq = gpu_freq_br.readLine();
				// System.out.println("In readGPU_freq: " + GPU_freq);
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return GPU_freq;
	}

	/**
	 * Read current GPU frequency
	 * 
	 * @return
	 */
	private String readGPU_load() {
		// get gpu usage
		java.lang.Process gpu_proc = null;
		String gpu_load = null;
		try {
			gpu_proc = Runtime.getRuntime().exec(
					new String[] { "adb", "shell","su","-c",
							"cat " + "/sys/class/kgsl/kgsl-3d0/gpubusy" });
			BufferedReader gpu_br = new BufferedReader(new InputStreamReader(
					gpu_proc.getInputStream()));
			gpu_load = gpu_br.readLine();
			//System.out.println("In readGPU_load(): " + gpu_load);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] load_data = gpu_load.split("\\s{1,}");
		return gpu_load;
		//return load_data[0]+","+load_data[1]+","+(Double.parseDouble(load_data[1])/Double.parseDouble(load_data[2]));
	}
}
