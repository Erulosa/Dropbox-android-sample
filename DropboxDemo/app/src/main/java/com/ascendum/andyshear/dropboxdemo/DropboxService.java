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
                dropboxItem.entry = mDBApi.metadata(path, 15, null, true, null);

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
