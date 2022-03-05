package com.example.CapstoneMesh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class RadioActivity extends AppCompatActivity {

    MainActivity mainActivity = null;
    private int groupOneCheckedId;
    private int groupTwoCheckedId;
    private int groupThreeCheckedid;
    private int numOfFriend;

    CheckBox button1 = null;
    CheckBox button2 = null;
    CheckBox button3 = null;
    CheckBox button4 = null;
    CheckBox button5 = null;
    CheckBox button6 = null;
    CheckBox button7 = null;
    CheckBox button8 = null;
    CheckBox button9 = null;
    CheckBox button10 = null;
    CheckBox button11 = null;
    CheckBox button12 = null;
    CheckBox button13 = null;
    CheckBox button14 = null;
    CheckBox button15 = null;
    CheckBox button16 = null;
    CheckBox button17 = null;
    CheckBox button18 = null;

    CheckBox isChangeState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio);
        ///////////////
        Bundle bundle = getIntent().getExtras();

        button1 = findViewById(R.id.radioButton1);
        button2 = findViewById(R.id.radioButton2);
        button3 = findViewById(R.id.radioButton3);
        button4 = findViewById(R.id.radioButton4);
        button5 = findViewById(R.id.radioButton5);
        button6 = findViewById(R.id.radioButton6);
        button7 = findViewById(R.id.radioButton7);
        button8 = findViewById(R.id.radioButton8);
        button9 = findViewById(R.id.radioButton9);
        button10 = findViewById(R.id.radioButton10);
        button11 = findViewById(R.id.radioButton11);
        button12 = findViewById(R.id.radioButton12);
        button13 = findViewById(R.id.radioButton13);
        button14 = findViewById(R.id.radioButton14);
        button15 = findViewById(R.id.radioButton15);
        button16 = findViewById(R.id.radioButton16);
        button17 = findViewById(R.id.radioButton17);
        button18 = findViewById(R.id.radioButton18);
        isChangeState = findViewById(R.id.isChangeState);

        Intent intent = getIntent();
        int policy = intent.getIntExtra("policy",0);
        int state = intent.getIntExtra("state", 0);
        numOfFriend = intent.getIntExtra("number", 0);

        //읽기전용
        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        button5.setEnabled(false);
        button6.setEnabled(false);
///////////////////////////////////////////////////
        if((1 & state) == 1) {
            button1.setChecked(true);
        }
        if((1 & state >> 1) == 1) {
            button2.setChecked(true);
        }
        if((1 & state >> 2) == 1) {
            button3.setChecked(true);
        }
        if((1 & state >> 3) == 1) {
            button4.setChecked(true);
        }
        if((1 & state >> 4) == 1) {
            button5.setChecked(true);
        }
        if((1 & state >> 5) == 1) {
            button6.setChecked(true);
        }
        ////////////////////////////////
        if((1 & policy) == 1) {
            button7.setChecked(true);
        }
        if((1 & policy>>>1) == 1) {
            button8.setChecked(true);
        }
        if((1 & policy>>>2) == 1) {
            button9.setChecked(true);
        }
        if((1 & policy>>>3) == 1) {
            button10.setChecked(true);
        }
        if((1 & policy>>>4) == 1) {
            button11.setChecked(true);
        }
        if((1 & policy>>>5) == 1) {
            button12.setChecked(true);
        }


    }

    @Override
    public void onBackPressed() {

        Intent intent = getIntent();

        int newPolicy = 0;
        if(button13.isChecked()) {
           newPolicy |= 1;
        }
        if(button14.isChecked()) {
            newPolicy |= (1<<1);
        }
        if(button15.isChecked()) {
            newPolicy |= (1<<2);
        }
        if(button16.isChecked()) {
            newPolicy |= (1<<3);
        }
        if(button17.isChecked()) {
            newPolicy |= (1<<4);
        }
        if(button18.isChecked()) {
            newPolicy |= (1<<5);
        }
        Intent returnIntent = getIntent();
        returnIntent.putExtra("policy_return", newPolicy);


        if(isChangeState.isChecked()) {

            int newState = 0;
            if (button7.isChecked()) {
                newState |= 1;
            }
            if (button8.isChecked()) {
                newState |= (1<<1);
            }
            if (button9.isChecked()) {
                newState |= (1<<2);
            }
            if (button10.isChecked()) {
                newState |= (1<<3);
            }
            if (button11.isChecked()) {
                newState |= (1<<4);
            }
            if (button12.isChecked()) {
                newState |= (1<<5);
            }
            returnIntent.putExtra("state_return", newState);
        }
        returnIntent.putExtra("numOfFriend", numOfFriend);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }
}