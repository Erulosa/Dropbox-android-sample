package com.ascendum.andyshear.dropboxdemo;

import android.graphics.Bitmap;

import com.dropbox.client2.DropboxAPI;

/**
 * Created by andyshear on 12/14/15.
 */
public interface AsyncResponse {
    void processFinish(String method, String output);
    void processFinish(DropboxItem dropboxItem);
}
