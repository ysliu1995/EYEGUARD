package com.jins_jp.memelib_realtime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeRealtimeData;
import com.jins_jp.meme.MemeRealtimeListener;

public class DataActivity extends AppCompatActivity {

    private MemeLib memeLib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //Starts receiving realtime data
        memeLib.startDataReport(memeRealtimeListener);
    }

    void init() {
        memeLib = MemeLib.getInstance();
    }

    final MemeRealtimeListener memeRealtimeListener = new MemeRealtimeListener() {
        @Override
        public void memeRealtimeCallback(final MemeRealtimeData memeRealtimeData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if(memeRealtimeData.getBlinkStrength()>0||
//                        memeRealtimeData.getBlinkSpeed()>0||
//                            memeRealtimeData.getEyeMoveUp()>0||
//                            memeRealtimeData.getEyeMoveDown()>0||
//                            memeRealtimeData.getEyeMoveLeft()>0||
//                            memeRealtimeData.getEyeMoveRight()>0){
                        Log.d("ap123", memeRealtimeData.getBlinkStrength() + ", " +
                                memeRealtimeData.getBlinkSpeed() + ", " +
                                memeRealtimeData.getEyeMoveUp() + ", " +
                                memeRealtimeData.getEyeMoveDown() + ", " +
                                memeRealtimeData.getEyeMoveLeft() + ", " +
                                memeRealtimeData.getEyeMoveRight() );
                   // }

                }
            });
        }
    };
}
