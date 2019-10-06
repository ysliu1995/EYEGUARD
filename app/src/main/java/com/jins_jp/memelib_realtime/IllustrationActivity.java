package com.jins_jp.memelib_realtime;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class IllustrationActivity extends AppCompatActivity {
    private static final String TAG = "IllustrationActivity";
    private DatabaseReference mdatabase;
    private ImageView illu_1, illu_2, illu_3, illu_4, illu_5, illu_6, home;
    private Integer[] tree = { R.drawable.tree1, R.drawable.tree2, R.drawable.tree3, R.drawable.tree4, R.drawable.tree5, R.drawable.loading};
    private ArrayList<ImageView> pic;
    private ArrayList<String> sentence;
    private Integer index = 0;
    private String account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illustration);
        Init_view();
        account = getSharedPreferences("login", MODE_PRIVATE).getString("account", "");
        mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("Users").child(account).child("illustration").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, dataSnapshot.getValue().toString());
                if(dataSnapshot.getValue().toString().equals("0")){
                    Log.d(TAG, "123");
                    pic.get(index).setImageDrawable(getResources().getDrawable(R.drawable.question));
                    pic.get(index).setClickable(false);
                }
                index++;
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
    private void Init_view(){
        index = 0;
        home = findViewById(R.id.home);
        illu_1 = findViewById(R.id.illu_1);
        illu_2 = findViewById(R.id.illu_2);
        illu_3 = findViewById(R.id.illu_3);
        illu_4 = findViewById(R.id.illu_4);
        illu_5 = findViewById(R.id.illu_5);
        illu_6 = findViewById(R.id.illu_6);
        illu_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog(R.drawable.tree1, sentence.get(0), sentence.get(1));
            }
        });
        illu_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog(R.drawable.tree2,sentence.get(2), sentence.get(3));
            }
        });
        illu_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog(R.drawable.tree3,sentence.get(4), sentence.get(5));
            }
        });
        illu_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        illu_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        illu_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IllustrationActivity.this, MainActivity.class));
                finish();
            }
        });
        pic = new ArrayList<>();
        sentence = new ArrayList<>();

        pic.add(illu_1);
        pic.add(illu_2);
        pic.add(illu_3);
        pic.add(illu_4);
        pic.add(illu_5);
        pic.add(illu_6);

        sentence.add("The power of habit is great.");
        sentence.add("-Latin Proverb");
        sentence.add("Good habits formed at youth \nmake all the difference.");
        sentence.add("-Aristotle");
        sentence.add("Success is the sum of small efforts \nrepeated day in day out.");
        sentence.add("-Robert Collier");
    }
    private void showCustomDialog(Integer image_id, String quation, String author) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tree_detial, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ImageView image = dialogView.findViewById(R.id.detial_image);
        TextView detial_sentence, detial_author;
        detial_sentence = dialogView.findViewById(R.id.detial_sentence);
        detial_author = dialogView.findViewById(R.id.detial_author);
        image.setImageResource(image_id);
        detial_sentence.setText(quation);
        detial_author.setText(author);
    }
}
