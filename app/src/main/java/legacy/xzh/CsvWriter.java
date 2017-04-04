package legacy.xzh;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;

/**
 * Created by zhx on 2016/6/26.
 */
public class CsvWriter {
/*    public static final String mComma = ",";
    private static StringBuilder mStringBuilder = null;
    private static String mFileName = null;*/
    private static String filepath="/sdcard/Log/";

    public static void writeSDFile(String filename,String write_str) throws IOException{

        //String filepath="/sdcard/Log/";
        File file = new File(filepath+filename);
        if(!file.exists())
            file.createNewFile();
 /*       FileWriter fileWriter=new FileWriter(filepath+filename,true);
        fileWriter.write(write_str);
        fileWriter.close();*/
        try {
            RandomAccessFile fis = new RandomAccessFile(file, "rw"); //单一线程的读写同步
            long fileLength = fis.length();
            fis.seek(fileLength);
            FileChannel fcin = fis.getChannel(); // 获得文件通道
            FileLock flin = null; //声明文件锁对象
            while (true) {
                flin = fcin.tryLock(0L, Long.MAX_VALUE, true);
                //tryLock是尝试获取锁，有可能为空，所以要判断
                if (flin == null) {
                    Log.i("xposed", "********************文件被锁定");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else
                    break;
            }
            byte[] bytes = write_str.getBytes();
            fis.write(bytes);
            if (flin != null && flin.isValid()) {
                flin.release();
            }
            fcin.close();
            fis.close();
            fis = null;
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
}

    public static void writeNewSDFile(String filename,String value) throws IOException{
        File file = new File(filepath+filename);
        if(!file.exists())
            file.createNewFile();
        try {
            RandomAccessFile fis = new RandomAccessFile(file, "rw"); //单一线程的读写同步
            FileChannel fcin = fis.getChannel(); // 获得文件通道
            FileLock flin = null; //声明文件锁对象
            while (true) {
                flin = fcin.tryLock(0L, Long.MAX_VALUE, true);
                //tryLock是尝试获取锁，有可能为空，所以要判断
                if (flin == null) {
                    Log.i("xposed", "********************文件被锁定");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else
                    break;
            }

            byte[] bytes = value.getBytes();
            fis.write(bytes);
            if (flin != null && flin.isValid()) {
                flin.release();
            }
            fcin.close();
            fis.close();
            fis = null;
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readSDFile(String filename) throws IOException{
        String currentPackage="system";
        File file = new File(filepath+filename);
        if(file.exists()) {
            try {
                RandomAccessFile fis = new RandomAccessFile(file, "rw"); //单一线程的读写同步
                FileChannel fcin = fis.getChannel(); // 获得文件通道
                FileLock flin = null; //声明文件锁对象
                while (true) {
                    flin = fcin.tryLock(0L, Long.MAX_VALUE, true);
                    //tryLock是尝试获取锁，有可能为空，所以要判断
                    if (flin == null) {
                        Log.i("xposed", "********************文件被锁定");
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else
                        break;
                }
                currentPackage = fis.readLine();
                if (flin != null && flin.isValid()) {
                    flin.release();
                }
                fcin.close();
                fis.close();
                fis = null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return currentPackage;
    }

    public static void readSDFile(ArrayList<String> service, String filename){
        File file = new File(filepath+filename);
        if(file.exists()) {
            FileReader fileReader=null;
            BufferedReader bufferedReader=null;
            try {
                fileReader=new FileReader(filepath+filename);
                bufferedReader=new BufferedReader(fileReader);
                String b;
                while((b=bufferedReader.readLine())!=null){
                    service.add(b);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }finally {
                try{
                    if(bufferedReader!=null)
                        bufferedReader.close();
                    if(fileReader!=null)
                        fileReader.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean createDir() {
        File dir = new File("/sdcard/");
        if(dir.exists()&&dir.canWrite()) {
            dir = new File(filepath);
            if(dir.exists())
                return true;
            else if(dir.mkdirs())
                return true;
            else
                return false;
            }
        else
            return false;
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(filepath+fileName);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }


   /* public static void open(String filename, int clounmNum) {
        String folderName = null;
        //if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (path != null) {
                folderName = path +"/CSV/";
            }
        //}

        File fileRobo = new File(folderName);
        if(!fileRobo.exists()){
            fileRobo.mkdir();
        }
        mFileName = folderName + filename+".csv";
        mStringBuilder = new StringBuilder();
        for(int i=0;i<clounmNum-1;i++){
            mStringBuilder.append("column"+(i+1));
            mStringBuilder.append(mComma);
        }
        mStringBuilder.append("column"+clounmNum);
        mStringBuilder.append("\n");
    }

    public static void writeCsv(String... value) {

        for(int i=0;i<value.length-1;i++){
            mStringBuilder.append(value[i]);
            mStringBuilder.append(mComma);
        }
        mStringBuilder.append(value[value.length-1]);
        mStringBuilder.append("\n");
    }

    public static void flush() {
        if (mFileName != null) {
            try {
                File file = new File(mFileName);
                FileOutputStream fos = new FileOutputStream(file, false);
                fos.write(mStringBuilder.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("You should call open() before flush()");
        }
    }*/
}
