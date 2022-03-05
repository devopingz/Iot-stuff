package com.example.CapstoneMesh.ui.dashboard2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.CapstoneMesh.MainActivity;
import com.example.CapstoneMesh.R;



public class Dashboard2Fragment extends Fragment {

    private Dashboard2ViewModel dashboard2ViewModel;
    private int groupOneCheckedId;
    private int groupTwoCheckedId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard2, container, false);

        //라디오 그룹 추가
        final RadioGroup radioGroup1 = root.findViewById(R.id.radioGroup1);
        final RadioGroup radioGroup2 = root.findViewById(R.id.radioGroup2);

        final RadioButton button11 = root.findViewById(R.id.radioButton1);
        final RadioButton button22 = root.findViewById(R.id.radioButton2);
        final RadioButton button33 = root.findViewById(R.id.radioButton3);
        final RadioButton button44 = root.findViewById(R.id.radioButton4);
        final RadioButton button55 = root.findViewById(R.id.radioButton5);
        final RadioButton button66 = root.findViewById(R.id.radioButton6);
        final RadioButton button77 = root.findViewById(R.id.radioButton7);
        final RadioButton button88 = root.findViewById(R.id.radioButton8);
        final RadioButton button99 = root.findViewById(R.id.radioButton9);
        final RadioButton button100 = root.findViewById(R.id.radioButton10);


        //라디오 그룹 리스너 등록
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                //if(checkedId == R.id.)
                //int 에 기록
                groupOneCheckedId = checkedId;

            }
        });
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                //if(checkedId == R.id.)
                //네트워크로 전송
                groupTwoCheckedId = checkedId;
                ((MainActivity)getActivity()).sendDataUsingHomeFragment(groupOneCheckedId,groupTwoCheckedId);
            }
        });

        return root;
    }


}