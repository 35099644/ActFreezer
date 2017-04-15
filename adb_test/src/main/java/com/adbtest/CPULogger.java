package com.adbtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class CPULogger extends Thread {

        final int CORE_NUMBER = 8;
        String[][] cpu_time = new String[CORE_NUMBER + 1][];
        String[][] cpu_time_last = new String[CORE_NUMBER + 1][];

        public void run() {
            try {
                //initialize cpu time;
                java.lang.Process init_reading = Runtime.getRuntime().exec(new String[]{"adb", "shell", "cat " + "/proc/stat"});
                BufferedReader init_br = new BufferedReader(new InputStreamReader(init_reading.getInputStream()));
                for (int i = 0; i <= CORE_NUMBER; i++) {
                    String line = init_br.readLine();
                    cpu_time_last[i] = line.split("\\s{1,}");
                    int sum = 0;
                    for (int j = 1; j <= 7; j++) {
                        sum += Integer.parseInt(cpu_time_last[i][j]);
                    }
                    //System.out.println("Initialize CPU time: " + line + " " + sum);
                }
                init_br.close();

                while (true) {
                    Thread.sleep(1000);
                    readCPU_load();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        /**
         * Read and print the cpu load of each core
         *
         * @return
         */
        private String readCPU_load() {
            //get per-core CPU utiliztaion


            java.lang.Process reading = null;
            StringBuffer result = new StringBuffer();
            try {
                reading = Runtime.getRuntime().exec(new String[]{"adb", "shell", "cat " + "/proc/stat"});
                BufferedReader br = new BufferedReader(new InputStreamReader(reading.getInputStream()));

                for (int i = 0; i <= CORE_NUMBER; i++) {
                    String line = br.readLine();
                    cpu_time[i] = line.split("\\s{1,}");
                    int sum = 0;
                    int sum_last = 0;
                    for (int j = 1; j <= 7; j++) {
                        sum += Integer.parseInt(cpu_time[i][j]);
                        sum_last += Integer.parseInt(cpu_time_last[i][j]);
                    }
                    double load = 1 - (Integer.parseInt(cpu_time[i][4]) - Integer.parseInt(cpu_time_last[i][4])) * 1.0 / (sum - sum_last);
                    //System.out.println("Core " + i + " In readCPU_load(): " + "unused slot: " + (Integer.parseInt(cpu_time[i][4]) - Integer.parseInt(cpu_time_last[i][4])) + " total slot: " + (sum - sum_last) + " load: " + load);//+" "+(Integer.parseInt(cpu_time[i][4])+ " "+ Integer.parseInt(cpu_time_last[i][4])));
                    result.append(","+load);
                    //copy cpu_time to cpu_time_last;
                    for (int k = 0; k < cpu_time_last[i].length; k++) {
                        cpu_time_last[i][k] = cpu_time[i][k];
                    }
                }
                System.out.println(""+System.currentTimeMillis()+result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();

        }


        /**
         * Read and print the cpu load of each core
         *
         * @return
         */
        private String readCPU_freq() {
            int CORE_NUMBER = 8;
            //get per-core CPU frequency
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < CORE_NUMBER; i++) {
                java.lang.Process reading = null;
                try {
                    reading = Runtime.getRuntime().exec(new String[]{"adb", "shell", "cat " + "/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_cur_freq"});
                    BufferedReader br = new BufferedReader(new InputStreamReader(reading.getInputStream()));
                    String line = br.readLine();
                    result.append(line + ";");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("In readCPU_freq():" + result);
            return result.toString();
        }

    }