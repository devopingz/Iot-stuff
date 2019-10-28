package com.example.myapplication.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        final Button button11 = root.findViewById(R.id.button1);;
        final Button button22 = root.findViewById(R.id.button2);;
        final Button button33 = root.findViewById(R.id.button3);;
        final Button button44 = root.findViewById(R.id.button4);;
        final Button button55 = root.findViewById(R.id.button5);;
        final Button button66 = root.findViewById(R.id.button6);;
        final Button button77 = root.findViewById(R.id.button7);;
        final Button button88 = root.findViewById(R.id.button8);;
        final Button button99 = root.findViewById(R.id.button9);;
        final Button button100 = root.findViewById(R.id.button10);;

        button11.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Button button1 = button11.findViewById(R.id.button1);
                Button button2 = button22.findViewById(R.id.button2);
                Button button4 = button44.findViewById(R.id.button4);
                Button button6 = button66.findViewById(R.id.button6);
                Button button8 = button88.findViewById(R.id.button8);
                Button button10 = button100.findViewById(R.id.button10);

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        button1.setText("button1 Push");
                        button2.setText("button2 Open");
                        button4.setText("button4 Open");
                        button6.setText("button6 Open");
                        button8.setText("button8 Open");
                        button10.setText("button10 Open");
                        break;
                    case MotionEvent.ACTION_UP:
                        button1.setText("Button1");
                        button2.setText("button2");
                        button4.setText("button4");
                        button6.setText("button6");
                        button8.setText("button8");
                        button10.setText("button10");
                        break;
                }
                return false;
            }
        });

        /*button2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button button3 = button.findViewById(R.id.button3);
                Button button2 = button.findViewById(R.id.button2);
                Button button4 = button.findViewById(R.id.button4);
                Button button6 = button.findViewById(R.id.button6);
                Button button8 = button.findViewById(R.id.button8);
                Button button10 = button.findViewById(R.id.button10);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        button3.setText("button3 Push");
                        button2.setText("button2 Open");
                        button4.setText("button4 Open");
                        button6.setText("button6 Open");
                        button8.setText("button8 Open");
                        button10.setText("button10 Open");
                        break;
                    case MotionEvent.ACTION_UP:
                        button3.setText("Button3");
                        button2.setText("button2");
                        button4.setText("button4");
                        button6.setText("button6");
                        button8.setText("button8");
                        button10.setText("button10");
                        break;
                }
                return false;
            }
        });

        button3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button button5 = button.findViewById(R.id.button5);
                Button button2 = button.findViewById(R.id.button2);
                Button button4 = button. findViewById(R.id.button4);
                Button button6 = button. findViewById(R.id.button6);
                Button button8 = button. findViewById(R.id.button8);
                Button button10 = button. findViewById(R.id.button10);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        button5.setText("button5 Push");
                        button2.setText("button2 Open");
                        button4.setText("button4 Open");
                        button6.setText("button6 Open");
                        button8.setText("button8 Open");
                        button10.setText("button10 Open");
                        break;
                    case MotionEvent.ACTION_UP:
                        button5.setText("Button5");
                        button2.setText("button2");
                        button4.setText("button4");
                        button6.setText("button6");
                        button8.setText("button8");
                        button10.setText("button10");
                        break;
                }
                return false;
            }
        });

        button4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button button7 = button. findViewById(R.id.button7);
                Button button2 = button. findViewById(R.id.button2);
                Button button4 = button. findViewById(R.id.button4);
                Button button6 = button. findViewById(R.id.button6);
                Button button8 = button. findViewById(R.id.button8);
                Button button10 = button. findViewById(R.id.button10);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        button7.setText("button7 Push");
                        button2.setText("button2 Open");
                        button4.setText("button4 Open");
                        button6.setText("button6 Open");
                        button8.setText("button8 Open");
                        button10.setText("button10 Open");
                        break;
                    case MotionEvent.ACTION_UP:
                        button7.setText("Button7");
                        button2.setText("button2");
                        button4.setText("button4");
                        button6.setText("button6");
                        button8.setText("button8");
                        button10.setText("button10");

                        break;
                }
                return false;
            }
        });

        button5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button button9 = button.findViewById(R.id.button9);
                Button button2 = button.findViewById(R.id.button2);
                Button button4 = button.findViewById(R.id.button4);
                Button button6 = button.findViewById(R.id.button6);
                Button button8 = button.findViewById(R.id.button8);
                Button button10 = button. findViewById(R.id.button10);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        button9.setText("button9 Push");
                        button2.setText("button2 Open");
                        button4.setText("button4 Open");
                        button6.setText("button6 Open");
                        button8.setText("button8 Open");
                        button10.setText("button10 Open");
                        break;
                    case MotionEvent.ACTION_UP:
                        button9.setText("Button9");
                        button2.setText("button2");
                        button4.setText("button4");
                        button6.setText("button6");
                        button8.setText("button8");
                        button10.setText("button10");
                        break;
                }
                return false;
            }
        });*/

        return root;
    }

}