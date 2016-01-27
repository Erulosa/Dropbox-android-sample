package com.ascendum.andyshear.dropboxdemo;

/**
 * Created by andyshear on 1/24/16.
 */
public class KnurldAppModelService {

    public String appModelId;
    public String body;
    private KnurldAsyncTask knurldAsync;
    AsyncKnurldResponse response;

    public KnurldAppModelService(AsyncKnurldResponse response) {
        this.response = response;
    }

    public KnurldAppModel indexAppModel(){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.GET, "app-models", null, null);
        return new KnurldAppModel();
    }

    public void showAppModel(String appModel){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.GET, "app-models", appModel, null);
    }

    public void createAppModel(String body){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.POST, "app-models", null, body);
    }

    public void updateAppModel(String appModel, String body){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.POST, "app-models", appModel, body);
    }
}
