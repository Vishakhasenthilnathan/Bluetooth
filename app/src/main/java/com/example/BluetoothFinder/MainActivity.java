package com.example.BluetoothFinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    TextView textView;
    Button search;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> bluetoothDevices = new ArrayList<>();

    @SuppressLint("MissingPermission")
    public void SearchClicked(View view) {
        search.setEnabled(false);
        textView.setText("Searching...");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevices.clear();
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("action", action);
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(context, "Discovery complete", Toast.LENGTH_SHORT).show();
                textView.setText("Finished!");
                search.setEnabled(true);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                String name = bluetoothDevice.getName();
                String address = bluetoothDevice.getAddress();
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                bluetoothDevices.add("Name : " + name + "Address : "+ address +"rssi : "+ rssi);
                arrayAdapter.notifyDataSetChanged();
                Log.i("Device details", "name: "+name+" address: "+ address + "rssi : "+rssi);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);
        textView = findViewById(R.id.textView);
        search = findViewById(R.id.button);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,bluetoothDevices);

        listView.setAdapter(arrayAdapter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(broadcastReceiver,intentFilter);

    }
}