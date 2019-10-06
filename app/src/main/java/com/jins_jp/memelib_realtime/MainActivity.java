package com.jins_jp.memelib_realtime;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeRealtimeData;
import com.jins_jp.meme.MemeRealtimeListener;
import com.jins_jp.memelib_realtime.service.DataMonitorService;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String JOB_ID = "0001";
    private MemeLib memeLib;
    private Button eye, body, calen, illu;
    private DatabaseReference mdatabase;
    private ImageView growup, now_status, next_stauts;
    private ArrayList<Integer> score1, score2, score3;
    private ArrayList<Integer>  tree1, tree2, tree3, fail1, fail2, fail3;
    private MaterialCalendarView calendarview;
    private ArrayList<String> sentence1, sentence2, sentence3, fail_sentence1, fail_sentence2, fail_sentence3;
    private TextView words,people;
    private String pos_score,neg_score;
    private ProgressBar progress;
    private TextView sign;
    private GlobalVariable gv;
    private String account;
    private MediaPlayer mPlayer;
    private Timer mTimer;
    private Integer eyeblinking;
    private ArrayList<Integer> eye_list;
    private double roll, pitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
        if(gv.getFirststart()==true)
        {
            gv.setFirststart(false);
            mPlayer=MediaPlayer.create(getApplicationContext(),R.raw.bgm);
            mPlayer.setLooping(true);
            mPlayer.start();
            gv.setMplayer(mPlayer);
        }
        else
        {
            mPlayer=gv.getmPlayer();
            mPlayer.seekTo(gv.getCut_point());
            mPlayer.setVolume(1.0f,1.0f);
            mPlayer.start();
        }

