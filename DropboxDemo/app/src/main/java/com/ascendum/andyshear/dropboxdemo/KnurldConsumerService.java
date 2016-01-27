package com.ascendum.andyshear.dropboxdemo;

/**
 * Created by andyshear on 1/24/16.
 */
public class KnurldConsumerService {

    private KnurldAsyncTask knurldAsync;
    AsyncKnurldResponse response;

    public KnurldConsumerService(AsyncKnurldResponse response) {
        this.response = response;
    }

    public KnurldConsumerModel indexConsumer(){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.GET, "consumers", null, null);
        return new KnurldConsumerModel();
    }

    public void showConsumer(String consumer){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.GET, "consumers", consumer, null);
    }

    public void createConsumer(String body){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.POST, "consumers", null, body);
    }

    public void updateConsumer(String consumer, String body){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.POST, "consumers", consumer, body);
    }
}
