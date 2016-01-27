package com.ascendum.andyshear.dropboxdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyshear on 1/12/16.
 */
public class KnurldActivity extends Activity implements AsyncKnurldResponse {

    public KnurldService knurldService;
    public KnurldAppModel knurldAppModel;
    public KnurldConsumerModel knurldConsumerModel;
    public KnurldEnrollmentsModel knurldEnrollmentsModel;
    public KnurldVerificationModel knurldVerificationModel;
    public KnurldAnalysisModel knurldAnalysisModel;


    public String taskName;
    public JSONObject intervals;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_loading);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressSpinner);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(226, 132, 59), PorterDuff.Mode.MULTIPLY);

        knurldService = new KnurldService();
        knurldService.getToken();
    }

    public void recordEnrollment(View view) {
        Intent intent = new Intent(this, RecordWAVActivity.class);
        startActivity(intent);
    }

//    public void setKnurldAppModel(View view) {
//        String testAppModel = "{\"vocabulary\":[\"Boston\", \"Boston\", \"Boston\"],\"verificationLength\":\"3\"}";
//        if (knurldAppModel == null) {
//            knurldAppModel = new KnurldAppModel();
//            knurldService.createAppModel(testAppModel);
//        } else if (knurldAppModel.getHref() != null) {
//            knurldService.updateAppModel(knurldAppModel.appModelId, testAppModel);
//        }
//    }
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
//    public void setKnurldEnrollment(View view) {
//        String testEnrollment = "{\"consumer\":\"" + knurldConsumerModel.getHref() + "\",\"application\":\"" + knurldAppModel.getHref() + "\"}";
//        if (knurldEnrollmentsModel == null) {
//            knurldEnrollmentsModel = new KnurldEnrollmentsModel();
//            knurldService.createEnrollment(testEnrollment);
//        } else if (knurldEnrollmentsModel.enrollmentId != null){
//            JSONObject enrollmentBody = new JSONObject();
//            JSONArray phrases = knurldAnalysisModel.intervals;
//            JSONArray vocab = knurldAppModel.getVocabulary();
//            for (int i = 0; i<phrases.length(); i++) {
//                try {
//                    JSONObject j = phrases.getJSONObject(i);
////                    j.put("phrase", "Boston");
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
//            knurldService.updateEnrollment(knurldEnrollmentsModel.enrollmentId, enrollmentBody.toString());
//        }
//    }
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


}
