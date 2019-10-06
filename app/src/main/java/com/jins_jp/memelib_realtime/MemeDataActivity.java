package com.jins_jp.memelib_realtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;

import com.jins_jp.meme.MemeConnectListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeRealtimeData;
import com.jins_jp.meme.MemeRealtimeListener;
import com.jins_jp.meme.MemeResponse;
import com.jins_jp.meme.MemeResponseListener;
import com.jins_jp.meme.MemeStandardData;
import com.jins_jp.meme.MemeStandardListener;


public class MemeDataActivity extends AppCompatActivity {

    MemeLib memeLib;
    ListView dataItemListView;
    boolean isRealtimeMode = true;

    MemeStandardDataItemAdapter memeStandardDataItemAdapter;
    MemeRealtimeDataItemAdapter memeRealtimeDataItemAdapter;

    final MemeRealtimeListener memeRealtimeListener = new MemeRealtimeListener() {
        @Override
        public void memeRealtimeCallback(final MemeRealtimeData memeRealtimeData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setSupportProgressBarIndeterminateVisibility(true);
                    memeRealtimeDataItemAdapter.updateMemeData(memeRealtimeData);
                    memeRealtimeDataItemAdapter.notifyDataSetChanged();
                    if (!isRealtimeMode) {
                        isRealtimeMode = true;
                        invalidateOptionsMenu();
                    }
                    setSupportProgressBarIndeterminateVisibility(false);
                }
            });
        }
    };

    final MemeStandardListener memeStandardListener = new MemeStandardListener() {
        @Override
        public void memeStandardCallback(final MemeStandardData memeStandardData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setSupportProgressBarIndeterminateVisibility(true);
                    memeStandardDataItemAdapter.updateMemeData(memeStandardData);
                    memeStandardDataItemAdapter.notifyDataSetChanged();
                    if (isRealtimeMode) {
                        isRealtimeMode = false;
                        invalidateOptionsMenu();
                    }
                    setSupportProgressBarIndeterminateVisibility(false);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_meme_data);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();

        //Sets MemeConnectListener to get connection result.
        memeLib.setMemeConnectListener(new MemeConnectListener() {
            @Override
            public void memeConnectCallback(boolean b) {
                //describe actions after connection with JINS MEME
            }

            @Override
            public void memeDisconnectCallback() {
                //describe actions after disconnection from JINS MEME
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meme_data, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isRealtimeMode) {
            menu.findItem(R.id.action_change_to_standard).setVisible(true);
            menu.findItem(R.id.action_change_to_realtime).setVisible(false);
        } else {
            menu.findItem(R.id.action_change_to_standard).setVisible(false);
            menu.findItem(R.id.action_change_to_realtime).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_disconnect) {
            //disconnect JINS MEME
            memeLib.disconnect();

            // By setting autoConnect false, this app won't connect automatically last connected JINS MEME.
            // Otherwise, the app start to establish the connection with it just after disconnection.
            memeLib.setAutoConnect(false);
            this.finish();
            return true;
        } else if (id == R.id.action_change_to_standard) {

            memeLib.setResponseListener(new MemeResponseListener() {
                @Override
                public void memeResponseCallback(MemeResponse memeResponse) {
                    // Event Code 4 is stop realtime mode successfully
                    if (memeResponse.getEventCode() == 4) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dataItemListView.setAdapter(memeStandardDataItemAdapter);
                                memeLib.startDataReport(memeStandardListener);
                                isRealtimeMode = false;
                                invalidateOptionsMenu();
                            }
                        });
                    }
                }
            });

            memeLib.stopDataReport();

        } else if (id == R.id.action_change_to_realtime) {

            memeLib.setResponseListener(new MemeResponseListener() {
                @Override
                public void memeResponseCallback(MemeResponse memeResponse) {
                    // Event Code 4 is stop standard mode successfully
                    if (memeResponse.getEventCode() == 3) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dataItemListView.setAdapter(memeRealtimeDataItemAdapter);
                                memeLib.startDataReport(memeRealtimeListener);
                                isRealtimeMode = true;
                                invalidateOptionsMenu();
                            }
                        });
                    }
                }
            });

            memeLib.stopDataReport();
        }
        return super.onOptionsItemSelected(item);
    }

    void init() {
        memeLib = MemeLib.getInstance();

        dataItemListView = (ListView)findViewById(R.id.data_item_list_view);

        // Adapter for Standard mode
        memeStandardDataItemAdapter = new MemeStandardDataItemAdapter(this);

        // Adapter for Realtime mode
        memeRealtimeDataItemAdapter = new MemeRealtimeDataItemAdapter(this);

        dataItemListView.setAdapter(memeRealtimeDataItemAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //Starts receiving realtime data
        memeLib.startDataReport(memeRealtimeListener);
    }
}
