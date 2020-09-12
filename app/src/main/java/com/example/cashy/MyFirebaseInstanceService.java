package com.example.cashy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

//    public  static int NOTIFICATION_ID = 1;

//    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//
//        Log.d("TOKENFIREBASE",s);
//    }

//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
////        super.onMessageReceived(remoteMessage);
////        if (remoteMessage.getData().isEmpty()) {
////
////            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
////        }
////
////        else
////        {
////            showNotification(remoteMessage.getData());
////
////        }
//
//        generateNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
//
//    }
//
//    private void generateNotification(String title, String body) {
//        Intent intent = new Intent(this,MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
//                            intent,PendingIntent.FLAG_ONE_SHOT);
//
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)//change to app icon
//                .setContentTitle(title)
//                .setContentText(body)
//                .setAutoCancel(true)
//                .setSound(soundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager)
//                getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if(NOTIFICATION_ID>1073741824){
//            NOTIFICATION_ID = 0;
//        }
//
//        notificationManager.notify(NOTIFICATION_ID++,notificationBuilder.build());
//
//
//    }

//    private void showNotification(Map<String, String> data) {
//        String title = data.get("title").toString();
//        String body = data.get("body").toString();
//
//
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        String NOTIFICATION_CHANNEL_ID = "example.cashy.test";
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//        {
//            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            notificationChannel.setDescription("Free games");
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.BLUE);
//            notificationChannel.enableLights(true);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
//
//        notificationBuilder.setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setContentInfo("Info");
//
//        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());
//
//
//    }

//    private void showNotification(String title, String body) {
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        String NOTIFICATION_CHANNEL_ID = "example.cashy.test";
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//        {
//            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            notificationChannel.setDescription("Free games");
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.BLUE);
//            notificationChannel.enableLights(true);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
//
//        notificationBuilder.setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setContentInfo("Cashy");
//
//        notificationManager.notify(new Random().nextInt(),notificationBuilder.build());
//    }
//

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("received", "onMessageReceived: Received...");
        String key = remoteMessage.getData().get("Key");
        Intent intent = new Intent(this,MainActivity.class);
        intent.setData(Uri.parse(key));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
                           intent,PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Default";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId);
        notificationBuilder.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId
                                    ,"Default channel",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0,notificationBuilder.build());

    }




}
