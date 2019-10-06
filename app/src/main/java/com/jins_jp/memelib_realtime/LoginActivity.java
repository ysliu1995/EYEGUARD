package com.jins_jp.memelib_realtime;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference mdatabase;
    private EditText account, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Init();
        mdatabase = FirebaseDatabase.getInstance().getReference();

        if(!isNetworkAvailable(LoginActivity.this)){
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Notification")
                    .setMessage("Internet not found, please go setting to start.")
                    .setCancelable(true)
                    .show();
        }

        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!account.getText().toString().equals("") && !password.getText().toString().equals("")){
                    mdatabase.child("Users").child(account.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount() == 0){
                                Register();
                            }else{
                                if((dataSnapshot.child("password").getValue().toString()).equals(password.getText().toString())){
                                    SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                                    pref.edit()
                                            .putString("account", account.getText().toString())
                                            .apply();
                                    startActivity(new Intent(LoginActivity.this, StatusActivity.class));
                                    //startActivity(new Intent(LoginActivity.this, StatusActivity.class));
                                    finish();
                                }else if(!(dataSnapshot.child("password").getValue().toString()).equals(password.getText().toString())) {
                                    Toast.makeText(getApplicationContext(), "password error or account has been registered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }
    private void Init(){
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
    }
    private void Register(){
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Sign up")
                .setMessage("Do you want to register ?")
                .setNegativeButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mdatabase.child("Users").child(account.getText().toString()).child("password").setValue(password.getText().toString());
                                mdatabase.child("Users").child(account.getText().toString()).child("pos_score").setValue(0);
                                mdatabase.child("Users").child(account.getText().toString()).child("nag_score").setValue(0);
                                startActivity(new Intent(LoginActivity.this, StatusActivity.class));
                                finish();
                            }
                        })
                .setPositiveButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

    }
    private  boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = mgr.getAllNetworkInfo();
        if (infos != null) {
            for (int i = 0; i < infos.length; i++) {
                if (infos[i].isConnected() == true) {
                    return true;
                }
            }
        }
        return false;
    }
}
