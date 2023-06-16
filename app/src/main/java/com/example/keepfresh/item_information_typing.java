package com.example.keepfresh;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

//식품정보 입력 페이지
public class item_information_typing extends AppCompatActivity {

    private Button cancel_btn;
    private Button submit_btn;

    private Button btn_move;

    private Realm realm;
    private Realm exp_realm;

    private EditText dateEditText;
    private PopupWindow popupWindow;
    private Calendar selectedCalendar = Calendar.getInstance();

    SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_information_typing);

        btn_move =findViewById(R.id.cancel_btn);
        dateEditText = findViewById(R.id.expiration_id);

        // 유통기한 입력 -> calendarView로 달력에서 입력받음
       dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarPopup();
            }
        });

        RealmConfiguration expConfig = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        realm = Realm.getInstance(expConfig);
        exp_realm = Realm.getDefaultInstance();

        cancel_btn = findViewById(R.id.cancel_btn);
        submit_btn = findViewById(R.id.submit_btn);


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(item_information_typing.this , MainActivity.class);
                startActivity(intent); //액티비티 이동
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            EditText itemEditText = findViewById(R.id.item_id);
            Spinner spinner = findViewById(R.id.Spinner);

            @Override
            public void onClick(View v) {
                String nameText = itemEditText.getText().toString();
                int selectedStorage = 0;

                /************************************
                 * TODO 보관방법 spiner 추가되면 바꾸기 *
                 ***********************************/
                /*
                String selectedStorageText = spinner.getSelectedItem().toString();

                if(selectedStorageText == "실온보관") {
                    selectedStorage = 0;
                } else if(selectedStorageText == "냉장보관") {
                    selectedStorage = 1;
                } else if(selectedStorageText == "냉동보관") {
                    selectedStorage = 2;
                }

                 */
                Log.i("addd" ,nameText + selectedStorage + selectedCalendar.getTime().toString());
                createTuple(nameText, selectedStorage, selectedCalendar.getTime());

                Intent intent = new Intent(item_information_typing.this , MainActivity.class);
                startActivity(intent); //액티비티 이동
            }
        });

    }


    public void createTuple(final String name, final int storage, final Date selectedDate){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ItemList itemList = realm.createObject(ItemList.class);

                /*******input_date 설정*******/
                itemList.setInputDate(new Date());

                /*******name 설정*******/
                itemList.setName(name);

                /*******id 설정*******/
                String id = idFormat.format(itemList.getInputDate()) + String.valueOf(itemList.getStorage());
                itemList.setId(id);

                /*******storage 설정*******/
                itemList.setStorage(storage);

                /*******expire_date 설정*******/
                itemList.setExpireDate(selectedDate);
            }
        });
    }

    private void showCalendarPopup() {
        // PopupWindow를 생성하고 크기를 조정
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_calendar, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // PopupWindow의 위치 설정
        int[] location = new int[2];
        dateEditText.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1] + dateEditText.getHeight();
        popupWindow.showAtLocation(dateEditText, Gravity.NO_GRAVITY, x, y);

        // CalendarView의 날짜 선택 이벤트 처리
        CalendarView calendarView = popupView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 선택된 날짜 처리
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                dateEditText.setText(selectedDate);
                selectedCalendar.set(year, month, dayOfMonth);
                popupWindow.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exp_realm.close();
        realm.close();
    }

}