// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.ascendum.andyshear.dropboxdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
