package com.knurld.dropboxdemo.model;

import com.knurld.dropboxdemo.service.KnurldModelService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyshear on 2/16/16.
 */
public class EnrollmentModel extends KnurldModelService {
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

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.enrollmentId = href.substring(href.lastIndexOf("/") + 1);
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
                JSONObject instructions = item.has("instructions") ? item.getJSONObject("instructions") : null;
                JSONObject data = (instructions != null) && instructions.has("data") ? instructions.getJSONObject("data") : null;
                phrasesArray = ((data != null) && data.has("phrases")) ? data.getJSONArray("phrases") : null;
                phrases = phrasesArray != null ? phrasesArray.join(", ") : null;
//                if (instructions != null) {
////                    JSONObject data = instructions.has("data") ? instructions.getJSONObject("data") : null;
//                    if (data != null) {
//                        phrasesArray = data.has("phrases") ? data.getJSONArray("phrases") : null;
//                        phrases = phrasesArray.join(", ");
////                        if (phrasesArray != null) {
////                            for (int i = 0; i < phrasesArray.length(); i++) {
////                                phrases += phrasesArray.getString(i) + " ";
////                            }
////                        }
//                    }
//                }
                failed = item.has("status") && item.getString("status").contains("failed");
                enrolled = item.has("status") && item.getString("status").contains("completed");
                intervals = item.has("intervals") ? item.getJSONArray("intervals") : null;
                String h = item.has("href") ? item.getString("href") : null;
                if (h != null) {
                    setHref(h);
                }
            } else {
                JSONObject instructions = jsonParam.has("instructions") ? jsonParam.getJSONObject("instructions") : null;
                JSONObject data = (instructions != null) && instructions.has("data") ? instructions.getJSONObject("data") : null;
                phrasesArray = ((data != null) && data.has("phrases")) ? data.getJSONArray("phrases") : null;
                phrases = phrasesArray != null ? phrasesArray.join(", ") : null;
//                if (instructions != null) {
//                    JSONObject data = instructions.has("data") ? instructions.getJSONObject("data") : null;
//                    if (data != null) {
//                        phrasesArray = data.has("phrases") ? data.getJSONArray("phrases") : null;
//                        phrases = phrasesArray.join(", ");
////                        if (phrasesArray != null) {
////                            for (int i = 0; i < phrasesArray.length(); i++) {
////                                phrases += phrasesArray.getString(i) + " ";
////                            }
////                        }
//                    }
//                }
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

    @Override
    public void buildFromId(String id) {
        this.enrollmentId = id;
        if (id == null) {
            buildFromResponse(index());
        } else {
            buildFromResponse(show(id));
        }
    }

    @Override
    public String index() {
        return request("GET", "enrollments", null);
    }

    @Override
    public String show(String urlParam) {
        return request("GET", "enrollments", urlParam);
    }

    @Override
    public String create(String body) {
        return request("POST", "enrollments", null, body);
    }

    @Override
    public String update(String... params) {
        return request("POST", "enrollments", params[0], params[1]);
    }
}
