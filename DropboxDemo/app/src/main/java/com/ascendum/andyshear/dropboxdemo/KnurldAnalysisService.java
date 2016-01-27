package com.ascendum.andyshear.dropboxdemo;

/**
 * Created by andyshear on 1/24/16.
 */
public class KnurldAnalysisService {

    private KnurldAsyncTask knurldAsync;
    AsyncKnurldResponse response;

    public KnurldAnalysisService(AsyncKnurldResponse response) {
        this.response = response;
    }

    public void showEndpointAnalysis(String endpointAnalysis){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.GET, "endpointAnalysis", endpointAnalysis, null);
    }

    public KnurldAnalysisModel createEndpointAnalysis(String body){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.POST, "endpointAnalysis", null, body);
        return new KnurldAnalysisModel();
    }
}
