package com.example.keepfresh;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private static Realm realm;
    private static Realm exp_realm;
    SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private LinearLayout container;

    private TextView roomTitleText;
    private TextView refriTitleText;
    private TextView freezeTitleText;

    private Button btn_move;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private Bitmap capturedImage;
    private int itemClassId = -1;
    private String chkModelName;
    private int chkModelStorage = -1;
    private Date chkModelDate;

    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        roomTitleText = (TextView) findViewById(R.id.roomTitleText);
        refriTitleText = (TextView) findViewById(R.id.refriTitleText);
        freezeTitleText = (TextView) findViewById(R.id.freezeTitleText);

        RealmConfiguration expConfig = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        exp_realm = Realm.getInstance(expConfig);
        realm = Realm.getDefaultInstance();


        btn_move = findViewById(R.id.button3);
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    openCameraIntent();
                }
            }
        });

        btn_move = findViewById(R.id.button4);
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , item_information_typing.class);
                startActivity(intent); //액티비티 이동
            }
        });
        btn_move = findViewById(R.id.button5);
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SetAlert.class);
                startActivity(intent);
            }
        });

        if(!MyApplication.initExp){
            // .json파일의 정보를 읽어서 ExpList 테이블 생성
            parsingItemInfo();

            MyApplication.initExp = true;
        }

        showResult();
        chkLayout();

        // 카메라 촬영 이후 동작
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        capturedImage = (Bitmap) data.getExtras().get("data");
                        try {
                            ModelServer.sendPhotoToServer(capturedImage, new ModelServer.Callback() {
                                @Override
                                public void onSuccess(String response) {
                                    itemClassId = parsingModelResult(response);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (itemClassId != -1) {
                                                setInputItemInfo(itemClassId);
                                            } else {
                                                noDetectionsAlert();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(IOException e) {
                                    // 오류 처리
                                }
                            });

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "한 번 더 뒤로가기 버튼을 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();

        new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 아무것도 수행하지 않음
            }

            @Override
            public void onFinish() {
                doubleBackToExitPressedOnce = false;
            }
        }.start();
    }

    // expList에 정보 넣기 위한 포맷 설정(모델에서 인식할 클래스에 대한 유통기한)
    public void addExpList(int item_num, String name, int recommend_storage, String[] storage_info, int[] exp_info){

        //이미 있으면 생성하지 않음
        if(exp_realm.where(ExpList.class).equalTo("item_num", item_num).findAll().size() != 0)
            return;

        exp_realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ExpList expList = exp_realm.createObject(ExpList.class);
                expList.setItem_num(item_num);
                expList.setName(name);
                expList.setRecommend_storage(recommend_storage);
                for (int i = 0; i < 3; i++) {
                    expList.setStorage_info(storage_info[i], i);
                    expList.setExp_info(exp_info[i], i);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exp_realm.close();
        realm.close();
    }

    public void parsingItemInfo(){

        int item_num;
        String name;
        int recommendStore;
        String[] storageInfoArray = new String[3];
        int[] expInfoArray = new int[3];

        AssetManager assetManager = getAssets();
        try {
            InputStream filePath = assetManager.open("itemInfo.json");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePath))) {
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }

                JSONArray jsonArray = new JSONArray(jsonContent.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    item_num = ((JSONObject) jsonObject).optInt("item_no");
                    name = ((JSONObject) jsonObject).optString("item_name");
                    recommendStore = jsonObject.optInt("recommend_store");
                    storageInfoArray[0] = jsonObject.optString("storage_info_0");
                    storageInfoArray[1] = jsonObject.optString("storage_info_1");
                    storageInfoArray[2] = jsonObject.optString("storage_info_2");
                    expInfoArray[0] = jsonObject.optInt("exp_info_0");
                    expInfoArray[1] = jsonObject.optInt("exp_info_1");
                    expInfoArray[2] = jsonObject.optInt("exp_info_2");

                    addExpList(item_num, name, recommendStore, storageInfoArray, expInfoArray);

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int parsingModelResult(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray resultArray = jsonObject.getJSONArray("result");
            if (resultArray.length() >= 0) {
                return resultArray.getInt(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void setInputItemInfo(int inputId) {
        if(exp_realm.where(ExpList.class).equalTo("item_num", inputId).findAll().size() != 0) {
            ExpList ExpTuple = exp_realm.where(ExpList.class).equalTo("item_num", inputId).findAll().first();
            chkModelName = ExpTuple.getName();

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.popup_set_storage);

            Button button1 = dialog.findViewById(R.id.button_info1);
            Button button2 = dialog.findViewById(R.id.button_info2);
            Button button3 = dialog.findViewById(R.id.button_info3);

            String buttonText1 = ExpTuple.getStorage_info(0) +
                    "\n보관 : " + (ExpTuple.getExp_info(0) == -1? "불가능" : ExpTuple.getExp_info(0) + "일");
            String buttonText2 = ExpTuple.getStorage_info(1) +
                    "\n보관 : " + (ExpTuple.getExp_info(1) == -1? "불가능" : ExpTuple.getExp_info(1) + "일");
            String buttonText3 = ExpTuple.getStorage_info(2) +
                    "\n보관 : " + (ExpTuple.getExp_info(2) == -1? "불가능" : ExpTuple.getExp_info(2) + "일");

            button1.setText(buttonText1);
            button2.setText(buttonText2);
            button3.setText(buttonText3);


            // 버튼추가
            if(ExpTuple.getExp_info(0) > 0) {
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { showStorageDialog(0, ExpTuple); }

                });
            }

            if(ExpTuple.getExp_info(1) > 0) {
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showStorageDialog(1, ExpTuple);
                    }
                });
            }

            if(ExpTuple.getExp_info(2) > 0) {
                button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showStorageDialog(2, ExpTuple);
                    }
                });
            }

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); // 전체 화면 크기로 설정
            dialog.show();
        }


    }

    public void runModelIntent() {
        Intent intent = new Intent(MainActivity.this, item_information_typing.class);
        intent.putExtra("itemName", chkModelName);
        intent.putExtra("itemStorage", chkModelStorage);
        intent.putExtra("itemExpDate", chkModelDate);
        startActivity(intent);
    }

    public void noDetectionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("인식할 수 없는 물체입니다.");

        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // ItemList에 저장된 식품정보 불러오기
    private void showResult(){
        RealmResults<ItemList> results = realm.where(ItemList.class).findAll();

        results = results.sort("expire_date", Sort.ASCENDING);

        for(ItemList data : results){

            setContainer(data.getStorage());
            chkContainer();

            final Button button = new Button(this);
            button.setText(data.toString());
            container.addView(button);
            if(container.getChildAt(0) != null)
                container.getChildAt(0).setVisibility(View.VISIBLE);

            final String id = data.getId();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //삭제하시겠습니까? 메세지
                    //예 아니오 --> 예 클릭 시 해당 데이터베이스 삭제
                    showMessage(id, button);
                }
            });
        }
    }

    private void showMessage(final String id, final Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("품목 삭제");
        builder.setMessage("삭제하시겠습니까?");

        //예 클릭 시
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //해당 id를 갖는 tuple 삭제
                try {
                    final RealmResults<ItemList> results = realm.where(ItemList.class).equalTo("id", id).findAll();

                    setContainer((results.get(0).getStorage()));

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            results.deleteFirstFromRealm();
                            container.removeView(button);
                            chkContainer();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                container.invalidate();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    public void openCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    public void setContainer(int storage) {
        if(storage == 0) {
            container = (LinearLayout) findViewById(R.id.room_list);
        } else if(storage == 1) {
            container = (LinearLayout) findViewById(R.id.refri_list);
        } else if(storage == 2) {
            container = (LinearLayout) findViewById(R.id.freeze_list);
        } else {
            // 미분류
            container = (LinearLayout) findViewById(R.id.freeze_list);
        }
    }

    public void chkContainer() {
        if(container == null) {
            return;
        }

        if(container.getChildCount() <= 1) {
            container.getChildAt(0).setVisibility(View.INVISIBLE);
        } else {
            container.getChildAt(0).setVisibility(View.VISIBLE);
        }
        chkLayout();
    }

    public void chkLayout() {
        LinearLayout roomListLayout = findViewById(R.id.room_list);
        LinearLayout refriListLayout = findViewById(R.id.refri_list);
        LinearLayout freezeListLayout = findViewById(R.id.freeze_list);

        RelativeLayout.LayoutParams roomListLayoutParams = (RelativeLayout.LayoutParams) roomListLayout.getLayoutParams();
        RelativeLayout.LayoutParams refriListLayoutParams = (RelativeLayout.LayoutParams) refriListLayout.getLayoutParams();
        RelativeLayout.LayoutParams freezeListLayoutParams = (RelativeLayout.LayoutParams) freezeListLayout.getLayoutParams();

        /* room:true, refri:true, freese:true */
        if(roomTitleText.getVisibility() == View.VISIBLE) {
            /* room:true */
            if(refriTitleText.getVisibility() == View.VISIBLE) {
                /* room:true, refri:true */
                roomListLayoutParams.addRule(RelativeLayout.BELOW, R.id.list_layout);
                refriListLayoutParams.addRule(RelativeLayout.BELOW, R.id.room_list);
                freezeListLayoutParams.addRule(RelativeLayout.BELOW, R.id.refri_list);
            } else {
                /* room:true, refri:false*/
                roomListLayoutParams.addRule(RelativeLayout.BELOW, R.id.list_layout);
                refriListLayoutParams.removeRule(RelativeLayout.BELOW);
                freezeListLayoutParams.addRule(RelativeLayout.BELOW, R.id.room_list);
            }
        } else {
            /* room:false */
            if(refriTitleText.getVisibility() == View.VISIBLE) {
                /* room:false, refri:true*/
                roomListLayoutParams.removeRule(RelativeLayout.BELOW);
                refriListLayoutParams.addRule(RelativeLayout.BELOW, R.id.list_layout);
                freezeListLayoutParams.addRule(RelativeLayout.BELOW, R.id.refri_list);
            } else {
                /* room:false, refri:false*/
                roomListLayoutParams.removeRule(RelativeLayout.BELOW);
                refriListLayoutParams.removeRule(RelativeLayout.BELOW);
                freezeListLayoutParams.addRule(RelativeLayout.BELOW, R.id.list_layout);
            }
        }

    }

    public void showStorageDialog(int num, ExpList ExpTuple) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("보관방법 선택");
        builder.setMessage((num == 0 ? "상온보관" : num == 1 ? "냉장보관" : "냉동보관") + "을 선택하시겠습니까?");

        //예 클릭 시
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chkModelStorage = num;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, ExpTuple.getExp_info(chkModelStorage));
                chkModelDate = calendar.getTime();
                runModelIntent();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chkModelStorage = -1;
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    // 렐름 정보 초기화 함수
    public static void clearData() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });

        exp_realm.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });

        realm.close();
        exp_realm.close();
    }

}