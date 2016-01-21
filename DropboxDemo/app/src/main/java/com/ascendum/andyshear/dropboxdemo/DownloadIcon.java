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

/**
 * Created by andyshear on 12/15/15.
 */
public class DownloadIcon {
    private DropboxAPI<AndroidAuthSession> mDBApi;
    public DropboxActivity done;
    public Context context;

    public DownloadIcon(Context context) {
        this.context = context;
    }

    public Bitmap downloadThumb(String path, String fileName, DropboxAPI api) {
        mDBApi = api;
        ThumbnailLoadTask thumb = new ThumbnailLoadTask(path, fileName);
        thumb.execute();
        return null;
    }

    public Bitmap setFolder(String path, DropboxAPI api) {
        mDBApi = api;
        FolderLoadTask folderLoadTask = new FolderLoadTask(path);
        folderLoadTask.execute();
        return null;
    }

    public Bitmap setDoc(String path, DropboxAPI api) {
        mDBApi = api;
        DocLoadTask docLoadTask = new DocLoadTask(path);
        docLoadTask.execute();
        return null;
    }

    public Bitmap setPlaceholder(String path, DropboxAPI api) {
        mDBApi = api;
        DocLoadTask docLoadTask = new DocLoadTask(path);
        docLoadTask.execute();
        return null;
    }

    public class ThumbnailLoadTask extends AsyncTask<Void, Void, Bitmap> {
        public String path;
        public String fileName;

        public ThumbnailLoadTask(String path, String fileName) {
            this.fileName = fileName;
            this.path = path;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            DropboxAPI.Entry existingEntry = null;
            Bitmap image = null;
            try {
                DropboxAPI.DropboxInputStream dis = mDBApi.getThumbnailStream(path, DropboxAPI.ThumbSize.ICON_256x256, DropboxAPI.ThumbFormat.JPEG);
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
            File file = new File(filePath, "test"+fileName+".jpeg");
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


            done.processFinish("images", "test"+fileName+".jpeg");

        }
    }

    public class FolderLoadTask extends AsyncTask<Void, Void, Bitmap> {
        public String path;

        public FolderLoadTask(String path) {
            this.path = path.substring(1);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            DropboxAPI.Entry existingEntry = null;
            Bitmap image = null;
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            FileOutputStream out;
            result = Bitmap.createScaledBitmap(result, 256, 256, false);
            String filePath = Environment.getExternalStorageDirectory().toString();
            File file = new File(filePath, "test"+path+".jpeg");
            try {
                out = new FileOutputStream(file);
                result.compress(Bitmap.CompressFormat.PNG, 75, out);

                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            done.processFinish("images", "test"+path+".jpeg");
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
            Bitmap image = null;
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.document);

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            FileOutputStream out;
            String filePath = Environment.getExternalStorageDirectory().toString();
            File file = new File(filePath, path+".png");
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


            done.processFinish("images", path+".png");
        }
    }

    public class PlaceholderLoadTask extends AsyncTask<Void, Void, Bitmap> {
        public String path;

        public PlaceholderLoadTask(String path) {
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            DropboxAPI.Entry existingEntry = null;
            Bitmap image = null;
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder);

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            FileOutputStream out;
            String filePath = Environment.getExternalStorageDirectory().toString();
            File file = new File(filePath, path+".png");
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


            done.processFinish("images", path+".png");
        }
    }

}
