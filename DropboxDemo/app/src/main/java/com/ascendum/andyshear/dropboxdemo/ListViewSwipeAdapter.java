package com.ascendum.andyshear.dropboxdemo;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
    private KnurldService knurldService;
    private PopupWindow popupWindow;

    public ListViewSwipeAdapter(Context context, DropboxItem dropboxItem, KnurldService knurldService) {
        this.knurldService = knurldService;
        this.context = context;
        this.dropboxItem = dropboxItem;
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int i, ViewGroup viewGroup) {
        final int position = i;
        final View v = LayoutInflater.from(context).inflate(R.layout.activity_folder_swipe, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(i));
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
                swipeLayout.close(true);
            }
        });

        v.findViewById(R.id.lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof DropboxActivity && knurldService.isUserReady) {
                    RecordWAVService recordWAVService = new RecordWAVService(context, v, knurldService);
                    dropboxItem.entry.contents.get(position).readOnly = (dropboxItem.entry.contents.get(position).readOnly == true) ? false : true;
                    if (dropboxItem.entry.contents.get(position).readOnly) {
                        recordWAVService.lock(dropboxItem.entry.contents.get(position).fileName());

                    } else {
                        recordWAVService.unLock(dropboxItem.entry.contents.get(position).fileName());
                    }
                    swipeLayout.close(true);
                }
                else {
                    showErrorPopup(v);
                }
            }
        });

        return v;
    }

    public void showErrorPopup(View view) {
        View errorView = LayoutInflater.from(context).inflate(R.layout.knurld_loading_popup, null);
        popupWindow = new PopupWindow(errorView, 500, 500);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        popupWindow.dismiss();
                    }
                }, 3000);
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
            Uri uri = Uri.fromFile(file);
            if (uri != null) {
                imageView.setImageURI(Uri.fromFile(file));
            }
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
