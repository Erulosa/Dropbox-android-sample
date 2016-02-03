package com.ascendum.andyshear.dropboxdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
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

/**
 * Created by andyshear on 1/12/16.
 */
public class KnurldActivity extends Activity implements AsyncKnurldResponse, AsyncKnurldVerification {

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
        View view = LayoutInflater.from(this).inflate(R.layout.knurld_setup, null);
        View spinnerView = LayoutInflater.from(this).inflate(R.layout.loading_popup, null);
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

    public void recordEnrollment(View view) {
        Intent intent = new Intent(this, RecordWAVActivity.class);
        startActivity(intent);
    }

    public void setKnurldAppModel(View view) {
        knurldService.createKnurldAppModel();
    }
//
//    public void getKnurldAppModel(View view) {
//        if (knurldAppModel == null) {
////            setKnurldAppModel(view);
//            knurldAppModel = new KnurldAppModel();
//            knurldService.indexAppModel();
//        } else if (knurldAppModel.appModelId != null){
//            knurldService.showAppModel(knurldAppModel.appModelId);
//        }
//    }
//
//    public void setKnurldConsumer(View view) {
//        String testConsumer = "{\"username\":\"Andy_Shear\",\"gender\":\"M\",\"password\":\"pass\"}";
//        if (knurldConsumerModel == null) {
//            knurldConsumerModel = new KnurldConsumerModel();
//            knurldService.createConsumer(testConsumer);
//        } else if (knurldConsumerModel.getHref() != null){
//            knurldService.updateConsumer(knurldConsumerModel.getHref(), testConsumer);
//        }
//    }
//
//    public void getKnurldConsumer(View view) {
//        if (knurldConsumerModel == null) {
//            knurldConsumerModel = new KnurldConsumerModel();
//            knurldService.indexConsumer();
//        } else if (knurldConsumerModel.consumerModelId != null){
//            knurldService.showConsumer(knurldConsumerModel.consumerModelId);
//        }
//    }
//
//    public void setKnurldEndpointAnalysis(View view) {
//        if (knurldAnalysisModel == null) {
//            knurldAnalysisModel = new KnurldAnalysisModel();
//        }
//        String testEndpoint = "{\"filedata\":\"enrollment.wav\",\"words\":\"3\"}";
//        knurldService.createEndpointAnalysis(testEndpoint);
//    }
//
//    public void getKnurldEndpointAnalysis(View view) {
//        if (knurldAnalysisModel == null) {
//            knurldAnalysisModel = new KnurldAnalysisModel();
//            String testEndpoint = "{\"filedata\":\"enrollment.wav\",\"words\":\"3\"}";
//            knurldService.createEndpointAnalysis(testEndpoint);
//        } else if (knurldAnalysisModel.taskName != null){
//            knurldService.showEndpointAnalysis(knurldAnalysisModel.taskName);
//        }
//
//    }
//
    public void setKnurldEnrollment(View view) {
//        knurldService.createKnurldEnrollment();
        popupWindow = showLoading();
        knurldService.startEnrollment();
        popupWindow.dismiss();
        showInstructions();
    }

    public void updateKnurldEnrollment(View view) {
//        knurldService.updateKnurldEnrollment();
        popupWindow = showLoading();
        knurldService.enroll();
        popupWindow.dismiss();


    }
//
//    public void getKnurldEnrollment(View view) {
//        if (knurldEnrollmentsModel == null) {
//            knurldEnrollmentsModel = new KnurldEnrollmentsModel();
//            knurldService.indexEnrollment();
//        } else if (knurldEnrollmentsModel.enrollmentId != null){
//            knurldService.showEnrollment(knurldEnrollmentsModel.enrollmentId);
//        }
//    }
//
//    public void setKnurldVerification(View view) {
//        String testVerification = "{\"consumer\":\"" + knurldConsumerModel.getHref() + "\",\"application\":\"" + knurldAppModel.getHref() + "\"}";
//        if (knurldVerificationModel == null) {
//            knurldVerificationModel = new KnurldVerificationModel();
//            knurldService.createVerification(testVerification);
//        } else if (knurldVerificationModel.verificationId != null){
//            JSONObject enrollmentBody = new JSONObject();
//            JSONArray phrases = knurldAnalysisModel.intervals;
//            JSONArray vocab = knurldAppModel.getVocabulary();
//            for (int i = 0; i<phrases.length(); i++) {
//                try {
//                    JSONObject j = phrases.getJSONObject(i);
//                    j.put("phrase", vocab.get(i));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                enrollmentBody.put("intervals", phrases);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            knurldService.updateVerification(knurldVerificationModel.getHref(), enrollmentBody.toString());
//        }
//    }
//
//    public void getKnurldVerification(View view) {
//        if (knurldVerificationModel == null) {
//            knurldVerificationModel = new KnurldVerificationModel();
//            knurldService.indexVerification();
//        } else if (knurldVerificationModel.verificationId != null){
//            knurldService.showVerification(knurldVerificationModel.verificationId);
//        }
//    }

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
