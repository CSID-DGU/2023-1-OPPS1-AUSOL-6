package com.example.keepfresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

//식품정보 입력 페이지
public class item_information_typing extends AppCompatActivity {

    private Button cancel_btn;
    private Button submit_btn;
    private ConstraintLayout rootView;
    private EditText itemEditText;
    private Spinner spinner;

    private Realm realm;
    private Realm exp_realm;

    // 유통기한 화면 출력용 EditText
    private EditText dateEditText;

    // dateEditText 클릭시 나오는 popup (+ calendarView)
    private PopupWindow popupWindow;

    // popup에서 날짜 선택 시 java에서도 calendar생성해서 날짜 저장
    private Calendar selectedCalendar = Calendar.getInstance();

    private boolean isTupleCreated = false;

    SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_information_typing);

        rootView = findViewById(R.id.rootView);
        dateEditText = findViewById(R.id.expiration_id);
        itemEditText = findViewById(R.id.item_id);
        spinner = findViewById(R.id.Spinner);

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

        // 식품명 ExitText 클릭 후 다른곳 클릭시 키보드 없애기
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // EditText의 포커스 제거
                itemEditText.clearFocus();

                // 키보드 숨기기
                InputMethodManager imm = (InputMethodManager ) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return false;
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(item_information_typing.this , MainActivity.class);
                startActivity(intent); //액티비티 이동
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {


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

                if(isTupleCreated) {
                    Intent intent = new Intent(item_information_typing.this, MainActivity.class);
                    startActivity(intent); //액티비티 이동
                }
            }
        });

    }


    public void createTuple(final String name, final int storage, final Date selectedDate){

        if(name.isBlank()) {
            showInputMessage("식품명을 입력 하세요.");
            return;
        }

        if(chkValidDate(selectedDate)) {
            showInputMessage("유통기한은 오늘 날짜 이후로 설정해야 합니다.");
            return;
        }

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

        isTupleCreated = true;
    }

    // 유통기한이 현재 시간 이전 인지 확인
    private boolean chkValidDate(Date inputDate) {
        Date currentDate = new Date();

        if (inputDate.before(currentDate)) {
            return true;
        }
        else {
            return false;
        }
    }

    // 유통기한 선택 팝업
    private void showCalendarPopup() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dateEditText.getWindowToken(), 0);

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
                selectedCalendar.set(year, month, dayOfMonth);
                dateEditText.setText(dateFormat.format(selectedCalendar.getTime()));


                popupWindow.dismiss();
            }
        });
    }

    // 입력된 내용 없을 경우 메시지 출력
    private void showInputMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);

        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exp_realm.close();
        realm.close();
    }

}