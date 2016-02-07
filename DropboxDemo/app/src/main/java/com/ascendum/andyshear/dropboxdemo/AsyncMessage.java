// Copyright 2016 Intellisis Inc.  All rights reserved.
//
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file
package com.ascendum.andyshear.dropboxdemo;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

public interface AsyncMessage {
    void processFinish(PopupWindow popupWindow);
}
