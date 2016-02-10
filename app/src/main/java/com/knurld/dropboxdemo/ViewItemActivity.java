// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.knurld.dropboxdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;

public class ViewItemActivity extends Activity implements AsyncResponse{

    final static private String APP_KEY = "d3zx13rhlc2jbpr";
    final static private String APP_SECRET = "rfnin8j6dr3uhuv";


    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final String FILE_PATH = "FILE_PATH";

    protected DropboxAPI<AndroidAuthSession> mDBApi;
    public Context context;

    public String folderPath;

    public DropboxService dropboxService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_loading);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressSpinner);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(226, 132, 59), PorterDuff.Mode.MULTIPLY);

        Intent intent = getIntent();

        if (mDBApi == null) {
            AndroidAuthSession session = buildSession();
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        }

        String filePathIntent = intent.getStringExtra(FILE_PATH);

        if (mDBApi.getSession().isLinked() && filePathIntent == null) {
            context = this;
            dropboxService = new DropboxService(mDBApi, this, "/");

        } else if (mDBApi.getSession().isLinked() && filePathIntent.equals("/")) {
            context = this;
            dropboxService = new DropboxService(mDBApi, this, "/");

        }else if (mDBApi.getSession().isLinked() && filePathIntent.startsWith("/")) {
            context = this;
            dropboxService = new DropboxService(mDBApi, this, filePathIntent);
            dropboxService.getPath(mDBApi, this, filePathIntent);

        } else {
            mDBApi.getSession().startOAuth2Authentication(ViewItemActivity.this);
        }


        folderPath = "/";
    }

    public void onStop() {
        super.onStop();
        finish();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);

        SharedPreferences preferences = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = preferences.getString(ACCESS_KEY_NAME, null);
        String secret = preferences.getString(ACCESS_SECRET_NAME, null);

        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) {
            return session;
        }
        if (key.equals("oauth2:")) {
            session.setOAuth2AccessToken(secret);
        }
        return session;
    }

    protected void onResume() {
        super.onResume();

    }

    @Override
    public void processFinish(String method, String output) {

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        setContentView(R.layout.activity_preview);

        ImageView image = (ImageView)findViewById(R.id.preview);
        TextView text = (TextView)findViewById(R.id.previewLabel);
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path, "test"+ dropboxService.dropboxItem.entry.fileName() +"Preview.jpeg");
        image.setImageURI(Uri.fromFile(file));
        text.setText(dropboxService.dropboxItem.entry.fileName());
    }


    @Override
    public void processFinish(DropboxItem folder) {
        dropboxService.dropboxItem = folder;

        String path = dropboxService.dropboxItem.entry.path;
        String fileName = dropboxService.dropboxItem.entry.fileName();
        String type = dropboxService.dropboxItem.entry.mimeType;
        DownloadPreview downloadPreview = new DownloadPreview(context);
        downloadPreview.done = this;
        if(type == null){
        } else if(type.startsWith("application/")){
            downloadPreview.setDoc(path, mDBApi);
        } else {
            downloadPreview.downloadThumb(path, fileName, mDBApi);
        }
    }

}
