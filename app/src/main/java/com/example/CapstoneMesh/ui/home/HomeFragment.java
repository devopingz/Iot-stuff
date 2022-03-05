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
    BluetoothServerSocket serverSocket; // 서버 소켓
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
        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        mainActivity = (MainActivity)getActivity();

        // 리스트뷰 등록
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

///////////블루투스 체크박스 조절 ////////////////////////
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

////////////페어링 리스트 누를 때 //////////////////////
        Btn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button button1 = Btn1.findViewById(R.id.PairedBtn);
                TextView st1 = text1.findViewById(R.id.read);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(bluetoothAdapter.isEnabled()){
                            Toast.makeText(getActivity().getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
                            searchDevice(); // 장비 찾기
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
//////////////// 블루투스 ON ///////////////////////////
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
///////////////블루투스 OFF /////////////////////////////
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
/////////////서버 스레드를 열기 ///////////////////
        ServerThread serverThread=new ServerThread();
        serverThread.start(); // 서버 스레드
        ((MainActivity)getActivity()).homeFragment = this;

        receiveData();
        return rootView;
    }
/////////////////////////////리스트뷰를 누를 때 ///////////////////////////////////
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            com.example.CapstoneMesh.ui.home.HomeFragment.ClientThread clientThread = new com.example.CapstoneMesh.ui.home.HomeFragment.ClientThread(); //
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
                            com.example.CapstoneMesh.ui.home.HomeFragment.ClientThread clientThread = new com.example.CapstoneMesh.ui.home.HomeFragment.ClientThread(); //
                            clientThread.start(); //클라이언트 쓰레드 시작
                        }
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    private int readBufferPosition; // 버퍼 내 문자 저장 위치
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
    //버퍼를 만들고=>스레드->인터럽트 아니면=>수신 바이트 수 리턴->한바이트 씩 읽음=>널문자 나오면 읽은 위치까지
    // =>스트링으로 변환
    public void receiveData() {
        final Handler handler = new Handler();
        // 데이터를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0; // 읽은 글자 수
        readBuffer = new byte[1024];

        // 데이터를 수신하기 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {

            @Override

            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        if(false) { // 연결이 되기 전까지는 대기
                            // 데이터를 수신했는지 확인합니다.
                            int byteAvailable = inputStream.available(); // 읽을 수 있는 바이트의 수 리턴
                            // 데이터가 수신 된 경우
                            if (byteAvailable > 0) {
                                // 입력 스트림에서 바이트 단위로 읽어 옵니다.
                                byte[] bytes = new byte[byteAvailable]; // 수신할 바이트 변수
                                inputStream.read(bytes);

                                // 입력 스트림 바이트를 한 바이트씩 읽어 옵니다.
                                for (int i = 0; i < byteAvailable; i++) {
                                    byte tempByte = bytes[i];
                                    // 개행문자를 기준으로 받음(한줄)
                                    if (tempByte == '\n') { // 개행 문자가 나타나면
                                        // readBuffer 배열을 encodedBytes로 복사
                                        byte[] encodedBytes = new byte[readBufferPosition]; // 지금까지 읽은 글자 수를
                                        // 원배열 // 소스배열 개시위치  // 목적 배열 //개시위치 //카피되는 배열 요소의 수
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                        // 인코딩 된 바이트 배열을 문자열로 변환
                                        final String text = new String(encodedBytes, "US-ASCII");

                                        readBufferPosition = 0; // 읽은 글자 수
                                        // 핸들러 시작
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 텍스트 뷰에 출력
                                                ((MainActivity)getActivity()).connectFragment.outputText.append(text + "\n");
                                            }
                                        });
                                    } // 개행 문자가 아닐 경우
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
                        // 1초마다 받아옴
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
        Handler handler = new Handler();
        @Override
        public void run() {
            try {
                //add~~~~~~~~~~~~~
                bluetoothAdapter.cancelDiscovery(); // 탐색 종료
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID); // 소켓 연결
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
                if(!isConnected) { // 연결이 안 되었을 때 접근
                    if (bluetoothAdapter.isEnabled()) { // 블루투스가 켜졌으면
                        try {
                            //add~~~~~~~~~~

                            serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord("kkang_test", MY_UUID);
                            //연결 요청이 들어오기 까지 대기
                            socket=serverSocket.accept();
                            // 이후에는 연결이 되었음

                            outputStream = socket.getOutputStream();
                            inputStream = socket.getInputStream();
                            ((MainActivity)getActivity()).outputStream = outputStream;
                            ((MainActivity)getActivity()).inputStream = inputStream;
                            // 데이터 수신 함수 호출
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
