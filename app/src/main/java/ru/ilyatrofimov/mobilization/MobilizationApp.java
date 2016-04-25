package ru.ilyatrofimov.mobilization;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import com.orm.SugarContext;


/**
 * @author Ilya Trofimov
 *
 * App instance
 */
public class MobilizationApp extends Application {
    public static final boolean IS_LOLLIPOP_OR_HIGHER
            = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    private static MobilizationApp mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        SugarContext.init(getApplicationContext());
        this.setAppContext(getApplicationContext());
    }

    public static MobilizationApp getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public void setAppContext(Context mAppContext) {
        MobilizationApp.mAppContext = mAppContext;
    }

    @Override
    public void onTerminate() {
        SugarContext.terminate();

        super.onTerminate();
    }
}
