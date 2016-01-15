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
        setKnurldAppModel("");
    }

    public void setKnurldConsumer(View view) {
        setKnurldConsumer("", null);
    }

    public void setKnurldEndpointAnalysis(View view) {
        setKnurldEndpointAnalysis("enrollment.wav");
    }

    public void getKnurldEndpointAnalysis(View view) {
        getKnurldEndpointAnalysis("824633e9cbe60a014101e04e5f2124bf");
    }

    public void setKnurldAppModel(String appModel){
        String testAppModel = "{\"vocabulary\":[\"Chicago\", \"Cleveland\", \"Madrid\"],\"verificationLength\":\"3\"}";
        knurldService.createAppModel(testAppModel);
    }

    public void getKnurldAppModel(String appModel){
        if (appModel == null) {
            knurldService.indexAppModel();
        } else {
            knurldService.showAppModel(appModel);
        }

    }

    public void setKnurldConsumer(String consumer, String consumerId){
        String testConsumer = "{\"username\":\"Roy_Delgado\",\"gender\":\"F\",\"password\":\"pass\"}";
        if (consumerId == null) {
            knurldService.createConsumer(testConsumer);
        } else {
            knurldService.updateConsumer(consumerId, consumer);
        }

    }

    public void getKnurldConsumer(String consumer){
        if (consumer == null) {
            knurldService.indexConsumer();
        } else {
            knurldService.showConsumer(consumer);
        }

    }

    public void setKnurldEnrollment(String enrollment, String enrollmentId){
        String testEnrollment = "{\"consumer\":\"https://api.knurld.io/v1/consumers/" + knurldConsumerModel.getHref() + "\",\"application\":\"https://api.knurld.io/v1/app-models/" + knurldAppModel.getHref() + "\"}";
        String testEnrollmentUpdate = "{\"enrollments.wav\":\"https://drive.google.com/file/d/0B4KlynGB6FSkc09FN0FuYVJuZzg/view?usp=sharing\",\"intervals\":[{\"start\":\"720\",\"stop\":\"1408\",\"phrase\":\"chicago\"},{\"start\":\"1440\",\"stop\":\"1888\",\"phrase\":\"cleveland\"},{\"start\":\"2528\",\"stop\":\"2864\",\"phrase\":\"madrid\"}]}";
        if (enrollmentId == null) {
            knurldService.createEnrollment(enrollment);
        } else {
            knurldService.updateEnrollment(enrollmentId, enrollment);
        }

    }

    public void getKnurldEnrollment(String enrollment){
        if (enrollment == null) {
            knurldService.indexEnrollment();
        } else {
            knurldService.showEnrollment(enrollment);
        }

    }

    public void setKnurldVerification(String verification, String verificationId){
        String testVerification = "{\"consumer\":\"https://api.knurld.io/v1/consumers/" + knurldConsumerModel.getHref() + "\",\"application\":\"https://api.knurld.io/v1/app-models/" + knurldAppModel.getHref() + "\"}";
        String testVerificationUpdate = "{\"verification.wav\":\"https://drive.google.com/file/d/0B4KlynGB6FSkc09FN0FuYVJuZzg/view?usp=sharing\",\"intervals\":[{\"start\":\"720\",\"stop\":\"1408\",\"phrase\":\"chicago\"},{\"start\":\"1440\",\"stop\":\"1888\",\"phrase\":\"cleveland\"},{\"start\":\"2528\",\"stop\":\"2864\",\"phrase\":\"madrid\"}]}";
        if (verificationId == null) {
            knurldService.createVerification(verification);
        } else {
            knurldService.updateVerification(verificationId, verification);
        }

    }

    public void getKnurldVerification(String verification){
        if (verification == null) {
            knurldService.indexVerification();
        } else {
            knurldService.showVerification(verification);
        }

    }

    public void setKnurldEndpointAnalysis(String filename){
        String testEndpoint = "{\"filedata\":\"" + filename + "\",\"words\":\"3\"}";
        knurldService.createEndpointAnalysis(testEndpoint);

    }

    public void getKnurldEndpointAnalysis(String task){
        knurldService.showEndpointAnalysis(taskName);
    }

    @Override
    public void processFinish(String call, String method, String result) {
        JSONObject jsonParam = null;
        if (method.equals("accessToken")) {
            setContentView(R.layout.knurld_setup);
        }

        try {
            jsonParam = new JSONObject(result);
            if (method.equals("createEndpointAnalysis")) {
                taskName = jsonParam.getString("taskName");
            } else if (method.equals("createEndpointAnalysis")) {
                intervals = jsonParam.getJSONObject("intervals");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String test = "";

    }


}
