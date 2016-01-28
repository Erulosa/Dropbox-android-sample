package com.ascendum.andyshear.dropboxdemo;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by andyshear on 1/11/16.
 */
public class KnurldService implements AsyncKnurldResponse {
    private static String CLIENT_TOKEN;


    private AsyncKnurldResponse response;
    public KnurldAsyncTask knurldAsync;
    private AsyncKnurldVerification resp;

    //SERVICES
    public KnurldAppModelService appModelService;
    public KnurldConsumerService consumerService;
    public KnurldAnalysisService analysisService;
    public KnurldEnrollmentService enrollmentService;
    public KnurldVerificationService verificationService;

    //MODELS
    public KnurldAppModel knurldAppModel;
    public KnurldConsumerModel knurldConsumerModel;
    public KnurldEnrollmentsModel knurldEnrollmentsModel;
    public KnurldVerificationModel knurldVerificationModel;
    public KnurldAnalysisModel knurldAnalysisModel;

    public boolean isUserReady;

    public KnurldService() {
        response = this;
        isUserReady = false;
        getToken();
        setupKnurldUser();
    }


    public KnurldService(AsyncKnurldVerification verificationResponse) {
        response = this;
        this.resp = verificationResponse;
        isUserReady = false;
        getToken();
        setupKnurldUser();
    }

    public KnurldService(AsyncKnurldVerification verificationResponse, String accessToken, String appModelId, String consumerId, String enrollmentId) {
        response = this;
        this.resp = verificationResponse;
        isUserReady = false;
        if (accessToken != null) {
            CLIENT_TOKEN = accessToken;
            setupExistingKnurldUser();
//            knurldAppModel = appModelId != null ? new KnurldAppModel(appModelId) : appModelService.indexAppModel();
            knurldConsumerModel = consumerId != null ? new KnurldConsumerModel(consumerId) : consumerService.indexConsumer();
            knurldEnrollmentsModel = enrollmentId != null ? new KnurldEnrollmentsModel(enrollmentId) : enrollmentService.indexEnrollment();
            createKnurldVerification();
        } else {
            getToken();
            setupKnurldUser();
        }
    }

    public KnurldService(AsyncKnurldResponse response, String token) {
        this.response = response;

        isUserReady = false;
        CLIENT_TOKEN = token;
        setupKnurldUser();
    }

    public String getAccessToken(){
        return CLIENT_TOKEN;
    }

