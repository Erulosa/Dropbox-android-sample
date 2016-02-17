package com.knurld.dropboxdemo.model;

import com.knurld.dropboxdemo.service.KnurldModelService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyshear on 2/16/16.
 */
public class VerificationModel extends KnurldModelService {
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

    @Override
    public void buildFromResponse(String response) {

        JSONObject jsonParam = null;

        try {
            jsonParam = new JSONObject(response);
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

    @Override
    public void buildFromId(String id) {
        this.verificationId = id;
        if (id == null) {
            buildFromResponse(index());
        } else {
            buildFromResponse(show(id));
        }
    }

    @Override
    public String index() {
        return getRequest("verifications", null);
    }

    @Override
    public String show(String urlParam) {
        return getRequest("verifications", urlParam);
    }

    @Override
    public String create(String body) {
        return postRequest("verifications", null, body);
    }

    @Override
    public String update(String... params) {
        return postRequest("verifications", params[0], params[1]);
    }
}
