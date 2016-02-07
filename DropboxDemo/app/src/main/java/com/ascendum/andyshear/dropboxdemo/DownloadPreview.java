// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.ascendum.andyshear.dropboxdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadPreview {
    private DropboxAPI<AndroidAuthSession> mDBApi;
    public ViewItemActivity done;
    public Context context;

    public DownloadPreview(Context context) {
        this.context = context;
    }

    public Bitmap downloadThumb(String path, String fileName, DropboxAPI api) {
        mDBApi = api;
        PreviewLoadTask previewLoadTask = new PreviewLoadTask(path, fileName);
        previewLoadTask.execute();
        return null;
    }

    public Bitmap setDoc(String path, DropboxAPI api) {
        mDBApi = api;
        DocLoadTask docLoadTask = new DocLoadTask(path);
        docLoadTask.execute();
        return null;
    }

    public class PreviewLoadTask extends AsyncTask<Void, Void, Bitmap> {
        public String path;
        public String fileName;

        public PreviewLoadTask(String path, String fileName) {
            this.fileName = fileName;
            this.path = path;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            DropboxAPI.Entry existingEntry = null;
            Bitmap image = null;
            try {
                DropboxAPI.DropboxInputStream dis = mDBApi.getThumbnailStream(path, DropboxAPI.ThumbSize.BESTFIT_960x640, DropboxAPI.ThumbFormat.PNG);
                image = BitmapFactory.decodeStream(dis);
                dis.close();

            } catch (DropboxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            result = Bitmap.createScaledBitmap(result, result.getWidth(), result.getHeight(), false);
            FileOutputStream out;
            String filePath = Environment.getExternalStorageDirectory().toString();
            File file = new File(filePath, "test"+fileName+"Preview.jpeg");
            try {
                out = new FileOutputStream(file);
                result.compress(Bitmap.CompressFormat.JPEG, 75, out);

                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            done.processFinish("images", "test"+fileName+"Preview.jpeg");

        }
    }

    public class DocLoadTask extends AsyncTask<Void, Void, Bitmap> {
        public String path;

        public DocLoadTask(String path) {
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            DropboxAPI.Entry existingEntry = null;
            Bitmap image;
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.document);

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            FileOutputStream out;
            String filePath = Environment.getExternalStorageDirectory().toString();
            File file = new File(filePath, "test"+path+"Preview.jpeg");
            try {
                out = new FileOutputStream(file);
                result.compress(Bitmap.CompressFormat.JPEG, 100, out);

                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            done.processFinish("images", "test"+path+"Preview.jpeg");
        }
    }
}
