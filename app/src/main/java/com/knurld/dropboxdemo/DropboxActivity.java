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
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.knurld.dropboxdemo.service.*;

import java.util.ArrayList;

public class DropboxActivity extends Activity implements AsyncResponse {

    final static private String APP_KEY = "d3zx13rhlc2jbpr";
    final static private String APP_SECRET = "rfnin8j6dr3uhuv";


    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final String DROPBOX_UID = "DROPBOX_UID";

    private static final String FILE_PATH = "FILE_PATH";

    protected DropboxAPI<AndroidAuthSession> mDBApi;

    public ListView listView;
    public String folderPath;

    public DropboxService dropboxService;

    public KnurldService knurldService;

    private static final String KNURLD_TOKEN = "KNURLD_TOKEN";
    private static final String KNURLD_APP_MODEL = "KNURLD_APP_MODEL";
    private static final String KNURLD_CONSUMER = "KNURLD_CONSUMER";
    private static final String KNURLD_VERIFICATION = "KNURLD_VERIFICATION";
    private static final String KNURLD_ENROLLMENT = "KNURLD_ENROLLMENT";

    private ArrayList<String> lockedFiles;

    public VerificationItem verificationItem;

    private Context context;

    private Thread knurldServiceThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_loading);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressSpinner);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(226, 132, 59), PorterDuff.Mode.MULTIPLY);

        Intent intent = getIntent();

        lockedFiles = new ArrayList<String>();

        final String knurldToken = intent.getStringExtra(KNURLD_TOKEN);
        final String knurldApp = intent.getStringExtra(KNURLD_APP_MODEL);
        final String knurldConsumer = intent.getStringExtra(KNURLD_CONSUMER);
        final String knurldEnrollment = intent.getStringExtra(KNURLD_ENROLLMENT);


        knurldServiceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                knurldService = new KnurldService(knurldToken, knurldApp, knurldConsumer, knurldEnrollment);
            }
        });
        knurldServiceThread.start();

        context = this;


        // Dropbox state management, need to refactor to DropboxService
        String username = null;
        if (mDBApi == null) {
            AndroidAuthSession session = buildSession();
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            username = getDropboxUsername();
        }

        String filePathIntent = intent.getStringExtra(FILE_PATH);
        lockedFiles = ((ArrayList<String>) LockedItems.getItems(this, "locked") == null) ? new ArrayList<String>() : (ArrayList<String>) LockedItems.getItems(this, "locked");

        if (mDBApi.getSession().isLinked() && filePathIntent == null) {
            dropboxService = new DropboxService(mDBApi, this, "/");

        } else if (mDBApi.getSession().isLinked() && filePathIntent != null) {
            if (mDBApi.getSession().isLinked() && filePathIntent.equals("/")) {
                dropboxService = new DropboxService(mDBApi, this, "/");

            } else if (mDBApi.getSession().isLinked() && filePathIntent.startsWith("/")) {
                dropboxService = new DropboxService(mDBApi, this, filePathIntent);
                dropboxService.getPath(mDBApi, this, filePathIntent);
            }
        } else {
            mDBApi.getSession().startOAuth2Authentication(DropboxActivity.this);
        }


        folderPath = "/";
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
        // Get list of locked files
        lockedFiles = ((ArrayList<String>) LockedItems.getItems(this, "locked") == null) ? new ArrayList<String>() : (ArrayList<String>) LockedItems.getItems(this, "locked");
    }

    protected void onPause() {
        super.onPause();
        // Update list of locked files, removing any repeats
        ArrayList<String> updatedLock = new ArrayList<String>();
        for (String file : lockedFiles) {
            if (!updatedLock.contains(file)) {
                updatedLock.add(file);
            }
        }
        LockedItems.saveItems(this, "locked", updatedLock);
    }

    public void toggleLockOn(final String item, String message, final Boolean locked, final String verificationId ,final String phrases) {

        Activity parent = (Activity) context;
        final View view = LayoutInflater.from(parent).inflate(R.layout.activity_folder_swipe, null);

        final PopupWindow loadingWindow = showLoadingPopup(view);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean verified = false;
                verified = knurldService.verify(verificationId, phrases);

                if (!verified) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingWindow.dismiss();
                            showErrorPopup(view);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingWindow.dismiss();
                            verificationItem = new VerificationItem();
                            verificationItem.itemName = item;
                            verificationItem.locked = locked;
                            authenticateItem();
                        }
                    });

                }
            }
        }).start();

    }

    public PopupWindow showLoadingPopup(View view) {
        View spinnerView = LayoutInflater.from((Activity) context).inflate(R.layout.loading_popup, null);
        ProgressBar progressBar = (ProgressBar) spinnerView.findViewById(R.id.speakProgress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        PopupWindow popupWindow = new PopupWindow(spinnerView, 500, 500);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    public void showErrorPopup(View view) {
        View errorView = LayoutInflater.from(context).inflate(R.layout.error_popup, null);
        final PopupWindow errorWindow = new PopupWindow(errorView, 500, 500);
        errorWindow.setFocusable(true);
        errorWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        errorWindow.dismiss();
                    }
                }, 3000);
    }

    public void authenticateItem() {
        setItem(verificationItem.locked, verificationItem.itemName);
    }

    public void setItem(boolean locked, String item) {
        if (locked) {
            lockedFiles.add(item);
            LockedItems.saveItems(this, "locked", lockedFiles);
            Toast.makeText(context, "Item is Locked", Toast.LENGTH_SHORT).show();
        } else {
            lockedFiles.remove(item);
            LockedItems.saveItems(this, "locked", lockedFiles);
            Toast.makeText(context, "Item is Unlocked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void processFinish(String method, String output) {

        if (output.equals("finished")) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            setContentView(R.layout.activity_folder_list);

            while (knurldServiceThread.isAlive()) {
                try {
                    knurldServiceThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ListViewSwipeAdapter adapter = new ListViewSwipeAdapter(this, dropboxService.dropboxItem, knurldService);
            listView = (ListView)findViewById(R.id.list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String fileType = dropboxService.dropboxItem.entry.contents.get(position).mimeType;
                    boolean locked = dropboxService.dropboxItem.entry.contents.get(position).readOnly;

                    if (locked) {
                        Toast.makeText(context, "Item is Locked", Toast.LENGTH_SHORT).show();
                    } else if (fileType == null) {
                        String folderPath = dropboxService.dropboxItem.entry.contents.get(position).path;
                        getFolder(folderPath);
                    } else if (fileType.startsWith("image") || fileType.startsWith("video")) {
                        String filePath = dropboxService.dropboxItem.entry.contents.get(position).path;
                        getFile(filePath);
                    }
                }
            });
        }
    }

    protected void getFolder(String folderPath){
        Intent intent = new Intent(this, DropboxActivity.class);
        intent.putExtra(FILE_PATH, folderPath);
        putStringExtra(intent);
        startActivity(intent);
    }

    protected void getFile(String filePath){
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra(FILE_PATH, filePath);
        putStringExtra(intent);
        startActivity(intent);
    }

    // Pass Id's from KnurldService to next activity
    private Intent putStringExtra(Intent intent) {
        if (knurldService.getClientToken() != null) {
            intent.putExtra(KNURLD_TOKEN, knurldService.getClientToken());
        }
        if (knurldService.getAppModel() != null) {
            intent.putExtra(KNURLD_APP_MODEL, knurldService.getAppModel().appModelId);
        }
        if (knurldService.getConsumerModel() != null) {
            intent.putExtra(KNURLD_CONSUMER, knurldService.getConsumerModel().consumerModelId);
        }
        if (knurldService.getEnrollmentModel() != null) {
            intent.putExtra(KNURLD_VERIFICATION, knurldService.getEnrollmentModel().resourceId);
        }
        return intent;
    }

    @Override
    public void processFinish(DropboxItem folder) {
        dropboxService.dropboxItem = folder;

        for(int i = 0; i < dropboxService.dropboxItem.entry.contents.size(); i++){
            String path = dropboxService.dropboxItem.entry.contents.get(i).path;
            String fileName = dropboxService.dropboxItem.entry.contents.get(i).fileName();
            String type = dropboxService.dropboxItem.entry.contents.get(i).mimeType;
            DownloadIcon iconDownload = new DownloadIcon(context);
            iconDownload.done = this;
            iconDownload.finished = this;

            if (lockedFiles != null && lockedFiles.contains(dropboxService.dropboxItem.entry.contents.get(i).fileName())) {
                dropboxService.dropboxItem.entry.contents.get(i).readOnly = true;
            }

            if(type == null){
                iconDownload.setFolder(path, mDBApi);
            } else if(type.startsWith("application/")){
                iconDownload.setDoc(path, mDBApi);
            } else if (type.startsWith("image") || type.startsWith("video")){
                iconDownload.downloadThumb(path, fileName, mDBApi);
            }

            if (i+1 == dropboxService.dropboxItem.entry.contents.size()) {
                iconDownload.finish();
            }
        }
    }


    // Get Dropbox username when user logs in, use this to create/get Knurld Consumer
    public String getDropboxUsername() {
        String dbx_uid = null;
        SharedPreferences preferences = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        dbx_uid = preferences.getString(DROPBOX_UID, null);
        SharedPreferences.Editor edit = preferences.edit();

        if (dbx_uid == null) {
            final DropboxAPI.Account[] account = new DropboxAPI.Account[1];
            Thread dbxThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        account[0] = mDBApi.accountInfo();
                    } catch (DropboxException e) {
                        e.printStackTrace();
                    }
                }
            });
            dbxThread.start();
            try {
                dbxThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            edit.putString(DROPBOX_UID, account[0].email);
            edit.commit();
            return account[0].email;
        } else {
            edit.putString(DROPBOX_UID, dbx_uid);
            edit.commit();
            return dbx_uid;
        }
    }

}
