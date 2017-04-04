package legacy.xzh;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.test.mock.MockContentResolver;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import org.xml.sax.ErrorHandler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;




/**
 * Created by Administrator on 2016/6/23.
 */

public class NotificationHook implements IXposedHookZygoteInit,IXposedHookLoadPackage{

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    String filename="log.txt";
    final static String packageHolder="currentPackage.txt";
    final AppNameGetter nameGetter = new AppNameGetter();
    String bindAction="android.intent.action.SERVICEBIND";
    String unbindAction="android.intent.action.SERVICEUNBIND";

    @Override
    public void initZygote(final StartupParam startupParam) throws Throwable {
        XposedBridge.log("log from xposed");

        /*//劫持openOrCreateDatabase
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "openOrCreateDatabase", String.class, int.class,
                SQLiteDatabase.CursorFactory.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //系统时间
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        String str = formatter.format(curDate);
                        //源componet
                        Object obj = param.thisObject;
                        String path = (String) param.args[0];
                        int pid = android.os.Process.myPid();//获取进程pid
                        int uid = android.os.Process.myUid();//获取uid
                        XposedBridge.log(",openOrCreateDatabase,"+path+","+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());
                    }
                });
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "openOrCreateDatabase", String.class, int.class,
                SQLiteDatabase.CursorFactory.class, DatabaseErrorHandler.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //系统时间
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        String str = formatter.format(curDate);
                        //源componet
                        Object obj = param.thisObject;
                        String path = (String) param.args[0];
                        int pid = android.os.Process.myPid();//获取进程pid
                        int uid = android.os.Process.myUid();//获取uid
                        XposedBridge.log(",openOrCreateDatabase,"+path+","+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());
                    }
                });
        //劫持SQLiteOpenHelper(Context,String, SQLiteDatabase.CursorFactory,int)
        XposedHelpers.findAndHookConstructor(SQLiteOpenHelper.class, Context.class, String.class, SQLiteDatabase.CursorFactory.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                //源componet
                Object obj = param.thisObject;
                String path = (String) param.args[1];
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                XposedBridge.log(",SQLiteOpenHelper,"+path+","+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());
            }
        });
        //劫持SQLiteOpenHelper(Context,String, SQLiteDatabase.CursorFactory,int,DatabaseErrorHandler)
        XposedHelpers.findAndHookConstructor(SQLiteOpenHelper.class, Context.class, String.class, SQLiteDatabase.CursorFactory.class, int.class, DatabaseErrorHandler.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                //源componet
                Object obj = param.thisObject;
                String path = (String) param.args[1];
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                XposedBridge.log(",openOrCreateDatabase,"+path+","+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());
            }
        });
        //劫持getReadableDatabase
        XposedHelpers.findAndHookMethod(SQLiteOpenHelper.class, "getReadableDatabase", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                //源componet
                Object obj = param.thisObject;
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                XposedBridge.log(",getReadableDatabase,,"+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());

            }
        });
        //劫持getWritableDatabase
        XposedHelpers.findAndHookMethod(SQLiteOpenHelper.class, "getWritableDatabase", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                //源componet
                Object obj = param.thisObject;
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                XposedBridge.log(",getWritableDatabase,,"+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());

            }
        });*/

        //劫持notify
        XposedHelpers.findAndHookMethod(NotificationManager.class, "notify", String.class, int.class, Notification.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                int id = (int) param.args[1];
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename, "notify," + id + "," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid) + ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",notify," + id + "," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid) + ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }
        });

        //劫持activity的onCreate和onDestroy
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                //源componet
                Object obj = param.thisObject;
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                XposedBridge.log(",activityOnCreate,,"+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());
                CsvWriter.writeSDFile(filename,"activityOnCreate,,"+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName()+"\n");

            }
        });

        XposedHelpers.findAndHookMethod(Activity.class, "onDestroy", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                //源componet
                Object obj = param.thisObject;
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                XposedBridge.log(",activityOnDestroy,,"+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());
                CsvWriter.writeSDFile(filename,"activityOnDestroy,,"+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName()+"\n");
            }
        });
            //劫持getContentResolver
            XposedHelpers.findAndHookMethod(ContextWrapper.class, "getContentResolver", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    Object obj = param.thisObject;
                    int pid = android.os.Process.myPid();
                    int uid = android.os.Process.myUid();
                    if(obj!=null) {
                        CsvWriter.writeSDFile(filename,"getContentResolver," + "," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid) + ",,"
                                + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName()+"\n");
                        XposedBridge.log(",getContentResolver," + "," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid) + ",,"
                                + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName());
                    }else{
                        CsvWriter.writeSDFile(filename,"getContentResolver," + "," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid) + ",,"
                                +nameGetter.getPkgNameFromUid(uid)+"\n");
                        XposedBridge.log(",getContentResolver," + "," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid) + ",,"
                                +nameGetter.getPkgNameFromUid(uid));
                    }
                }
            });
        //劫持acquireProvider(Uri)
        XposedHelpers.findAndHookMethod(ContentResolver.class, "acquireProvider",Uri.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                Uri uri = (Uri) param.args[0];
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                XposedBridge.log(",beforeacquireProvider,"+uri.getAuthority()+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                Uri uri = (Uri) param.args[0];
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"acquireProvider,"+uri.getAuthority()+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",acquireProvider,"+uri.getAuthority()+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }
        });

        //劫持acquireProvider(String)
        XposedHelpers.findAndHookMethod(ContentResolver.class, "acquireProvider",String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = (String) param.args[0];
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                XposedBridge.log(",beforeacquireProvider," + auth+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = (String) param.args[0];
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"acquireProvider," +auth+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",acquireProvider," + auth+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));

            }
        });

        //劫持acquireUnstableProvider(Uri)
        XposedHelpers.findAndHookMethod(ContentResolver.class, "acquireUnstableProvider",Uri.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                Uri uri = (Uri) param.args[0];
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                XposedBridge.log(",beforeacquireUnstableProvider,"+uri.getAuthority()+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                Uri uri = (Uri) param.args[0];
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"acquireProvider,"+uri.getAuthority()+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",acquireUnstableProvider,"+uri.getAuthority()+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));

            }
        });

        //劫持acquireUnstableProvider
        XposedHelpers.findAndHookMethod(ContentResolver.class, "acquireUnstableProvider",String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = (String) param.args[0];
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                XposedBridge.log(",beforeacquireUnstableProvider," + auth+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = (String) param.args[0];
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"acquireProvider," +auth+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",acquireUnstableProvider," + auth+"," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));

            }
        });
        //劫持query
        XposedHelpers.findAndHookMethod(ContentResolver.class, "query", Uri.class, String[].class, String.class, String[].class, String.class, CancellationSignal.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = ((Uri)param.args[0]).getAuthority();
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"beforequery,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",beforequery,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = ((Uri)param.args[0]).getAuthority();
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"afterquery,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",afterquery,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }
        });

        //劫持update
        XposedHelpers.findAndHookMethod(ContentResolver.class, "update", Uri.class, ContentValues.class, String.class, String[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = ((Uri)param.args[0]).getAuthority();
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"beforeupdate,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",beforeupdate,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = ((Uri)param.args[0]).getAuthority();
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"afterupdate,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",afterupdate,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }
        });

        //劫持insert
        XposedHelpers.findAndHookMethod(ContentResolver.class, "insert", Uri.class, ContentValues.class,new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = ((Uri)param.args[0]).getAuthority();
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"beforeinsert,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",beforeinsert,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = ((Uri)param.args[0]).getAuthority();
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"afterinsert,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",afterinsert,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }
        });

        //劫持delete
        XposedHelpers.findAndHookMethod(ContentResolver.class, "delete", Uri.class, String.class, String[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = ((Uri)param.args[0]).getAuthority();
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"beforedelete,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",beforedelete,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = ((Uri)param.args[0]).getAuthority();
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"afterdelete,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",afterdelete,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }
        });

  /*      //劫持getType
        XposedHelpers.findAndHookMethod(ContentResolver.class, "getType", Uri.class, String.class, String[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = ((Uri)param.args[0]).getAuthority();
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"beforegetType,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",beforegetType,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String auth = ((Uri)param.args[0]).getAuthority();
                int pid = android.os.Process.myPid();
                int uid = android.os.Process.myUid();
                CsvWriter.writeSDFile(filename,"aftergetType,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid)+"\n");
                XposedBridge.log(",aftergetType,"+auth+","+str+","+pid+","+uid+ "," + nameGetter.getNameFromUid(uid)+ ",,"
                        +nameGetter.getPkgNameFromUid(uid));
            }
        });*/

            //劫持Notification.Bulider的构造函数
            /*XposedHelpers.findAndHookConstructor(Notification.Builder.class, Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    Context context = (Context) param.args[0];
                    int pid = android.os.Process.myPid();
                    int uid = android.os.Process.myUid();
                    CsvWriter.writeSDFile(filename, "Builder,," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid) + ",,"
                                    + nameGetter.getPkgNameFromUid(uid) + "," + context.getClass().getName() + "\n");
                    XposedBridge.log(",Builder,," + str + "," + pid + "," + uid + "," + nameGetter.getNameFromUid(uid) + ",,"
                                    + nameGetter.getPkgNameFromUid(uid) + "," + context.getClass().getName());
                    }
            });*/


            //劫持startService
            XposedHelpers.findAndHookMethod(ContextWrapper.class, "startService", Intent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
/*                    int uid = android.os.Process.myUid();//获取uid
                    Intent intent = (Intent) param.args[0];
                    ComponentName com = intent.getComponent();
                    //第三方应用
                    if(nameGetter.isThirdPartyApp(nameGetter.getNameFromUid(uid))){
                        //不是启动自己的service
                        if(!nameGetter.getNameFromPkgName(com.getPackageName()).equals(nameGetter.getNameFromUid(uid))) {
                            Log.i("xposed", "劫持" + nameGetter.getNameFromUid(uid)+"唤醒"+nameGetter.getNameFromPkgName(com.getPackageName()));
                            Intent newIntent = new Intent();
                            param.args[0] = newIntent;
                        }
                    }*/
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    Object obj = param.thisObject;
                    int pid = android.os.Process.myPid();//获取进程pid
                    Intent intent = (Intent) param.args[0];
                    ComponentName com = intent.getComponent();
                    int uid = android.os.Process.myUid();//获取uid
                    String currentPackage=CsvWriter.readSDFile(packageHolder);
                    Log.i("xposed","==================="+currentPackage);
                    if(com!=null) {
                        CsvWriter.writeSDFile(filename, "startService," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName() + ","
                                + nameGetter.getNameFromPkgName(com.getPackageName()) + ","
                                + com.getPackageName() + "," + com.getClassName() + "\n");
                        XposedBridge.log(",startService," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName() + ","
                                + nameGetter.getNameFromPkgName(com.getPackageName()) + ","
                                + com.getPackageName() + "," + com.getClassName());
                        if(nameGetter.isThirdPartyApp(nameGetter.getNameFromUid(uid))&&nameGetter.getNameFromUid(uid)!=nameGetter.getNameFromPkgName(com.getPackageName())){
                            Log.i("act",nameGetter.getNameFromUid(uid)+"************唤醒"+nameGetter.getNameFromPkgName(com.getPackageName()));
                        }
                    }

                }
            });

            //劫持stopService
            XposedHelpers.findAndHookMethod(ContextWrapper.class, "stopService", Intent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    Intent intent = (Intent) param.args[0];
                    ComponentName com = intent.getComponent();
                    Object obj = param.thisObject;
                    int pid = android.os.Process.myPid();//获取进程pid
                    int uid = android.os.Process.myUid();//获取uid\
                    obj.getClass().getPackage().getName();
                    CsvWriter.writeSDFile(filename, "stopService," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                        + nameGetter.getNameFromUid(uid) + ",,"
                                        + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName() + ","
                                        + nameGetter.getNameFromPkgName(com.getPackageName()) + ","
                                        + com.getPackageName() + "," + com.getClassName() + "\n");
                    XposedBridge.log(",stopService," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                        + nameGetter.getNameFromUid(uid) + ",,"
                                        + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName() + ","
                                        + nameGetter.getNameFromPkgName(com.getPackageName()) + ","
                                        + com.getPackageName() + "," + com.getClassName());
                }
            });

        XposedHelpers.findAndHookMethod(Service.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                //源componet
                Object obj = param.thisObject;
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                XposedBridge.log(",onCreate,,"+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());
                CsvWriter.writeSDFile(filename,"onCreate,,"+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName()+"\n");
            }
        });

        XposedHelpers.findAndHookMethod(Service.class, "onDestroy", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                //源componet
                Object obj = param.thisObject;
                Context con = (Context) obj;
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                XposedBridge.log(",onDestroy,,"+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());
                CsvWriter.writeSDFile(filename,"onDestroy,,"+str+","+pid+","+uid+","+nameGetter.getNameFromUid(uid)+",,"+nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName()+"\n");
            }
        });

            //劫持bindService
            XposedHelpers.findAndHookMethod(ContextWrapper.class, "bindService", Intent.class, ServiceConnection.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    /*int uid = android.os.Process.myUid();//获取uid
                    Intent intent = (Intent) param.args[0];
                    ComponentName com = intent.getComponent();
                    //第三方应用
                    if(nameGetter.isThirdPartyApp(nameGetter.getNameFromUid(uid))&&com!=null){
                    //不是启动自己的service
                    if(!nameGetter.getNameFromPkgName(com.getPackageName()).equals(nameGetter.getNameFromUid(uid))) {
                        Log.i("xposed", "劫持" + nameGetter.getNameFromUid(uid)+"唤醒"+nameGetter.getNameFromPkgName(com.getPackageName()));
                        Intent newIntent = new Intent();
                        param.args[0] = newIntent;
                    }
                }*/
            }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    //系统时间
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    //目的componet
                    Intent intent = (Intent) param.args[0];
                    ServiceConnection serviceConnection = (ServiceConnection) param.args[1];
                    ComponentName com = null;
                    if (intent.getComponent() != null)
                        com = intent.getComponent();
                    //源componet
                    Object obj = param.thisObject;
                    int pid = android.os.Process.myPid();//获取进程pid
                    int uid = android.os.Process.myUid();//获取uid
                    Context con = (Context) obj;
                    String currentPackage=CsvWriter.readSDFile(packageHolder);
                    Log.i("xposed","==================="+currentPackage);
                    if (com != null) {
                        CsvWriter.writeSDFile(filename, "bindService," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                        + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                        + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName() + ","
                                        + nameGetter.getNameFromPkgName(com.getPackageName()) + ","
                                        + com.getPackageName() + "," + com.getClassName() +","+serviceConnection.getClass().getName()+ "\n");
                        XposedBridge.log(",bindService," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                        + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                        + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName() + ","
                                        + nameGetter.getNameFromPkgName(com.getPackageName()) + ","
                                        + com.getPackageName() + "," + com.getClassName()+","+serviceConnection.getClass().getName() );
                        if(nameGetter.isThirdPartyApp(nameGetter.getNameFromUid(uid))&&nameGetter.getNameFromUid(uid)!=nameGetter.getNameFromPkgName(com.getPackageName())){
                            Log.i("act",nameGetter.getNameFromUid(uid)+"************唤醒"+nameGetter.getNameFromPkgName(com.getPackageName()));
                        }
                        //bind的是第三方app的service
/*                        if(nameGetter.isThirdPartyApp(nameGetter.getNameFromPkgName(com.getPackageName()))){
                            Intent broadcastIntent=new Intent();
                            broadcastIntent.setAction(bindAction);
                            broadcastIntent.putExtra("packageAndService",com.getPackageName()+"/"+com.getClassName());
                            broadcastIntent.putExtra("serviceConnection",serviceConnection.getClass().getName());
                            con.sendBroadcast(broadcastIntent);
                        }*/
                    } else {
                        CsvWriter.writeSDFile(filename, "bindService," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                    + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName()+","+serviceConnection.getClass().getName() + "\n");
                        XposedBridge.log(",bindService," + intent.getAction() + ",," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                    + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName()+","+serviceConnection.getClass().getName());
                    }

                }
            });
            //劫持unbindService
            XposedHelpers.findAndHookMethod(ContextWrapper.class, "unbindService", ServiceConnection.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    //系统时间
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    //源componet
                    Object obj = param.thisObject;
                    int pid = android.os.Process.myPid();//获取进程pid
                    int uid = android.os.Process.myUid();//获取uid
                    Context con = (Context) obj;
                    ServiceConnection serviceConnection = (ServiceConnection) param.args[0];
                    String currentPackage=CsvWriter.readSDFile(packageHolder);
                    Log.i("xposed","==================="+currentPackage);
                    CsvWriter.writeSDFile(filename, "unbindService,," + str + "," + pid + "," + uid + ","
                                + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName() + ",,," + serviceConnection.getClass().getName() + "\n");
                    XposedBridge.log(",unbindService,," + str + "," + pid + "," + uid + ","
                                + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName() + ",,," + serviceConnection.getClass().getName());
                    //unbind的是第三方app
/*                    if(nameGetter.isThirdPartyApp(nameGetter.getPkgNameFromUid(uid))){
                        Intent broadcastIntent=new Intent();
                        broadcastIntent.setAction(unbindAction);
                        broadcastIntent.putExtra("serviceConnection",serviceConnection.getClass().getName());
                        con.sendBroadcast(broadcastIntent);
                    }*/
                }
            });

            //劫持sendBroadcast
            XposedHelpers.findAndHookMethod(ContextWrapper.class, "sendBroadcast", Intent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
/*                    int uid = android.os.Process.myUid();
                    if(nameGetter.isThirdPartyApp(nameGetter.getNameFromUid(uid))) {
                        Log.i("xposed", "劫持"+nameGetter.getNameFromUid(uid));
                        Intent intent = new Intent();
                        param.args[0] = intent;
                    }*/
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    //系统时间
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    Intent intent = (Intent) param.args[0];
                    Object obj = param.thisObject;
                    Context con = (Context) obj;
                    int pid = android.os.Process.myPid();//获取进程pid
                    int uid = android.os.Process.myUid();//获取uid
                    CsvWriter.writeSDFile(filename, "sendBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ",,"
                                    + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName() + "\n");
                    XposedBridge.log(",sendBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ",,"
                                    + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName());
                    }
            });

        //劫持sendBroadcast(Intent,String)
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "sendBroadcast", Intent.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                Intent intent = (Intent) param.args[0];
                Object obj = param.thisObject;
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                CsvWriter.writeSDFile(filename, "sendBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                            + nameGetter.getNameFromUid(uid) + ",,"
                            + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName() + "\n");
                XposedBridge.log(",sendBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                            + nameGetter.getNameFromUid(uid) + ",,"
                            + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName());
            }
        });

        //劫持sendOrderedBroadcast(Intent,String)
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "sendOrderedBroadcast", Intent.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                Intent intent = (Intent) param.args[0];
                Object obj = param.thisObject;
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                CsvWriter.writeSDFile(filename, "sendOrderedBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                            + nameGetter.getNameFromUid(uid) + ",,"
                            + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName() + "\n");
                XposedBridge.log(",sendOrderedBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                            + nameGetter.getNameFromUid(uid) + ",,"
                            + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName());
            }
        });

        //劫持sendOrderedBroadcast(Intent,String,BroadcastReceiver,Handler,int,String,Bundle)
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "sendOrderedBroadcast", Intent.class, String.class, BroadcastReceiver.class, Handler.class, int.class,
                String.class, Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //系统时间
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        String str = formatter.format(curDate);
                        Intent intent = (Intent) param.args[0];
                        Object obj = param.thisObject;
                        int pid = android.os.Process.myPid();//获取进程pid
                        int uid = android.os.Process.myUid();//获取uid
                        CsvWriter.writeSDFile(filename, "sendOrderedBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ",,"
                                    + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName() + "\n");
                        XposedBridge.log(",sendOrderedBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ",,"
                                    + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName());
                    }
                });

