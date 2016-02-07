// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.ascendum.andyshear.dropboxdemo;

public class KnurldAnalysisService {

    private KnurldAsyncTask knurldAsync;
    private KnurldAPIResource knurldAPIResource;
    AsyncKnurldResponse response;

    public KnurldAnalysisService(AsyncKnurldResponse response) {
        this.response = response;
    }

    public KnurldAnalysisService(String token) {
        knurldAPIResource = new KnurldAPIResource(token);
    }

    public void showEndpointAnalysis(String endpointAnalysis){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.GET, "endpointAnalysis", endpointAnalysis, null);
    }

    public KnurldAnalysisModel createEndpointAnalysis(String body){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.POST, "endpointAnalysis", null, body);
        return new KnurldAnalysisModel();
    }

    public String[] showAnalysis(String endpointAnalysis){
        return knurldAPIResource.request(KnurldUtility.GET, "endpointAnalysis", endpointAnalysis, null);
    }

    public String[] createAnalysis(String body){
        return knurldAPIResource.request(KnurldUtility.POST, "endpointAnalysis", null, body);
    }
}
