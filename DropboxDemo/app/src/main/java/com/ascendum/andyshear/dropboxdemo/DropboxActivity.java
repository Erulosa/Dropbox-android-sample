package com.ascendum.andyshear.dropboxdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by andyshear on 12/21/15.
 */
public class DropboxActivity extends Activity implements AsyncResponse, AsyncKnurldVerification{

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
    public boolean doneEnrolling;
    public boolean isUserReady;
    public AsyncKnurldVerification knurldVerification;



    public KnurldService knurldService;
    public KnurldAppModel knurldAppModel;
    public KnurldConsumerModel knurldConsumerModel;
    public KnurldEnrollmentsModel knurldEnrollmentsModel;
    public KnurldVerificationModel knurldVerificationModel;
    public KnurldAnalysisModel knurldAnalysisModel;

    private String knurldAccessToken;

    private static final String KNURLD_TOKEN = "KNURLD_TOKEN";
    private static final String KNURLD_APP_MODEL = "KNURLD_APP_MODEL";
    private static final String KNURLD_CONSUMER = "KNURLD_CONSUMER";
    private static final String KNURLD_VERIFICATION = "KNURLD_VERIFICATION";
    private static final String KNURLD_ENROLLMENT = "KNURLD_ENROLLMENT";
    private static final String KNURLD_ANALYSIS = "KNURLD_ANALYSIS";

    private ArrayList<String> lockedFiles;

    public VerificationItem verificationItem;

    public Thread knurldServiceThread;

    private PopupWindow popupWindow;


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

        knurldVerification = this;
        knurldServiceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                knurldService = new KnurldService(knurldVerification, knurldToken, knurldApp, knurldConsumer, knurldEnrollment);
            }
        });

        knurldServiceThread.start();
        isUserReady = false;

//        getKnurldService();


