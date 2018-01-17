package online.himakeit.chitchat;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.facebook.stetho.inspector.database.DefaultDatabaseConnectionProvider;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;
import com.socks.library.KLog;

import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.display.FadeInBitmapDisplayer;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.RealTimeLocationMessageProvider;
import io.rong.imlib.ipc.RongExceptionHandler;
import online.himakeit.chitchat.message.TestMessage;
import online.himakeit.chitchat.message.provider.ContactNotificationMessageProvider;
import online.himakeit.chitchat.message.provider.TestMessageProvider;
import online.himakeit.chitchat.stetho.MyDatabaseDriver;
import online.himakeit.chitchat.stetho.MyDatabaseFilesProvider;
import online.himakeit.chitchat.utils.SharedPreferencesContext;
import online.himakeit.chitchat.utils.Toasts;

/**
 * @author：LiXueLong
 * @date：2018/1/6
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class MyApplication extends MultiDexApplication {

    private static DisplayImageOptions options;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        Stetho.initialize(new Stetho.Initializer(this) {
            @Override
            protected Iterable<DumperPlugin> getDumperPlugins() {
                return new Stetho.DefaultDumperPluginsBuilder(context).finish();
            }

            @Override
            protected Iterable<ChromeDevtoolsDomain> getInspectorModules() {
                Stetho.DefaultInspectorModulesBuilder defaultInspectorModulesBuilder = new Stetho.DefaultInspectorModulesBuilder(context);
                defaultInspectorModulesBuilder.provideDatabaseDriver(new MyDatabaseDriver(context, new MyDatabaseFilesProvider(context), new DefaultDatabaseConnectionProvider()));
                return defaultInspectorModulesBuilder.finish();
            }
        });
        if (getApplicationInfo().packageName.equals(getCurProcessName(context))) {

            // TODO: 2018/1/8 推送消息配置

            /**
             * 注意：
             *
             * IMKit SDK调用第一步 初始化
             *
             * context上下文
             *
             * 只有两个进程需要初始化，主进程和 push 进程
             */
            RongIM.init(this);
            initLog();
            initToast();
            SealAppContext.init(this);
            SharedPreferencesContext.init(this);
            Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));

            try {
                RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
                RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());
                RongIM.registerMessageType(TestMessage.class);
                RongIM.registerMessageTemplate(new TestMessageProvider());
            } catch (Exception e) {
                e.printStackTrace();
            }

            openSealDBIfHasCachedToken();
            initImageOptiions();

        }
    }

    private void initToast() {
        Toasts.register(context);
    }

    public static Context getContext() {
        return context;
    }

    private void openSealDBIfHasCachedToken() {

    }

    private void initImageOptiions() {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.de_default_portrait)
                .showImageOnFail(R.drawable.de_default_portrait)
                .showImageOnLoading(R.drawable.de_default_portrait)
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    private void initLog() {
        KLog.init(BuildConfig.LOG_DEBUG, "*******XueLong*******");
    }

    public static DisplayImageOptions getOptions() {
        return options;
    }

    public static String getCurProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : activityManager.getRunningAppProcesses()) {
            if (appProcessInfo.pid == pid) {
                return appProcessInfo.processName;
            }
        }

        return null;
    }
}
