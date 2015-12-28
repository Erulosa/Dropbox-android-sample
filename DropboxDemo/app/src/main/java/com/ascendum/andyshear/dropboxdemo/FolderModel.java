package com.ascendum.andyshear.dropboxdemo;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by andyshear on 12/18/15.
 */
public class FolderModel implements Serializable {

    private static final long serialVersionUID = 1L;
    public String name;
    public String fileName;
    public String imageName;
    public String icon;
    public Bitmap image;

    public FolderModel(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }
}