//        if (knurldToken == null) {
//            knurldService = new KnurldService(this);
//            knurldService.getToken();
//        } else {
//            knurldService = new KnurldService(this, knurldToken);
//            knurldAccessToken = knurldToken;
//            if (knurldApp != null) {
//                knurldAppModel = new KnurldAppModel();
//                knurldAppModel.appModelId = knurldApp;
//            }
//            if (knurldConsumer != null) {
//                knurldConsumerModel = new KnurldConsumerModel();
//                knurldConsumerModel.consumerModelId = knurldConsumer;
//            }
//            if (knurldVerification != null) {
//                knurldVerificationModel = new KnurldVerificationModel();
//                knurldVerificationModel.verificationId = knurldVerification;
//            }
//            if (knurldAnalysis != null) {
//                knurldAnalysisModel = new KnurldAnalysisModel();
//                knurldAnalysisModel.taskName = knurldAnalysis;
//            }
//        }



        if (mDBApi == null) {
            AndroidAuthSession session = buildSession();
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        }

        String filePathIntent = intent.getStringExtra(FILE_PATH);
        lockedFiles = ((ArrayList<String>) LockedItems.getItems(this, "locked") == null) ? new ArrayList<String>() : (ArrayList<String>) LockedItems.getItems(this, "locked");

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
        lockedFiles = ((ArrayList<String>) LockedItems.getItems(this, "locked") == null) ? new ArrayList<String>() : (ArrayList<String>) LockedItems.getItems(this, "locked");
    }

    protected void onPause() {
        super.onPause();
        for (DropboxAPI.Entry item : dropboxService.dropboxItem.entry.contents) {
            if (item.readOnly && !lockedFiles.contains(item.fileName())) {
                lockedFiles.add(item.fileName());
            }
        }
//        TODO reset saved locked files, need better way to clear files
//        lockedFiles = new ArrayList<String>();
        LockedItems.saveItems(this, "locked", lockedFiles);
    }

    public void lockItem(String item) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_folder_swipe, null);
        View spinnerView = LayoutInflater.from(context).inflate(R.layout.loading_popup, null);
        ProgressBar progressBar = (ProgressBar) spinnerView.findViewById(R.id.speakProgress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        popupWindow = new PopupWindow(spinnerView, 500, 500);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


//        knurldService.knurldAnalysis(this);
//        knurldService.startVerification();
        verificationItem = new VerificationItem();
        verificationItem.itemName = item;
        verificationItem.locked = true;
    }

    public void unlockItem(String item) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_folder_swipe, null);
        View spinnerView = LayoutInflater.from(context).inflate(R.layout.loading_popup, null);
        ProgressBar progressBar = (ProgressBar) spinnerView.findViewById(R.id.speakProgress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        popupWindow = new PopupWindow(spinnerView, 500, 500);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


//        knurldService.knurldAnalysis(this);
//        knurldService.startVerification();
        verificationItem = new VerificationItem();
        verificationItem.itemName = item;
        verificationItem.locked = false;
    }

    public void authenticateItem() {
        popupWindow.dismiss();
        setItem(verificationItem.locked, verificationItem.itemName);
    }

    public void setItem(boolean locked, String item) {
        if (locked) {
            lockedFiles.remove(item);
            LockedItems.saveItems(this, "locked", lockedFiles);
        } else {
            lockedFiles.add(item);
            LockedItems.saveItems(this, "locked", lockedFiles);
        }
    }

    @Override
    public void processFinish(String method, String output) {


        if (output.equals("finished")) {

            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            setContentView(R.layout.activity_folder_list);
            ListViewSwipeAdapter adapter = new ListViewSwipeAdapter(this, dropboxService.dropboxItem, knurldService);
            listView = (ListView)findViewById(R.id.list);
            listView.setAdapter(adapter);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isUserReady) {
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

                }
            });
        }
    }

    protected void getFolder(String folderPath){
        Intent intent = new Intent(this, DropboxActivity.class);
        intent.putExtra(FILE_PATH, folderPath);
        if (knurldAccessToken != null) {
            intent.putExtra(KNURLD_TOKEN, knurldAccessToken);
        }
        if (knurldAppModel != null && knurldAppModel.appModelId != null) {
            intent.putExtra(KNURLD_APP_MODEL, knurldAppModel.appModelId);
        }
        if (knurldConsumerModel != null && knurldConsumerModel.consumerModelId != null) {
            intent.putExtra(KNURLD_CONSUMER, knurldConsumerModel.consumerModelId);
        }
        if (knurldVerificationModel != null && knurldVerificationModel.verificationId != null) {
            intent.putExtra(KNURLD_VERIFICATION, knurldVerificationModel.verificationId);
        }
        if (knurldAnalysisModel != null && knurldAnalysisModel.taskName != null) {
            intent.putExtra(KNURLD_ANALYSIS, knurldAnalysisModel.taskName);
        }
        startActivity(intent);
    }

    protected void getFile(String filePath){
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra(FILE_PATH, filePath);
        if (knurldAccessToken != null) {
            intent.putExtra(KNURLD_TOKEN, knurldAccessToken);
        }
        if (knurldAppModel != null && knurldAppModel.appModelId != null) {
            intent.putExtra(KNURLD_APP_MODEL, knurldAppModel.appModelId);
        }
        if (knurldConsumerModel != null && knurldConsumerModel.consumerModelId != null) {
            intent.putExtra(KNURLD_CONSUMER, knurldConsumerModel.consumerModelId);
        }
        if (knurldVerificationModel != null && knurldVerificationModel.verificationId != null) {
            intent.putExtra(KNURLD_VERIFICATION, knurldVerificationModel.verificationId);
        }
        if (knurldAnalysisModel != null && knurldAnalysisModel.taskName != null) {
            intent.putExtra(KNURLD_ANALYSIS, knurldAnalysisModel.taskName);
        }
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
            } else {
                iconDownload.setPlaceholder(path, mDBApi);
            }

            if (i+1 == dropboxService.dropboxItem.entry.contents.size()) {
                iconDownload.finish();
            }
        }
    }


    @Override
    public void processFinish(String method, boolean result) {

        switch (method) {
            case "userReady":
                isUserReady = result;
                break;
            case "analysis":
                knurldService.knurldVerify(this);
                break;
            case "verification":
                if (result)
                    authenticateItem();
        }


    }
    
}
