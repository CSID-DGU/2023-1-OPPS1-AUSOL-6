package com.example.keepfresh;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.keepfresh.databinding.ActivityMainBinding;

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
import java.util.Stack;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE = 101;

    private static Realm realm;
    private static Realm exp_realm;
    SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMddHHmmss");


    private ActivityMainBinding binding;

    private LinearLayout container;

    private TextView roomTitleText;
    private TextView refriTitleText;
    private TextView freezeTitleText;

    private Button btn_move;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private Bitmap capturedImage;


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

            // 테스트 튜플 추가 테스트
            createTuple("사과", 1);
            createTuple("바나나", 0);
            createTuple("귤", 2);
            createTuple("팽이버섯", 1);

            MyApplication.initExp = true;

            // Test 실행시마다 튜플 추가하기 때문에 지워주기
            // clearData();
        }

        showResult();

        // Test 이미지 확인용 view
        //ImageView imageView = findViewById(R.id.testImage);

        // 카메라 촬영 이후 동작
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            capturedImage = (Bitmap) data.getExtras().get("data");

                            // Test 이미지 확인용 코드
                            //imageView.setImageBitmap(capturedImage);

                            // TODO 사진 입력시 모델로 전송 함수 실행
                        }
                    }
                }
            });

    }

    // expList에 정보 넣기 위한 포맷 설정(모델에서 인식할 클래스에 대한 유통기한)
    public void addExpList(String name, int recommend_storage, String[] storage_info, int[] exp_info){

        //이미 있으면 생성하지 않음
        if(exp_realm.where(ExpList.class).equalTo("name", name).findAll().size() != 0)
            return;

        exp_realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ExpList expList = exp_realm.createObject(ExpList.class);
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
                    name = ((JSONObject) jsonObject).optString("item_name");
                    recommendStore = jsonObject.optInt("recommend_store");
                    storageInfoArray[0] = jsonObject.optString("storage_info_0");
                    storageInfoArray[1] = jsonObject.optString("storage_info_1");
                    storageInfoArray[2] = jsonObject.optString("storage_info_2");
                    expInfoArray[0] = jsonObject.optInt("exp_info_0");
                    expInfoArray[1] = jsonObject.optInt("exp_info_1");
                    expInfoArray[2] = jsonObject.optInt("exp_info_2");

                    addExpList(name, recommendStore, storageInfoArray, expInfoArray);

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // DB에 정보 추가할 튜플 생성
    // 모델 이용한 유통기한 추가
    public void createTuple(final String name, final int storage){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ItemList itemList = realm.createObject(ItemList.class);

                /*******input_date 설정*******/
                itemList.setInputDate(new Date());

                /*******name 설정*******/
                itemList.setName(name);

                /*******storage 설정*******/
                itemList.setStorage(storage);

                /*******id 설정*******/
                // 시간 + 저장방법
                String id = idFormat.format(itemList.getInputDate()) + String.valueOf(itemList.getStorage());
                itemList.setId(id);

                //나머지 data는 expList table을 참고해서 설정함
                ExpList expList = exp_realm.where(ExpList.class).equalTo("name", name).findAll().first();

                /*******expire_date 설정*******/
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(itemList.getInputDate());
                calendar.add(Calendar.DATE, expList.getExp_info(storage));
                itemList.setExpireDate(calendar.getTime());
            }
        });
    }

    // ItemList에 저장된 식품정보 불러오기
    private void showResult(){
        RealmResults<ItemList> results = realm.where(ItemList.class).findAll();

        Log.i("aaa", results.toString());

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
        if(container == null) {
            return;
        }

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