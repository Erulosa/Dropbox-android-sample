// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.knurld.dropboxdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

public class LoadingPopup {

    private Context context;
    private PopupWindow popupWindow;

    public LoadingPopup( Context context, Message message) {
        this.context = context;
        Activity parent = (Activity) context;
        LinearLayout parentLayout = (LinearLayout) parent.findViewById(R.id.loadingPanel);
        showLoading(parentLayout, context);
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public void showLoading(LinearLayout parentLayout, Context context) {
        View spinnerView = LayoutInflater.from(context).inflate(R.layout.loading_popup, null);
        ProgressBar progressBar = (ProgressBar) spinnerView.findViewById(R.id.speakProgress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        popupWindow = new PopupWindow(spinnerView, 500, 500);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(parentLayout, Gravity.CENTER, 0, 0);
    }
}
