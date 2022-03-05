package com.example.CapstoneMesh;

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
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.CapstoneMesh.ui.connect.ConnectFragment;
import com.example.CapstoneMesh.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements Serializable {

    public HomeFragment homeFragment;
    public ConnectFragment connectFragment;
    private BluetoothAdapter bluetoothAdapter;
    public ArrayAdapter<String> bluetoothArrayAdapter;
    Handler handler = new Handler();
    private Fragment movieInfo;
    public String connectFragScreenBuf;
    public PacketData  packetData = null;

    public InputStream inputStream = null;
    public OutputStream outputStream = null;

    public String textViewBackUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_connect,
                R.id.navigation_setting
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

    /////////////////// DashboardFragment 에서 액티비티 통해 HomeActivity에 있는 함수에 접근 //////////////////////
    public void sendDataUsingHomeFragment(int radioOne, int radioTwo) {
        int radioOneName=0, radioTwoName=0;

        switch (radioOne) {
            case R.id.radioButton1: radioOneName = 1; break;
            case R.id.radioButton2: radioOneName = 2; break;
            case R.id.radioButton3: radioOneName = 3; break;
            case R.id.radioButton4: radioOneName = 4; break;
            case R.id.radioButton5: radioOneName = 5; break;
        }

        switch (radioTwo) {
            case R.id.radioButton6: radioTwoName = 6; break;
            case R.id.radioButton7: radioTwoName = 7; break;
            case R.id.radioButton8: radioTwoName = 8; break;
            case R.id.radioButton9: radioTwoName = 9; break;
            case R.id.radioButton10: radioTwoName = 10; break;
        }

        String convertParatoString = String.format("SRC %d is connected to DEST %d", radioOneName, radioTwoName);
        if(homeFragment != null)
            connectFragScreenBuf = convertParatoString;
            homeFragment.sendData(convertParatoString);
    }

////////////////////////// BACK 버튼 눌러 종료 시 /////////////////////////////////
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
        return false ;
    }

/////////////////////////////프로그레스 바 띄우기////////////////////////////
    public void handler_exit(){
        final ProgressBar bar1 = findViewById(R.id.progress_bar1);
        bar1.setVisibility(View.INVISIBLE);
        handler.removeCallbacksAndMessages(null);
    }

    //////////////////////DISCOVER 성공했을 때 ///////////////////////
    final BroadcastReceiver blReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                bluetoothArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                bluetoothArrayAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "find", Toast.LENGTH_SHORT).show();
            }
        }
    };
///////////////////////// DISCOVER 모드 ///////////////////////
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
                bar1.setVisibility(View.VISIBLE); // 프로그레스 바 보여주기
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() { // 조금 이후에 프로그레스 바 없애기
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
////////////////////////////////////////////////////////
    public void setHomeFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    } // 홈프래그먼트
    public ArrayAdapter<String> getBluetoothArrayAdapter() { // 블루투스 어댑터
        return bluetoothArrayAdapter;
    }
    public void sendDataUsingHomeFragment(String string) {
        if(homeFragment != null)
            homeFragment.sendData(string);
    } // sendData
}
