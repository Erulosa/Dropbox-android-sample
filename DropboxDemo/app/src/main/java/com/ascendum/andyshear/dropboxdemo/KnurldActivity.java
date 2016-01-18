package com.ascendum.andyshear.dropboxdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

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

    public KnurldModelService knurldModelService;

    public String taskName;
    public JSONObject intervals;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_loading);

        knurldService = new KnurldService(this);
        knurldService.getToken();
    }

    public void setKnurldAppModel(View view) {
        String testAppModel = "{\"vocabulary\":[\"Boston\", \"Boston\", \"Boston\"],\"verificationLength\":\"3\"}";
        if (knurldAppModel == null) {
            knurldAppModel = new KnurldAppModel();
            knurldService.createAppModel(testAppModel);
        } else if (knurldAppModel.getHref() != null) {
            knurldService.updateAppModel(knurldAppModel.appModelId, testAppModel);
        }
    }

    public void getKnurldAppModel(View view) {
        if (knurldAppModel == null) {
//            setKnurldAppModel(view);
            knurldAppModel = new KnurldAppModel();
            knurldService.indexAppModel();
        } else if (knurldAppModel.appModelId != null){
            knurldService.showAppModel(knurldAppModel.appModelId);
        }
    }

    public void setKnurldConsumer(View view) {
        String testConsumer = "{\"username\":\"Andy_Shear\",\"gender\":\"M\",\"password\":\"pass\"}";
        if (knurldConsumerModel == null) {
            knurldConsumerModel = new KnurldConsumerModel();
            knurldService.createConsumer(testConsumer);
        } else if (knurldConsumerModel.getHref() != null){
            knurldService.updateConsumer(knurldConsumerModel.getHref(), testConsumer);
        }
    }

    public void getKnurldConsumer(View view) {
        if (knurldConsumerModel == null) {
//            setKnurldConsumer(view);
            knurldConsumerModel = new KnurldConsumerModel();
            knurldService.indexConsumer();
        } else if (knurldConsumerModel.consumerModelId != null){
            knurldService.showConsumer(knurldConsumerModel.consumerModelId);
        }
    }

    public void setKnurldEndpointAnalysis(View view) {
        if (knurldAnalysisModel == null) {
            knurldAnalysisModel = new KnurldAnalysisModel();
        }
        String testEndpoint = "{\"filedata\":\"enrollment.wav\",\"words\":\"3\"}";
        knurldService.createEndpointAnalysis(testEndpoint);
    }

    public void getKnurldEndpointAnalysis(View view) {
        if (knurldAnalysisModel == null) {
            knurldAnalysisModel = new KnurldAnalysisModel();
            String testEndpoint = "{\"filedata\":\"enrollment.wav\",\"words\":\"3\"}";
            knurldService.createEndpointAnalysis(testEndpoint);
        } else if (knurldAnalysisModel.taskName != null){
            knurldService.showEndpointAnalysis(knurldAnalysisModel.taskName);
        }

    }

    public void setKnurldEnrollment(View view) {
        String testEnrollment = "{\"consumer\":\"" + knurldConsumerModel.getHref() + "\",\"application\":\"" + knurldAppModel.getHref() + "\"}";
        if (knurldEnrollmentsModel == null) {
            knurldEnrollmentsModel = new KnurldEnrollmentsModel();
            knurldService.createEnrollment(testEnrollment);
        } else if (knurldEnrollmentsModel.enrollmentId != null){
            JSONObject enrollmentBody = new JSONObject();
            try {
                enrollmentBody.put("intervals", knurldAnalysisModel.intervals);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            knurldService.updateEnrollment(knurldEnrollmentsModel.enrollmentId, enrollmentBody.toString());
        }
    }

    public void getKnurldEnrollment(View view) {
        if (knurldEnrollmentsModel == null) {
//            setKnurldEnrollment(view);
            knurldEnrollmentsModel = new KnurldEnrollmentsModel();
            knurldService.indexEnrollment();
        } else if (knurldEnrollmentsModel.enrollmentId != null){
            knurldService.showEnrollment(knurldEnrollmentsModel.enrollmentId);
        }
    }

    public void setKnurldVerification(View view) {
        String testVerification = "{\"consumer\":\"" + knurldConsumerModel.getHref() + "\",\"application\":\"" + knurldAppModel.getHref() + "\"}";
        if (knurldVerificationModel == null) {
            knurldVerificationModel = new KnurldVerificationModel();
            knurldService.createVerification(testVerification);
        } else if (knurldVerificationModel.verificationId != null){
            knurldService.updateVerification(knurldVerificationModel.getHref(), testVerification);
        }
    }

    public void getKnurldVerification(View view) {
        if (knurldVerificationModel == null) {
//            setKnurldVerification(view);
            knurldVerificationModel = new KnurldVerificationModel();
            knurldService.indexVerification();
        } else if (knurldVerificationModel.verificationId != null){
            knurldService.showVerification(knurldVerificationModel.verificationId);
        }
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
        } else if(method.contains("consumers")) {
            knurldConsumerModel.buildFromResponse(result);
        } else if(method.contains("verifications")) {
            knurldVerificationModel.buildFromResponse(result);
        }

    }


}
