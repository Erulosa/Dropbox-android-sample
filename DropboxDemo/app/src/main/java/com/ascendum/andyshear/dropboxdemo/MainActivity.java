package com.ascendum.andyshear.dropboxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

public class MainActivity extends AppCompatActivity {

    final static private String APP_KEY = "d3zx13rhlc2jbpr";
    final static private String APP_SECRET = "rfnin8j6dr3uhuv";

    // In the class declaration section:
    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);


        // MyActivity below should be your activity class name
        mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
    }
}
