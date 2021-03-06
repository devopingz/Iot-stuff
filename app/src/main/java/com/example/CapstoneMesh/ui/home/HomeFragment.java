package com.example.CapstoneMesh.ui.home;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.CapstoneMesh.MainActivity;
import com.example.CapstoneMesh.R;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;

public class HomeFragment extends Fragment {
    private HomeViewModel HomeViewModel;
    private BluetoothAdapter bluetoothAdapter;
    UUID MY_UUID;
    BluetoothDevice bluetoothDevice;
    public BluetoothSocket socket = null;
    BluetoothServerSocket serverSocket; // μλ² μμΌ
    boolean isConnected = false;
    boolean serverFlag = true;

    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private ListView mDevicesListView;
    private MainActivity mainActivity = null;


    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = rootView.findViewById(R.id.text_home);
        HomeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // λΈλ£¨ν¬μ€ μ΄λν°λ₯Ό λν΄νΈ μ΄λν°λ‘ μ€μ 
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        mainActivity = (MainActivity)getActivity();

        // λ¦¬μ€νΈλ·° λ±λ‘
        mDevicesListView = (ListView)rootView.findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mainActivity.getBluetoothArrayAdapter());
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

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

///////////λΈλ£¨ν¬μ€ μ²΄ν¬λ°μ€ μ‘°μ  ////////////////////////
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

