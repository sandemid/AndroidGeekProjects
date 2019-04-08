package com.example.androidgeekproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class BackgroundService extends Service {

    private static final String ANDROID_CHANNEL_ID = "com.example.androidgeekproject";
    private static final CharSequence ANDROID_CHANNEL_NAME = "MY_CHANNEL";
    private int messageId = 0;
    private long t;
    private boolean bol;
    private NotificationManager mManager;
    private SensorManager sensorManager;
    private Sensor sensorTemp;
    private Handler handler;
//    private MyWorkerThread mWorkerThread;
    private SensorEventListener listenerTemp;
    private float curValue;

    public BackgroundService() {
        super();
        handler = new Handler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)  {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
//        mWorkerThread = new MyWorkerThread("mWorkerThread");
        t = System.currentTimeMillis();
        listenerTemp = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //проверяем, изменилось ли значение датчика
                if (curValue != event.values[0]) {
                    t = System.currentTimeMillis();
                    bol = true;
                    curValue = event.values[0];
                    //если изменений нет, то ждем 5 секунд и выводим уведомление 1 раз
                } else  {
                    if (System.currentTimeMillis() - t >= 5000 && bol) {
                        t = System.currentTimeMillis();
                        bol = false;
                        handler.post(() -> makeNote("Значение датчика t = " + Float.toString(curValue)));
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(listenerTemp, sensorTemp,
                SensorManager.SENSOR_DELAY_NORMAL);


        //Вариант использования многопоточности в сервисах. На АПИ 23 не сработал со слушателями. Оставлю для примера
//          Thread task = new Thread(() -> {
//            listenerTemp = new SensorEventListener() {
//                @Override
//                public void onSensorChanged(SensorEvent event) {
//                    //проверяем, изменилось ли значение датчика
//                    if (curValue != event.values[0]) {
//                        t = System.currentTimeMillis();
//                        bol = true;
//                        curValue = event.values[0];
//                    //если изменений нет, то ждем 5 секунд и выводим уведомление 1 раз
//                    } else  {
//                        if (System.currentTimeMillis() - t >= 5000 && bol) {
//                            t = System.currentTimeMillis();
//                            bol = false;
//                            handler.post(() -> makeNote("Значение датчика t = " + Float.toString(curValue)));
//                        }
//                    }
//                }
//                @Override
//                public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//                }
//            };
//            sensorManager.registerListener(listenerTemp, sensorTemp,
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        });
//        mWorkerThread.start();
//        mWorkerThread.prepareHandler();
//        mWorkerThread.postTask(task);
        return super.onStartCommand(intent, flags, startId);
    }

    class MyWorkerThread extends HandlerThread {

        private Handler mWorkerHandler;

        public MyWorkerThread(String name) {
            super(name);
        }

        public void postTask(Runnable task){
            mWorkerHandler.post(task);
        }

        public void prepareHandler(){
            mWorkerHandler = new Handler(getLooper());
        }
    }

    // Вывод уведомления в строке состояния, реализована поддержка разных API для уведомлений
    private void makeNote(String message){
        Notification.Builder builder;
        NotificationChannel androidChannel;
        if (Build.VERSION.SDK_INT >= 26) {
            androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            getManager().createNotificationChannel(androidChannel);
            builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Main service notification")
                    .setContentText(message);
        } else {
            builder = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Main service notification")
                    .setContentText(message);
        }
        Intent resultIntent = new Intent(this, BackgroundService.class);
        PendingIntent.getActivity(this,0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++, builder.build());
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(listenerTemp, sensorTemp);
//        mWorkerThread.quit();
//        makeNote("onDestroy");
    }
}