/*
        //劫持sendStickyBroadcast(Intent)
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "sendStickyBroadcast", Intent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //系统时间
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                Intent intent = (Intent) param.args[0];
                Object obj = param.thisObject;
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                CsvWriter.writeSDFile(filename, "sendStickyBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                            + nameGetter.getNameFromUid(uid) + ",,"
                            + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName() + "\n");
                XposedBridge.log(",sendStickyBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                            + nameGetter.getNameFromUid(uid) + ",,"
                            + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName());
            }
        });

        //劫持sendStickyOrderedBroadcast(Intent,BroadcastReceiver,Handler,int,String,Bundle)
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "sendStickyOrderedBroadcast", Intent.class, BroadcastReceiver.class, Handler.class, int.class,
                String.class, Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //系统时间
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        String str = formatter.format(curDate);
                        Intent intent = (Intent) param.args[0];
                        Object obj = param.thisObject;
                        int pid = android.os.Process.myPid();//获取进程pid
                        int uid = android.os.Process.myUid();//获取uid
                        CsvWriter.writeSDFile(filename, "sendStickyOrderedBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ",,"
                                    + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName() + "\n");
                        XposedBridge.log(",sendStickyOrderedBroadcast," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ",,"
                                    + nameGetter.getPkgNameFromUid(uid)  + "," + obj.getClass().getName());
                    }
                });
*/

        //劫持registerReceiver(BroadcastReceiver,IntentFilter)
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "registerReceiver", BroadcastReceiver.class, IntentFilter.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                Object obj=param.thisObject;
                IntentFilter intentFilter=(IntentFilter) param.args[1];
                Iterator<String> iterator=intentFilter.actionsIterator();
                String action="";
                while (iterator.hasNext()){
                    action+=iterator.next()+":";
                }
                XposedBridge.log(",registerReceiver," + action + "," + str + "," + pid + "," + uid + ","
                        + nameGetter.getNameFromUid(uid)+",,"
                        +nameGetter.getPkgNameFromUid(uid)+","+ obj.getClass().getName());
                CsvWriter.writeSDFile(filename,"registerReceiver," + action + "," + str + "," + pid + "," + uid + ","
                        + nameGetter.getNameFromUid(uid)+",,"
                        +nameGetter.getPkgNameFromUid(uid)+","+ obj.getClass().getName()+"\n");
            }
        });

        //劫持registerReceiver(BroadcastReceiver,IntentFilter,String,Handler)
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "registerReceiver", BroadcastReceiver.class, IntentFilter.class, String.class, Handler.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                Object obj=param.thisObject;
                IntentFilter intentFilter=(IntentFilter) param.args[1];
                Iterator<String> iterator=intentFilter.actionsIterator();
                String action="";
                while (iterator.hasNext()){
                    action+=iterator.next()+":";
                }
                XposedBridge.log(",registerReceiver," + action + "," + str + "," + pid + "," + uid + ","
                        + nameGetter.getNameFromUid(uid)+",,"
                        +nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName());
                CsvWriter.writeSDFile(filename,"registerReceiver," + action + "," + str + "," + pid + "," + uid + ","
                        + nameGetter.getNameFromUid(uid)+",,"
                        +nameGetter.getPkgNameFromUid(uid)+","+obj.getClass().getName()+"\n");
            }
        });

        //劫持unregisterReceiver
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "unregisterReceiver", BroadcastReceiver.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                int pid = android.os.Process.myPid();//获取进程pid
                int uid = android.os.Process.myUid();//获取uid
                Object obj=param.thisObject;
               XposedBridge.log(",unregisterReceiver," + "," + str + "," + pid + "," + uid + ","
                        + nameGetter.getNameFromUid(uid)+",,"
                        +nameGetter.getPkgNameFromUid(uid)+","+ obj.getClass().getName());
                CsvWriter.writeSDFile(filename,"unregisterReceiver," + "," + str + "," + pid + "," + uid + ","
                        + nameGetter.getNameFromUid(uid)+",,"
                        +nameGetter.getPkgNameFromUid(uid)+","+ obj.getClass().getName()+"\n");
            }
        });

