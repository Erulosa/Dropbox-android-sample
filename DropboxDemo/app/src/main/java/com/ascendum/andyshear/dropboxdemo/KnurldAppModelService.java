package com.ascendum.andyshear.dropboxdemo;


import org.json.JSONArray;

/**
 * Created by andyshear on 1/24/16.
 */
public class KnurldAppModelService extends KnurldResource {

    private KnurldAppModel knurldAppModel;

    public KnurldAppModelService(AsyncKnurldResponse response) {
        this.response = response;
        knurldAppModel = new KnurldAppModel();
    }

    @Override
     public String getEndpoint() {
        if (this.endpoint == null) setEndpoint("app-models");
        return this.endpoint;
    }

    @Override
    public AsyncKnurldResponse getResponse() {
        return this.response;
    }

    public KnurldAppModel getKnurldAppModel() {
        return this.knurldAppModel;
    }

    public void setKnurldAppModel(String knurldAppModel) {
        this.knurldAppModel.buildFromResponse(knurldAppModel);
    }

    public String getHref() {
        return this.knurldAppModel.getHref();
    }

    public JSONArray getVocab() {
        return this.knurldAppModel.getVocabulary();
    }

    public int getVerificationLength() {
        return this.knurldAppModel.getVerificationLength();
    }

    public String getAppModelId() {
        return this.knurldAppModel.appModelId;
    }

    public boolean appModelReady() {
        return this.knurldAppModel.appModelId != null;
    }
}
