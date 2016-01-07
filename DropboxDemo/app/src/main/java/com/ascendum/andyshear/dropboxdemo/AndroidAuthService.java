package com.ascendum.andyshear.dropboxdemo;

import android.content.SharedPreferences;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

/**
 * Created by andyshear on 1/6/16.
 */
public class AndroidAuthService {

    final static private String APP_KEY = "d3zx13rhlc2jbpr";
    final static private String APP_SECRET = "rfnin8j6dr3uhuv";


    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    protected static DropboxAPI<AndroidAuthSession> mDBApi;


    public AndroidAuthService(DropboxAPI<AndroidAuthSession> mDBApi){
        this.mDBApi = mDBApi;
    }





}
