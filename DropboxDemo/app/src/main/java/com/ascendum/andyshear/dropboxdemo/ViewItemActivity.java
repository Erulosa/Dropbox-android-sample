package com.ascendum.andyshear.dropboxdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;

/**
 * Created by andyshear on 1/5/16.
 */
public class ViewItemActivity extends Activity implements AsyncResponse{

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

    public DropboxFolder folder;
    public ListView listView;

    public DropboxAPI.Entry entry;

    public DropboxItem dropboxItem;

    public String folderPath;

    public int count = 0;

    public DropboxService dropboxService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_loading);

        Intent intent = getIntent();
        accessToken = intent.getStringExtra(ACCESS_TOKEN);

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        setIntent(intent);
    }

    public void onStop() {
        super.onStop();
        finish();
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putString("accessToken", this.accessToken);
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        this.accessToken = savedInstanceState.getString("accessToken");
//    }

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

    protected void getFolder(String folderPath){
        this.folderPath = folderPath;

        Intent intent = new Intent(this, DropboxActivity.class);
        intent.putExtra(FILE_PATH, folderPath);
        startActivity(intent);
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

    @Override
    public void processFinish(DropboxFolder folder) {

        this.folder = folder;

        for(int i = 0; i < folder.folders.size(); i++){
            String path = folder.folders.get(i);
            String fileName = folder.fileNames.get(i);
            String type = folder.files.get(i);
            DownloadPreview downloadPreview = new DownloadPreview(context);
            downloadPreview.done = this;
            if(type == null){
            } else if(type.startsWith("application/")){
                downloadPreview.setDoc(path, mDBApi);
            } else {
                downloadPreview.downloadThumb(path, fileName, mDBApi);
            }
        }
        count = 0;



    }

    @Override
    public void processFinish(DropboxAPI.Entry entry) {

        this.entry = entry;

        for(int i = 0; i < entry.contents.size(); i++){
            String path = entry.contents.get(i).path;
            String fileName = entry.contents.get(i).fileName();
            String type = entry.contents.get(i).mimeType;
            DownloadPreview downloadPreview = new DownloadPreview(context);
            downloadPreview.done = this;
            if(type == null){
            } else if(type.startsWith("application/")){
                downloadPreview.setDoc(path, mDBApi);
            } else {
                downloadPreview.downloadThumb(path, fileName, mDBApi);
            }
        }
        count = 0;



    }

}
