package com.ascendum.andyshear.dropboxdemo;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.util.ArrayList;

/**
 * Created by andyshear on 12/30/15.
 */
public class DropboxService{
    final static private String APP_KEY = "d3zx13rhlc2jbpr";
    final static private String APP_SECRET = "rfnin8j6dr3uhuv";


    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private String accessToken;

    public final static String DROPBOX_ITEMS = "com.ascendum.andyshear.dropboxdemo.ITEMS";

    private DropboxAPI<AndroidAuthSession> mDBApi;
    public DropboxItem dropboxItem;
    AsyncResponse response;


    public DropboxService(DropboxAPI<AndroidAuthSession> mDBApi, AsyncResponse response, String path) {
        this.response = response;
        if (path.equals("/")) {
            login(mDBApi, response);
        }

    }

    private void login(DropboxAPI<AndroidAuthSession> mDBApi, AsyncResponse response){
        this.mDBApi = mDBApi;
        DropboxMetadata login = new DropboxMetadata();
        login.delegate = response;
        login.execute("");
    }

    public void getPath(DropboxAPI<AndroidAuthSession> mDBApi, AsyncResponse response, String path){
        this.mDBApi = mDBApi;
        DropboxMetadata items = new DropboxMetadata();
        items.delegate = response;
        items.execute(path);
    }



    private class DropboxMetadata extends AsyncTask<String, String, DropboxItem> {
        public AsyncResponse delegate;
        @Override
        protected DropboxItem doInBackground(String... params) {

            String path = params[0];

            DropboxItem dropboxItem = new DropboxItem();
            try {
                dropboxItem.entry = mDBApi.metadata(path, 10, null, true, null);

            } catch (DropboxException e) {
                e.printStackTrace();
            }
            return dropboxItem;

        }

        protected void onPostExecute(DropboxItem dropboxItem) {
            delegate.processFinish(dropboxItem);
        }
    }



}
