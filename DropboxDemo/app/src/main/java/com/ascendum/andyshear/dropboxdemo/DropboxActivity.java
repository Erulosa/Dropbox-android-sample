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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andyshear on 12/21/15.
 */
public class DropboxActivity extends Activity implements AsyncResponse, AsyncKnurldResponse {

    final static private String APP_KEY = "d3zx13rhlc2jbpr";
    final static private String APP_SECRET = "rfnin8j6dr3uhuv";


    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final String FILE_PATH = "FILE_PATH";

    protected DropboxAPI<AndroidAuthSession> mDBApi;
    public Context context;

    public ListView listView;
    public String folderPath;

    public int count = 0;

    public DropboxService dropboxService;

    public KnurldService knurldService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_loading);

        knurldService = new KnurldService(this);

        Intent intent = getIntent();

        if (mDBApi == null) {
            AndroidAuthSession session = buildSession();
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        }

        String filePathIntent = intent.getStringExtra(FILE_PATH);

        if (mDBApi.getSession().isLinked() && filePathIntent == null) {
            context = this;
            dropboxService = new DropboxService(mDBApi, this, "/");

        } else if (mDBApi.getSession().isLinked() && filePathIntent != null) {
            if (mDBApi.getSession().isLinked() && filePathIntent.equals("/")) {
                context = this;
                dropboxService = new DropboxService(mDBApi, this, "/");

            } else if (mDBApi.getSession().isLinked() && filePathIntent.startsWith("/")) {
                context = this;
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

    }

    @Override
    public void processFinish(String call, String method, String result) {
        JSONObject jsonParam = null;
        try {
            jsonParam = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String test = "";

    }

    @Override
    public void processFinish(String method, String output) {
        count++;
        if (count >= dropboxService.dropboxItem.entry.contents.size()) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            setContentView(R.layout.activity_folder_list);
//            FolderAdapter adapter = new FolderAdapter(this, dropboxService.dropboxItem);
            ListViewSwipeAdapter adapter = new ListViewSwipeAdapter(this, dropboxService.dropboxItem);
            listView = (ListView)findViewById(R.id.list);
            listView.setAdapter(adapter);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileType = dropboxService.dropboxItem.entry.contents.get(position).mimeType;
                boolean test = dropboxService.dropboxItem.entry.contents.get(position).readOnly;
                if (fileType == null) {
                    String folderPath = dropboxService.dropboxItem.entry.contents.get(position).path;
                    getFolder(folderPath);
                } else if (fileType.startsWith("video")){
                    String filePath = dropboxService.dropboxItem.entry.contents.get(position).path;
                    getFile(filePath);
                } else if (fileType.startsWith("image")){
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
        startActivity(intent);
    }

    protected void getFile(String filePath){
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra(FILE_PATH, filePath);
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
}
