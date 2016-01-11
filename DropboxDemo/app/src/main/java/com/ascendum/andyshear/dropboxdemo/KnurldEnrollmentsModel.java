package com.ascendum.andyshear.dropboxdemo;

/**
 * Created by andyshear on 1/11/16.
 */
public class KnurldEnrollmentsModel {
    private String developerId;
    private String authorization;
    private String consumer;
    private String application;

    public KnurldEnrollmentsModel(String developerId, String authorization, String consumer, String application) {
        this.developerId = developerId;
        this.authorization = authorization;
        this.consumer = consumer;
        this.application = application;
    }
}
