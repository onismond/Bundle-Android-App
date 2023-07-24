package com.bundlegh.bundle.util

import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler


open class MyGridLayoutManager(
    context: Context,
    spanCount: Int
): GridLayoutManager(context, spanCount) {
    private var mPendingTargetPos = -1
    private var mPendingPosOffset = -1

    override fun onLayoutChildren(recycler: Recycler?, state: RecyclerView.State) {
        if (mPendingTargetPos != -1 && state.itemCount > 0) {
            /*
            Data is present now, we can set the real scroll position
            */
            scrollToPositionWithOffset(mPendingTargetPos, mPendingPosOffset)
            mPendingTargetPos = -1
            mPendingPosOffset = -1
        }
        super.onLayoutChildren(recycler, state)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        /*
        May be needed depending on your implementation.

        Ignore target start position if InstanceState is available (page existed before already, keep position that user scrolled to)
         */
        mPendingTargetPos = -1
        mPendingPosOffset = -1
        super.onRestoreInstanceState(state)
    }

    /**
     * Sets a start position that will be used **as soon as data is available**.
     * May be used if your Adapter starts with itemCount=0 (async data loading) but you need to
     * set the start position already at this time. As soon as itemCount > 0,
     * it will set the scrollPosition, so that given itemPosition is visible.
     * @param position
     * @param offset
     */
    fun setTargetStartPos(position: Int, offset: Int) {
        mPendingTargetPos = position
        Log.e("Error", "Target position is $position")
        mPendingPosOffset = offset
    }

    override fun canScrollVertically() = false
}