package com.jins_jp.memelib_realtime;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jins_jp.meme.MemeConnectListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeScanListener;
import com.jins_jp.meme.MemeStatus;

import java.util.ArrayList;

public class StatusActivity extends AppCompatActivity implements MemeConnectListener {

    private static final int LOCATION_PERMISSIONS_REQUEST = 1001;
    private static final int BLE_PERMISSIONS_REQUEST = 1002;
    private static final String TAG = "StatusActivity";

    private static final String appClientId = "256796531280417";
    private static final String appClientSecret = "2syrxcjgq2c15isiv07yz0y92jy906wl";

    private MemeLib memeLib;
    private Button scan;
    private ListView ble_list;
    private ArrayList<String> ble_address;
    private ArrayAdapter scannedAddressAdapter;
    private ProgressBar netprogress,bltprogress, status_progress;
    private ImageView netCHK,bltCHK;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Init();
        Log.d(TAG, isNetworkAvailable(StatusActivity.this)+"");
        Log.d(TAG, isBleAvaiable(StatusActivity.this)+"");

        Intent intent_account = this.getIntent();
        account = intent_account.getStringExtra("account");

        netCHK.setVisibility(View.INVISIBLE);
        bltCHK.setVisibility(View.INVISIBLE);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isNetworkAvailable(StatusActivity.this)){
//                    netCHK.setImageDrawable(getResources().getDrawable( R.drawable.check ));
                    netprogress.setVisibility(View.INVISIBLE);
                    netCHK.setVisibility(View.VISIBLE);
                }


                if(isBleAvaiable(StatusActivity.this)) {
//                    bltCHK.setImageDrawable(getResources().getDrawable( R.drawable.check ));
                    bltprogress.setVisibility(View.INVISIBLE);
                    bltCHK.setVisibility(View.VISIBLE);
                }else{
                    startScanWithPermissionCheck();
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, BLE_PERMISSIONS_REQUEST);
                }
            }
        }, 2000);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
        ble_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "Get index: " + i + ", " + ble_address.get(i));
                stopScan();
                memeLib.connect(ble_address.get(i));
                status_progress.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BLE_PERMISSIONS_REQUEST){
//            bltCHK.setImageDrawable(getResources().getDrawable( R.drawable.check ));
            bltprogress.setVisibility(View.INVISIBLE);
            bltCHK.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Sets MemeConnectListener to get connection result.
                memeLib.setMemeConnectListener(StatusActivity.this);
            }
        }, 1000);

        // Configure auto connection to JINS MEME
//        memeLib.setAutoConnect(true);
    }

    @Override
    public void memeConnectCallback(boolean b) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status_progress.setVisibility(View.INVISIBLE);
            }
        });

        Log.d(TAG, "connect!");
        startActivity(new Intent(StatusActivity.this,MainActivity.class));
        finish();
    }

    @Override
    public void memeDisconnectCallback() {
        //describe actions after disconnection from JINS MEME
        Log.d(TAG, "disconnect!");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            }
        }
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
    private boolean isBleAvaiable(Context context){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null)
        {
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }


    private void startScanWithPermissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSIONS_REQUEST);
            }
        }
    }

    private void startScan() {
        ble_address.clear();
        scannedAddressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ble_address);
        ble_list.setAdapter(scannedAddressAdapter);

        //starts scanning JINS MEME
        MemeStatus status = memeLib.startScan(new MemeScanListener() {
            @Override
            public void memeFoundCallback(String address) {
                Log.d(TAG, address+"");
                ble_address.add(address);

                runOnUiThread(new Runnable() {
                    public void run() {
                        scannedAddressAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        Log.d(TAG, status+"");

        if (status == MemeStatus.MEME_ERROR_APP_AUTH) {
            Toast.makeText(this, "App Auth Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void stopScan() {
        //stop scanning JINS MEME
        if (memeLib.isScanning())
            memeLib.stopScan();
    }



    private void Init() {
        //Authentication and authorization of App and SDK
        MemeLib.setAppClientID(getApplicationContext(), appClientId, appClientSecret);
        memeLib = MemeLib.getInstance();//MemeLib is singleton
        memeLib.setVerbose(true);

        netprogress = findViewById(R.id.internetprogress);
        bltprogress = findViewById(R.id.bluetoothprogress);
        status_progress = findViewById(R.id.status_progress);
        netCHK = findViewById(R.id.netCHK);
        bltCHK = findViewById(R.id.bltCHK);
        scan = findViewById(R.id.scan);
        ble_list = findViewById(R.id.ble_list);

        ble_address = new ArrayList<>();

    }


}
