package com.example.myapplication.ui.home;

import android.annotation.SuppressLint;
import android.app.MediaRouteButton;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

public class HomeFragment extends Fragment {

    private HomeViewModel HomeViewModel;

    private BluetoothAdapter bluetoothAdapter;
    UUID MY_UUID;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket socket = null;
    BluetoothServerSocket serverSocket; // 서버 소켓
    boolean isConnected = false;
    boolean serverFlag = true;

    private TextView outputText;
    private EditText inputEditText;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> bluetoothArrayAdapter;
    private ListView mDevicesListView;
    private CheckBox mLED1;
    private MainActivity mainActivity = null;
    private ArrayAdapter<String> mBTArrayAdapter;
   // private AdapterView.OnItemClickListener mDeviceClickListener;
    IntentFilter stateFilter = new IntentFilter();

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        HomeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity)getActivity();
        bluetoothArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1);
        mDevicesListView = (ListView)rootView.findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);
        final ListView mDevicesListView2 = mDevicesListView.findViewById(R.id.devicesListView);
        final Button Btn1 = rootView.findViewById(R.id.PairedBtn);
        final Button Btn2 = rootView.findViewById(R.id.on);
        final Button Btn3 = rootView.findViewById(R.id.off);
        final Button Btn4 = rootView.findViewById(R.id.discover);
        final TextView text1 = rootView.findViewById(R.id.status);
        final TextView text2 = rootView.findViewById(R.id.read);
        final CheckBox check1 = rootView.findViewById(R.id.checkboxLED1);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        TextView st1 = text1.findViewById(R.id.status);
        TextView st2 = text2.findViewById(R.id.read);
        CheckBox checkbox1 = check1.findViewById(R.id.checkboxLED1);

        if(bluetoothAdapter.isEnabled()){

            st1.setText("BlueTooth Enabled");
            st2.setText("BlueTooth turned on");
            checkbox1.setChecked(true);
        }

        else{

            st1.setText("BlueTooth Disabled");
            st2.setText("BlueTooth turned off");
            checkbox1.setChecked(false);
        }

        Btn1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button button1 = Btn1.findViewById(R.id.PairedBtn);
                TextView st1 = text1.findViewById(R.id.read);

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        //st1.setText("Searching");
                        if(bluetoothAdapter.isEnabled()){
                            Toast.makeText(getActivity().getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
                            searchDevice();
                        }

                        else Toast.makeText(getActivity().getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();

                        break;
                    case MotionEvent.ACTION_UP:
                        button1.setText("SHOW PAIRED DEVICES");
                        break;
                }
                return false;
            }
        });

        Btn2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button button1 = Btn2.findViewById(R.id.on);
                CheckBox checkbox1 = check1.findViewById(R.id.checkboxLED1);
                TextView st1 = text1.findViewById(R.id.status);
                TextView st2 = text2.findViewById(R.id.read);
                BluetoothAdapter ap1 = BluetoothAdapter.getDefaultAdapter();

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                            ap1.enable();
                            st1.setText("BlueTooth Enabled");
                            st2.setText("BlueTooth turned on");
                            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth on", Toast.LENGTH_SHORT).show();
                            checkbox1.setChecked(true);

                        break;
                    case MotionEvent.ACTION_UP:
                        button1.setText("BLUETOOTH ON");
                        break;
                }
                return false;
            }
        });

        Btn3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button button1 = Btn3.findViewById(R.id.off);
                TextView st1 = text1.findViewById(R.id.status);
                TextView st2 = text2.findViewById(R.id.read);
                CheckBox checkbox1 = check1.findViewById(R.id.checkboxLED1);
                BluetoothAdapter ap1 = BluetoothAdapter.getDefaultAdapter();

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                            ap1.disable();
                            st1.setText("BlueTooth Disabled");
                            st2.setText("BlueTooth turned off");
                            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth off", Toast.LENGTH_SHORT).show();
                            checkbox1.setChecked(false);
                            ((MainActivity)getActivity()).handler_exit();

                        break;
                    case MotionEvent.ACTION_UP:
                        button1.setText("BLUETOOTH OFF");
                        break;
                }
                return false;
            }
        });


        Btn4.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button button1 = Btn4.findViewById(R.id.discover);
                TextView st1 = text1.findViewById(R.id.status);
                TextView st2 = text2.findViewById(R.id.read);
                BluetoothAdapter ap1 = BluetoothAdapter.getDefaultAdapter();

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        mDevicesListView = (ListView)getView().findViewById(R.id.devicesListView);
                        mDevicesListView.setAdapter(((MainActivity)getActivity()).bluetoothArrayAdapter);
                        ((MainActivity)getActivity()).discover();
                        break;

                    case MotionEvent.ACTION_UP:
                        button1.setText("DISCOVER NEW DEVICES");
                        break;
                }
                return false;
            }
        });

        ServerThread serverThread=new ServerThread();
        serverThread.start(); // 서버 스레드
        ((MainActivity)getActivity()).homeFragment = this;
        return rootView;
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            com.example.myapplication.ui.home.HomeFragment.ClientThread clientThread = new com.example.myapplication.ui.home.HomeFragment.ClientThread(); //
            Toast.makeText(getActivity().getApplicationContext(), "connecting...!", Toast.LENGTH_SHORT).show();
            clientThread.start(); //클라이언트 쓰레드 시작
        }
    };

    public void searchDevice() { // 장비찾기
        final Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices(); // 장비 목록
        if (devices.size() > 0) {
            final String[] deviceArray = new String[devices.size()];
            Iterator<BluetoothDevice> iter = devices.iterator(); //반복자 받기
            int i = 0;
            while (iter.hasNext()) {
                BluetoothDevice d = iter.next();
                deviceArray[i] = d.getName(); // 디바이스 이름들 넣기
                i++;
            }

            // dialog로 페어링 디바이스 목록 출력
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("device choice");

            builder.setItems(deviceArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectDeivceStr = deviceArray[which];
                    for (BluetoothDevice device : devices) {
                        if (device.getName().equals(selectDeivceStr)) {
                            bluetoothDevice = device; //장비 등록
                            // thread에서 연결 요청
                            com.example.myapplication.ui.home.HomeFragment.ClientThread clientThread = new com.example.myapplication.ui.home.HomeFragment.ClientThread(); //
                            clientThread.start(); //클라이언트 쓰레드 시작
                        }
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

   public void sendData(String text) {

        // 문자열에 개행문자("\n")를 추가해줍니다.
        if(isConnected) {
            text += "\n"; //개행문자 추가

            try {
                // 데이터 송신
                outputStream.write(text.getBytes());

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    class ClientThread extends Thread {
        @Override
        public void run() {
            try {
                //add~~~~~~~~~~~~~
                bluetoothAdapter.cancelDiscovery(); // 탐색 종료
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID); // 소켓 연결
                socket.connect();

                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                isConnected = true;
                outputText.append("connected\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ServerThread extends Thread {
        @Override
        public void run() {
            while (serverFlag) {
                if(!isConnected) {
                    if (bluetoothAdapter.isEnabled()) { // 블루투스가 켜졌으면
                        try {
                            //add~~~~~~~~~~

                            serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord("kkang_test", MY_UUID);
                            //연결 요청이 들어오기 까지 대기
                            socket=serverSocket.accept();
                            // 이후에는 연결이 되었음

                            outputStream = socket.getOutputStream();
                            inputStream = socket.getInputStream();
                            // 데이터 수신 함수 호출
                            isConnected = true;
                            outputText.append("connected\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("kkang", e.getMessage());
                        }
                        if (socket != null) {
                            try {
                                serverSocket.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else {
                    SystemClock.sleep(1000);
                }
            }
        }
    }






   /* private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!bluetoothAdapter.isEnabled()) {
                Toast.makeText(getActivity().getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            //mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
        //creates secure outgoing connection with BT device using UUID
    }

     */
}
