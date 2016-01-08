package com.ascendum.andyshear.dropboxdemo;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.io.File;

/**
 * Created by andyshear on 1/8/16.
 */
public class ListViewSwipeAdapter extends BaseSwipeAdapter {

    private Context context;
    DropboxItem dropboxItem;

    public ListViewSwipeAdapter(Context context, DropboxItem dropboxItem) {
        this.context = context;
        this.dropboxItem = dropboxItem;
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int i, ViewGroup viewGroup) {

        View v = LayoutInflater.from(context).inflate(R.layout.activity_folder_swipe, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(i));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
//                Toast.makeText(context, "swiped", Toast.LENGTH_SHORT).show();
            }
        });

        v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
            }
        });

        v.findViewById(R.id.lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "lock", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void fillValues(int i, View view) {

        TextView textView = (TextView) view.findViewById(R.id.label);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);

        String fileName = dropboxItem.entry.contents.get(i).fileName();
        textView.setText(fileName);
        String path = Environment.getExternalStorageDirectory().toString();
        if (dropboxItem.entry.contents.get(i) == null) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.folder));
        } else {
            File file = new File(path, "test"+ fileName +".jpeg");
            imageView.setImageURI(Uri.fromFile(file));
        }
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

}