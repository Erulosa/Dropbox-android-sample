package com.ascendum.andyshear.dropboxdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Created by andyshear on 1/11/16.
 */
public class KnurldVerificationModel {
    private String developerId;
    private String consumer;
    private String appModel;
    private String verifivationWAV;
    public JSONArray intervals;
    public boolean verified;
    public boolean failed;
    public boolean isActive;
    public String phrases;
    public JSONArray phrasesArray;

    private String href;

    public String verificationId;
    public String activeVerification;

    public KnurldVerificationModel() {
        verified = false;
        failed = false;
    }

    public KnurldVerificationModel(String developerId, String consumer, String appModel) {
        this.developerId = developerId;
        this.consumer = consumer;
        this.appModel = appModel;
        this.verified = false;
        this.failed = false;
    }

    public String getVerifivationWAV() {
        return verifivationWAV;
    }

    public void setVerifivationWAV(String verifivationWAV) {
        this.verifivationWAV = verifivationWAV;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.verificationId = href.substring(href.lastIndexOf("/") + 1);
        this.href = href;
    }

    public void setVerification(String href) {
        this.activeVerification = href.substring(href.lastIndexOf("/") + 1);
        this.href = href;
    }


    public boolean isVerified() {
        return verified;
    }

    public void buildFromResponse(String result) {
        JSONObject jsonParam = null;

        try {
            jsonParam = new JSONObject(result);
            JSONArray items = jsonParam.has("items") ? jsonParam.getJSONArray("items") : null;
            if (items != null && items.length() > 0) {
                JSONObject item = (JSONObject) items.get(0);
                intervals = item.has("intervals") ? item.getJSONArray("intervals") : null;
                JSONObject instructions = item.has("instructions") ? item.getJSONObject("instructions") : null;
                if (instructions != null) {
                    JSONObject data = instructions.has("data") ? instructions.getJSONObject("data") : null;
                    if (data != null) {
                        phrasesArray = data.has("phrases") ? data.getJSONArray("phrases") : null;
                        phrases = "";
                        if (phrasesArray != null) {
                            for (int i = 0; i < phrasesArray.length(); i++) {
                                phrases += phrasesArray.getString(i) + " ";
                            }
                        }
                    }
                }
                failed = item.has("status") && item.getString("status").contains("failed");
                verified = item.has("status") && item.getString("status").contains("completed");
                if (!verified) {
                    isActive = jsonParam.has("status") && jsonParam.getString("status").contains("initialized");
                }
                String h = item.has("href") ? item.getString("href") : null;
                if (h != null && isActive) {
                    setHref(h);
                }
            } else {
                intervals = jsonParam.has("intervals") ? jsonParam.getJSONArray("intervals") : null;
                JSONObject instructions = jsonParam.has("instructions") ? jsonParam.getJSONObject("instructions") : null;
                if (instructions != null) {
                    JSONObject data = instructions.has("data") ? instructions.getJSONObject("data") : null;
                    if (data != null) {
                        phrasesArray = data.has("phrases") ? data.getJSONArray("phrases") : null;
                        phrases = "";
                        if (phrasesArray != null) {
                            for (int i = 0; i < phrasesArray.length(); i++) {
                                phrases += phrasesArray.getString(i) + " ";
                            }
                        }

                    }
                }

                failed = jsonParam.has("status") && jsonParam.getString("status").contains("failed");
                verified = jsonParam.has("status") && jsonParam.getString("status").contains("completed");
                if (!verified) {
                    isActive = jsonParam.has("status") && jsonParam.getString("status").contains("initialized");
                }
                String h = jsonParam.has("href") ? jsonParam.getString("href") : null;
                if (this.activeVerification != null) {
                    setHref(h);
                }
                if (h != null && !verified) {
                    setVerification(h);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
