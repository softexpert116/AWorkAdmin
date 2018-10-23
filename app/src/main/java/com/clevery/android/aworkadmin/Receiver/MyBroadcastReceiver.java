package com.clevery.android.aworkadmin.Receiver;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.clevery.android.aworkadmin.App;
import com.clevery.android.aworkadmin.MainActivity;
import com.clevery.android.aworkadmin.R;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyBroadcastReceiver extends ParsePushBroadcastReceiver {
    String TAG = "TAG";
    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.d(TAG, json.getString("alert").toString());

            final String notificationTitle = json.getString("title").toString();
            final String notificationContent = json.getString("alert").toString();

            JSONObject data = json.getJSONObject("data");
            String type = data.getString("type");
            String senderId = data.getString("senderId");
            String report_type = data.getString("partyId");

            ArrayList<String> array = new ArrayList<>();
            if (!type.equals("report")) {
                return;
            }
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String currentClass = cn.getClassName();
            final String mainActivity = context.getPackageName() + ".MainActivity";

            if (currentClass.equals(mainActivity)) {
                // refresh main activity, no notification
                MainActivity.parseReportFetch();
                App.cancelAllNotifications();
                return;
            }

            Intent pendingIntent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
//Customize your notification - sample code
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.app_icon)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationContent)
                            .setContentIntent(PendingIntent.getActivity(
                                    context,
                                    0,
                                    pendingIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            ))
                            .setAutoCancel(true);

            int mNotificationId = 001;
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, builder.build());


        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }


    }
}