////////////νμ΄λ§ λ¦¬μ€νΈ λλ₯Ό λ //////////////////////
        Btn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button button1 = Btn1.findViewById(R.id.PairedBtn);
                TextView st1 = text1.findViewById(R.id.read);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(bluetoothAdapter.isEnabled()){
                            Toast.makeText(getActivity().getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
                            searchDevice(); // μ₯λΉ μ°ΎκΈ°
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
//////////////// λΈλ£¨ν¬μ€ ON ///////////////////////////
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
///////////////λΈλ£¨ν¬μ€ OFF /////////////////////////////
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
////////////////////// DISCOVER ////////////////////////////
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
/////////////μλ² μ€λ λλ₯Ό μ΄κΈ° ///////////////////
        ServerThread serverThread=new ServerThread();
        serverThread.start(); // μλ² μ€λ λ
        ((MainActivity)getActivity()).homeFragment = this;

        receiveData();
        return rootView;
    }
/////////////////////////////λ¦¬μ€νΈλ·°λ₯Ό λλ₯Ό λ ///////////////////////////////////
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            com.example.CapstoneMesh.ui.home.HomeFragment.ClientThread clientThread = new com.example.CapstoneMesh.ui.home.HomeFragment.ClientThread(); //
            Toast.makeText(getActivity().getApplicationContext(), "connecting...!", Toast.LENGTH_SHORT).show();
            clientThread.start(); //ν΄λΌμ΄μΈνΈ μ°λ λ μμ
        }
    };

    public void searchDevice() { // μ₯λΉμ°ΎκΈ°
        final Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices(); // μ₯λΉ λͺ©λ‘
        if (devices.size() > 0) {
            final String[] deviceArray = new String[devices.size()];
            Iterator<BluetoothDevice> iter = devices.iterator(); //λ°λ³΅μ λ°κΈ°
            int i = 0;
            while (iter.hasNext()) {
                BluetoothDevice d = iter.next();
                deviceArray[i] = d.getName(); // λλ°μ΄μ€ μ΄λ¦λ€ λ£κΈ°
                i++;
            }

            // dialogλ‘ νμ΄λ§ λλ°μ΄μ€ λͺ©λ‘ μΆλ ₯
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("device choice");

            builder.setItems(deviceArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectDeivceStr = deviceArray[which];
                    for (BluetoothDevice device : devices) {
                        if (device.getName().equals(selectDeivceStr)) {
                            bluetoothDevice = device; //μ₯λΉ λ±λ‘
                            // threadμμ μ°κ²° μμ²­
                            com.example.CapstoneMesh.ui.home.HomeFragment.ClientThread clientThread = new com.example.CapstoneMesh.ui.home.HomeFragment.ClientThread(); //
                            clientThread.start(); //ν΄λΌμ΄μΈνΈ μ°λ λ μμ
                        }
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    private int readBufferPosition; // λ²νΌ λ΄ λ¬Έμ μ μ₯ μμΉ
    private byte[] readBuffer; // μμ  λ λ¬Έμμ΄μ μ μ₯νκΈ° μν λ²νΌ
    private Thread workerThread = null; // λ¬Έμμ΄ μμ μ μ¬μ©λλ μ°λ λ
    //λ²νΌλ₯Ό λ§λ€κ³ =>μ€λ λ->μΈν°λ½νΈ μλλ©΄=>μμ  λ°μ΄νΈ μ λ¦¬ν΄->νλ°μ΄νΈ μ© μ½μ=>λλ¬Έμ λμ€λ©΄ μ½μ μμΉκΉμ§
    // =>μ€νΈλ§μΌλ‘ λ³ν
    public void receiveData() {
        final Handler handler = new Handler();
        // λ°μ΄ν°λ₯Ό μμ νκΈ° μν λ²νΌλ₯Ό μμ±
        readBufferPosition = 0; // μ½μ κΈμ μ
        readBuffer = new byte[1024];

        // λ°μ΄ν°λ₯Ό μμ νκΈ° μν μ°λ λ μμ±
        workerThread = new Thread(new Runnable() {

            @Override

            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        if(false) { // μ°κ²°μ΄ λκΈ° μ κΉμ§λ λκΈ°
                            // λ°μ΄ν°λ₯Ό μμ νλμ§ νμΈν©λλ€.
                            int byteAvailable = inputStream.available(); // μ½μ μ μλ λ°μ΄νΈμ μ λ¦¬ν΄
                            // λ°μ΄ν°κ° μμ  λ κ²½μ°
                            if (byteAvailable > 0) {
                                // μλ ₯ μ€νΈλ¦Όμμ λ°μ΄νΈ λ¨μλ‘ μ½μ΄ μ΅λλ€.
                                byte[] bytes = new byte[byteAvailable]; // μμ ν  λ°μ΄νΈ λ³μ
                                inputStream.read(bytes);

                                // μλ ₯ μ€νΈλ¦Ό λ°μ΄νΈλ₯Ό ν λ°μ΄νΈμ© μ½μ΄ μ΅λλ€.
                                for (int i = 0; i < byteAvailable; i++) {
                                    byte tempByte = bytes[i];
                                    // κ°νλ¬Έμλ₯Ό κΈ°μ€μΌλ‘ λ°μ(νμ€)
                                    if (tempByte == '\n') { // κ°ν λ¬Έμκ° λνλλ©΄
                                        // readBuffer λ°°μ΄μ encodedBytesλ‘ λ³΅μ¬
                                        byte[] encodedBytes = new byte[readBufferPosition]; // μ§κΈκΉμ§ μ½μ κΈμ μλ₯Ό
                                        // μλ°°μ΄ // μμ€λ°°μ΄ κ°μμμΉ  // λͺ©μ  λ°°μ΄ //κ°μμμΉ //μΉ΄νΌλλ λ°°μ΄ μμμ μ
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                        // μΈμ½λ© λ λ°μ΄νΈ λ°°μ΄μ λ¬Έμμ΄λ‘ λ³ν
                                        final String text = new String(encodedBytes, "US-ASCII");

                                        readBufferPosition = 0; // μ½μ κΈμ μ
                                        // νΈλ€λ¬ μμ
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                // νμ€νΈ λ·°μ μΆλ ₯
                                                ((MainActivity)getActivity()).connectFragment.outputText.append(text + "\n");
                                            }
                                        });
                                    } // κ°ν λ¬Έμκ° μλ κ²½μ°
                                    else {
                                        readBuffer[readBufferPosition++] = tempByte;
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        // 1μ΄λ§λ€ λ°μμ΄
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        workerThread.start();
    }
   public void sendData(String text) {

        // λ¬Έμμ΄μ κ°νλ¬Έμ("\n")λ₯Ό μΆκ°ν΄μ€λλ€.
        if(isConnected) {
            text += "\n"; //κ°νλ¬Έμ μΆκ°

            try {
                // λ°μ΄ν° μ‘μ 
                outputStream.write(text.getBytes());

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    class ClientThread extends Thread {
        Handler handler = new Handler();
        @Override
        public void run() {
            try {
                //add~~~~~~~~~~~~~
                bluetoothAdapter.cancelDiscovery(); // νμ μ’λ£
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID); // μμΌ μ°κ²°
                socket.connect();

                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                ((MainActivity)getActivity()).outputStream = outputStream;
                ((MainActivity)getActivity()).inputStream = inputStream;
                isConnected = true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "As client connected", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ServerThread extends Thread {
        Handler handler = new Handler();
        @Override
        public void run() {
            while (serverFlag) {
                if(!isConnected) { // μ°κ²°μ΄ μ λμμ λ μ κ·Ό
                    if (bluetoothAdapter.isEnabled()) { // λΈλ£¨ν¬μ€κ° μΌμ‘μΌλ©΄
                        try {
                            //add~~~~~~~~~~

                            serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord("kkang_test", MY_UUID);
                            //μ°κ²° μμ²­μ΄ λ€μ΄μ€κΈ° κΉμ§ λκΈ°
                            socket=serverSocket.accept();
                            // μ΄νμλ μ°κ²°μ΄ λμμ

                            outputStream = socket.getOutputStream();
                            inputStream = socket.getInputStream();
                            ((MainActivity)getActivity()).outputStream = outputStream;
                            ((MainActivity)getActivity()).inputStream = inputStream;
                            // λ°μ΄ν° μμ  ν¨μ νΈμΆ
                            isConnected = true;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "As server connected", Toast.LENGTH_SHORT).show();
                                }
                            });
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
                    if(socket.isConnected()==false) {
                        isConnected = false;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "connection terminated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    SystemClock.sleep(1000);
                }
            }
        }
    }


}
