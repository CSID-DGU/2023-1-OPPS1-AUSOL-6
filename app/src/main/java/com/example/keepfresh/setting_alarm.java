package com.example.keepfresh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

//알람 설정 페이지
public class setting_alarm extends AppCompatActivity {
    SharedPreferences spref;
    SharedPreferences.Editor editor;
    Button btn01, btn02, btn03, btn_cancel, btn_apply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_alarm);

    }
}