// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.ascendum.andyshear.dropboxdemo;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

public final class LockedItems {

    private LockedItems() {}

    public static void saveItems(Context context, String key, Object object) {
        try {
            FileOutputStream fout = context.openFileOutput(key, Context.MODE_PRIVATE);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            oout.writeObject(object);
            oout.close();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getItems(Context context, String key) {
        FileInputStream fin = null;
        Object object = null;
        try {
            fin = context.openFileInput(key);
            ObjectInputStream oin = new ObjectInputStream(fin);
            object = oin.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object;
    }
}
