package com.netlab.util;

import android.os.Environment;
import android.os.Process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by laizeqi on 4/16/17.
 */

public class Tools {


    private static String log_path = Environment.getExternalStorageDirectory().getPath();

    private static File log = null;

    private static BufferedWriter log_bw = null;

    private static boolean append = false;

    public static void writeToLog(String line) {
        try {
            log_bw.append(line + "\n");
            log_bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void setLogPath(String path) {
        log_path = path;
    }

    public static void setAppend(boolean append) {
        Tools.append = append;
    }

    public static void initialize(String path, boolean append) {
        Tools.setLogPath(path);
        Tools.setAppend(append);

        if (append) {
            log = new File(log_path);
            try {
                Runtime.getRuntime().exec("su");
                log_bw = new BufferedWriter(new FileWriter(log, true));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Process = " + Process.myPid());
            }
        } else {
            log = new File(log_path);
            try {
                if (log.exists()) {
                    log.delete();
                    log.createNewFile();
                }
                log_bw = new BufferedWriter(new FileWriter(log));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    static final int[] uid_array =
            {
                    10075,
                    1001,
                    1001,
                    10029,
                    10001,
                    10008,
                    10009,
                    10079,
                    10105,
                    10016,
                    10098,
                    10031,
                    1000,
                    10116,
                    10125,
                    10086,
                    10042,
                    10010,
                    10041,
                    10052,
                    1001,
                    10127,
                    1001,
                    10081,
                    10082,
                    10008,
                    10109,
                    10090,
                    10113,
                    10089,
                    10065,
                    10097,
                    10063,
                    10078,
                    10005,
                    10007,
                    10136,
                    10008,
                    10022,
                    10060,
                    10111,
                    1000,
                    10084,
                    10126,
                    10038,
                    10003,
                    10076,
                    1000,
                    10134,
                    10114,
                    10019,
                    10047,
                    1027,
                    1001,
                    10000,
                    10094,
                    10128,
                    10048,
                    10066,
                    1001,
                    10026,
                    10095,
                    10068,
                    1001,
                    10135,
                    10024,
                    1001,
                    1000,
                    10025,
                    10059,
                    10069,
                    1001,
                    10033,
                    10101,
                    1000,
                    10014,
                    10117,
                    10120,
                    10092,
                    10107,
                    10040,
                    10021,
                    10043,
                    10058,
                    10077,
                    10004,
                    10074,
                    10013,
                    10118,
                    1000,
                    10046,
                    10044,
                    10099,
                    1000,
                    10067,
                    10035,
                    10039,
                    10121,
                    10017,
                    10012,
                    10012,
                    10028,
                    10051,
                    10115,
                    10073,
                    10002,
                    10018,
                    10072,
                    10096,
                    10023,
                    10015,
                    10062,
                    10036,
                    10020,
                    10088,
                    1001,
                    10106,
                    10061,
                    10087,
                    10012,
                    10057,
                    1000,
                    10091,
                    10108,
                    10112,
                    10110,
                    10055,
                    10012,
                    10119,
                    1000,
                    10050,
                    10103,
                    10070,
                    10123,
                    10133,
                    10080,
                    10071,
                    10124,
                    10006,
                    10104,
                    10054,
                    10129,
                    10030,
                    10083,
                    10053,
                    10085,
                    10102,
                    1001,
                    2000,
                    10002,
                    10049,
                    1000,
                    10027,
                    10132,
                    10034,
                    1000,
                    10032,
                    10122,
                    10064,
                    10011,
                    1002,
                    1000,
                    10002,
                    10037,
                    10045,
                    10100,
                    10093,
                    10056,
            };


    final static String[] pkg_array = {


            "com.google.android.youtube",
            "com.android.providers.telephony",
            "com.android.sdm.plugins.connmo",
            "com.google.android.googlequicksearchbox",
            "com.android.providers.calendar",
            "com.android.providers.media",
            "com.huawei.entitlement",
            "com.google.android.apps.docs.editors.docs",
            "com.qiyi.video",
            "com.google.android.onetimeinitializer",
            "com.sohu.inputmethod.sogou",
            "com.android.wallpapercropper",
            "com.quicinc.cne.CNEService",
            "com.mogujie",
            "com.sankuai.meituan",
            "com.speedsoftware.rootexplorer",
            "com.android.documentsui",
            "com.android.externalstorage",
            "com.google.android.apps.enterprise.dmagent",
            "com.android.htmlviewer",
            "com.huawei.callstatisticsutils",
            "jp.naver.line.android",
            "com.android.mms.service",
            "com.google.android.apps.docs.editors.sheets",
            "com.google.android.apps.docs.editors.slides",
            "com.android.providers.downloads",
            "com.dianping.v1",
            "com.facebook.moments",
            "com.cleanmaster.mguard_cn",
            "com.mt.mtxx.mtxx",
            "com.google.android.apps.messaging",
            "com.grubhub.android",
            "android.autoinstalls.config.google.nexus",
            "com.google.android.apps.genie.geniewidget",
            "com.google.android.configupdater",
            "com.android.defcontainer",
            "de.robv.android.xposed.installer",
            "com.android.providers.downloads.ui",
            "com.android.vending",
            "com.android.pacprocessor",
            "com.netlab.actfreezer",
            "com.qualcomm.cabl",
            "com.speedsoftware.explorer",
            "com.tencent.mm",
            "com.android.certinstaller",
            "com.android.carrierconfig",
            "com.google.android.marvin.talkback",
            "android",
            "com.ftw_and_co.happn",
            "com.baidu.searchbox",
            "com.android.hotwordenrollment",
            "com.google.android.apps.inputmethod.hindi",
            "com.android.nfc",
            "com.android.stk",
            "com.android.backupconfirm",
            "com.instagram.android",
            "com.waze",
            "com.google.android.launcher",
            "com.google.android.deskclock",
            "org.codeaurora.ims",
            "com.android.statementservice",
            "com.instagram.layout",
            "com.google.android.gm",
            "com.android.sdm.plugins.sprintdm",
            "com.google.android.instantapps.supervisor",
            "com.google.android.setupwizard",
            "com.qualcomm.qcrilmsgtunnel",
            "com.android.providers.settings",
            "com.android.sharedstoragebackup",
            "com.google.android.music",
            "com.android.printspooler",
            "com.android.sdm.plugins.diagmon",
            "com.android.dreams.basic",
            "com.baidu.appsearch",
            "com.android.inputdevices",
            "com.google.android.dialer",
            "com.meitu.meiyancamera",
            "com.tencent.mtt",
            "me.msqrd.android",
            "fm.xiami.main",
            "com.google.android.apps.cloudprint",
            "com.android.musicfx",
            "com.google.android.apps.docs",
            "com.google.android.apps.maps",
            "com.google.android.apps.plus",
            "com.android.cellbroadcastreceiver",
            "com.google.android.webview",
            "com.google.android.contacts",
            "com.ss.android.article.news",
            "com.android.server.telecom",
            "com.google.android.syncadapters.contacts",
            "com.android.facelock",
            "com.youdao.dict",
            "com.android.keychain",
            "com.google.android.gm.exchange",
            "com.google.android.calculator",
            "com.android.chrome",
            "com.toutiaoke",
            "com.google.android.packageinstaller",
            "com.google.android.gms",
            "com.google.android.gsf",
            "com.google.android.tag",
            "com.google.android.tts",
            "com.baidu.input",
            "com.google.android.apps.walletnfcrel",
            "com.android.calllogbackup",
            "com.google.android.partnersetup",
            "com.google.android.videos",
            "com.instagram.boomerang",
            "com.android.proxyhandler",
            "com.google.android.feedback",
            "com.google.android.apps.photos",
            "com.google.android.calendar",
            "com.android.managedprovisioning",
            "com.facebook.katana",
            "com.android.sdm.plugins.dcmo",
            "com.baidu.BaiduMap",
            "com.android.providers.partnerbookmarks",
            "shaojuanzi.receiverapp",
            "com.google.android.gsf.login",
            "com.android.wallpaper.livepicker",
            "com.huawei.mmitest",
            "com.facebook.orca",
            "com.Qunar",
            "com.qzone",
            "shaojuanzi.senderapp",
            "com.google.android.inputmethod.korean",
            "com.google.android.backuptransport",
            "com.tencent.news",
            "com.android.settings",
            "com.google.android.inputmethod.pinyin",
            "com.jingdong.app.mall",
            "com.google.android.setupwizard.overlay.smartdevice",
            "com.tmall.wireless",
            "com.tinder",
            "com.google.android.apps.books",
            "com.google.android.apps.tycho",
            "com.taobao.idlefish",
            "com.android.omadm.service",
            "ctrip.android.view",
            "com.huawei.sarcontrolservice",
            "com.google.android.apps.pdfviewer",
            "com.android.vpndialogs",
            "com.google.android.keep",
            "com.google.android.talk",
            "eu.chainfire.supersu",
            "com.sina.weibo",
            "com.android.phone",
            "com.android.shell",
            "com.android.providers.userdictionary",
            "com.google.android.inputmethod.japanese",
            "com.android.location.fused",
            "com.android.systemui",
            "com.taggedapp",
            "com.android.bluetoothmidiservice",
            "com.android.huawei.hiddenmenu",
            "com.google.android.launcher.layouts.angler",
            "com.taobao.taobao",
            "com.google.android.play.games",
            "com.google.android.apps.gcs",
            "com.android.bluetooth",
            "com.qualcomm.timeservice",
            "com.android.providers.contacts",
            "com.android.captiveportallogin",
            "com.google.android.GoogleCamera",
            "com.eg.android.AlipayGphone",
            "com.snapchat.android",
            "com.google.android.inputmethod.latin"

    };

    static final HashMap<Integer, String> uid_pkg = new HashMap();

    static {
        for (int index = 0; index < uid_array.length; index++) {
            uid_pkg.put(uid_array[index], pkg_array[index]);
        }
    }

    public static boolean checkUid (int uid)
    {
        return uid_pkg.containsKey(uid);
    }

    public static String getPkgName(int uid)
    {
        return uid_pkg.get(uid);
    }

}
