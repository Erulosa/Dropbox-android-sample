package com.ascendum.andyshear.dropboxdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.DropBoxManager;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andyshear on 12/21/15.
 */
public class DropboxFolder implements Serializable {

    private static final long serialVersionUID = 1L;
    public String name;
    public HashMap<Integer, String> folders;
    public HashMap<Integer, String> fileNames;
    public HashMap<Integer, String> files;
    public Context context;
    private DropboxAPI<AndroidAuthSession> mDBApi;

    public DropboxFolder(DropboxAPI.Entry entry, Context context, DropboxAPI<AndroidAuthSession> mDBApi) {
        this.context = context;
        this.name = entry.toString();
        this.folders = getFolders(entry);
        this.files = getFiles(entry);
        this.fileNames = getFileNames(entry);
        this.mDBApi = mDBApi;
    }

    public HashMap<Integer, String> getFolders(DropboxAPI.Entry entry) {
        HashMap<Integer, String> folders = new HashMap<>();
        for(int i = 0; i < entry.contents.size(); i++) {
            folders.put(i, entry.contents.get(i).path);
        }
        return folders;
    }
    public HashMap<Integer, String> getFiles(DropboxAPI.Entry entry) {
        HashMap<Integer, String> files = new HashMap<>();
        for(int i = 0; i < entry.contents.size(); i++) {
            files.put(i, entry.contents.get(i).mimeType);
        }
        return files;
    }

    public HashMap<Integer, String> getFileNames(DropboxAPI.Entry entry) {
        HashMap<Integer, String> folders = new HashMap<>();
        for(int i = 0; i < entry.contents.size(); i++) {
            folders.put(i, entry.contents.get(i).fileName());
        }
        return folders;
    }
}
