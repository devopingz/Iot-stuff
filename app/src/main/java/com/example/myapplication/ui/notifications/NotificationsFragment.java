package com.example.myapplication.ui.notifications;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

         final TextView outputText1 =  rootView.findViewById(R.id.outputTextView);
         final EditText inputEditText1 = rootView.findViewById(R.id.inputEditText);
         final Button sendBtn1 = rootView.findViewById(R.id.sendBtn); // 문자 보내기
        final Button searchButton = rootView.findViewById(R.id.searchBtn);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button searchBtn2 = searchButton.findViewById(R.id.searchBtn);
                TextView outputText2 = outputText1.findViewById(R.id.outputTextView);
                outputText2.setText("pressed");
                inputEditText1.setText("pressed");
                searchDevice(); //블루투스 장비 찾기
            }
        });

        sendBtn1.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) { // 버튼 누를 때
                Button searchBtn2 = searchButton.findViewById(R.id.searchBtn);
                searchBtn2.setText("send receive");
                //TextView outputText2 = outputText.findViewById(R.id.outputTextView);
                //outputText2.setText("pressed");
                //outputText1.setText("pressed");
                //inputEditText1.setText("pressed");
                //sendData(inputEditText.getText().toString()); // 데이터를 보냄.
                //sendBtn1.setText(""); // 데이터를 비움
            }
        });

        // ServerThread serverThread=new ServerThread();
        //serverThread.start(); // 서버 스레드

        return rootView;
    }

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
            builder.setTitle("device 선택");

            builder.setItems(deviceArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectDeivceStr = deviceArray[which];
                    for (BluetoothDevice device : devices) {
                        if (device.getName().equals(selectDeivceStr)) {
                            bluetoothDevice = device; //장비 등록
                            // thread에서 연결 요청
                            ClientThread clientThread = new ClientThread(); //
                            clientThread.start(); //클라이언트 쓰레드 시작
                        }
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    void sendData(String text) {

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

    public class ClientThread extends Thread {
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
}