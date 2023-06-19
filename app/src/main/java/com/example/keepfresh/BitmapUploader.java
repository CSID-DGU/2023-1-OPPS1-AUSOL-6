package com.example.keepfresh;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUploader {

    private static final String LINE_FEED = "\r\n";
    private static final String BOUNDARY = "---------------------------1234567890";

    public void uploadBitmap(Bitmap bitmap, String serverUrl) throws IOException {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(serverUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            outputStream = new DataOutputStream(connection.getOutputStream());

            // Add the bitmap data as a part
            addBitmapPart(outputStream, bitmap, "image", "image.jpg");

            // Send multipart form data
            outputStream.writeBytes("--" + BOUNDARY + "--" + LINE_FEED);
            outputStream.flush();

            // Get the server response
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                // Process the response if needed
            } else {
                // Handle the error response
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void addBitmapPart(DataOutputStream outputStream, Bitmap bitmap, String paramName, String fileName) throws IOException {
        outputStream.writeBytes("--" + BOUNDARY + LINE_FEED);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                paramName + "\"; filename=\"" +
                fileName + "\"" + LINE_FEED);
        outputStream.writeBytes("Content-Type: image/jpeg" + LINE_FEED);
        outputStream.writeBytes(LINE_FEED);

        // Convert bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        // Encode the byte array using Base64
        String encodedImage = Base64.encodeToString(bitmapData, Base64.DEFAULT);
        outputStream.write(encodedImage.getBytes());

        outputStream.writeBytes(LINE_FEED);
    }
}