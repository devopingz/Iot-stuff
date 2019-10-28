package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.myapplication.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    public HomeFragment homeFragment;
    private BluetoothAdapter bluetoothAdapter;
    public ArrayAdapter<String> bluetoothArrayAdapter;
    Handler handler = new Handler();
    private Fragment movieInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_dashboard2, R.id.navigation_notifications,
                R.id.navigation_message
        )
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        //homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_home);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        bluetoothArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1) ;
    }


    // DashboardFragment 에서 액티비티 통해 HomeActivity에 있는 함수에 접근
    public void sendDataUsingHomeFragment(int radioOne, int radioTwo) {
        String convertParatoString = String.format("%d is connected to %d", radioOne, radioTwo);

        //HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_home);
        //if(homeFragment == null) homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_home);
        //if(homeFragment == null) Toast.makeText(getApplicationContext(),"null입니다",Toast.LENGTH_SHORT).show();
        //else Toast.makeText(getApplicationContext(),"휴 다행",Toast.LENGTH_SHORT).show();

        homeFragment.sendData(convertParatoString);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
            d.setTitle("exit status");
            d.setMessage("Are you sure end this app?");
            d.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.removeCallbacksAndMessages(null);
                    bluetoothAdapter.cancelDiscovery();
                    MainActivity.this.finish();

                }
            });

            d.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            d.show();

            return true;
        }
        return onKeyDown(keyCode, event);
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                bluetoothArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                Toast.makeText(getApplicationContext(), "find", Toast.LENGTH_SHORT).show();
                bluetoothArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void handler_exit(){
        final ProgressBar bar1 = findViewById(R.id.progress_bar1);
        bar1.setVisibility(View.INVISIBLE);
        handler.removeCallbacksAndMessages(null);
    }


    public void discover(){
        // Check if the device is already discovering
       final ProgressBar bar1 = findViewById(R.id.progress_bar1);

        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            handler.removeCallbacksAndMessages(null);
            bar1.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Discovery stopped", Toast.LENGTH_SHORT).show();
        }

        else{

            if(bluetoothAdapter.isEnabled()) {

                bluetoothArrayAdapter.clear(); // clear items
                bluetoothAdapter.startDiscovery();
                bar1.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        bar1.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Discovery stopped", Toast.LENGTH_SHORT).show();
                    }
                }, 13000);
            }

            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setHomeFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    public ArrayAdapter<String> getBluetoothArrayAdapter() {
        return bluetoothArrayAdapter;
    }

    public void sendDataUsingHomeFragment(String string) { homeFragment.sendData(string); }
}
