package com.example.CapstoneMesh.ui.setting;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.CapstoneMesh.R;
import com.example.CapstoneMesh.ui.home.HomeViewModel;

public class SettingFragment extends Fragment {

    LocationManager locationManager;
    private HomeViewModel SettingViewModel;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, @Nullable final Bundle savedInstanceState) {
        SettingViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        final View aboutButton = root.findViewById(R.id.about_button);
        final View versionButton = root.findViewById(R.id.version_button);
        final View helpButton = root.findViewById(R.id.help_button);
        final View networkButton = root.findViewById(R.id.network_button);
        final View GPSButton = root.findViewById(R.id.gps_button);
        final View displayButton = root.findViewById(R.id.display_button);
        final View scanButton = root.findViewById(R.id.scan_button);

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

        GPSButton.findViewById(R.id.image)  //이미지랑 이름설정
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.gps));
        final TextView extra1Title = GPSButton.findViewById(R.id.title);
        extra1Title.setText("GPS");
        final TextView extra1ButtonSub = GPSButton.findViewById(R.id.text);
        extra1ButtonSub.setVisibility(View.VISIBLE);
        extra1ButtonSub.setText("GPS check");    //서브타이틀

        displayButton.findViewById(R.id.image)  //이미지랑 이름설정
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.display));
        final TextView extra2Title = displayButton.findViewById(R.id.title);
        extra2Title.setText("Display");
        final TextView extra2ButtonSub = displayButton.findViewById(R.id.text);
        extra2ButtonSub.setVisibility(View.VISIBLE);
        extra2ButtonSub.setText("Display check");    //서브타이틀

        scanButton.findViewById(R.id.image)  //이미지랑 이름설정
                .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.scan));
        final TextView scanTitle = scanButton.findViewById(R.id.title);
        scanTitle.setText("Scan"); //타이틀
        final TextView scanButtonSub = scanButton.findViewById(R.id.text);
        scanButtonSub.setVisibility(View.VISIBLE);
        scanButtonSub.setText("Scan bluetooth mesh device");

        aboutButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) { // 터치 시 다이얼로그 뜸
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("DEVELOPERS").
                        setMessage(R.string.settings_about);
                // Create the AlertDialog object and return it
                builder.create().show();
                return true;
            }
        });

        versionButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) { // 터치 시 다이얼로그 뜸
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("APP VERSION").
                        setMessage(R.string.settings_version);
                // Create the AlertDialog object and return it
                builder.create().show();
                return true;
            }
        });

        helpButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) { // 터치 시 다이얼로그 뜸
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("HELP").
                        setMessage(R.string.settings_help);
                // Create the AlertDialog object and return it
                builder.create().show();
                return true;
            }
        });

        networkButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) { // 터치 시 다이얼로그 뜸
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("NETWORK").
                        setMessage(R.string.settings_network);
                // Create the AlertDialog object and return it
                builder.create().show();
                return true;
            }
        });


        GPSButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        displayButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                startActivity(intent);
            }
        });

        scanButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {


                //Intent intent = new Intent(getActivity(), ScanActivity.class);
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
            }
        });



        return root;
    }
}