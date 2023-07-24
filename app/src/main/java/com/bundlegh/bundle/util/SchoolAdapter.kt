package com.bundlegh.bundle.util

import android.view.View
import android.widget.AdapterView

class SchoolAdapter: AdapterView.OnItemSelectedListener {

    var school = -1

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        val schoolName: String = parent?.getItemAtPosition(pos).toString()
        school = pos + 1
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

}