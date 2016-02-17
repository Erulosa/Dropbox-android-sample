package com.knurld.dropboxdemo.service;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.knurld.dropboxdemo.AsyncKnurldResponse;
import com.knurld.dropboxdemo.AsyncKnurldVerification;
import com.knurld.dropboxdemo.KnurldAnalysisModel;
import com.knurld.dropboxdemo.KnurldAnalysisService;
import com.knurld.dropboxdemo.KnurldAppModelService;
import com.knurld.dropboxdemo.KnurldAsyncTask;
import com.knurld.dropboxdemo.KnurldConsumerModel;
import com.knurld.dropboxdemo.KnurldConsumerService;
import com.knurld.dropboxdemo.KnurldEnrollmentService;
import com.knurld.dropboxdemo.KnurldEnrollmentsModel;
import com.knurld.dropboxdemo.KnurldUtility;
import com.knurld.dropboxdemo.KnurldVerificationModel;
import com.knurld.dropboxdemo.KnurldVerificationService;
import com.knurld.dropboxdemo.R;
import com.knurld.dropboxdemo.model.AppModel;
import com.knurld.dropboxdemo.model.ConsumerModel;
import com.knurld.dropboxdemo.model.EnrollmentModel;
import com.knurld.dropboxdemo.model.VerificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by andyshear on 2/15/16.
 */
public class KnurldService {
    private static String CLIENT_TOKEN;

    // Models
    private AppModel appModel;
    private ConsumerModel consumerModel;
    private EnrollmentModel enrollmentModel;

    // Model getters/setters
    public AppModel getAppModel() {
        return appModel;
    }
    public void setAppModel(AppModel appModel) {
        this.appModel = appModel;
    }
    public ConsumerModel getConsumerModel() {
        return consumerModel;
    }
    public void setConsumerModel(ConsumerModel consumerModel) {
        this.consumerModel = consumerModel;
    }
    public EnrollmentModel getEnrollmentModel() {
        return enrollmentModel;
    }
    public void setEnrollmentModel(EnrollmentModel enrollmentModel) {
        this.enrollmentModel = enrollmentModel;
    }