    public void getToken(){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.ACCESS_TOKEN, "ACCESS_TOKEN", null, null);
    }

    public void setupKnurldUser() {
        appModelService = (appModelService == null) ? new KnurldAppModelService(response) : appModelService;
        consumerService = (consumerService == null) ? new KnurldConsumerService(response) : consumerService;
        analysisService = (analysisService == null) ? new KnurldAnalysisService(response) : analysisService;
        enrollmentService = (enrollmentService == null) ? new KnurldEnrollmentService(response) : enrollmentService;
        verificationService = (verificationService == null) ? new KnurldVerificationService(response) : verificationService;

        appModelService.index();
        knurldConsumerModel = consumerService.indexConsumer();
        knurldEnrollmentsModel = enrollmentService.indexEnrollment();
        knurldVerificationModel = verificationService.indexVerification();
    }

    public void setupExistingKnurldUser() {
        appModelService = (appModelService == null) ? new KnurldAppModelService(response) : appModelService;
        consumerService = (consumerService == null) ? new KnurldConsumerService(response) : consumerService;
        analysisService = (analysisService == null) ? new KnurldAnalysisService(response) : analysisService;
        enrollmentService = (enrollmentService == null) ? new KnurldEnrollmentService(response) : enrollmentService;
        verificationService = (verificationService == null) ? new KnurldVerificationService(response) : verificationService;
    }

    public void isUserReady() {
//        boolean modelReady = (knurldAppModel.appModelId != null);
        boolean consumerReady = (knurldConsumerModel.consumerModelId != null);
        boolean enrollmentReady = (knurldEnrollmentsModel.enrollmentId != null);
        boolean verificationReady = (knurldVerificationModel.activeVerification != null);
        if(appModelService.appModelReady() && consumerReady && enrollmentReady && verificationReady) {
            resp.processFinish("userReady", true);
        }
    }

    public void createKnurldVerification() {
        String testVerification = "{\"consumer\":\"" + knurldConsumerModel.getHref() + "\",\"application\":\"" + appModelService.getHref() + "\"}";
//        String testVerification = "{\"consumer\":\"" + knurldConsumerModel.getHref() + "\",\"application\":\"" + knurldAppModel.getHref() + "\"}";
        knurldVerificationModel = new KnurldVerificationModel();
        verificationService.createVerification(testVerification);
    }

    public void knurldAnalysis(AsyncKnurldVerification response) {
        String testEndpoint = "{\"filedata\":\"verification.wav\",\"words\":\"3\"}";
        KnurldAsyncVerification kAV = new KnurldAsyncVerification();
        kAV.delegate = response;
        this.resp = response;
        kAV.execute("analysisStart", testEndpoint);
    }

    public void knurldVerify(AsyncKnurldVerification response) {


        JSONObject enrollmentBody = new JSONObject();
        JSONArray phrases = knurldAnalysisModel.intervals;
        JSONArray vocab = appModelService.getVocab();
//        JSONArray vocab = knurldAppModel.getVocabulary();
        for (int i = 0; i<phrases.length(); i++) {
            try {
                JSONObject j = phrases.getJSONObject(i);
                j.put("phrase", vocab.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            enrollmentBody.put("intervals", phrases);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        KnurldAsyncVerification kAV = new KnurldAsyncVerification();
        kAV.delegate = response;
        this.resp = response;
        kAV.execute("verify", enrollmentBody.toString());
    }

    private class KnurldAsyncVerification extends AsyncTask<String, String, String[]> implements AsyncKnurldResponse {
        public AsyncKnurldVerification delegate = null;
        @Override
        protected String[] doInBackground(String... params) {
            String type = params[0];
            String param = (params[1] == null) ? null : params[1];

            switch (type) {
                case "analysisStart":
                    knurldAnalysisModel = analysisService.createEndpointAnalysis(param);

                    return new String[]{"analysisStart"};
                case "verify":
                    verificationService.updateVerification(knurldVerificationModel.activeVerification, param);
                    return new String[]{"verified"};
                default:
                    return new String[]{""};
            }

        }

        protected void onPostExecute(String... result) {

        }

        @Override
        public void processFinish(String call, String method, String output) {

            switch (method) {

                case "endpointAnalysis" :
                    knurldAnalysisModel.buildFromResponse(output);
                    boolean analysis = (knurldAnalysisModel.intervals == null) ? false : true;
                    if (analysis) {
                        delegate.processFinish("analysis", false);
                    }
                    break;
                case "verifications" :
                    knurldVerificationModel.buildFromResponse(output);
                    delegate.processFinish("verification", true);
                    break;
            }

        }
    }

    public boolean isAnalysisDone() {
        boolean analysis = (knurldAnalysisModel.intervals == null) ? false : true;
        if (!analysis) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                }
            });
            t.start();
            try {
                t.sleep(1000, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            analysisService.showEndpointAnalysis(knurldAnalysisModel.taskName);
        }
        return analysis;
    }

    public boolean isVerificationFinished() {
        if (!knurldVerificationModel.verified && knurldAnalysisModel != null) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                }
            });
            t.start();
            try {
                t.sleep(1000, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            verificationService.showVerification(knurldVerificationModel.verificationId);
        }
        return knurldVerificationModel.verified;
    }

    @Override
    public void processFinish(String call, String method, String output) {

        switch (method) {
            case "accessToken" :
                break;
            case "app-models" :
                knurldAppModel.buildFromResponse(output);
                isUserReady();
                break;
            case "consumers" :
                knurldConsumerModel.buildFromResponse(output);
                isUserReady();
                break;
            case "endpointAnalysis" :
                knurldAnalysisModel.buildFromResponse(output);
                if (isAnalysisDone()) {
                    resp.processFinish("analysis", false);
                }
                break;
            case "endpointAnalysis/file" :
                knurldAnalysisModel.buildFromResponse(output);
                if (isAnalysisDone()) {
                    resp.processFinish("analysis", false);
                }
                break;
            case "enrollments" :
                knurldEnrollmentsModel.buildFromResponse(output);
                isUserReady();
                break;
            case "setupVerification" :
                knurldVerificationModel.buildFromResponse(output);
                if (knurldVerificationModel.activeVerification == null) {
                    createKnurldVerification();
                } else {
                    isUserReady();
                }
                break;
            case "verifications" :
                knurldVerificationModel.buildFromResponse(output);
                if (isVerificationFinished()) {
                    knurldVerificationModel.verified = false;
                    resp.processFinish("verification", true);
                }
                break;
            case "verificationFailed" :
                createKnurldVerification();
                break;
        }

    }
}
