// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.ascendum.andyshear.dropboxdemo;

public class KnurldVerificationService {

    private KnurldAsyncTask knurldAsync;
    private KnurldAPIResource knurldAPIResource;
    AsyncKnurldResponse response;

    public KnurldVerificationService(AsyncKnurldResponse response) {
        this.response = response;
    }

    public KnurldVerificationService(String token) {
        knurldAPIResource = new KnurldAPIResource(token);
    }

    public KnurldVerificationModel indexVerification(){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.GET, "verifications", null, null);
        return new KnurldVerificationModel();
    }

    public void showVerification(String verification){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.GET, "verifications", verification, null);
    }

    public void createVerification(String body){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.POST, "verifications", null, body);
    }

    public void updateVerification(String verification, String body){
        knurldAsync = new KnurldAsyncTask(response, KnurldUtility.POST, "verifications", verification, body);
    }

    public String[] show(String verification){
        return knurldAPIResource.request(KnurldUtility.GET, "verifications", verification, null);
    }

    public String[] create(String body){
        return knurldAPIResource.request(KnurldUtility.POST, "verifications", null, body);
    }

    public String[] update(String verification, String body){
        return knurldAPIResource.request(KnurldUtility.POST, "verifications", verification, body);
    }
}
