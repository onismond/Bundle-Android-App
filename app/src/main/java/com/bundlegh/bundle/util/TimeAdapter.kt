package com.bundlegh.bundle.util

import android.view.View
import android.widget.AdapterView

class TimeAdapter: AdapterView.OnItemSelectedListener {

    var time = 7

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        time = pos + 7
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

}