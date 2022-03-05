package com.example.CapstoneMesh.ui.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.CapstoneMesh.BuildConfig;
import com.example.CapstoneMesh.DBHelper;
import com.example.CapstoneMesh.DevProfile;
import com.example.CapstoneMesh.MainActivity;
import com.example.CapstoneMesh.PacketData;
import com.example.CapstoneMesh.PacketState;
import com.example.CapstoneMesh.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ConnectFragment extends Fragment {

    private ConnectViewModel notificationsViewModel;

    private BluetoothAdapter bluetoothAdapter;
    UUID MY_UUID;

    boolean isConnected = false;

    Handler handler = new Handler();
    MainActivity mainActivity = null;

    public TextView outputText;
    private EditText inputEditText;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
/////////// 연결 프래그먼트가 비활성화 시 텍스트뷰 내용 저장

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((MainActivity)getActivity()).connectFragment = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).textViewBackUp = outputText.getText().toString();
    }

    ///////////////연결 프래그먼트가 활성화 될 때마다 내용 로드
    @Override
    public void onResume() {
        super.onResume();

        if(((MainActivity)getActivity()).textViewBackUp != null && ((MainActivity)getActivity()).textViewBackUp.length() >0 ) { // 기존 내용 로드
            outputText.setText(((MainActivity)getActivity()).textViewBackUp+"\n");
        }
        if(outputText != null &&
                ((MainActivity)getActivity()).connectFragScreenBuf != null &&
                ((MainActivity)getActivity()).connectFragScreenBuf.length() >0 &&
                ((MainActivity)getActivity()).connectFragScreenBuf != "") {

            outputText.append(((MainActivity)getActivity()).connectFragScreenBuf+"\n");
            ((MainActivity) getActivity()).connectFragScreenBuf = "";
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(ConnectViewModel.class);
        View root = inflater.inflate(R.layout.fragment_connect, container, false);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        final TextView outputText1 =  rootView.findViewById(R.id.PeripheralTextView);
        outputText = outputText1; // 출력 글씨 창
        final EditText inputEditText1 = rootView.findViewById(R.id.inputEditText);
        final Button sendBtn1 = rootView.findViewById(R.id.sendBtn); // 문자 보내기
        final Button clearButton = rootView.findViewById(R.id.clearButton);
        final Button openButton = rootView.findViewById(R.id.openButton);
        final Button saveButton = rootView.findViewById(R.id.saveButton);
        final Button confirmButton = rootView.findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outputStream = ((MainActivity)getActivity()).outputStream;
                inputStream = ((MainActivity)getActivity()).inputStream ;

                processWithESP();
            }
        });
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { /// 데이터베이스 /////////////////
                DBHelper helper=new DBHelper(getActivity());
                SQLiteDatabase db=helper.getWritableDatabase();
                Cursor cursor=db.rawQuery("select title, content from outputText", null);

                while (cursor.moveToNext()){

                    outputText.setText(cursor.getString(1));
                }
                db.close();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper helper=new DBHelper(getActivity());
                SQLiteDatabase db=helper.getWritableDatabase();
                db.execSQL("insert into outputText (title, content) values (?,?)",
                        new String[]{"title", outputText1.getText().toString()});
                db.close();
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outputText.setText("");
            }
        });

/////////////////////////전송버튼 누를 때///////////////////////////
        sendBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 버튼 누를 때
                ((MainActivity)getActivity()).sendDataUsingHomeFragment(inputEditText1.getText().toString());
                outputText.append(inputEditText1.getText().toString()+"\n");
            }
        });
        mainActivity = (MainActivity)getActivity();
        return rootView;
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

