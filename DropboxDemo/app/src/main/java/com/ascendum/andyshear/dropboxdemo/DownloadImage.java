package com.ascendum.andyshear.dropboxdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by andyshear on 12/15/15.
 */
public class DownloadImage {
        private DropboxAPI<AndroidAuthSession> mDBApi;
    public MainActivity done;
    public Context context;

    public DownloadImage(Context context) {
        this.context = context;
    }

    public Bitmap downloadThumb(String path, DropboxAPI api) {
            mDBApi = api;
            ThumbnailLoadTask thumb = new ThumbnailLoadTask("/");
            thumb.execute();
            return null;
        }

        public class ThumbnailLoadTask extends AsyncTask<Void, Void, Bitmap[]> {


            public ThumbnailLoadTask(String path) {

            }
            @Override
            protected Bitmap[] doInBackground(Void... params) {
                DropboxAPI.Entry existingEntry = null;
                Bitmap images[] = null;
                try {
                    existingEntry = mDBApi.metadata("/", 10, null, true, null);
                    images = new Bitmap[existingEntry.contents.size()];
                    for(int i = 0; i < existingEntry.contents.size(); i++){
                        String icon = existingEntry.contents.get(i).icon;
                        if (!icon.equals("folder")){
                            DropboxAPI.DropboxInputStream dis = mDBApi.getThumbnailStream(existingEntry.contents.get(i).path, DropboxAPI.ThumbSize.ICON_256x256, DropboxAPI.ThumbFormat.JPEG);
                            images[i] = BitmapFactory.decodeStream(dis);
                        } else {
                            images[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
                        }
                    }

                } catch (DropboxException e) {
                    e.printStackTrace();
                }
                return images;
            }

            @Override
            protected void onPostExecute(Bitmap[] results) {
                super.onPostExecute(results);
                for(int i = 0; i < results.length; i++){
                    FileOutputStream out;
                    String path = Environment.getExternalStorageDirectory().toString();
                    File file = new File(path, "test"+i+".jpeg");
                    try {
                        out = new FileOutputStream(file);
                        results[i].compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

//                done.processFinish("images", "done");
//                imageView.setImageBitmap(result);
            }
        }

}
