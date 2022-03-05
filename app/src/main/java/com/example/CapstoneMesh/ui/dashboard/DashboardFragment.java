package com.example.CapstoneMesh.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.CapstoneMesh.MainActivity;
import com.example.CapstoneMesh.PacketData;
import com.example.CapstoneMesh.R;
import com.example.CapstoneMesh.RadioActivity;

import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    View mainDevButton = null;
    PacketData packetData = null;
    MainActivity mainActivity = null;
    CardView cardView = null;
    ListView friendsList = null;
    ArrayAdapter<String> friends = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mainActivity = (MainActivity)getActivity();
        packetData = ((MainActivity)getActivity()).packetData;
        mainDevButton = root.findViewById(R.id.connected_button);

        // 메인 디바이스 클릭 시 ////////////
        mainDevButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 장치의 세팅 페이지로 넘어감 
                Intent intent = new Intent(getActivity(), RadioActivity.class);

                packetData = ((MainActivity)getActivity()).packetData;
                if(packetData != null) {
                    intent.putExtra("number", 0);
                    intent.putExtra("policy", packetData.getDev()[0].getPolicy());
                    intent.putExtra("state", packetData.getDev()[0].getState());
                    startActivityForResult(intent,10);
                }
            }
        });
        //// 간접 연결된 장비 목록 //////////////////
        friends = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        friendsList = (ListView)root.findViewById(R.id.friendsList);
        friendsList.setAdapter(friends);
        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), RadioActivity.class);
                packetData = ((MainActivity)getActivity()).packetData;
                if(packetData != null && packetData.getDev()[i] != null) {
                    intent.putExtra("number", i);
                    intent.putExtra("policy", packetData.getDev()[i].getPolicy());
                    intent.putExtra("state", packetData.getDev()[i].getState());
                    startActivityForResult(intent,10);
                }
            }
        });
        return root;
    }

    @Override // 선택창에서 선택 다하고 돌아오는 경우 , policy, state를 보냄.
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 10 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                packetData = ((MainActivity)getActivity()).packetData; // 객체이므로 콜바이 레퍼런스 
                int i = data.getIntExtra("numOfFriend", 0);
                
                int currentPolicy = packetData.getDev()[i].getPolicy();
                packetData.getDev()[i].setPolicy(data.getIntExtra("policy_return", currentPolicy));
                
                int currentState = packetData.getDev()[i].getState();
                packetData.getDev()[i].setState(data.getIntExtra("state_return", currentState));
                
                ((MainActivity)getActivity()).connectFragment.sendPacket(packetData);
                mainActivity.connectFragment.processWithESP(); // 상태 새로고침
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        packetData = ((MainActivity)getActivity()).packetData;
        if(packetData != null) {
            TextView macMain = (TextView)mainDevButton.findViewById(R.id.title);
            TextView idMain = (TextView)mainDevButton.findViewById(R.id.text);
            idMain.setVisibility(View.VISIBLE);

            StringBuffer mac = new StringBuffer();
            for(int i=0; i<6; i++) {
                mac.append(String.format("%x", packetData.getDev()[0].getMac()[i]));
            }
            macMain.setText(mac.toString());
            idMain.setText(packetData.getDev()[0].getId()+"");

            for(int i=1; i<packetData.getNum_dev(); i++) {
                StringBuffer stringBuffer = new StringBuffer();
                byte[] macFriends =packetData.getDev()[i].getMac();
                for(int j=0; j<6; j++ ) {
                    stringBuffer.append(String.format("%x", macFriends[j]));
                }
                friends.add(stringBuffer.toString() + "\n" + packetData.getDev()[i].getId());
                friends.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPause() {
        friends.clear();
        super.onPause();
    }
}