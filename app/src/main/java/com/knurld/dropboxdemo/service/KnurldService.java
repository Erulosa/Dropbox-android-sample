package com.knurld.dropboxdemo.service;

import com.knurld.dropboxdemo.model.AppModel;
import com.knurld.dropboxdemo.model.ConsumerModel;
import com.knurld.dropboxdemo.model.EnrollmentModel;
import com.knurld.dropboxdemo.model.VerificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyshear on 2/15/16.
 */
public class KnurldService {
    // Knurld toke, Getter/Setter
    private static String CLIENT_TOKEN = null;
    public String getClientToken() {
        return CLIENT_TOKEN;
    }
    public void setClientToken(String clientToken) {
        CLIENT_TOKEN = clientToken;
    }

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

    // Empty constructor for KnurldActivity
    public KnurldService() {
        CLIENT_TOKEN = requestToken();
        KnurldModelService.setClientToken(CLIENT_TOKEN);
        setupExistingKnurldUser(null, null, null);
    }

    // Start knurld service by getting token, or
    // Start knurld service with existing token, pass in model Id's if they exist
    public KnurldService(String token, String appModelId, String consumerModelId, String enrollmentModelId) {
        CLIENT_TOKEN = token == null ? requestToken() : token;
        KnurldModelService.setClientToken(CLIENT_TOKEN);
        setupExistingKnurldUser(appModelId, consumerModelId, enrollmentModelId);
    }

    // Start thread to request token
    public String requestToken() {
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
        return token[0];
    }

    // Get knurld access token
    public String getAccessToken(){
        KnurldTokenService knurldTokenService = new KnurldTokenService();
        return CLIENT_TOKEN = knurldTokenService.getToken();
    }

    // Set up an existing knurld user with ID's, or each model will be built from index call
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
    public void startEnrollment() {
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
        enrollmentModel.buildFromResponse(enrollmentModel.show(enrollmentModel.enrollmentId));
        setEnrollmentModel(enrollmentModel);
    }

    // Set up a knurld user who has not yet created an enrollment
    public boolean enroll() {
        AppModel appModel = getAppModel();

        final EnrollmentModel enrollmentModel = getEnrollmentModel();

        // Create analysis endpoint
        int words = appModel.getVocabulary().length();
        final JSONObject body = new JSONObject();
        try {
            body.put("filedata", "enrollment.wav");
            body.put("words", words);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get phrase intervals from analysis service
        final JSONArray intervals = runAnalysis(body);

        // Add phrases to analysis intervals
        JSONObject analysisObj = prepareAnalysisJSON(intervals, getAppModel().getVocabulary(), getAppModel().getEnrollmentRepeats(), getAppModel().getVocabulary().length());

        // Return false if there is a bad analysis, re-record enrollment and try again
        if (analysisObj == null) {
            return false;
        }

        // Update enrollment with valid intervals from analysis, then set enrollment
        enrollmentModel.buildFromResponse(enrollmentModel.update(enrollmentModel.enrollmentId, analysisObj.toString()));
        enrollmentModel.buildFromResponse(enrollmentModel.show(enrollmentModel.enrollmentId));

        Thread enrollmentThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // Poll for enrollment to finish every 0.5 seconds until complete
                while (!enrollmentModel.enrolled && !enrollmentModel.failed) {
                    try {
                        Thread.sleep(500, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    enrollmentModel.buildFromResponse(enrollmentModel.show(enrollmentModel.enrollmentId));
                }
            }
        });
        enrollmentThread.start();

        try {
            enrollmentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        setEnrollmentModel(enrollmentModel);
        return enrollmentModel.enrolled;
    }

    public String[] startVerification() {
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
        verificationModel.buildFromResponse(verificationModel.show(verificationModel.activeVerification));
        return new String[]{verificationModel.phrases, verificationModel.activeVerification, verificationModel.phrasesArray.toString()};
    }

    // Set up and run a knurld verification
    public boolean verify(String verificationId, String vocab) {
        AppModel appModel = getAppModel();

        final VerificationModel verificationModel = new VerificationModel();
        verificationModel.buildFromId(verificationId);

        // Create analysis endpoint
        int words = appModel.getVerificationLength();
        final JSONObject body = new JSONObject();
        try {
            body.put("filedata", "verification.wav");
            body.put("words", words);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get phrase intervals from analysis service
        JSONArray intervals = runAnalysis(body);

        // Add phrases to analysis intervals
        JSONArray vocabArray = null;
        try {
            vocabArray = new JSONArray(vocab);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject analysisObj = prepareAnalysisJSON(intervals, vocabArray, 1, vocabArray.length());

        // Return false if there is a bad analysis, re-record enrollment and try again
        if (analysisObj == null) {
            return false;
        }

        // Update enrollment with valid intervals from analysis, then set enrollment
        verificationModel.buildFromResponse(verificationModel.update(verificationId, analysisObj.toString()));
        verificationModel.buildFromResponse(verificationModel.show(verificationModel.activeVerification));

        Thread verificationThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // Poll for enrollment to finish every 0.5 seconds until complete
                while (!verificationModel.verified && !verificationModel.failed && !verificationModel.completed) {
                    try {
                        Thread.sleep(500, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    verificationModel.buildFromResponse(verificationModel.show(verificationModel.activeVerification));
                }
            }
        });
        verificationThread.start();

        try {
            verificationThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return verificationModel.verified;
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
                        String analysisId = new JSONObject(analysis[0]).getString("taskName");
                        intervals[0] = knurldAnalysisService.getAnalysis(analysisId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    protected JSONObject prepareAnalysisJSON(JSONArray phrases, JSONArray vocab, int repeats, int words) {
        JSONObject body = new JSONObject();

        // Add phrases to intervals, accounting for enrollmentRepeats
        boolean validPhrases = true;
        JSONArray newPhrases = new JSONArray();
        for (int i = 0; i< words * repeats; i++) {
            try {
                JSONObject j = phrases.getJSONObject(i);
                int start = j.getInt("start");
                int stop = j.getInt("stop");
                if ((stop - start) < 600) {
                    validPhrases = false;
                }
                j.put("phrase", vocab.get(i%words));
                newPhrases.put(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            body.put("intervals", newPhrases);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Check if all phrases are valid, if not, try recording enrollment again
        if (validPhrases) {
            return body;
        }
        return null;
    }

}
