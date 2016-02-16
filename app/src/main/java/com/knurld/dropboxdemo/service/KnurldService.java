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
public class KnurldService implements AsyncKnurldResponse {
    private static String CLIENT_TOKEN;
    private static final String CLIENT_ID = "EGVYDlI9Xgwhtd7GBvZsTjIPAmTjVxMR";
    private static final String CLIENT_SECRET = "e7yCrwbBeOzdholu";


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
    public KnurldConsumerModel knurldConsumerModel;
    public KnurldEnrollmentsModel knurldEnrollmentsModel;
    public KnurldVerificationModel knurldVerificationModel;
    public KnurldAnalysisModel knurldAnalysisModel;

    public boolean isUserReady;

    private Context context;

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
    }

    public KnurldService(String token) {
        CLIENT_TOKEN = token;
    }

    public String getAccessToken(){
        return CLIENT_TOKEN == null ? getToken() : CLIENT_TOKEN;
    }

    public String getToken(){
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        String urlString = "https://api.knurld.io/oauth/client_credential/accesstoken?grant_type=client_credentials";
        String credentials = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(credentials);
            out.flush();
            out.close();

            int HttpResult = urlConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {

                    sb.append(line);
                }
                JSONObject jsonResponse = new JSONObject(sb.toString());
                CLIENT_TOKEN = jsonResponse.getString("access_token");
                br.close();

                System.out.println("" + sb.toString());
                System.out.println("TOKEN " + CLIENT_TOKEN);
            } else{
                System.out.println(urlConnection.getResponseMessage());
            }

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            return e.getMessage();
        }
        return CLIENT_TOKEN;
    }

    public void setupKnurldUser() {
        AppModel appModel = new AppModel();
        appModel.index();
    }

    public void setupExistingKnurldUser() {
        appModelService = (appModelService == null) ? new KnurldAppModelService(response) : appModelService;
        consumerService = (consumerService == null) ? new KnurldConsumerService(response) : consumerService;
        analysisService = (analysisService == null) ? new KnurldAnalysisService(response) : analysisService;
        enrollmentService = (enrollmentService == null) ? new KnurldEnrollmentService(response) : enrollmentService;
        verificationService = (verificationService == null) ? new KnurldVerificationService(response) : verificationService;
    }

    public void isUserReady() {
        boolean consumerReady = (knurldConsumerModel.consumerModelId != null);
        boolean enrollmentReady = (knurldEnrollmentsModel.enrollmentId != null);
        if(appModelService.appModelReady() && consumerReady && enrollmentReady) {
            isUserReady = true;
            resp.processFinish("userReady", true);
        }
    }

    public void createKnurldAppModel() {
        String testAppModel = "{\"vocabulary\":[\"Boston\", \"Chicago\", \"Atlanta\" , \"Cleveland\", \"Madrid\"],\"verificationLength\":\"5\"}";
        appModelService.create(testAppModel);
    }



    public void createKnurldVerification() {
        String testVerification = "{\"consumer\":\"" + knurldConsumerModel.getHref() + "\",\"application\":\"" + appModelService.getHref() + "\"}";
        knurldVerificationModel = new KnurldVerificationModel();
        verificationService.createVerification(testVerification);
    }

    public void createKnurldEnrollment() {
        String testEnrollment = "{\"consumer\":\"" + knurldConsumerModel.getHref() + "\",\"application\":\"" + appModelService.getHref() + "\"}";
        knurldEnrollmentsModel = new KnurldEnrollmentsModel();
        enrollmentService.createEnrollment(testEnrollment);
    }

    protected JSONObject prepareAnalysisJSON() {
        JSONObject enrollmentBody = new JSONObject();
        JSONArray phrases = knurldAnalysisModel.intervals;
        JSONArray vocab = appModelService.getVocab();

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

        return enrollmentBody;
    }

    public void updateKnurldEnrollment() {
        enrollmentService.updateEnrollment(knurldEnrollmentsModel.enrollmentId, prepareAnalysisJSON().toString());
    }

    public void knurldVerify(AsyncKnurldVerification response) {
        KnurldAsyncVerification kAV = new KnurldAsyncVerification();
        kAV.delegate = response;
        this.resp = response;

        kAV.execute("verify", prepareAnalysisJSON().toString());
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
            try {
                Thread.sleep(1000, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            analysisService.showEndpointAnalysis(knurldAnalysisModel.taskName);
        }
        return analysis;
    }

    public boolean isVerificationFinished() {
        if (!knurldVerificationModel.verified && knurldAnalysisModel != null) {
            try {
                Thread.sleep(1000, 0);
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
                CLIENT_TOKEN = output;
                break;
            case "app-models" :
                appModelService.setKnurldAppModel(output);
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
            case "setupEnrollment" :
                knurldEnrollmentsModel.buildFromResponse(output);
                if (knurldEnrollmentsModel.intervals != null) {
                    resp.processFinish("enrollmentReady", true);
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


    public String startEnrollment() {
        final String[] response = {null};

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Create enrollment
                String testEnrollment = "{\"consumer\":\"" + knurldConsumerModel.getHref() + "\",\"application\":\"" + appModelService.getHref() + "\"}";
                knurldEnrollmentsModel = new KnurldEnrollmentsModel();

                enrollmentService = new KnurldEnrollmentService(CLIENT_TOKEN);
                knurldEnrollmentsModel.buildFromResponse(enrollmentService.create(testEnrollment)[1]);
                response[0] = enrollmentService.show(knurldEnrollmentsModel.enrollmentId)[1];
                knurldEnrollmentsModel.buildFromResponse(response[0]);
                try {
                    finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        t.start();

        while (t.isAlive()) {

        }

        return response[0];
    }

    public String startVerification() {
        final String[] response = {null};

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Create verification
                String testVerification = "{\"consumer\":\"" + knurldConsumerModel.getHref() + "\",\"application\":\"" + appModelService.getHref() + "\"}";
                knurldVerificationModel = new KnurldVerificationModel();

                verificationService = new KnurldVerificationService(CLIENT_TOKEN);
                knurldVerificationModel.buildFromResponse(verificationService.create(testVerification)[1]);
                response[0] = verificationService.show(knurldVerificationModel.activeVerification)[1];
                knurldVerificationModel.buildFromResponse(response[0]);
                try {
                    finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        t.start();

        while (t.isAlive()) {

        }

        return response[0];
    }

    public PopupWindow showLoading(View view, PopupWindow popupWindow, Context context) {
        View spinnerView = LayoutInflater.from(context).inflate(R.layout.loading_popup, null);
        ProgressBar progressBar = (ProgressBar) spinnerView.findViewById(R.id.speakProgress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        popupWindow = new PopupWindow(spinnerView, 500, 500);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        return popupWindow;
    }

    // Perform analysis and enrollment synchronously, returns when enrollment passes/fails
    public boolean enroll() {
        final String[] response = {null, null};
        final boolean[] failed = {false};

        final Thread enrollThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // Create analysis endpoint
                int words = appModelService.getVocab().length();
                JSONObject enrollmentBody1 = new JSONObject();
                try {
                    enrollmentBody1.put("filedata", "enrollment.wav");
                    enrollmentBody1.put("words", words);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                knurldAnalysisModel = new KnurldAnalysisModel();
                analysisService = new KnurldAnalysisService(CLIENT_TOKEN);
                knurldAnalysisModel.buildFromResponse(analysisService.createAnalysis(enrollmentBody1.toString())[1]);


                // Poll for analysis to finish
                while (knurldAnalysisModel.intervals == null) {
                    try {
                        Thread.sleep(500, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    knurldAnalysisModel.buildFromResponse(analysisService.showAnalysis(knurldAnalysisModel.taskName)[1]);
                }

                // Update enrollment, set phrases to intervals
                JSONObject enrollmentBody = new JSONObject();
                JSONArray phrases = knurldAnalysisModel.intervals;
                JSONArray vocab = appModelService.getVocab();

                // Add phrases to intervals, accounting for 3x (15) repeated phrases
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

                    // Try to update enrollment, if it fails, restart analysis
                    String[] responses =  enrollmentService.update(knurldEnrollmentsModel.enrollmentId, enrollmentBody.toString());
                    response[0] = responses[0];
                    if (responses[0].equals("failed")) {
                        try {
                            finalize();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    } else {
                        knurldEnrollmentsModel.buildFromResponse(responses[1]);

                        // Poll for verification to finish
                        while (!knurldEnrollmentsModel.enrolled && knurldAnalysisModel != null && !failed[0]) {
                            try {
                                Thread.sleep(500, 0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Try to get verification, if it fails, restart analysis
                            response[1] = enrollmentService.show(knurldEnrollmentsModel.enrollmentId)[1];
                            if (response[1].equals("failed") || knurldEnrollmentsModel.failed) {
                                try {
                                    failed[0] = true;
                                    finalize();

                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            } else {
                                knurldEnrollmentsModel.buildFromResponse(response[1]);

                            }
                        }
                    }
                }

                try {

                    finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        enrollThread.start();

        while (enrollThread.isAlive() ) {
            String test = "";
        }

        enrollThread.interrupt();
        boolean response1 = response[0] != null ? response[0].equals("failed") : true;
        boolean response2 = response[1] != null ? response[1].equals("failed") : true;


        if (response1 && response2 && !knurldEnrollmentsModel.enrolled) {
            return false;
        } else {
            return true;
        }
    }

    // Perform analysis and verification synchronously, returns when verification passes/fails
    public boolean verify() {
        final String[] response = {null, null};
        final boolean[] failed = {false};

        final Thread verifyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Create analysis endpoint
                int words = appModelService.getVerificationLength();
                JSONObject verificationBody = new JSONObject();
                try {
                    verificationBody.put("filedata", "verification.wav");
                    verificationBody.put("words", words);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                knurldAnalysisModel = new KnurldAnalysisModel();
                analysisService = new KnurldAnalysisService(CLIENT_TOKEN);
                knurldAnalysisModel.buildFromResponse(analysisService.createAnalysis(verificationBody.toString())[1]);


                // Poll for analysis to finish
                while (knurldAnalysisModel.intervals == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    knurldAnalysisModel.buildFromResponse(analysisService.showAnalysis(knurldAnalysisModel.taskName)[1]);
                }

                // Update verification, set phrases to intervals
                JSONObject enrollmentBody = new JSONObject();
                JSONArray phrases = knurldAnalysisModel.intervals;
                JSONArray vocab = knurldVerificationModel.phrasesArray;

                // Add phrases to intervals, accounting for 3x (15) repeated phrases
                boolean validPhrases = true;

                // Remove any intervals that are under 400ms
                JSONArray editedPhrases = new JSONArray();
                if (phrases.length() > words) {
                    for (int i = 0; i < phrases.length(); i++) {
                        try {
                            JSONObject j = phrases.getJSONObject(i);
                            int start = j.getInt("start");
                            int stop = j.getInt("stop");
                            if ((stop - start) > 400) {
                                j.put("phrase", vocab.get(i));
                                editedPhrases.put(j);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    editedPhrases = phrases;
                }

                // Check if editedPhrases contain the same amount of intervals as words
                if (editedPhrases.length() != words) {
                    validPhrases = false;
                }

                // Check editedPhrases to ensure each utterance is more than 600ms
                JSONArray newPhrases = new JSONArray();
                for (int i = 0; i< words; i++) {
                    try {
                        JSONObject j = editedPhrases.getJSONObject(i);
                        int start = j.getInt("start");
                        int stop = j.getInt("stop");
                        if ((stop - start) < 600) {
                            validPhrases = false;
                        }
                        j.put("phrase", vocab.get(i));
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

                    // Try to update verification, if it fails, restart analysis
                    // responses contains method/failed message, and response body
                    String[] responses = verificationService.update(knurldVerificationModel.activeVerification, enrollmentBody.toString());
                    response[0] = responses[0];
                    if (responses[0].equals("failed") || knurldVerificationModel.failed) {
                        try {
                            finalize();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    } else {
                        knurldVerificationModel.buildFromResponse(responses[1]);

                        // Poll for verification to finish
                        while (!knurldVerificationModel.verified && knurldAnalysisModel != null && !failed[0]) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Try to get verification, if it fails, restart analysis
                            response[1] = verificationService.show(knurldVerificationModel.verificationId)[1];
                            if (knurldVerificationModel.failed) {
                                try {
                                    failed[0] = true;
                                    finalize();

                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            } else {
                                knurldVerificationModel.buildFromResponse(response[1]);
                            }
                        }
                    }
                }
            }
        });
        verifyThread.start();

        try {
            verifyThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return knurldVerificationModel.verified;
    }


}
