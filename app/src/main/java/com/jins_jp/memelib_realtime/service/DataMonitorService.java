package com.jins_jp.memelib_realtime.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeRealtimeData;
import com.jins_jp.meme.MemeRealtimeListener;
import com.jins_jp.memelib_realtime.GlobalVariable;
import com.jins_jp.memelib_realtime.MainActivity;
import com.jins_jp.memelib_realtime.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class DataMonitorService extends Service {

    public static final String TAG = "MyService";
    private MemeLib memeLib;
    private Timer mTimer;
    private Integer eyeblinking,eye_up,eye_down,eye_left,eye_right,capacity;
    private ArrayList<Integer> eye_list,up_list,down_list,left_list,right_list;
    private double roll, pitch;
    private String account;
    private GlobalVariable gv;
    private Integer pos_cnt, dis_cnt, ele_blink_cnt;
    private DatabaseReference mdatabase;


    @Override
    public void onCreate() {
        super .onCreate();
        Log.d("Slifecircle", "onCreate");
        pitch = 0f;
        roll = 0f;
        pos_cnt = 0;
        dis_cnt = 0;
        ele_blink_cnt = 0;
        account = getSharedPreferences("login", MODE_PRIVATE).getString("account", "");
        mdatabase = FirebaseDatabase.getInstance().getReference();


        eye_list = new ArrayList<>();
        for(int i=0;i<60;i++){
            eye_list.add(0);
        }
        up_list= new ArrayList<>();
        down_list= new ArrayList<>();
        left_list= new ArrayList<>();
        right_list= new ArrayList<>();
        mTimer = new Timer();
        setTimerTask();

        gv = (GlobalVariable)getApplicationContext();
        gv.setIsTraing(false);

        memeLib = MemeLib.getInstance();
        memeLib.startDataReport(new MemeRealtimeListener() {
            @Override
            public void memeRealtimeCallback(MemeRealtimeData memeRealtimeData) {

                eyeblinking = Integer.parseInt(memeRealtimeData.getBlinkStrength()+"");
                roll = Double.parseDouble(memeRealtimeData.getRoll()+"");
                pitch = Double.parseDouble(memeRealtimeData.getPitch()+"");
                ele_blink_cnt = eyeblinking > 1 ? 1 : 0;
//                Log.d(TAG, ele_blink_cnt+"");

                gv.setRoll(Double.parseDouble(memeRealtimeData.getRoll()+""));
                gv.setPitch(Double.parseDouble(memeRealtimeData.getPitch()+""));
                eye_up    = memeRealtimeData.getEyeMoveUp();
                eye_down = memeRealtimeData.getEyeMoveDown();
                eye_left   = memeRealtimeData.getPowerLeft();
                eye_right  = memeRealtimeData.getEyeMoveRight();
                eye_excercise_vb(eye_up,eye_down,eye_left,eye_right);



//                Log.d(TAG, "roll : " + gv.getRoll() + " pitch : "+ gv.getPitch());

//                if(eyeblinking > 0){
//                    eye_list.add(1);
//                }else{
//                    eye_list.add(0);
//                }
//                eye_list.remove(0);
//                if(Window_Sum(eye_list) > 30){
//                    if(!gv.getisTraing()) ShowNotification("Take a break!!!", true);
//                    for(int i=0;i<60*20;i++){
//                        eye_list.set(i, 0);
//                    }
//                }
            }
        });
        Log.d(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(roll < -50 || roll > 50 || pitch < -50 || pitch > 50){
                    if(!gv.getisTraing()) pos_cnt++;
                    if(!gv.getisTraing() && pos_cnt>5){
                        ShowNotification("Incorrect posture, please modify your situation!", true);
                        pos_cnt = 0;
                    }
                    else if(!gv.getisTraing()){
                        ShowNotification("Incorrect posture, plese modify your situation!", false);
                    }
                }

                eye_list.add(ele_blink_cnt);
                if(eye_list.size()>60) eye_list.remove(0);
                Log.d(TAG, Window_Sum(eye_list)+"");
                if(Window_Sum(eye_list) > 6){
                    if(!gv.getisTraing()) ShowNotification("Take a break!!!", true);
//                    for(int i=0;i<60;i++){
//                        eye_list.set(i, 0);
//                    }
                }

            }
        }, 1000, 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);

        DatabaseReference database=FirebaseDatabase.getInstance().getReference();
        database.child("Users").child(account).child("webduino").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(Integer.valueOf((String)dataSnapshot.child("cm").getValue()) < 30){
                    if(!gv.getisTraing()) dis_cnt++;
                    if(!gv.getisTraing() && dis_cnt > 5){
                        ShowNotification("Too close! please little far away", true);
                        dis_cnt = 0;
                    }
                    else if(!gv.getisTraing()){
                        ShowNotification("Too close! please little far away", false);
                    }
                }
            }

            @Override
            public void onChildChanged( DataSnapshot dataSnapshot,  String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Float Window_Sum(ArrayList<Integer> arr){
        float sum = 0;
        for(int i=0;i<arr.size();i++)
            sum += arr.get(i);
        return sum;
    }
    private void ShowNotification(String warning, Boolean count){
        if(count){
            mdatabase.child("Users").child(account).child("nag_score").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mdatabase.child("Users").child(account).child("nag_score").setValue(Integer.parseInt(dataSnapshot.getValue().toString())+1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_ID = getString(R.string.app_name);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_ID, "Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Let's walk channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setClass(this, DatabaseReference.class);
//        PendingIntent hangPendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
////        builder.setFullScreenIntent(hangPendingIntent,true);

        NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this, NOTIFICATION_ID);
        notificationbuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Eye Guard")
                .setContentText(warning)
                .setPriority(Notification.PRIORITY_HIGH)
//                .setFullScreenIntent(hangPendingIntent,true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notificationManager.notify(1, notificationbuilder.build());
    }
    private void eye_excercise_vb(Integer u,Integer d,Integer l,Integer r)
    {
        capacity=60;
        if( up_list.isEmpty() || up_list.size() < capacity)
        {
            Log.d("Correct", "eye_excercise_vb: " + String.valueOf(up_list.size()));
            up_list.add(u);
            down_list.add(d);
            left_list.add(l);
            right_list.add(r);
        }
        else
        {
            Log.d("Correct", "eye_excercise_vb: " + String.valueOf(up_list.size()));
            up_list.remove(0);
            up_list.add(u);
            down_list.remove(0);
            down_list.add(d);
            left_list.remove(0);
            left_list.add(l);
            right_list.remove(0);
            right_list.add(r);

        }
        gv.setU(up_list);
        gv.setD(down_list);
        gv.setL(left_list);
        gv.setR(right_list);
    }
}
