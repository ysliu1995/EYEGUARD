package com.jins_jp.memelib_realtime;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeRealtimeData;
import com.jins_jp.meme.MemeRealtimeListener;
import com.jins_jp.memelib_realtime.service.DataMonitorService;

import java.util.ArrayList;

public class BodyActivity extends AppCompatActivity {
    private static final String TAG = "BodyActivity";
//    private MemeLib memeLib;
    private Button start;
    private ImageView image, cd, step, home, tree, body, eye, illustration;
    private ArrayList<Integer> image_list;
    private Integer good = 0, bad = 0,second=0,step_count=0;
    private Integer index = 0;
    private Integer[] cd_img = { R.drawable.countdown_10, R.drawable.countdown_9, R.drawable.countdown_8, R.drawable.countdown_7, R.drawable.countdown_6,
                                 R.drawable.countdown_5, R.drawable.countdown_4, R.drawable.countdown_3, R.drawable.countdown_2, R.drawable.countdown_1};
    private CountDownTimer countdowntimer;
    private Boolean start_flag=false;
    private DatabaseReference mdatabase;
    private String account;
    private GlobalVariable gv;
    private MediaPlayer mp,mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);
        init();
        account = getSharedPreferences("login", MODE_PRIVATE).getString("account", "");
        mp=MediaPlayer.create(getApplicationContext(),R.raw.blank);
        mPlayer=gv.getmPlayer();
        mPlayer.seekTo(gv.getCut_point());
        mPlayer.start();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 0;
               if (start.getText().toString().equals("start"))
               {

                   mPlayer.setVolume(0.2f,0.2f);

                   gv.setIsTraing(true);
                   start.setText("stop");
                   start_body_excercise();
                   start_flag=true;
               }
               else
               {
                   mPlayer=gv.getmPlayer();
                   mPlayer.setVolume(1.0f,1.0f);
                   if(mp.isPlaying())
                   {
                       mp.stop();
                   }
                   gv.setIsTraing(false);
                   ResetAlpha();
                   cd.setVisibility(View.INVISIBLE);
                   start.setText("start");
                   start_flag=false;
                   countdowntimer.cancel();
                   step.setImageDrawable(getResources().getDrawable(R.drawable.step_0));
                   image.setImageDrawable(getResources().getDrawable(R.drawable.body_1));
                   creat_dialog(0 , 0);

               }
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start_flag==true)
                {
                    mPlayer=gv.getmPlayer();
                    mPlayer.setVolume(1.0f,1.0f);
                    if(mp.isPlaying())
                    {
                        mp.stop();
                    }
                    cd.setVisibility(View.INVISIBLE);
                    ResetAlpha();
                    countdowntimer.cancel();
                    index = 0;
                    gv.setIsTraing(false);
                }
                startActivity(new Intent(BodyActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(start_flag==true)
        {
            if(mp.isPlaying())
            {
                mp.stop();
            }
            cd.setVisibility(View.INVISIBLE);
            ResetAlpha();
            countdowntimer.cancel();
            index = 0;
            gv.setIsTraing(false);
        }
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        mPlayer.pause();
        gv.setMplayer(mPlayer);
        gv.setCut_point(mPlayer.getCurrentPosition());

    }
    private void start_body_excercise()
    {
        Log.d(TAG, "enter");
        good = 0;
        bad= 0 ;
        second=0;
        step_count=0;
        countdowntimer=new CountDownTimer(100000, 1000){

            @Override
            public void onTick(long l) {
                sendmp();
                Log.d(TAG, gv.getRoll() + ", " + gv.getPitch());
                if((l/1000) > 89){

                    SetAlpha();
                    step.setImageResource(R.drawable.step_1);
                    image.setImageResource(image_list.get(1));
                    cd.setVisibility(View.VISIBLE);
                    cd.setImageResource(cd_img[index]);
                    index++;
                }else if((l/1000) > 74){
                    ResetAlpha();
                    cd.setVisibility(View.INVISIBLE);
                    index = 0;
                    image.setImageResource(image_list.get(1));
                    if(gv.getRoll() < -20) good += 1;
                    else {bad += 1;}
                }else if((l/1000) > 64){
                    SetAlpha();
                    step.setImageResource(R.drawable.step_2);
                    image.setImageResource(image_list.get(2));
                    cd.setVisibility(View.VISIBLE);
                    cd.setImageResource(cd_img[index]);
                    index++;
                }else if((l/1000) > 49){
                    ResetAlpha();
                    cd.setVisibility(View.INVISIBLE);
                    index = 0;
                    image.setImageResource(image_list.get(2));
                    if(gv.getRoll() > 20) good += 1;
                    else {bad += 1;}
                }else if((l/1000) > 39){
                    SetAlpha();
                    step.setImageResource(R.drawable.step_3);
                    image.setImageResource(image_list.get(3));
                    cd.setVisibility(View.VISIBLE);
                    cd.setImageResource(cd_img[index]);
                    index++;
                }else if((l/1000) > 24){
                    ResetAlpha();
                    cd.setVisibility(View.INVISIBLE);
                    index = 0;
                    image.setImageResource(image_list.get(3));
                    if(gv.getPitch() < -15) good += 1;
                    else {bad += 1;}
                }else if((l/1000) > 14){
                    SetAlpha();
                    image.setImageResource(image_list.get(4));
                    step.setImageResource(R.drawable.step_4);
                    cd.setVisibility(View.VISIBLE);
                    cd.setImageResource(cd_img[index]);
                    index++;
                }else{
                    ResetAlpha();
                    cd.setVisibility(View.INVISIBLE);
                    index = 0;
                    image.setImageResource(image_list.get(4));
                    if(gv.getPitch() > 15) good += 1;
                    else {bad += 1;}
                }
                Log.d(TAG, good + ", " + bad);
            }

            @Override
            public void onFinish() {
                if(gv.getPitch() > 15) good += 1;
                else {bad += 1;}

                Log.d(TAG, "Good : " + good.toString() + "Bad : " + bad.toString());
                creat_dialog(good, bad);
            }
         };
        countdowntimer.start();
    }
    private void init() {
        mdatabase = FirebaseDatabase.getInstance().getReference();
        start = findViewById(R.id.body_start);
        image = findViewById(R.id.body_image);
        cd = findViewById(R.id.countdown);
        step = findViewById(R.id.step);
        home = findViewById(R.id.home);
        tree = findViewById(R.id.tree);
        body = findViewById(R.id.body);
        eye = findViewById(R.id.eye);
        illustration = findViewById(R.id.illustration);

        image_list = new ArrayList<>();
        image_list.add(R.drawable.body_1);
        image_list.add(R.drawable.body_2);
        image_list.add(R.drawable.body_3);
        image_list.add(R.drawable.body_4);
        image_list.add(R.drawable.body_5);

        gv = (GlobalVariable)getApplicationContext();
    }
    private void SetAlpha(){
        image.setAlpha((float)0.5);
        step.setAlpha((float)0.5);
        home.setAlpha((float)0.5);
        tree.setAlpha((float)0.5);
        body.setAlpha((float)0.5);
        eye.setAlpha((float)0.5);
        illustration.setAlpha((float)0.5);
    }
    private void ResetAlpha(){
        image.setAlpha((float)1);
        step.setAlpha((float)1);
        home.setAlpha((float)1);
        tree.setAlpha((float)1);
        body.setAlpha((float)1);
        eye.setAlpha((float)1);
        illustration.setAlpha((float)1);
    }
    private void creat_dialog(Integer good, Integer bad)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
//        TextView textView = dialogView.findViewById(R.id.score_textview);
        TextView dialog_sentence = dialogView.findViewById(R.id.dialog_sentence);
        Button button=dialogView.findViewById(R.id.close_button);
        if(good == 0 && bad == 0){
//            textView.setText("Not finish");
            dialog_sentence.setText("Such a Shame!\n100 points cut.");
            mdatabase.child("Users").child(account).child("nag_score").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mdatabase.child("Users").child(account).child("nag_score").setValue(Integer.parseInt(dataSnapshot.getValue().toString())+1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            if(good > bad){
                mp=MediaPlayer.create(getApplicationContext(),R.raw.sucseed);
                mp.start();
//                textView.setText("Score : " + (int)((float)good/(good+bad)*100) + " %");
                dialog_sentence.setText("Nice job!\n200 points in");
                mdatabase.child("Users").child(account).child("pos_score").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mdatabase.child("Users").child(account).child("pos_score").setValue(Integer.parseInt(dataSnapshot.getValue().toString())+200);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else {
                mp=MediaPlayer.create(getApplicationContext(),R.raw.loss);
                mp.start();
//                textView.setText("Score : " + (int)((float)good/(good+bad)*100) + " %");
                dialog_sentence.setText("Such a Shame!\n100 points cut.");
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
        }

        button.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(BodyActivity.this, MainActivity.class));
                finish();
            }

        });
    }
    private void  sendmp()
    {
        if(second%25==0)
        {
            mp=MediaPlayer.create(getApplicationContext(),R.raw.please);
            mp.start();
        }
        else if (second%25==3)
        {
            if(second/25==0)
                mp=MediaPlayer.create(getApplicationContext(),R.raw.head_right);
            else if(second/25==1)
                mp=MediaPlayer.create(getApplicationContext(),R.raw.head_left);
            else if(second/25==2)
                mp=MediaPlayer.create(getApplicationContext(),R.raw.head_up);
            else if(second/25==3)
                mp=MediaPlayer.create(getApplicationContext(),R.raw.head_down);
            mp.start();
        }
        else if(second%25==5)
        {
            mp=MediaPlayer.create(getApplicationContext(),R.raw.countdown5);
            mp.start();
        }
        else if(second%25==10)
        {
            mp=MediaPlayer.create(getApplicationContext(),R.raw.countdown15);
            mp.start();
        }
        second++;
    }
}
