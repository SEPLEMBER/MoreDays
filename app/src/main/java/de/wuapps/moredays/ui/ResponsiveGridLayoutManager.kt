package de.wuapps.moredays.ui

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ResponsiveGridLayoutManager (context: Context, minItemWidth:Int): GridLayoutManager(context, 1) {
    private var minItemWidth:Int = 0
    init{
        this.minItemWidth = minItemWidth
    }
    override fun onLayoutChildren(recycler: RecyclerView.Recycler,
                                  state:RecyclerView.State) {
        updateSpanCount()
        super.onLayoutChildren(recycler, state)
    }
    private fun updateSpanCount() {
        var spanCount = getWidth() / minItemWidth
        if (spanCount < 1)
        {
            spanCount = 1
        }
        this.setSpanCount(spanCount)
    }
}