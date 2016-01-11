package com.ascendum.andyshear.dropboxdemo;

/**
 * Created by andyshear on 1/11/16.
 */
public class KnurldVerificationModel {
    private String developerId;
    private String consumer;
    private String appModel;

    public KnurldVerificationModel(String developerId, String consumer, String appModel) {
        this.developerId = developerId;
        this.consumer = consumer;
        this.appModel = appModel;
    }
}
