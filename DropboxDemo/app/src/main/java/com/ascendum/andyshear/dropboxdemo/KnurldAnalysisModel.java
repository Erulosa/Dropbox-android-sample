package com.ascendum.andyshear.dropboxdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyshear on 1/18/16.
 */
public class KnurldAnalysisModel {

    public JSONArray intervals;
    public String taskName;

    public void buildFromResponse(String result) {
        JSONObject jsonParam = null;

        try {
            jsonParam = new JSONObject(result);
            taskName = jsonParam.has("taskName") ? jsonParam.getString("taskName") : null;
            intervals = jsonParam.has("intervals") ? jsonParam.getJSONArray("intervals") : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
