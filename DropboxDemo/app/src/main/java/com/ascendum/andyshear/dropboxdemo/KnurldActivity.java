package com.ascendum.andyshear.dropboxdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by andyshear on 1/12/16.
 */
public class KnurldActivity extends Activity implements AsyncKnurldResponse, AsyncKnurldVerification, AsyncMessage {

    public KnurldService knurldService;
    public KnurldAppModel knurldAppModel;
    public KnurldConsumerModel knurldConsumerModel;
    public KnurldEnrollmentsModel knurldEnrollmentsModel;
    public KnurldVerificationModel knurldVerificationModel;
    public KnurldAnalysisModel knurldAnalysisModel;


    public AsyncKnurldVerification knurldVerification;


    public Thread knurldServiceThread;

    public boolean isUserReady;

    public String taskName;
    public JSONObject intervals;

    public PopupWindow popupWindow;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_loading);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressSpinner);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(226, 132, 59), PorterDuff.Mode.MULTIPLY);

        context = this;
        isUserReady = false;
        knurldVerification = this;
        knurldServiceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                knurldService = new KnurldService(knurldVerification, context);
            }
        });

        knurldServiceThread.start();
    }


    public PopupWindow showLoading() {
        View view = LayoutInflater.from(context).inflate(R.layout.knurld_setup, null);
        View spinnerView = LayoutInflater.from(context).inflate(R.layout.loading_popup, null);
        ProgressBar progressBar = (ProgressBar) spinnerView.findViewById(R.id.speakProgress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        popupWindow = new PopupWindow(spinnerView, 500, 500);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupWindow;
    }

    public void showInstructions() {
        View view = LayoutInflater.from(this).inflate(R.layout.knurld_setup, null);
        View spinnerView = LayoutInflater.from(this).inflate(R.layout.instructions_popup, null);


        TextView textView = (TextView) spinnerView.findViewById(R.id.phraseText);
        String vocab = knurldService.appModelService.getVocab().toString();
        textView.setText("Speak in order 3x:\n" + vocab);

        popupWindow = new PopupWindow(spinnerView, 500, 500);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        popupWindow.dismiss();
                    }
                }, 5000);

    }

    @Override
    public void processFinish(PopupWindow popupWindow) {
        popupWindow.dismiss();
    }

    private class AsyncPopup extends AsyncTask<String, String, PopupWindow> {
        public AsyncMessage delegate = null;
        @Override
        protected PopupWindow doInBackground(String... params) {
            Looper.prepare();
            return showLoading();
        }

        protected void onPostExecute(PopupWindow result) {
            delegate.processFinish(result);
        }

    }

    public void recordEnrollment(View view) {
        Intent intent = new Intent(this, RecordWAVActivity.class);
        startActivity(intent);
    }

    public void setKnurldAppModel(View view) {
        knurldService.createKnurldAppModel();
    }

    public void setKnurldEnrollment(View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popupWindow = showLoading();
            }
        });
        knurldService.startEnrollment();
        popupWindow.dismiss();
        showInstructions();
    }

    public void updateKnurldEnrollment(View view) {
        popupWindow = showLoading();
        knurldService.enroll();
        popupWindow.dismiss();
    }


    @Override
    public void processFinish(String call, String method, String result) {

        if (method.equals("accessToken")) {
            setContentView(R.layout.knurld_setup);
        } else if(method.contains("app-models")) {
            knurldAppModel.buildFromResponse(result);
        } else if(method.contains("enrollments")) {
            knurldEnrollmentsModel.buildFromResponse(result);
        } else if(method.contains("endpointAnalysis")) {
            knurldAnalysisModel.buildFromResponse(result);
            JSONArray phrases = knurldAnalysisModel.intervals;
            if (phrases != null) {
                for (int i = 0; i<phrases.length(); i++) {
                    try {
                        JSONObject j = phrases.getJSONObject(i);
                        int start = j.getInt("start");
                        int stop = j.getInt("stop");
                        if ((j.getInt("stop") - j.getInt("start")) < 600) {
                            Toast.makeText(this, "Speak slower and try again", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else if(method.contains("consumers")) {
            knurldConsumerModel.buildFromResponse(result);
        } else if(method.contains("verifications")) {
            knurldVerificationModel.buildFromResponse(result);
        }

    }

    @Override
    public void processFinish(String method, boolean result) {

        switch (method) {
            case "userReady":
                setContentView(R.layout.knurld_setup);
                isUserReady = result;
                break;
        }


    }


}
