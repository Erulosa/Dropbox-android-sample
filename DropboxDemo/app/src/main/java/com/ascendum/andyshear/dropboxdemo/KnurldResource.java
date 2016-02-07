// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.ascendum.andyshear.dropboxdemo;

public abstract class KnurldResource {

    protected String endpoint;
    protected AsyncKnurldResponse response;

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setResponse(AsyncKnurldResponse response) {
        this.response = response;
    }

    public abstract String getEndpoint();
    public abstract AsyncKnurldResponse getResponse();

    public KnurldResource index() {
        new KnurldAsyncTask(getResponse(), KnurldUtility.GET, this.getEndpoint(), null, null);
        return this;
    }

    public KnurldResource show(String id) {
        new KnurldAsyncTask(getResponse(), KnurldUtility.GET, this.getEndpoint(), id, null);
        return this;
    }

    public KnurldResource create(String body) {
        new KnurldAsyncTask(getResponse(), KnurldUtility.POST, this.getEndpoint(), null, body);
        return this;
    }

    public KnurldResource update(String id, String body) {
        new KnurldAsyncTask(getResponse(), KnurldUtility.POST, this.getEndpoint(), id, body);
        return this;
    }

}
