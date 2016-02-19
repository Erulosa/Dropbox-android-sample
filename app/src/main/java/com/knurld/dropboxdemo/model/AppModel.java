package com.knurld.dropboxdemo.model;

import com.knurld.dropboxdemo.service.KnurldModelService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyshear on 2/16/16.
 */
public class AppModel extends KnurldModelService {
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

    public JSONArray getVocabulary() {
        return vocabulary;
    }

    public int getVerificationLength() {
        return verificationLength;
    }

    public int getEnrollmentRepeats() {
        return enrollmentRepeats;
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


    @Override
    public void buildFromResponse(String response) {
        JSONObject jsonParam = null;

        try {
            jsonParam = new JSONObject(response);
            JSONArray items = jsonParam.has("items") ? jsonParam.getJSONArray("items") : null;
            if (items != null && items.length() > 0) {
                JSONObject item = (JSONObject) items.get(items.length()-1);
                String h = item.has("href") ? item.getString("href") : null;
                JSONArray vocab = item.has("vocabulary") ? item.getJSONArray("vocabulary") : null;
                enrollmentRepeats = item.has("enrollmentRepeats") ? item.getInt("enrollmentRepeats") : null;
                verificationLength = item.has("verificationLength") ? item.getInt("verificationLength") : null;
                if (h != null) {
                    setHref(h);
                }
                if (vocab != null) {
                    setVocabulary(vocab);
                }
            } else {
                String h = jsonParam.has("href") ? jsonParam.getString("href") : null;
                enrollmentRepeats = jsonParam.has("enrollmentRepeats") ? jsonParam.getInt("enrollmentRepeats") : null;
                verificationLength = jsonParam.has("verificationLength") ? jsonParam.getInt("verificationLength") : null;
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

    @Override
    public void buildFromId(String id) {
        this.appModelId = id;
        if (id == null) {
            buildFromResponse(index());
        } else {
            buildFromResponse(show(id));
        }
    }

    @Override
    public String index() {
        return getRequest("app-models", null);
    }

    @Override
    public String show(String urlParam) {
        return getRequest("app-models", urlParam);
    }

    @Override
    public String create(String body) {
        return postRequest("app-models", null, body);
    }

    @Override
    public String update(String... params) {
        return postRequest("app-models", params[0], params[1]);
    }
}