//        Log.d("MPP", "onCreate: "+mPlayer.isPlaying());

        mdatabase = FirebaseDatabase.getInstance().getReference();
        account = getSharedPreferences("login", MODE_PRIVATE).getString("account", "");
        Log.d(TAG, account);
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        String store_date = getSharedPreferences("date", MODE_PRIVATE).getString("Now", "");
        Log.d(TAG, ft.format(date) + "," + store_date);
        if(!store_date.equals(ft.format(date))){
            mdatabase.child("Users").child(account).child("date").child(ft.format(date)).setValue(1);
            showCustomDialog();
            SharedPreferences pref = getSharedPreferences("date", MODE_PRIVATE);
            pref.edit()
                    .putString("Now", ft.format(date))
                    .apply();
        }

        mdatabase.child("Users").child(account).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pos_score = dataSnapshot.child("pos_score").getValue().toString();
                neg_score = dataSnapshot.child("nag_score").getValue().toString();
                Log.d("ap546", dataSnapshot.child("pos_score").getValue().toString());
                Log.d("ap546", dataSnapshot.child("nag_score").getValue().toString());
                if(Integer.parseInt(pos_score) - Integer.parseInt(neg_score)<0)
                {
                    pos_score="0";neg_score="0";
                }
                Integer[] a = transforem_score(Integer.parseInt(pos_score) - Integer.parseInt(neg_score));
                gv.setNagscore(Integer.parseInt(neg_score));
                gv.set_negtive_max((a[0]*50+100)*(a[1]+1));
                if(a[1]!=5)
                    progress.setMax((a[0]*50+100)*(a[1]+1));
                else
                    progress.setMax(300);
                progress.setProgress(a[2]);
                Log.d(TAG, a[0] + " , " + a[1] + " , "  + a[2]);
                if(a[0]==0){
                    if(Integer.parseInt(neg_score) > 0){
                        growup.setImageResource(fail1.get(a[1]-1));
                        words.setText(fail_sentence1.get((a[1]-1)*2));
                        people.setText(fail_sentence1.get((a[1]-1)*2+1));
//                        progress.setMax(score1.get(a[1]+1));
//                        progress.setProgress(Integer.parseInt(pos_score)-Integer.parseInt(neg_score));
                    }else{
                        growup.setImageResource(tree1.get(a[1]));
                        words.setText(sentence1.get((a[1])*2));
                        people.setText(sentence1.get((a[1])*2+1));
//                        progress.setMax(score1.get(a[1]+1));
//                        progress.setProgress(Integer.parseInt(pos_score));
                    }
                    now_status.setImageResource(tree1.get(a[1]));
                    next_stauts.setImageResource(tree1.get(a[1]+1));
                }else if(a[0]==1){
                    if(Integer.parseInt(neg_score) > 0){
                        growup.setImageResource(fail2.get(a[1]-1));
                        words.setText(fail_sentence2.get((a[1]-1)*2));
                        people.setText(fail_sentence2.get((a[1]-1)*2+1));
//                        progress.setMax(score2.get(a[1]+1));
//                        progress.setProgress(Integer.parseInt(pos_score)-Integer.parseInt(neg_score));
                    }else{
                        growup.setImageResource(tree2.get(a[1]));
                        words.setText(sentence2.get((a[1])*2));
                        people.setText(sentence2.get((a[1])*2+1));
//                        progress.setMax(score2.get(a[1]+1));
//                        progress.setProgress(Integer.parseInt(pos_score));
                    }
                    now_status.setImageResource(tree2.get(a[1]));
                    next_stauts.setImageResource(tree2.get(a[1]+1));
                }else{
                    if(Integer.parseInt(neg_score) > 0){
                        growup.setImageResource(fail3.get(a[1]-1));
                        words.setText(fail_sentence3.get((a[1]-1)*2));
                        people.setText(fail_sentence3.get((a[1]-1)*2+1));
//                        progress.setMax(score3.get(a[1]+1));
//                        progress.setProgress(Integer.parseInt(pos_score)-Integer.parseInt(neg_score));
                    }else{
                        growup.setImageResource(tree3.get(a[1]));
                        words.setText(sentence3.get((a[1])*2));
                        people.setText(sentence3.get((a[1])*2+1));
//                        progress.setMax(score3.get(a[1]+1));
//                        progress.setProgress(Integer.parseInt(pos_score));
                    }
                    now_status.setImageResource(tree3.get(a[1]));
                    next_stauts.setImageResource(tree3.get(a[1]+1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        illu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, IllustrationActivity.class));
                finish();
            }
        });
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EyeActivity.class));
                finish();
            }
        });

        body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BodyActivity.class));
                finish();
            }
        });
        calen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });

        Intent serviceintent = new Intent(MainActivity.this, DataMonitorService.class);
        startService(serviceintent);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(3000);
        ScaleAnimation scaleAnimation1 = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 1.0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f,1.0f);
        animationSet.addAnimation(scaleAnimation1);
        animationSet.addAnimation(alphaAnimation);


        growup.setAnimation(animationSet);

    }
    @Override
    protected void onPause()
    {
        super.onPause();
        if(mPlayer.isPlaying())
        {
            mPlayer.pause();
            gv.setMplayer(mPlayer);
            gv.setCut_point(mPlayer.getCurrentPosition());

        }
        Log.d("lifecircle", "onPause");
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("lifecircle", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecircle", "onDestroy");
    }


    private void InitView(){
        gv = (GlobalVariable)getApplicationContext();
        memeLib = MemeLib.getInstance();
        eye = findViewById(R.id.eye);
        body = findViewById(R.id.body);
        calen = findViewById(R.id.calendar_btn);
        illu = findViewById(R.id.illustartion);
        growup = findViewById(R.id.growup);
        people = findViewById(R.id.author);
        words = findViewById(R.id.sentence);
        now_status = findViewById(R.id.now_status);
        next_stauts = findViewById(R.id.next_status);
        progress = findViewById(R.id.progressBar);
        sign = findViewById(R.id.sign);


        tree1 = new ArrayList<>();
        tree2 = new ArrayList<>();
        tree3 = new ArrayList<>();
        fail1 = new ArrayList<>();
        fail2 = new ArrayList<>();
        fail3 = new ArrayList<>();
        sentence1 = new ArrayList<>();
        sentence2 = new ArrayList<>();
        sentence3 = new ArrayList<>();
        fail_sentence1 = new ArrayList<>();
        fail_sentence2 = new ArrayList<>();
        fail_sentence3 = new ArrayList<>();
        score1 = new ArrayList<>();
        score2 = new ArrayList<>();
        score3 = new ArrayList<>();

        tree1.add(R.drawable.seed_1);
        tree1.add(R.drawable.grow_1);
        tree1.add(R.drawable.grow_2);
        tree1.add(R.drawable.grow_3);
        tree1.add(R.drawable.grow_4);
        tree1.add(R.drawable.tree1);
        tree1.add(R.drawable.question);
        fail1.add(R.drawable.grow1_fail);
        fail1.add(R.drawable.grow2_fail);
        fail1.add(R.drawable.grow3_fail);
        fail1.add(R.drawable.grow4_fail);
        fail1.add(R.drawable.tree1_fail);

        tree2.add(R.drawable.seed_2);
        tree2.add(R.drawable.grow_1);
        tree2.add(R.drawable.grow_2);
        tree2.add(R.drawable.grow_3);
        tree2.add(R.drawable.grow_4);
        tree2.add(R.drawable.tree2);
        tree2.add(R.drawable.question);
        fail2.add(R.drawable.grow1_fail);
        fail2.add(R.drawable.grow2_fail);
        fail2.add(R.drawable.grow3_fail);
        fail2.add(R.drawable.grow4_fail);
        fail2.add(R.drawable.tree2_fail);

        tree3.add(R.drawable.seed_3);
        tree3.add(R.drawable.grow_1);
        tree3.add(R.drawable.grow_2);
        tree3.add(R.drawable.grow_3);
        tree3.add(R.drawable.grow_4);
        tree3.add(R.drawable.tree3);
        tree3.add(R.drawable.question);
        fail3.add(R.drawable.grow1_fail);
        fail3.add(R.drawable.grow2_fail);
        fail3.add(R.drawable.grow3_fail);
        fail3.add(R.drawable.grow4_fail);
        fail3.add(R.drawable.tree3_fail);

        sentence1.add("The eyes like sentinel\noccupy the highest place in the body.");
        sentence1.add("-Marcus");
        sentence1.add("Nearly all of what we do\neach day, every day, is simply habit.");
        sentence1.add("-Jack D. Hodge");
        sentence1.add("Where words are restrained,\nthe eyes often talk a great deal.");
        sentence1.add("-Samuel Richardson");
        sentence1.add("We first make our habits,\nand then our habits make us.");
        sentence1.add("-John Dryden");
        sentence1.add("Victory belongs to hold out\nuntil the last man.");
        sentence1.add("-Napoléon Bonaparte");
        sentence1.add("The power of habit is great.");
        sentence1.add("-Latin Proverb");


        sentence2.add("The eyes indicate \nthe antiquity of the soul.");
        sentence2.add("-Ralph Waldo Emerson");
        sentence2.add("Nearly all of what we do\neach day, every day, is simply habit.");
        sentence2.add("-Jack D. Hodge");
        sentence2.add("Where words are restrained,\nthe eyes often talk a great deal.");
        sentence2.add("-Samuel Richardson");
        sentence2.add("We first make our habits,\nand then our habits make us.");
        sentence2.add("-John Dryden");
        sentence2.add("Victory belongs to hold out\nuntil the last man.");
        sentence2.add("-Napoléon Bonaparte");
        sentence2.add("Good habits formed at youth \nmake all the difference.");
        sentence2.add("-Aristotle");


        sentence3.add("The eye is the jewel of the body.");
        sentence3.add("-Henry David Thoreau");
        sentence3.add("Nearly all of what we do\neach day, every day, is simply habit.");
        sentence3.add("-Jack D. Hodge");
        sentence3.add("Where words are restrained,\nthe eyes often talk a great deal.");
        sentence3.add("-Samuel Richardson");
        sentence3.add("We first make our habits,\nand then our habits make us.");
        sentence3.add("-John Dryden");
        sentence3.add("Victory belongs to hold out\nuntil the last man.");
        sentence3.add("-Napoléon Bonaparte");
        sentence3.add("Success is the sum of small efforts \nrepeated day in day out.");
        sentence3.add("-Robert Collier");



        fail_sentence1.add("Chains of habit are too light to be felt\nuntil they are too heavy to be broken.");
        fail_sentence1.add("-Warren Buffet");
        fail_sentence1.add("Believe you can \nand you're halfway there.");
        fail_sentence1.add("-Theodore Roosevelt");
        fail_sentence1.add("Winning is a habit. \nUnfortunately, so is losing.");
        fail_sentence1.add("-Vince Lombardi");
        fail_sentence1.add("Good habits, once established are just \nas hard to break as are bad habits.");
        fail_sentence1.add("-Robert Puller");
        fail_sentence1.add("Believe you can \nand you're halfway there.");
        fail_sentence1.add("-Theodore Roosevelt");

        fail_sentence2.add("Chains of habit are too light to be felt\nuntil they are too heavy to be broken.");
        fail_sentence2.add("-Warren Buffet");
        fail_sentence2.add("Believe you can \nand you're halfway there.");
        fail_sentence2.add("-Theodore Roosevelt");
        fail_sentence2.add("Winning is a habit. \nUnfortunately, so is losing.");
        fail_sentence2.add("-Vince Lombardi");
        fail_sentence2.add("Good habits, once established are just \nas hard to break as are bad habits.");
        fail_sentence2.add("-Robert Puller");
        fail_sentence2.add("Pain is \ntemporary Quitting lasts forever.");
        fail_sentence2.add("-Lance Armstrong");

        fail_sentence3.add("Chains of habit are too light to be felt\nuntil they are too heavy to be broken.");
        fail_sentence3.add("-Warren Buffet");
        fail_sentence3.add("Believe you can \nand you're halfway there.");
        fail_sentence3.add("-Theodore Roosevelt");
        fail_sentence3.add("Winning is a habit. \nUnfortunately, so is losing.");
        fail_sentence3.add("-Vince Lombardi");
        fail_sentence3.add("Good habits, once established are just \nas hard to break as are bad habits.");
        fail_sentence3.add("-Robert Puller");
        fail_sentence3.add("Difficult roads always lead to \nbeautiful destinations.");
        fail_sentence3.add("-Zig Ziglar");

        score1.add(0);
        score1.add(100);
        score1.add(300);
        score1.add(600);
        score1.add(1000);
        score1.add(1500);
        score2.add(1800);
        score2.add(1950);
        score2.add(2250);
        score2.add(2700);
        score2.add(3300);
        score2.add(4050);
        score3.add(4350);
        score3.add(4550);
        score3.add(4950);
        score3.add(5350);
        score3.add(6350);
        score3.add(7350);
    }

    private void showCustomDialog() {

//        sign.setVisibility(View.VISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.calendar_view, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        calendarview = dialogView.findViewById(R.id.calendarView);
        calendarview.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        calendarview.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        calendarview.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        calendarview.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Log.d("check_calendar", date + "_" + selected);
                calendarview.setDateSelected(date, !selected);
            }
        });
        mdatabase.child("Users").child(account).child("date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(String.valueOf(dataSnapshot.getValue()).equals("1")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = sdf.parse(String.valueOf(dataSnapshot.getKey()));
                        calendarview.setDateSelected(date, true);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private Integer[]  transforem_score(int score)
    {
        Integer tree_No = 0;
        Integer status = 0;
        Integer Positive = 0;
        Integer  tmp_score = score;
        while ( tmp_score !=0)
        {
            int tmp_a=((tree_No +100)*5)*3+300;
            int tmp_b=(status+1)*(tree_No +100);
            if( tmp_score >=tmp_a )
            {
                tmp_score-=tmp_a;
                tree_No++;
            }
            else if(tmp_score >= tmp_b )
            {
                tmp_score-=tmp_b;
                status++;
            }
            else
            {
                Positive=tmp_score;
                tmp_score=0;
            }
        }
        Integer []result={ tree_No ,status , Positive};
        return result;
    }
}
