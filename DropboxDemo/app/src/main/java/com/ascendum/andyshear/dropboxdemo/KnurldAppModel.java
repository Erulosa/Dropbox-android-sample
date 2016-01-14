package com.ascendum.andyshear.dropboxdemo;

import java.util.ArrayList;

/**
 * Created by andyshear on 1/11/16.
 */
public class KnurldAppModel {
    private String developerId;
    private String authorization;
    private int enrollmentRepeats;
    private ArrayList<String> vocabulary;
    private int verificationLength;
    private float threshold;
    private boolean autoThresholdEnable;
    private int autoThresholdClearance;
    private int authThresholdMaxRise;
    private boolean useModelUpdate;
    private int modelUpdateDailyLimit;
    private String href;

    public KnurldAppModel(String developerId, String authorization, int enrollmentRepeats,
                          ArrayList<String> vocabulary, int verificationLength, float threshold,
                          boolean autoThresholdEnable, int autoThresholdClearance, int authThresholdMaxRise,
                          boolean useModelUpdate, int modelUpdateDailyLimit, String href) {
        this.developerId = developerId;
        this.authorization = authorization;
        this.enrollmentRepeats = enrollmentRepeats;
        this.vocabulary = vocabulary;
        this.verificationLength = verificationLength;
        this.threshold = threshold;
        this.autoThresholdEnable = autoThresholdEnable;
        this.autoThresholdClearance = autoThresholdClearance;
        this.authThresholdMaxRise = authThresholdMaxRise;
        this.useModelUpdate = useModelUpdate;
        this.modelUpdateDailyLimit = modelUpdateDailyLimit;
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
