// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.ascendum.andyshear.dropboxdemo;

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
