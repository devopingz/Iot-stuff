package com.example.myapplication.ui.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.example.myapplication.ui.home.HomeViewModel;


public class MessageFragment extends Fragment {

    private HomeViewModel MessageViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, @Nullable final Bundle savedInstanceState) {
        MessageViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_message, container, false);

        final View aboutButton = root.findViewById(R.id.about_button);
        final View versionButton = root.findViewById(R.id.version_button);
        final View helpButton = root.findViewById(R.id.help_button);
        final View networkButton = root.findViewById(R.id.network_button);
        final View extra1Button = root.findViewById(R.id.extra1_button);
        final View extra2Button = root.findViewById(R.id.extra2_button);

        aboutButton.findViewById(R.id.image)  //이미지랑 이름설정
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.about));
        final TextView aboutTitle = aboutButton.findViewById(R.id.title);
        aboutTitle.setText(R.string.aboutButton); //타이틀
        final TextView aboutButtonSub = aboutButton.findViewById(R.id.text);
        aboutButtonSub.setVisibility(View.VISIBLE);
        aboutButtonSub.setText(R.string.aboutButton_Sub);    //서브타이틀

        versionButton.findViewById(R.id.image)  //이미지랑 이름설정
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.version));
        final TextView versionTitle = versionButton.findViewById(R.id.title);
        versionTitle.setText(R.string.versionButton);
        final TextView versionButtonSub = versionButton.findViewById(R.id.text);
        versionButtonSub.setVisibility(View.VISIBLE);
        versionButtonSub.setText(R.string.versionButton_Sub);    //서브타이틀

        helpButton.findViewById(R.id.image)  //이미지랑 이름설정
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.help));
        final TextView helpTitle = helpButton.findViewById(R.id.title);
        helpTitle.setText(R.string.helpButton);

        networkButton.findViewById(R.id.image)  //이미지랑 이름설정
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.network));
        final TextView networktTitle = networkButton.findViewById(R.id.title);
        networktTitle.setText(R.string.networkButton);

        extra1Button.findViewById(R.id.image)  //이미지랑 이름설정
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.network));
        final TextView extra1Title = extra1Button.findViewById(R.id.title);
        extra1Title.setText("extra1");
        final TextView extra1ButtonSub = extra1Button.findViewById(R.id.text);
        extra1ButtonSub.setVisibility(View.VISIBLE);
        //extra1ButtonSub.setText(R.string.versionButton_Sub);    //서브타이틀

        extra2Button.findViewById(R.id.image)  //이미지랑 이름설정
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.network));
        final TextView extra2Title = extra2Button.findViewById(R.id.title);
        extra2Title.setText("extra2");
        final TextView extra2ButtonSub = extra2Button.findViewById(R.id.text);
        extra2ButtonSub.setVisibility(View.VISIBLE);
        //extra2ButtonSub.setText(R.string.versionButton_Sub);    //서브타이틀

        aboutButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) { // 터치 시 about 다이얼로그 뜸
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("DEVELOPERS").
                        setMessage(R.string.settings_about);
                // Create the AlertDialog object and return it
                builder.create().show();
                return true;
            }
        });

        versionButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) { // 터치 시 about 다이얼로그 뜸
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("APP VERSION").
                        setMessage(R.string.settings_version);
                // Create the AlertDialog object and return it
                builder.create().show();
                return true;
            }
        });

        helpButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) { // 터치 시 about 다이얼로그 뜸
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("HELP").
                        setMessage(R.string.settings_help);
                // Create the AlertDialog object and return it
                builder.create().show();
                return true;
            }
        });

        networkButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) { // 터치 시 about 다이얼로그 뜸
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("NETWORK").
                        setMessage(R.string.settings_network);
                // Create the AlertDialog object and return it
                builder.create().show();
                return true;
            }
        });

        return root;
    }
}