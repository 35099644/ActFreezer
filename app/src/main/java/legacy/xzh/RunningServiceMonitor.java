package legacy.xzh;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by zhx on 2016/8/15.
 */
public class RunningServiceMonitor extends Thread {
    private ArrayList<String> runningServices;
    private ArrayList<ActivityManager.RunningServiceInfo> runningServiceInfos;
    private static ActivityManager activityManager;
    public static boolean started=false;
    private static Context context;
    private static ServiceFreezer serviceFreezer=new ServiceFreezer();
    public static String[] thirdPartyApp={
            "com.skype.raider",
            "com.google.android.youtube",
            "com.baidu.news",
            "com.qiyi.video",
            "com.shazam.android",
            "com.sankuai.meituan",
            "com.youku.phone",
            "com.viber.voip",
            "com.tencent.qqmusic",
            "com.lesports.glivesports",
            "com.whatsapp",
            "jp.naver.line.android",
            "com.tencent.androidqqmail",
            "com.changba.live",
            "com.dianping.v1",
            "com.facebook.moments",
            "com.mt.mtxx.mtxx",
            "com.taou.maimai",
            "com.hulu.plus",
            "com.paypal.android.p2pmobile",
            "com.duowan.mobile",
            "com.yixia.videoeditor",
            "com.autonavi.xmgd.navigator",
            "com.tudou.android",
            "me.ele",
            "com.taobao.fleamarket",
            "com.tencent.mm",
            "com.sankuai.meituan.takeoutnew",
            "com.sdu.didi.psnger",
            "com.xunlei.downloadprovider",
            "cn.amazon.mShop.android",
            "com.baidu.searchbox",
            "com.instagram.android",
            "com.wuba",
            "com.ohsame.android",
            "com.pinterest",
            "com.letv.android.client",
            "com.quvideo.xiaoying",
            "mobi.ifunny",
            "com.yixia.xiaokaxiu",
            "com.cheerfulinc.flipagram",
            "com.tencent.qqlive",
            "com.meitu.meiyancamera",
            "fm.xiami.main",
            "com.yelp.android",
            "com.thestore.main",
            "com.dropbox.android",
            "com.twitter.android",
            "com.pandora.android",
            "com.ss.android.article.news",
            "com.youdao.dict",
            "com.youdao.note",
            "com.meelive.ingkee",
            "com.enflick.android.TextNow",
            "com.UCMobile",
            "com.wenming.androidprocess",
            "com.sohu.sohuvideo",
            "tv.twitch.android.app",
            "com.baidu.tieba",
            "com.baidu.video",
            "com.tencent.mobileqq",
            "co.vine.android",
            "com.netease.newsreader.activity",
            "com.kugou.android",
            "com.example.android.pingme",
            "com.gotokeep.keep",
            "com.netflix.mediaclient",
            "com.zhiliaoapp.musically",
            "com.wandoujia.phoenix2",
            "com.spotify.music",
            "com.immomo.momo",
            "com.ubercab",
            "com.facebook.katana",
            "com.baidu.BaiduMap",
            "com.zhihu.android",
            "com.meitu.meipaimv",
            "com.tuniu.app.ui",
            "com.sgiggle.production",
            "com.facebook.orca",
            "com.Qunar",
            "org.mozilla.firefox",
            "com.nuomi",
            "com.qzone",
            "com.tencent.news",
            "com.evernote",
            "com.linkedin.android",
            "com.by.butter.camera",
            "com.jingdong.app.mall",
            "com.tmall.wireless",
            "com.tumblr",
            "ctrip.android.view",
            "cn.kuwo.player",
            "com.soundcloud.android",
            "com.moji.mjweather",
            "com.sina.weibo",
            "com.groupon",
            "com.smile.gifmaker",
            "com.ebay.mobile",
            "com.baidu.netdisk",
            "com.taobao.taobao",
            "com.hiapk.marketpho",
            "com.netease.cloudmusic",
            "com.changba",
            "com.eg.android.AlipayGphone",
            "com.snapchat.android",
            "com.achievo.vipshop",
    };
    int maxNum=500;

    public static void setContext(Context context) {
        RunningServiceMonitor.context = context;
    }

    public static boolean isThirdParty(String packagename){
        for(int i=0;i<thirdPartyApp.length;i++){
            if(thirdPartyApp[i].equals(packagename))
                return true;
        }
        return false;
    }

    @Override
    public void run() {
        super.run();
        activityManager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        runningServiceInfos=new ArrayList<>();
        runningServices=new ArrayList<>();
        Log.i("test","RunningServiceMonitor is running");
        started=true;
        while(true) {
            runningServiceInfos = (ArrayList) activityManager.getRunningServices(maxNum);
            if(runningServices.size()>0)
                runningServices.clear();
            for(ActivityManager.RunningServiceInfo runningService:runningServiceInfos) {
                //第三方app
                if (isThirdParty(runningService.service.getPackageName())) {
                    runningServices.add(runningService.service.getPackageName() + "/" + runningService.service.getClassName());
                    Log.i("test", "==========" + runningService.service.getPackageName() + "/" + runningService.service.getClassName());
                }
            }
/*            ServiceFreezer.setRunningServices(runningServices);
            ServiceFreezer.cancelRunningTask();
            ServiceFreezer.startTasks();*/
//            serviceFreezer.setRunningServices(runningServices);
//            serviceFreezer.cancelRunningTask();
//            serviceFreezer.startTasks();
            try{
                Thread.sleep(240000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