//////////// 명령어 상수 /////////////////////////
    final byte OP_STATE_OK = 0x21;
    final byte OP_WAIT = 0x22;
    final byte OP_SET_PRO = 0x32;
    final byte OP_GET_PRO = 0x33;
    final byte OP_STATE_ERR = 0x3F;
    final byte REQ_RDY = 0x39;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void sendPacket(Object object) {
        if(!(object instanceof PacketData)) {
            PacketState packetState = (PacketState)object;
            try {
                // 데이터 송신
                Log.i("sendPacketState","000");
                outputStream.write(packetState.getVerb());
                //Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ///////////////////////////////////////////////////////////
        else  {
            PacketData packetData = (PacketData)object;
            List arrayList = new ArrayList();
            try {
                /////////// 데이터 송신 /////////////
                //////////// VERB ///////////////////
                arrayList.add(packetData.getVerb());

                short paired_device_id = packetData.getPaired_device_id();
                repeatByByte(arrayList, paired_device_id, Short.BYTES);
                short num_dev = packetData.getNum_dev();
                repeatByByte(arrayList, num_dev, Short.BYTES);

                for(int i=0; i<packetData.getNum_dev(); i++) {
                    byte[] macArray = packetData.getDev()[i].getMac();
                    for(int j=0; j<6; j++)
                        arrayList.add(macArray[j]);

                    short id = packetData.getDev()[i].getId();
                    repeatByByte(arrayList,id, Short.BYTES);

                    int state =  packetData.getDev()[i].getState();
                    repeatByByte(arrayList,state, Integer.BYTES);

                    int policy = (packetData.getDev()[i].getPolicy());
                    repeatByByte(arrayList,policy, Integer.BYTES);
                }
                Log.i("send arraylist.length", ""+arrayList.size());

                Byte[] byteFromList = (Byte[])arrayList.toArray(new Byte[arrayList.size()]);
                byte[] unboxedBytes = new byte[byteFromList.length];
                for(int i=0;i<byteFromList.length;i++) {
                    unboxedBytes[i] = byteFromList[i].byteValue();
                    //Log.i("send arrayList value", String.format("%h", unboxedBytes[i]));
                }
                outputStream.write(unboxedBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
    public List repeatByByte(List arrayList, Number number, int size) {
        byte var;
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        if(size== Short.BYTES) {
            byteBuffer.putShort((short)number);

        } else if(size == Integer.BYTES) {
            byteBuffer.putInt((int)number);
        }

        for(int i=size-1;i>=0;i--)
            arrayList.add(byteBuffer.array()[i]);
        return arrayList;
    }
///////////////////////////////////////////////////////////////////////////////////////////
    public void processWithESP() {
        final PacketState packetState = new PacketState(OP_STATE_OK);
        sendPacket(packetState);
        Thread workerThread = new Thread();
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int loop =0 ;
                while(true) {
                    try {
                        Thread.sleep(500);
                        loop++;
                        if(loop > 6) return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] packet = receivePacket();

                    if (packet != null && packet[0] == OP_STATE_OK) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ////////// 프로필 설정 명령을 보내는 곳 ///////////////////
                        try {
                            outputStream.write(OP_GET_PRO);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        while(true) {
                            packet = receivePacket();
                            if(packet != null && packet[0] == OP_STATE_OK) break;
                            try {
                                outputStream.write(REQ_RDY);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                Thread.sleep(600);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (packet.length > 1) {
                            PacketState packetState = assemble_receive(packet); // 수신한 부분 합치기 // 패킷스테이트
                            PacketData packetData;
                            packetData = (PacketData) packetState; // 패킷데이터
                            inspection(packetData);
                            mainActivity.packetData = packetData;
                            sendPacket(packetData);
                        }
                        break;
                    }
                }
            }
        });
        workerThread.start();
    }
    /////////////////////// 검사 ///////////////////////////////
    public void inspection(PacketData packetData) {
        Log.i("inspection", "///////////////////////////");
        Log.i("verb", String.format("%x", packetData.getVerb()));
        Log.i("paried_id", String.format("%x", packetData.getPaired_device_id()));
        Log.i("num_dev", String.format("%x", packetData.getNum_dev()));
        for(int i=0;i<packetData.getNum_dev();i++) {
            Log.i("Mac", "//////////");
            for(int j=0; j<6; j++)
                Log.i(""+j, String.format("%x", packetData.getDev()[i].getMac()[j]));
            Log.i("id", String.format("%x", packetData.getDev()[i].getId()));
            Log.i("state", String.format("%x", packetData.getDev()[i].getState()));
            Log.i("policy", String.format("%x", packetData.getDev()[i].getPolicy()));
        }
    }
    ///////////////////////////////////////////////////////
    public byte[] receivePacket() {
        int byteAvailable;

        try {
            if(inputStream == null) {
                return null;
            }
            byteAvailable = inputStream.available();
            if(byteAvailable >0) {
                final byte[] bytes = new byte[byteAvailable]; // 수신할 바이트 변수
                inputStream.read(bytes);
                final StringBuffer stringBuffer = new StringBuffer();
                for(int i=0; i<byteAvailable; i++)
                    stringBuffer.append(String.format("%02x ",bytes[i]));

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        outputText.append(stringBuffer.toString());
                    }
                });
                return bytes;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //////////////////////////////////
    public PacketState assemble_receive(byte[] bytes) {
        byte verb = bytes[0];
        if(bytes.length == 1) {
            return new PacketState(verb);
        }
        for(int i=0; i<bytes.length; i++) {
            Log.i("recv Bytes",""+bytes[i]);
        }
        short paired_device_id = (short)reassmebleNum(bytes,1,Short.BYTES, true);
        Log.i("Paired_dev_id_initial",""+paired_device_id);
        short num_dev = (short)reassmebleNum(bytes, 3, Short.BYTES, true);
        DevProfile[] dev = assembleDevProfile(bytes,5, num_dev);
        return new PacketData(verb, paired_device_id, num_dev, dev);
    }
    //////////////////////////////////
    public Number reassmebleNum(byte[] bytes, int start, int size, boolean endian) {
//        long assembled =0;
//        assembled = ~assembled;
//        Log.d("assemblingNuminit",""+assembled);
//        for(int i=start; i<=end; i++) {
//            assembled &= (bytes[i] << i * 8);
//            Log.d("assemblingNum" + i, "" + assembled);
//        }
//        return assembled & ((0x1<<((end-start)*8+1))-1);
        byte[] newBytes = new byte[size];
        System.arraycopy(bytes, start, newBytes, 0, size);
        for(int i=0; i<size; i++) {
            Log.i("newBytes", ""+newBytes[i]);
        }
        ByteBuffer buffer = ByteBuffer.wrap(newBytes);
        if(endian == true)
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        else
            buffer.order(ByteOrder.BIG_ENDIAN);
        if (size == 2) {

//            Log.i("getShort", ""+buffer.getShort());
            return buffer.getShort();

        } else if (size == 4) {

            return buffer.getInt();
        }

        return null;
    }
    ///////////////////////////////////
    public DevProfile[] assembleDevProfile(byte[] bytes, int start, short num_dev) {
        int idx = start;
        List devProfileList = new ArrayList();
        Log.i("num_dev_in_assembleDevProfile", ""+num_dev+"and idx"+idx);
        for(int i=0; i<num_dev; i++) {
            byte[] macBytes = new byte[6];
            for(int j=0; j<6; j++) {
                macBytes[j] = bytes[idx++];
            }
            short id = (short)reassmebleNum(bytes,idx,Short.BYTES, true);
            idx = idx + Short.BYTES;
            int state = (int)reassmebleNum(bytes,idx, Integer.BYTES, true);
            idx = idx + Integer.BYTES;
            int policy = (int)reassmebleNum(bytes,idx,Integer.BYTES, true);
            idx = idx + Integer.BYTES;
            DevProfile devProfile = new DevProfile(macBytes,id,state,policy);
            devProfileList.add(devProfile);
        }
        DevProfile[] devProfiles = (DevProfile[])devProfileList.toArray(new DevProfile[devProfileList.size()]);
        return devProfiles;
    }
///////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
//    class ServerThread extends Thread {
//        @Override
//        public void run() {
//            while (serverFlag) {
//                if(!isConnected) {
//                    if (bluetoothAdapter.isEnabled()) { // 블루투스가 켜졌으면
//                        try {
//                            //add~~~~~~~~~~
//
//                            serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord("kkang_test", MY_UUID);
//                            //연결 요청이 들어오기 까지 대기
//                            socket=serverSocket.accept();
//                            // 이후에는 연결이 되었음
//
//                            outputStream = socket.getOutputStream();
//                            inputStream = socket.getInputStream();
//                            // 데이터 수신 함수 호출
//                            isConnected = true;
//                            outputText.append("connected\n");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.d("kkang", e.getMessage());
//                        }
//                        if (socket != null) {
//                            try {
//                                serverSocket.close();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }else {
//                    SystemClock.sleep(1000);
//                }
//
//            }
//        }
//    }
}