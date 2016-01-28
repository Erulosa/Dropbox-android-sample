package com.ascendum.andyshear.dropboxdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyshear on 1/11/16.
 */
public class KnurldEnrollmentsModel {
    private String developerId;
    private String authorization;
    private String consumer;
    private String application;
    private String href;

    public String enrollmentId;

    public JSONArray intervals;

    public KnurldEnrollmentsModel() {

    }

    public KnurldEnrollmentsModel(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public KnurldEnrollmentsModel(String developerId, String authorization, String consumer, String application) {
        this.developerId = developerId;
        this.authorization = authorization;
        this.consumer = consumer;
        this.application = application;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.enrollmentId = href.substring(href.lastIndexOf("/") + 1);
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
