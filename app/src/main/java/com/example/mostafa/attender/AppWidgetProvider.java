package com.example.mostafa.attender;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {
    RemoteViews remoteViews, remoteViews2;
    String teacherButtun = "teacher_login_click", studentButton = "student_login_click";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context,
                AppWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widID : allWidgetIds) {
            Log.d("ee5", "o3");
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.home_widget);
            remoteViews.setOnClickPendingIntent(R.id.teacherLogin_widget, getPendingSelfIntent(context, teacherButtun));
            remoteViews.setOnClickPendingIntent(R.id.studentLogin_widget, getPendingSelfIntent2(context, studentButton));
            appWidgetManager.updateAppWidget(widID, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, TeacherLogin.class);
        intent.setAction(action);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    protected PendingIntent getPendingSelfIntent2(Context context, String action) {
        Intent intent = new Intent(context, StudentLogin.class);
        intent.setAction(action);
        return PendingIntent.getActivity(context, 1, intent, 0);
    }
}
