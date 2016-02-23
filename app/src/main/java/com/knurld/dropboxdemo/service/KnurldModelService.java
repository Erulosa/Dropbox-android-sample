package com.knurld.dropboxdemo.service;

import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by andyshear on 2/15/16.
 */
public abstract class KnurldModelService {
    final static private String DEVELOPER_ID = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MDQ4MTY5MDUsInJvbGUiOiJhZG1pbiIsImlkIjoiMDQ5MTg0NDUxMzc4NTgzODg1MmQ1NTBmOTIwNjk5ZjgiLCJ0ZW5hbnQiOiJ0ZW5hbnRfbXJwdGF4M25tejVobzRsMm5ycmc2MnRibTUzdGc1ZHduNXZob3ozam5yMmdpM3J2bzV5cSsrKysiLCJuYW1lIjoiYWRtaW4ifQ.2EGZ3bXCq8yaawru5uzwcqEjEHhVs96MlD9HGnF4JQZmCwc2T3efca1F480dVcQSVKc7wwOk61NlgkiDB_6NXw";
    private static String CLIENT_TOKEN;

    private static final String LINE_FEED = "\r\n";

    public static void setClientToken(String clientToken) {
        CLIENT_TOKEN = clientToken;
    }

    public abstract void buildFromResponse(String response);
    public abstract void buildFromId(String id);
    public abstract String index();
    public abstract String show(String urlParam);
    public abstract String create(String body);
    public abstract String update(String... params);

    protected String getRequest(final String... params) {

        final String[] response = {null, null};

        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                response[0] = GET(params);
            }
        });
        requestThread.start();

        try {
            requestThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response[0];
    }

    protected String postRequest(final String... params) {

        final String[] response = {null, null};

        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                response[0] = POST(params);
            }
        });
        requestThread.start();

        try {
            requestThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response[0];
    }

    public static String GET(String... params) {
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        String method = params[0];
        String urlStringParams = (params[1] == null) ? "" : "/" + params[1];
        String[] result = {"", ""};

        String urlString = "https://api.knurld.io/v1/" + method + urlStringParams;

        if (method.contains("verification") && urlStringParams == "") {
            method = "setupVerification";
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Developer-Id", "Bearer: " + DEVELOPER_ID);
            urlConnection.setRequestProperty("Authorization", "Bearer " + CLIENT_TOKEN);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            int HttpResult = urlConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println("" + sb.toString());
            } else{
                System.out.println(urlConnection.getResponseMessage());
            }

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            result[0] = method;
            result[1] = e.getMessage();
            return result[1];
        }

        result[0] = method;
        result[1] = sb.toString();

        return result[1];
    }

    public static String POST(String... params) {
        StringBuilder sb = new StringBuilder();

        String method = params[0];
        String urlStringParams = (params[1] == null) ? "" : "/" + params[1];
        String body = params[2];
        File file;
        if (method.contains("endpointAnalysis")) {
            method = "endpointAnalysis/file";
        }
        String urlString = "https://api.knurld.io/v1/" + method + urlStringParams;
        String[] result = {"", ""};

        if (method.contains("verification") && urlStringParams == "") {
            method = "setupVerification";
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            if (method.contains("enrollment") && urlStringParams != "") {
                method = "setupEnrollment";
                String boundary = "Nonce";

                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                urlConnection.setRequestProperty("Developer-Id", "Bearer: " + DEVELOPER_ID);
                urlConnection.setRequestProperty("Authorization", "Bearer " + CLIENT_TOKEN);
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                OutputStream outputStream = urlConnection.getOutputStream();

                String filePath = Environment.getExternalStorageDirectory().getPath();
                filePath = filePath + "/AudioRecorder/";
                JSONObject jsonBody = new JSONObject(body);
                String filename = "enrollment.wav";
                file = new File(filePath, filename);

                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"content\"").append(LINE_FEED);
//                writer.append("Content-Type: application/json").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(body).append(LINE_FEED);
                writer.flush();


                String name = "enrollment.wav";
                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"").append(LINE_FEED);
                writer.append("Content-Type: audio/wav").append(LINE_FEED);
//                writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                FileInputStream fin = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = fin.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                fin.close();

                writer.append(LINE_FEED);
                writer.flush();

                writer.append(LINE_FEED).flush();
                writer.append("--" + boundary + "--").append(LINE_FEED);
                writer.close();

            } else if (method.contains("verification") && urlStringParams != "") {
                String boundary = "Nonce";

                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                urlConnection.setRequestProperty("Developer-Id", "Bearer: " + DEVELOPER_ID);
                urlConnection.setRequestProperty("Authorization", "Bearer " + CLIENT_TOKEN);
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                OutputStream outputStream = urlConnection.getOutputStream();

                String filePath = Environment.getExternalStorageDirectory().getPath();
                filePath = filePath + "/AudioRecorder/";
                JSONObject jsonBody = new JSONObject(body);
                String filename = "verification.wav";
                file = new File(filePath, filename);

                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"content\"").append(LINE_FEED);
//                writer.append("Content-Type: application/json").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(body).append(LINE_FEED);
                writer.flush();


                String name = "verification.wav";
                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"").append(LINE_FEED);
                writer.append("Content-Type: audio/wav").append(LINE_FEED);
//                writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                FileInputStream fin = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = fin.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                fin.close();

                writer.append(LINE_FEED);
                writer.flush();

                writer.append(LINE_FEED).flush();
                writer.append("--" + boundary + "--").append(LINE_FEED);
                writer.close();

            } else {
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Developer-Id", "Bearer: " + DEVELOPER_ID);
                urlConnection.setRequestProperty("Authorization", "Bearer " + CLIENT_TOKEN);
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                out.write(body);
                out.flush();
                out.close();
            }



            int HttpResult = urlConnection.getResponseCode();

            if (HttpResult == HttpURLConnection.HTTP_OK || HttpResult == HttpURLConnection.HTTP_CREATED || HttpResult == HttpURLConnection.HTTP_ACCEPTED){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                Log.d("ResponseMessage", sb.toString());
            } else{
                method = "verificationFailed";
                Log.d("ResponseMessage", urlConnection.getResponseMessage());
            }

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            result[0] = method;
            result[1] = e.getMessage();
            return result[1];
        }

        result[0] = method;
        result[1] = sb.toString();

        return result[1];
    }
}
