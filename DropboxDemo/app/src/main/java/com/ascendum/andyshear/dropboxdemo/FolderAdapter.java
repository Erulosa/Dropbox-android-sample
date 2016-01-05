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

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.util.List;

/**
 * Created by andyshear on 12/18/15.
 */
public class FolderAdapter extends BaseAdapter {

    private final Activity context;
    public Drawable folderDrawable;
    public DropboxItem dropboxItem;

    public View v;

    public FolderAdapter(Activity context, DropboxItem dropboxItem) {

        this.context = context;
        folderDrawable = context.getResources().getDrawable(R.drawable.folder);
        this.dropboxItem = dropboxItem;
    }

    @Override
    public int getCount() {
        return dropboxItem.entry.contents.size();
    }

    @Override
    public Object getItem(int position) {
        return dropboxItem.entry.contents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View v = view;

        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            v = inflater.inflate(R.layout.activity_folder, null, true);
            holder = new ViewHolder();
            holder.textView = (TextView) v.findViewById(R.id.label);
            holder.imageView = (ImageView) v.findViewById(R.id.icon);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        String fileName = dropboxItem.entry.contents.get(position).fileName();
        holder.textView.setText(fileName);
        String path = Environment.getExternalStorageDirectory().toString();
        if (dropboxItem.entry.contents.get(position) == null) {
            holder.imageView.setImageDrawable(folderDrawable);
        } else {
            File file = new File(path, "test"+ fileName +".jpeg");
            holder.imageView.setImageURI(Uri.fromFile(file));
        }

        return v;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

}
