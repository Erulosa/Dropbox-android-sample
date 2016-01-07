package com.ascendum.andyshear.dropboxdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

/**
 * Created by andyshear on 1/5/16.
 */
public class DropboxAuthActivity extends Activity {

    final static private String APP_KEY = "d3zx13rhlc2jbpr";
    final static private String APP_SECRET = "rfnin8j6dr3uhuv";


    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final String FILE_PATH = "FILE_PATH";
    private static final String DROPBOX_ITEM = "DROPBOX_ITEM";

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    private String accessToken;

    public final static String DROPBOX_ITEMS = "com.ascendum.andyshear.dropboxdemo.ITEMS";

    // In the class declaration section:
    protected DropboxAPI<AndroidAuthSession> mDBApi;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_loading);

        Intent intent = getIntent();

        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        if (mDBApi.getSession().isLinked()) {
//            DropboxActivity.mDBApi = mDBApi;
            intent = new Intent(this, DropboxActivity.class);
            accessToken = mDBApi.getSession().getOAuth2AccessToken();
            intent.putExtra(ACCESS_TOKEN, accessToken);
            startActivity(intent);
        } else {
            mDBApi.getSession().startOAuth2Authentication(DropboxAuthActivity.this);
        }

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
                this.accessToken = accessToken;

                SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(ACCESS_KEY_NAME, "oauth2:");
                edit.putString(ACCESS_SECRET_NAME, accessToken);
                edit.commit();

                context = this;

//                DropboxActivity.mDBApi = mDBApi;
                Intent intent = new Intent(this, DropboxActivity.class);
                intent.putExtra(ACCESS_TOKEN, accessToken);
                startActivity(intent);


            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }


    }

}
