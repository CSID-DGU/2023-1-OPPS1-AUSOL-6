package com.example.keepfresh;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModelServer {

    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

    public interface Callback {
        void onSuccess(String response);
        void onError(IOException e);
    }

    public static void sendPhotoToServer(final Bitmap photoBitmap, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = sendPhotoToServer(photoBitmap);
                    callback.onSuccess(response);
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError(e);
                }
            }
        }).start();
    }

    public static String sendPhotoToServer(Bitmap photoBitmap) throws IOException {
        // 서버의 엔드포인트 URL
        String serverUrl = "http://13.237.155.43/predict";

        // OkHttpClient 생성
        OkHttpClient client = new OkHttpClient();

        // Bitmap을 ByteArray로 변환
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // POST 요청 생성
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "photo.jpg", RequestBody.create(byteArray, MediaType.parse("multipart/form-data")))
                .build();

        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();


        // 요청 실행 및 응답 처리
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }


}
