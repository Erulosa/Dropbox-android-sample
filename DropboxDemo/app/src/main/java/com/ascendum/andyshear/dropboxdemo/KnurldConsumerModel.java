package com.ascendum.andyshear.dropboxdemo;

/**
 * Created by andyshear on 1/11/16.
 */
public class KnurldConsumerModel {

    private String developerId;
    private String authorization;
    private String gender;
    private String username;
    private String password;

    public KnurldConsumerModel(String developerId, String authorization, String gender, String username, String password) {
        this.developerId = developerId;
        this.authorization = authorization;
        this.gender = gender;
        this.username = username;
        this.password = password;
    }
}