//劫持startForeground
            XposedHelpers.findAndHookMethod(Service.class, "startForeground", int.class, Notification.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    Object obj = param.thisObject;
                    int uid = android.os.Process.myUid();
                    int pid = android.os.Process.myPid();
                    String currentPackage=CsvWriter.readSDFile(packageHolder);
                    Log.i("xposed","==================="+currentPackage);
                    CsvWriter.writeSDFile(filename, "startForeground,," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                    + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName() + "\n");
                    XposedBridge.log(",startForeground,," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                    + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName());
                    }
            });

            //劫持stopForeground
            XposedHelpers.findAndHookMethod(Service.class, "stopForeground", boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    Object obj = param.thisObject;
                    int uid = android.os.Process.myUid();
                    int pid = android.os.Process.myPid();
                    String currentPackage=CsvWriter.readSDFile(packageHolder);
                    Log.i("xposed","==================="+currentPackage);
                    CsvWriter.writeSDFile(filename, "stopForeground,," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                    + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName() + "\n");
                    XposedBridge.log(",stopForeground,," + str + "," + pid + "," + uid + ","
                                    + nameGetter.getNameFromUid(uid) + ","+currentPackage.contains(nameGetter.getPkgNameFromUid(uid))+","
                                    + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName());
                    }
            });
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        //劫持onReceive
//        if(!loadPackageParam.packageName.equals("com.example.android.pingme"))
//            return;\
/*        if(loadPackageParam.packageName.equals("com.xzh.helloworld")){
            Log.i("xposed","=================load helloworld");
            XposedHelpers.findAndHookMethod("com.xzh.helloworld.MainActivity", loadPackageParam.classLoader, "onReceive", Context.class, Intent.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Log.i("xposed","劫持动态注册广播");
                        }
                    });
        }*/
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        for(int i= AppNameGetter.getFirstReceiverByPkg(loadPackageParam.packageName);i<AppNameGetter.getLastReceiverByPkg(loadPackageParam.packageName);i++) {
            XposedHelpers.findAndHookMethod(nameGetter.getReceiverNameByIndex(i), loadPackageParam.classLoader, "onReceive", Context.class, Intent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    //系统时间
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    int pid = android.os.Process.myPid();//获取进程pid
                    int uid = android.os.Process.myUid();//获取uid
                    Intent intent = (Intent) param.args[1];
                    Object obj = param.thisObject;
                    XposedBridge.log(",beforeonReceive," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                            + nameGetter.getNameFromUid(uid) + ",,"
                            + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName());
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    //系统时间
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    int pid = android.os.Process.myPid();//获取进程pid
                    int uid = android.os.Process.myUid();//获取uid
                    Intent intent = (Intent) param.args[1];
                    Object obj = param.thisObject;
                    String currentPackage = CsvWriter.readSDFile(packageHolder);
                    Log.i("xposed", "===================" + currentPackage);
                    CsvWriter.writeSDFile(filename, "onReceive," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                            + nameGetter.getNameFromUid(uid) + "," + currentPackage.contains(nameGetter.getPkgNameFromUid(uid)) + ","
                            + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName() + "\n");
                    XposedBridge.log(",onReceive," + intent.getAction() + "," + str + "," + pid + "," + uid + ","
                            + nameGetter.getNameFromUid(uid) + "," + currentPackage.contains(nameGetter.getPkgNameFromUid(uid)) + ","
                            + nameGetter.getPkgNameFromUid(uid) + "," + obj.getClass().getName());
                }
            });
        }
    }
}
