package techub.cet.cetalks;

/**
 * Created by amrith on 4/17/17.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;



/**
 * Created by kevin on 2/15/17.
 */

public class RadioService extends Service {
    public static boolean iSRunning=false;
    private static final String LOG_TAG = "RadioService";
    MediaPlayer mp;
    boolean flag=true;
    Thread statusThread;
    @Override
    public void onCreate() {
        Log.d("Service","started");
        super.onCreate();
        mp=new MediaPlayer();
        statusThread=new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                Request request= new Request.Builder()
                        .url("https://public.radio.co/stations/s7114f1b4e/status").build();
                while (flag) {
                    try {
                        JSONObject json=new JSONObject(client.newCall(request).execute().body().string());
                        Log.d("trackname",new JSONObject(json.get("current_track").toString()).get("title").toString());
                        updateNotification(new JSONObject(json.get("current_track").toString()).get("title").toString());
                        Intent intent=new Intent();
                        intent.setAction(Constants.ACTION.SONG_CHANGE);
                        intent.putExtra("song",new JSONObject(json.get("current_track").toString()).get("title").toString());
                        sendBroadcast(intent);
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Intent intent=new Intent();
                intent.setAction(Constants.ACTION.LOADED);
                sendBroadcast(intent);
                mp.start();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent broadcast=new Intent();
        broadcast.setAction(Constants.ACTION.CHANGE_STATE);
        if(intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)){
            Log.d(LOG_TAG,"start action");
            updateNotification("My song");
            sendBroadcast(broadcast);
            try {
                mp.setDataSource("https://stream.radio.co/s7114f1b4e/listen");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            statusThread.start();
        }else if(intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)){
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            iSRunning=false;
//            mp.stop();
//            mp.reset();
            mp.release();
            flag=false;
            Intent i=new Intent();
            i.setAction(Constants.ACTION.LOADED);
            sendBroadcast(i);
            sendBroadcast(broadcast);
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }
    private void updateNotification(String songname){
        Intent notificationIntent = new Intent(this,Cetalks.class);
//        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Intent playIntent = new Intent(this, RadioService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);
        Intent pauseIntent = new Intent(this, RadioService.class);
        pauseIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent ppauseIntent = PendingIntent.getService(this, 0,
                pauseIntent, 0);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.logo);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Cetalks")
                .setTicker("Cetalks")
                .setContentText(songname)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setSmallIcon(R.drawable.button_pause)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_pause, "Pause",
                        ppauseIntent).build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
