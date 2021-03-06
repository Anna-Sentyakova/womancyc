package com.anna.sent.soft.womancyc.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.anna.sent.soft.logging.MyLog;

public class UpdateWidgetService extends IntentService {
    public UpdateWidgetService() {
        super(UpdateWidgetService.class.getSimpleName());
    }

    private String wrapMsg(String msg) {
        return getClass().getSimpleName() + ": " + msg;
    }

    private void log(String msg) {
        MyLog.getInstance().logcat(Log.DEBUG, wrapMsg(msg));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("create widget update service");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("destroy widget update service");
    }

    @Override
    protected void onHandleIntent(Intent service) {
        int appWidgetId = service.getIntExtra(
                MyCycleWidget.EXTRA_APP_WIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            AppWidgetProviderInfo info = manager.getAppWidgetInfo(appWidgetId);
            if (info != null) {
                String className = info.provider.getClassName();
                Builder builder = MyCycleWidget.getBuilder(className);
                log("update widget " + className);
                if (builder != null) {
                    RemoteViews views = builder.buildViews(this);
                    manager.updateAppWidget(appWidgetId, views);
                }
            }
        }
    }
}
