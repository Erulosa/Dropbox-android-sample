package com.ascendum.andyshear.dropboxdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by andyshear on 1/11/16.
 */
public class KnurldAppModel {
    private String developerId;
    private String authorization;
    private int enrollmentRepeats;
    private JSONArray vocabulary;
    private int verificationLength;
    private float threshold;
    private boolean autoThresholdEnable;
    private int autoThresholdClearance;
    private int authThresholdMaxRise;
    private boolean useModelUpdate;
    private int modelUpdateDailyLimit;
    private String href;

    public String appModelId;


    public KnurldAppModel() {

    }

    public KnurldAppModel(String developerId, String authorization, int enrollmentRepeats, JSONArray vocabulary, int verificationLength, float threshold, boolean autoThresholdEnable, int autoThresholdClearance, int authThresholdMaxRise, boolean useModelUpdate, int modelUpdateDailyLimit, String href, String appModelId) {
        this.developerId = developerId;
        this.authorization = authorization;
        this.enrollmentRepeats = enrollmentRepeats;
        this.vocabulary = vocabulary;
        this.verificationLength = verificationLength;
        this.threshold = threshold;
        this.autoThresholdEnable = autoThresholdEnable;
        this.autoThresholdClearance = autoThresholdClearance;
        this.authThresholdMaxRise = authThresholdMaxRise;
        this.useModelUpdate = useModelUpdate;
        this.modelUpdateDailyLimit = modelUpdateDailyLimit;
        this.href = href;
        this.appModelId = appModelId;
    }

    public JSONArray getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(JSONArray vocabulary) {
        this.vocabulary = vocabulary;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.appModelId = href.substring(href.lastIndexOf("/") + 1);
        this.href = href;
    }

    public void buildFromResponse(String result) {
        JSONObject jsonParam = null;

        try {
            jsonParam = new JSONObject(result);
            JSONArray items = jsonParam.has("items") ? jsonParam.getJSONArray("items") : null;
            if (items != null && items.length() > 0) {
                JSONObject item = (JSONObject) items.get(0);
                String h = item.has("href") ? item.getString("href") : null;
                JSONArray vocab = item.has("vocabulary") ? item.getJSONArray("vocabulary") : null;
                if (h != null) {
                    setHref(h);
                }
                if (vocab != null) {
                    setVocabulary(vocab);
                }
            } else {
                String h = jsonParam.has("href") ? jsonParam.getString("href") : null;
                JSONArray vocab = jsonParam.has("vocabulary") ? jsonParam.getJSONArray("vocabulary") : null;
                if (h != null) {
                    setHref(h);
                }
                if (vocab != null) {
                    setVocabulary(vocab);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
