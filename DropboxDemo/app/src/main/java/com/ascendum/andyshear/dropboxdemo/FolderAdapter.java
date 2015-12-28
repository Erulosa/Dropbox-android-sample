package com.ascendum.andyshear.dropboxdemo;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by andyshear on 12/18/15.
 */
public class FolderAdapter extends BaseAdapter {

    private final Activity context;
    public FolderModel folderModel;
    public DropboxFolder folderList;
    public Drawable folderDrawable;

    public FolderAdapter(Activity context, DropboxFolder folderList) {

        this.context = context;
        this.folderList = folderList;
        folderDrawable = context.getResources().getDrawable(R.drawable.folder);
    }

    @Override
    public int getCount() {
        return folderList.folders.size();
    }

    @Override
    public Object getItem(int position) {
        return folderList.folders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_folder, null, true);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        String folderPath = folderList.folders.get(position).substring(1);
        String fileName = folderList.fileNames.get(position);
        textView.setText(fileName);
        String path = Environment.getExternalStorageDirectory().toString();
//        File file = new File(path, folderList[position].fileName);
        if (folderList.files.get(position) == null) {
            imageView.setImageDrawable(folderDrawable);
        } else {
            File file = new File(path, "test"+ fileName +".jpeg");
            imageView.setImageURI(Uri.fromFile(file));
        }
        return rowView;
    }

}
