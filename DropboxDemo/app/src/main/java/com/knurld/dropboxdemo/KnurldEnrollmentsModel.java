// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.knurld.dropboxdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KnurldEnrollmentsModel {
    private String developerId;
    private String authorization;
    private String consumer;
    private String application;
    private String href;

    public String enrollmentId;

    public JSONArray intervals;
    public JSONArray phrasesArray;
    public String phrases;
    public boolean enrolled;
    public boolean failed;

    public KnurldEnrollmentsModel() {

    }

    public KnurldEnrollmentsModel(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public KnurldEnrollmentsModel(String developerId, String authorization, String consumer, String application) {
        this.failed = false;
        this.enrolled = false;
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
                JSONObject item = (JSONObject) items.get(items.length()-1);
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
                enrolled = item.has("status") && item.getString("status").contains("completed");
                intervals = item.has("intervals") ? item.getJSONArray("intervals") : null;
                String h = item.has("href") ? item.getString("href") : null;
                if (h != null) {
                    setHref(h);
                }
            } else {
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
                enrolled = jsonParam.has("status") && jsonParam.getString("status").contains("completed");
                failed = jsonParam.has("status") && jsonParam.getString("status").contains("failed");
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
