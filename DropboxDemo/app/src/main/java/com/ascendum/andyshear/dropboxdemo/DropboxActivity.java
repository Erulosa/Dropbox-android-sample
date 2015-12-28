package com.ascendum.andyshear.dropboxdemo;

import android.app.Activity;
import android.content.Context;
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

    public final static String DROPBOX_ITEMS = "com.ascendum.andyshear.dropboxdemo.ITEMS";

    // In the class declaration section:
    private DropboxAPI<AndroidAuthSession> mDBApi;
    public Context context;

    public DropboxFolder folder;
    public ListView listView;

    public String folderPath;

    public int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);


        mDBApi.getSession().startOAuth2Authentication(DropboxActivity.this);

        folderPath = "/";

    }

    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();

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
        if (count >= folder.folders.size()) {
            FolderAdapter adapter = new FolderAdapter(this, folder);
            listView = (ListView)findViewById(R.id.list);
            listView.setAdapter(adapter);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = folder.folders.get(position);
//                    Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT).show();
                    String test = "/" + folder.folders.get(position);
                    getFolder(test);
                }
            });
        }



    }

    protected void getFolder(String folderPath){
        this.folderPath = folderPath;
        DropboxNestedFolder login = new DropboxNestedFolder();
        login.delegate = this;
        login.execute();
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

    private class DropboxLogin extends AsyncTask<String, String, DropboxFolder> {
        public AsyncResponse delegate = null;
        @Override
        protected DropboxFolder doInBackground(String... params) {

            DropboxAPI.Entry entry = null;
            DropboxFolder dropboxFolder = null;
            try {
                entry = mDBApi.metadata("/", 10, null, true, null);
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
