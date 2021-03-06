// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.knurld.dropboxdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

public class MainActivity extends Activity {

    final static private String APP_KEY = "d3zx13rhlc2jbpr";
    final static private String APP_SECRET = "rfnin8j6dr3uhuv";

    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    protected DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

    }

    public void dropbox(View view) {

        if (mDBApi.getSession().isLinked()) {
            Intent intent = new Intent(this, DropboxActivity.class);
            startActivity(intent);
        } else {
            mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
        }

    }

    public void knurldSettings(View view) {
        Intent intent = new Intent(this, KnurldActivity.class);
        startActivity(intent);
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

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();


                SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(ACCESS_KEY_NAME, "oauth2:");
                edit.putString(ACCESS_SECRET_NAME, accessToken);
                edit.commit();

                Intent intent = new Intent(this, DropboxActivity.class);
                startActivity(intent);


            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }

    }


}
