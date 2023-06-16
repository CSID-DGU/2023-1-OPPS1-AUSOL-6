package com.example.keepfresh;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Button;

//알람 설정 페이지
public class setting_alarm extends AppCompatActivity {


    private Button btn_move;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_alarm);

        btn_move =findViewById(R.id.button12);

        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(setting_alarm.this , MainActivity.class);
                startActivity(intent); //액티비티 이동
            }
        });

    }
}