package com.jins_jp.memelib_realtime;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
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
import android.widget.Toast;
import android.widget.VideoView;

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

public class EyeActivity extends AppCompatActivity {
    private MemeLib memeLib;
    private int[] count_pic={R.drawable.countdown_1,R.drawable.countdown_2,R.drawable.countdown_3,R.drawable.countdown_4,R.drawable.countdown_5,R.drawable.countdown_6,R.drawable.countdown_7,R.drawable.countdown_8,R.drawable.countdown_9,R.drawable.countdown_10};
    private int[] pic={R.drawable.up,R.drawable.down,R.drawable.left,R.drawable.right,R.drawable.left_up,R.drawable.right_down,R.drawable.right_up,R.drawable.left_down};
    private int[] voice={R.raw.up,R.raw.down,R.raw.left,R.raw.right,R.raw.upper_left,R.raw.bottom_right,R.raw.upper_right,R.raw.bottom_left};
    private int[] step_pic={R.drawable.step_5,R.drawable.step_6,R.drawable.step_2,R.drawable.step_7,R.drawable.step_8,R.drawable.step_4};
    private int[] Clockwise={0,6,3,5,1,7,2,4};
    private int[] anti_Clockwise={0,4,2,7,1,5,3,6};
    private ArrayList< Integer > up_list,down_list,left_list,right_list;
    private String account;
    private ImageView eyePIC,countPIC,home,step;
    private Button start;
    private Integer index=0,score=0,times=0,circle=0,step_count=0;
    private float count_clock;
    private boolean have_score=false,count=false,start_flag=false;
    private Context context;
    private CountDownTimer countdowntimer;
    private GlobalVariable gv;
    private DatabaseReference mdatabase;
    private MediaPlayer mp,mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye);
        memeLib = MemeLib.getInstance();
        eyePIC = findViewById(R.id.eyepic);
        countPIC=findViewById(R.id.countpic);
        countPIC.setVisibility(View.INVISIBLE);
        home = findViewById(R.id.home);
        start = findViewById(R.id.start);
        step=findViewById(R.id.step);
        mdatabase = FirebaseDatabase.getInstance().getReference();
        account = getSharedPreferences("login", MODE_PRIVATE).getString("account", "");
        gv = (GlobalVariable)getApplicationContext();
        mp=MediaPlayer.create(getApplicationContext(),R.raw.blank);
        mPlayer=gv.getmPlayer();
        mPlayer.seekTo(gv.getCut_point());
        mPlayer.start();
        context=this;
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("a123", "onClick: " + start.getText().toString());
                if (start.getText().toString().equals("start"))
                {
                    mPlayer.setVolume(0.2f,0.2f);
                    gv.setIsTraing(true);
                    start_flag=true;
                    start.setText("stop");
                    start_eye_excercise();
                }
                else
                {
                    mPlayer=gv.getmPlayer();
                    mPlayer.setVolume(1.0f,1.0f);

                    gv.setIsTraing(false);
                    start_flag=false;
                    start.setText("start");
                    countdowntimer.onFinish();
                    countdowntimer.cancel();
                    memeLib.stopDataReport();
                    creat_dialog(0);
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

                    gv.setIsTraing(false);
                    countdowntimer.onFinish();
                    countdowntimer.cancel();
                }
                Intent serviceintent = new Intent(EyeActivity.this, DataMonitorService.class);
                startService(serviceintent);
                startActivity(new Intent(EyeActivity.this, MainActivity.class));
                finish();
            }
        });

        Intent intent = new Intent(EyeActivity.this,DataMonitorService.class);
        stopService(intent);

    }
    @Override
    protected void onPause()
    {
        super.onPause();
        mPlayer.pause();
        gv.setMplayer(mPlayer);
        gv.setCut_point(mPlayer.getCurrentPosition());

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(start_flag==true)
        {
            gv.setIsTraing(false);
            countdowntimer.onFinish();
            countdowntimer.cancel();
        }

    }
    private void creat_dialog(Integer score)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView textView=dialogView.findViewById(R.id.score_textview);
        TextView dialog_sentence = dialogView.findViewById(R.id.dialog_sentence);
        Button button=dialogView.findViewById(R.id.close_button);
        if(score == 0){
            mp=MediaPlayer.create(getApplicationContext(),R.raw.loss);
            mp.start();
            dialog_sentence.setText("Such a shame!\nCut 100 points.");
            mdatabase.child("Users").child(account).child("nag_score").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mdatabase.child("Users").child(account).child("nag_score").setValue(Integer.parseInt(dataSnapshot.getValue().toString())+ 1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            if(score >= 1500){
                mp=MediaPlayer.create(getApplicationContext(),R.raw.sucseed);
                mp.start();
                dialog_sentence.setText("Nice job!\n200 points in");
                Integer nag_score=gv.getNagscore();
                Integer pos_score=200;
                if(nag_score<200)
                {
                    mdatabase.child("Users").child(account).child("nag_score").setValue(0);
                    pos_score=200-nag_score;
                }
                final Integer finalPos_score = pos_score;
                mdatabase.child("Users").child(account).child("pos_score").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mdatabase.child("Users").child(account).child("pos_score").setValue(Integer.parseInt(dataSnapshot.getValue().toString())+ finalPos_score);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }else{
                mp=MediaPlayer.create(getApplicationContext(),R.raw.loss);
                mp.start();
                dialog_sentence.setText("Such a shame!\nCut 100 points ");
                Integer nag_score=gv.getNagscore();
                Integer nag_max=gv.getNegtive_max();
                if(nag_score+100 > nag_max)
                    mdatabase.child("Users").child(account).child("nag_score").setValue(String.valueOf(nag_max));
                else
                    mdatabase.child("Users").child(account).child("nag_score").setValue(String.valueOf(nag_max));

            }
        }


        button.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent serviceintent = new Intent(EyeActivity.this, DataMonitorService.class);
                startService(serviceintent);
                startActivity(new Intent(EyeActivity.this, MainActivity.class));
                finish();
            }

        });

    }
    private boolean check_direction(int D)
    {
        Boolean b=false;
        int u=0,d=0,l=0,r=0;
        int[] tmp=new int [4];
        for(int i=0;i< up_list.size();++i)
        {
            if (up_list.get(i)!=0)
                u=i;
            if (down_list.get(i)!=0)
                d=i;
            if (left_list.get(i)!=0)
                l=i;
            if (right_list.get(i)!=0)
                r=i;
          switch (D){
                case 0 ://上
                    if (u<d && u!=0)
                    {
                        b=true;
                        remove_element();
                    }
                    break;
                case 1 ://下
                    if (d<u && d!=0)
                    {
                        b=true;
                        remove_element();
                    }
                    break;
                case 2 ://左
                    if ( l!=0)
                    {
                        b=true;
                        remove_element();
                    }
                    break;
                case 3 ://右
                    if ( r!=0)
                    {
                        b=true;

                        remove_element();
                    }
                    break;
                case 4 ://左上
                    if ( d>u && l!=0 && u!=0)
                    {
                        b=true;
                        remove_element();
                    }
                    break;
                case 5 ://右下
                    if ( d<u && r!=0 && d!=0)
                    {
                        b=true;
                        remove_element();
                    }
                    break;
                case 6 ://右上
                    if ( d>u && r!=0 && u!=0)
                    {
                        b=true;
                        remove_element();
                    }
                    break;
                case 7 ://左下
                    if (  d<u && l!=0 && d!=0)
                    {
                        b=true;
                        remove_element();
                    }
                    break;
                case 8 ://順時針
                    if ( r!=0 && l!=0 && u!=0 && d!=0 && l > r)
                    {
                        b=true;
                        remove_element();
                    }
                    break;
                case 9 ://逆時針
                    if (  r!=0 && l!=0 && u!=0 && d!=0 && l < r)
                    {
                        b=true;
                        remove_element();
                    }
                    break;
                default:
                    b=false;
                    break;

            }
            if (b==true)
                return true;
        }
        Log.d("Check123","u: " +String.valueOf(u) + ",d: " + String.valueOf(d) +",l: "+  String.valueOf(l) +",r: " + String.valueOf(r));
        return false;
    }
    private void remove_element()
    {
        up_list.clear();
        down_list.clear();
        left_list.clear();
        right_list.clear();
        gv.setU(up_list);
        gv.setD(down_list);
        gv.setL(left_list);
        gv.setR(right_list);
    }
    private void start_eye_excercise()
    {
        step_count=0;score=0;index=0;times=0;circle=0;count_clock=20;count=false;

        countdowntimer=new CountDownTimer(204000,500){
            @Override
            public void onFinish() {
                eyePIC.setAlpha(1.0f);
                countPIC.setVisibility(View.INVISIBLE);
                eyePIC.setImageDrawable(getResources().getDrawable( R.drawable.mid ));
                step.setImageDrawable(getResources().getDrawable(R.drawable.step_0));
                memeLib.stopDataReport();
                if(mp.isPlaying())
                {
                    mp.stop();
                }
            }
            @Override
            public void onTick(long millisUntilFinished) {
                up_list=gv.getUlist();
                down_list=gv.getDlist();
                left_list=gv.getLlist();
                right_list=gv.getRlist();
                Log.d("checklist", String.valueOf(up_list.size()));
                sendmp();
                if(count==false)
                {
                    countPIC.setVisibility(View.VISIBLE);
                    eyePIC.setAlpha(0.5f);
                    if (count_clock-- % 2 ==0)
                    {
                        step.setImageDrawable(getResources().getDrawable( step_pic[step_count] ));
                        countPIC.setImageDrawable(getResources().getDrawable( count_pic[Math.round(count_clock/2)-1] ));

                    }
                    if(count_clock==0 )
                    {
                        step_count++;
                        eyePIC.setAlpha(1.0f);
                        count=true;
                        countPIC.setVisibility(View.INVISIBLE);
                        times=0;
                        circle=0;
                    }
                }
                else
                {
                    countPIC.setVisibility(View.INVISIBLE);
                }
                if (index<8)
                {
                    if(circle==3){
                        eyePIC.setImageDrawable(getResources().getDrawable( pic[index] ));
                        circle++;
                        if (check_direction(index)&& count==true)
                            have_score=true;
                    }
                    else if(circle==7){
                        eyePIC.setImageDrawable(getResources().getDrawable( R.drawable.mid ));
                        circle=0;
                        if ((check_direction(index) || have_score )&& count==true)
                        {
                            have_score=false;
                            score+=100;
                        }
                        switch (times)
                        {
                            case 0:
                            case 2:
                            case 4:
                                index++;
                                times++;
                                break;
                            case 1:
                            case 3:
                                index--;
                                times++;
                                break;
                            case 5:
                                index++;
                                times=0;
                                count=false;
                                count_clock=20;
                                circle=0;
                                break;
                        }
//                        Toast.makeText(context,"Score = " + String.valueOf(score),Toast.LENGTH_LONG).show();
                    }
                    else
                        circle++;
                }
                else if(index==8)
                {
                    eyePIC.setImageDrawable(getResources().getDrawable( pic[Clockwise[circle++]] ));
                    if( circle == 8)
                    {
                        times++;
                        circle=0;
                        if (check_direction(index)&& count==true)
                            score+=100;
//                        Toast.makeText(context,"Score = " + String.valueOf(score),Toast.LENGTH_LONG).show();
                    }
                    if(times==3)
                    {
                        times=0;
                        index++;
                        eyePIC.setImageDrawable(getResources().getDrawable( R.drawable.mid ));
                        circle=0;
                        count=false;
                        count_clock=20;
                    }

                }
                else if(index==9)
                {
                    eyePIC.setImageDrawable(getResources().getDrawable( pic[anti_Clockwise[circle++]] ));
                    if( circle == 8)
                    {
                        times++;
                        circle=0;
                        if (check_direction(index) &&  count==true)
                            score+=100;
//                        Toast.makeText(context,"Score = " + String.valueOf(score),Toast.LENGTH_LONG).show();
                    }
                    if(times==3)
                    {
                        eyePIC.setImageDrawable(getResources().getDrawable( R.drawable.mid ));
                        creat_dialog(score);
                        this.onFinish();
                        this.cancel();
                    }

                }

            }
        };
        countdowntimer.start();
    }
    private void  sendmp()
    {
        if(count==false)
        {
            if(count_clock==20)
            {
                if(index==0||index==2){
                    mp=MediaPlayer.create(getApplicationContext(),R.raw.please);
                    mp.start();
                }
                else if(index==4){
                    mp=MediaPlayer.create(getApplicationContext(),R.raw.eye_3);
                    mp.start();
                }
                else if(index==6)
                {
                    mp=MediaPlayer.create(getApplicationContext(),R.raw.eye_4);
                    mp.start();
                }


            }
            else if (count_clock==16)
            {
                if(index==0)
                    mp=MediaPlayer.create(getApplicationContext(),R.raw.eye_1);
                else if(index==2)
                    mp=MediaPlayer.create(getApplicationContext(),R.raw.eye_2);
                else if(index==8)
                    mp=MediaPlayer.create(getApplicationContext(),R.raw.eye_5);
                else if(index==9)
                    mp=MediaPlayer.create(getApplicationContext(),R.raw.eye_6);
                mp.start();
            }
            else if(count_clock==10)
            {
                mp=MediaPlayer.create(getApplicationContext(),R.raw.countdown5);
                mp.start();
            }
        }
        else
        {
            if(index<8)
            {
                if(circle==3)
                {
                    mp=MediaPlayer.create(getApplicationContext(),voice[index]);
                    mp.start();
                }
            }
            else if(circle==0)
            {
                if(times==0) {
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.first_one);
                    mp.start();
                }
                else if(times==1)
                {
                    mp=MediaPlayer.create(getApplicationContext(),R.raw.next_one);
                    mp.start();
                }
                else if(times==2)
                {
                    mp=MediaPlayer.create(getApplicationContext(),R.raw.last_one);
                    mp.start();
                }

            }
        }
    }
}