    // Start knurld service by getting token
    public KnurldService() {
        final String[] token = {null};
        Thread tokenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                token[0] = getAccessToken();
            }
        });
        tokenThread.start();

        try {
            tokenThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CLIENT_TOKEN = token[0] == null ? "failed" : token[0];
        setupKnurldUser();
    }

    // Start knurld service with existing token, no existing models
    public KnurldService(String token) {
        CLIENT_TOKEN = token;
        setupKnurldUser();
    }

    // Start knurld service with existing model ID's
    public KnurldService(String token, String appModelId, String consumerModelId, String enrollmentModelId) {
        CLIENT_TOKEN = token;
        setupExistingKnurldUser(appModelId, consumerModelId, enrollmentModelId);
    }

    // Get knurld access token
    public String getAccessToken(){
        KnurldTokenService knurldTokenService = new KnurldTokenService();
        return CLIENT_TOKEN == null ? knurldTokenService.getToken() : CLIENT_TOKEN;
    }

    // Set up a knurld user that has already been created
    public void setupKnurldUser() {
        AppModel appModel = new AppModel();
        ConsumerModel consumerModel = new ConsumerModel();
        EnrollmentModel enrollmentModel = new EnrollmentModel();

        appModel.buildFromResponse(appModel.index());
        consumerModel.buildFromResponse(consumerModel.index());
        enrollmentModel.buildFromResponse(enrollmentModel.index());

        setAppModel(appModel);
        setConsumerModel(consumerModel);
        setEnrollmentModel(enrollmentModel);
    }

    // Set up an existing knurld user with ID's
    public void setupExistingKnurldUser(String appModelId, String consumerModelId, String enrollmentModelId) {
        AppModel appModel = new AppModel();
        ConsumerModel consumerModel = new ConsumerModel();
        EnrollmentModel enrollmentModel = new EnrollmentModel();

        appModel.buildFromId(appModelId);
        consumerModel.buildFromId(consumerModelId);
        enrollmentModel.buildFromId(enrollmentModelId);

        setAppModel(appModel);
        setConsumerModel(consumerModel);
        setEnrollmentModel(enrollmentModel);
    }

    // Get existing appModel and consumerModel, then create an enrollment
    public void knurldEnroll() {
        AppModel appModel = getAppModel();
        ConsumerModel consumerModel = getConsumerModel();

        JSONObject body = new JSONObject();
        try {
            body.put("consumer", consumerModel.getHref());
            body.put("application", appModel.getHref());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EnrollmentModel enrollmentModel = new EnrollmentModel();
        enrollmentModel.buildFromResponse(enrollmentModel.create(body.toString()));
        setEnrollmentModel(enrollmentModel);
    }

    // Set up a knurld user who has not yet created an enrollment
    public boolean setupKnurldEnrollment() {
        AppModel appModel = new AppModel();
        ConsumerModel consumerModel = new ConsumerModel();

        // Build existing appModel and consumerModel
        appModel.buildFromResponse(appModel.index());
        consumerModel.buildFromResponse(consumerModel.index());

        setAppModel(appModel);
        setConsumerModel(consumerModel);

        // Get newly created enrollment, then update enrollment with enrollment.wav
        knurldEnroll();
        EnrollmentModel enrollmentModel = getEnrollmentModel();

        // Create analysis endpoint
        int words = appModel.getVocabulary().length();
        final JSONObject enrollmentBody = new JSONObject();
        try {
            enrollmentBody.put("filedata", "enrollment.wav");
            enrollmentBody.put("words", words);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get phrase intervals from analysis service
        JSONArray intervals = runAnalysis(enrollmentBody);

        // Add phrases to analysis intervals
        JSONObject analysisObj = prepareAnalysisJSON(intervals);

        // Return false if there is a bad analysis, re-record enrollment and try again
        if (analysisObj == null) {
            return false;
        }

        // Update enrollment with valid intervals from analysis, then set enrollment
        enrollmentModel.buildFromResponse(enrollmentModel.update(analysisObj.toString()));

        setEnrollmentModel(enrollmentModel);
        return true;
    }

    // Set up and run a knurld verification
    public boolean verify() {
        AppModel appModel = getAppModel();
        ConsumerModel consumerModel = getConsumerModel();

        JSONObject body = new JSONObject();
        try {
            body.put("consumer", consumerModel.getHref());
            body.put("application", appModel.getHref());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VerificationModel verificationModel = new VerificationModel();
        verificationModel.buildFromResponse(verificationModel.create(body.toString()));

        // Create analysis endpoint
        int words = appModel.getVerificationLength();
        final JSONObject enrollmentBody = new JSONObject();
        try {
            enrollmentBody.put("filedata", "verification.wav");
            enrollmentBody.put("words", words);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get phrase intervals from analysis service
        JSONArray intervals = runAnalysis(enrollmentBody);

        // Add phrases to analysis intervals
        JSONObject analysisObj = prepareAnalysisJSON(intervals);

        // Return false if there is a bad analysis, re-record enrollment and try again
        if (analysisObj == null) {
            return false;
        }

        // Update enrollment with valid intervals from analysis, then set enrollment
        verificationModel.buildFromResponse(verificationModel.update(analysisObj.toString()));

        return verificationModel.isVerified();
    }

    protected JSONArray runAnalysis(final JSONObject body) {
        // Perform analysis on enrollment.wav
        final com.knurld.dropboxdemo.service.KnurldAnalysisService knurldAnalysisService = new com.knurld.dropboxdemo.service.KnurldAnalysisService(CLIENT_TOKEN);

        // Start analysis on enrollment.wav
        final String[] analysis = {null};
        final JSONArray[] intervals = {null};
        Thread analysisThread = new Thread(new Runnable() {
            @Override
            public void run() {
                analysis[0] = knurldAnalysisService.startAnalysis(body.toString());

                // Poll for analysis to finish every 0.5 seconds until intervals are returned
                while (intervals[0] == null) {
                    try {
                        Thread.sleep(500, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    intervals[0] = knurldAnalysisService.getAnalysis(analysis[0]);
                }
            }
        });
        analysisThread.start();

        try {
            analysisThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return intervals[0];
    }

    protected JSONObject prepareAnalysisJSON(JSONArray phrases) {
        int words = getAppModel().getVocabulary().length();
        JSONObject enrollmentBody = new JSONObject();
        JSONArray vocab = getAppModel().getVocabulary();

        // Add phrases to intervals, accounting for 3x repeated phrases
        boolean validPhrases = true;
        JSONArray newPhrases = new JSONArray();
        for (int i = 0; i< words * 3; i++) {
            try {
                JSONObject j = phrases.getJSONObject(i);
                int start = j.getInt("start");
                int stop = j.getInt("stop");
                if ((stop - start) < 600) {
                    validPhrases = false;
                }
                j.put("phrase", vocab.get(i%5));
                newPhrases.put(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            enrollmentBody.put("intervals", newPhrases);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Check if all phrases are valid, if not, try recording enrollment again
        if (validPhrases) {
            return enrollmentBody;
        }
        return null;
    }

}
