package com.chen.natification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chen.natification.app.AppApplication;

public class MainActivity extends AppCompatActivity {

    Context mContext;

    String title = "推送";
    String text = "我是消息";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        IfNatification();
        launchPush();
    }


    //针对于安卓8.0之上 申请手机通知权限 需用户手动打开 无法自动开启
    //工信部2017年成立统一推送联盟 解决安卓因推送导致安卓手机卡顿的问题 推送部分因进程保活也是导致安卓卡顿的主要原因之一
    public void IfNatification(){
        if (!NotificationManagerCompat.from(AppApplication.context).areNotificationsEnabled()) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                    .title("请手动将通知打开")
                    .positiveText("确定")
                    .negativeText("取消");
            builder.onAny(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    if (which == DialogAction.NEUTRAL) {
                        Log.e("onClick", "更多信息: ");
                    } else if (which == DialogAction.POSITIVE) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <  Build.VERSION_CODES.O) {
                            Intent intent = new Intent();
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("app_package", MainActivity.this.getPackageName());
                            intent.putExtra("app_uid", MainActivity.this.getApplicationInfo().uid);
                            startActivity(intent);
                        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                            startActivity(intent);
                        } else {
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                            startActivity(localIntent);
                        }
                        Log.e("onClick", "同意: ");
                    } else if (which == DialogAction.NEGATIVE) {
                        Log.e("onClick", "不同意: ");
                    }
                }
            }).show();
        }
    }


    //发起通知栏推送
    //此处判断安卓版本号是否大于或者等于Android8.0

    public void launchPush(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat";//设置通道的唯一ID
            String channelName = "聊天消息";//设置通道名
            int importance = NotificationManager.IMPORTANCE_HIGH;//设置通道优先级
            createNotificationChannel(channelId, channelName, importance,title,text);
        } else {
            sendSubscribeMsg(title,text);
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
     private void createNotificationChannel(String channelId, String channelName, int importance,String title,String text) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) AppApplication.context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        sendSubscribeMsg(title,text);
    }

    public void sendSubscribeMsg(String title,String text) {
        NotificationManager manager = (NotificationManager) AppApplication.context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(AppApplication.context, "chat")
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(AppApplication.context.getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .build();
        manager.notify(2, notification);
    }

}
