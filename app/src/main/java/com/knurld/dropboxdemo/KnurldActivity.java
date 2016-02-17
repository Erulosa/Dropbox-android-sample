// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.knurld.dropboxdemo;

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

import com.knurld.dropboxdemo.service.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KnurldActivity extends Activity implements AsyncMessage {

    public com.knurld.dropboxdemo.service.KnurldService knurldService;

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
        String vocab = knurldService.getAppModel().getVocabulary().toString();
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

    public void setKnurldEnrollment(View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popupWindow = showLoading();
            }
        });
        knurldService.setupKnurldEnrollment();
        popupWindow.dismiss();
        showInstructions();
    }

    public void updateKnurldEnrollment(View view) {
        popupWindow = showLoading();
        knurldService.knurldEnroll();
        popupWindow.dismiss();
    }



}
