package com.ascendum.andyshear.dropboxdemo;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by andyshear on 1/11/16.
 */
public class KnurldService implements AsyncKnurldResponse {
    private static final String CLIENT_ID = "EGVYDlI9Xgwhtd7GBvZsTjIPAmTjVxMR";
    private static final String CLIENT_SECRET = "e7yCrwbBeOzdholu";
    private static String CLIENT_TOKEN;

    @Override
    public void processFinish(String method, String output) {

    }

    public String getToken(){

        HttpAsyncTask httpAsync = new HttpAsyncTask();
        httpAsync.delegate = this;
        httpAsync.execute("");

        return null;
    }

    public static String GET(String... params) {
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        String urlString = params[0];
        String result = "";

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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

                System.out.println(""+sb.toString());
            } else{
                System.out.println(urlConnection.getResponseMessage());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        return sb.toString();
    }

    public static String LOGIN(String... params) {
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        String urlString = params[0];
        String result = "";
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP));
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("grant_type", "client_credentials");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            String body = "grant_type=client_credentials";
            out.write(body);
            out.flush();
            out.close();

            int HttpResult = urlConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    JSONObject jsonResponse = new JSONObject(line);
                    CLIENT_TOKEN = jsonResponse.getString("access_token");
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println("" + sb.toString());
                System.out.println("TOKEN " + CLIENT_TOKEN);
            } else{
                System.out.println(urlConnection.getResponseMessage());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        return urlString;
    }

    public static String POST(String... params) {
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        String urlString = params[0];
        String body = params[1];
        String result = "";
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Authorization", "Bearer " + CLIENT_TOKEN);
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("grant_type", "client_credentials");

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(body);
            out.flush();
            out.close();

            int HttpResult = urlConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println(""+sb.toString());
            } else{
                System.out.println(urlConnection.getResponseMessage());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        return urlString;
    }


    private class HttpAsyncTask extends AsyncTask<String, String, String> {
        public AsyncKnurldResponse delegate = null;
        @Override
        protected String doInBackground(String... params) {
            LOGIN(params[0]);
            return GET("/api/rules");
        }

        protected void onPostExecute(String result) {
            delegate.processFinish("TEST", result);
        }
    }
}
