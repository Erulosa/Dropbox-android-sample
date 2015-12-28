package com.ascendum.andyshear.dropboxdemo;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        Intent intent = getIntent();
        String message = "message";
        System.out.println("MESSAGE ================ " + message);
        try {
            JSONArray items = new JSONArray(message);
            System.out.println(items);
            String[] itemTitles = new String[items.length()];
            String[] itemIcon = new String[items.length()];

            for(int i = 0; i < items.length(); i++){
                String name = items.getJSONObject(i).getString("path");
                String icon = items.getJSONObject(i).getString("icon");
                itemTitles[i] = name;
                itemIcon[i] = icon;
            }

            LinearLayout layout = (LinearLayout)findViewById(R.id.folderLayout);

            ImageView image = (ImageView) layout.findViewById(R.id.icon);
            String path = Environment.getExternalStorageDirectory().toString();
            File file = new File(path, "test.jpeg");
            image.setImageURI(Uri.fromFile(file));
            String test = "";



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





}
