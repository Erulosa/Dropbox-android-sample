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
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

/**
 * Created by andyshear on 12/21/15.
 */
public class DropboxActivity extends Activity implements AsyncResponse{

    final static private String APP_KEY = "d3zx13rhlc2jbpr";
    final static private String APP_SECRET = "rfnin8j6dr3uhuv";


    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final String FILE_PATH = "FILE_PATH";
    private static final String DROPBOX_ITEM = "DROPBOX_ITEM";

    private String accessToken;

    public final static String DROPBOX_ITEMS = "com.ascendum.andyshear.dropboxdemo.ITEMS";

    // In the class declaration section:
    private DropboxAPI<AndroidAuthSession> mDBApi;
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
        setContentView(R.layout.activity_folder_list);

        Intent intent = getIntent();

        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        String filePathIntent = intent.getStringExtra(FILE_PATH);

        if (mDBApi.getSession().isLinked() && filePathIntent.equals("/")) {
            context = this;
            dropboxService = new DropboxService(mDBApi, this, "/");

        } else if (mDBApi.getSession().isLinked() && filePathIntent.startsWith("/")) {
            context = this;
            dropboxService = new DropboxService(mDBApi, this, filePathIntent);
            dropboxService.getPath(mDBApi, this, filePathIntent);

        } else {
            mDBApi.getSession().startOAuth2Authentication(DropboxActivity.this);
        }


        folderPath = "/";
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        setIntent(intent);
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

                DropboxLogin login = new DropboxLogin();
                login.delegate = this;
                login.execute();


            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    @Override
    public void processFinish(String method, String output) {
        count++;
        if (count >= dropboxService.dropboxItem.entry.contents.size()) {
            FolderAdapter adapter = new FolderAdapter(this, dropboxService.dropboxItem);
            listView = (ListView)findViewById(R.id.list);
            listView.setAdapter(adapter);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String selectedItem = dropboxService.dropboxItem.entry.contents.get(position).fileName();
//                    Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT).show();
                    String folderPath = dropboxService.dropboxItem.entry.contents.get(position).path;
                    getFolder(folderPath);
                }
            });
        }



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

        for(int i = 0; i < dropboxService.dropboxItem.entry.contents.size(); i++){
            String path = dropboxService.dropboxItem.entry.contents.get(i).path;
            String fileName = dropboxService.dropboxItem.entry.contents.get(i).fileName();
            String type = dropboxService.dropboxItem.entry.contents.get(i).mimeType;
            DownloadIcon iconDownload = new DownloadIcon(context);
            iconDownload.done = this;
            if(type == null){
                iconDownload.setFolder(path, mDBApi);
            } else if(type.startsWith("application/")){
                iconDownload.setDoc(path, mDBApi);
            } else {
                iconDownload.downloadThumb(path, fileName, mDBApi);
            }
        }
        count = 0;
    }

    @Override
    public void processFinish(DropboxFolder folder) {

        this.folder = folder;

        for(int i = 0; i < folder.folders.size(); i++){
            String path = folder.folders.get(i);
            String fileName = folder.fileNames.get(i);
            String type = folder.files.get(i);
            DownloadIcon iconDownload = new DownloadIcon(context);
            iconDownload.done = this;
            if(type == null){
                iconDownload.setFolder(path, mDBApi);
            } else if(type.startsWith("application/")){
                iconDownload.setDoc(path, mDBApi);
            } else {
                iconDownload.downloadThumb(path, fileName, mDBApi);
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
            DownloadIcon iconDownload = new DownloadIcon(context);
            iconDownload.done = this;
            if(type == null){
                iconDownload.setFolder(path, mDBApi);
            } else if(type.startsWith("application/")){
                iconDownload.setDoc(path, mDBApi);
            } else {
                iconDownload.downloadThumb(path, fileName, mDBApi);
            }
        }
        count = 0;



    }

    private class DropboxLogin extends AsyncTask<String, String, DropboxAPI.Entry> {
        public AsyncResponse delegate = null;
        @Override
        protected DropboxAPI.Entry doInBackground(String... params) {
            String test = params[0];
            DropboxAPI.Entry entry = null;
            DropboxFolder dropboxFolder = null;
            try {
                entry = mDBApi.metadata("/", 10, null, true, null);
                dropboxFolder = new DropboxFolder(entry, context, mDBApi);


            } catch (DropboxException e) {
                e.printStackTrace();
            }
            return entry;

        }

        protected void onPostExecute(DropboxAPI.Entry entry) {
            delegate.processFinish(entry);
        }
    }

    private class DropboxNestedFolder extends AsyncTask<String, String, DropboxFolder> {
        public AsyncResponse delegate = null;
        @Override
        protected DropboxFolder doInBackground(String... params) {

            DropboxAPI.Entry entry = null;
            DropboxFolder dropboxFolder = null;
            try {
                entry = mDBApi.metadata(folderPath, 10, null, true, null);
                dropboxFolder = new DropboxFolder(entry, context, mDBApi);


            } catch (DropboxException e) {
                e.printStackTrace();
            }
            return dropboxFolder;

        }

        protected void onPostExecute(DropboxFolder folder) {
            delegate.processFinish(folder);
        }
    }
}
