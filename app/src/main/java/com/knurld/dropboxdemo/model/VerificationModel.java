package com.knurld.dropboxdemo.model;

import com.knurld.dropboxdemo.service.KnurldModelService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyshear on 2/16/16.
 */
public class VerificationModel extends Analysis {
    public static String endpoint = "verifications";
    public String getEndpoint() { return VerificationModel.endpoint; }
}
