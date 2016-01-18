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

    private String href;

    public String verificationId;

    public KnurldVerificationModel() {

    }

    public KnurldVerificationModel(String developerId, String consumer, String appModel) {
        this.developerId = developerId;
        this.consumer = consumer;
        this.appModel = appModel;
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

    public void buildFromResponse(String result) {
        JSONObject jsonParam = null;

        try {
            jsonParam = new JSONObject(result);
            JSONArray items = jsonParam.has("items") ? jsonParam.getJSONArray("items") : null;
            if (items != null && items.length() > 0) {
                JSONObject item = (JSONObject) items.get(0);
                intervals = item.has("intervals") ? item.getJSONArray("intervals") : null;
                String h = item.has("href") ? item.getString("href") : null;
                if (h != null) {
                    setHref(h);
                }
            } else {
                intervals = jsonParam.has("intervals") ? jsonParam.getJSONArray("intervals") : null;
                String h = jsonParam.has("href") ? jsonParam.getString("href") : null;
                if (h != null) {
                    setHref(h);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
