package de.wuapps.moredays.ui.tools

/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.max

/**
 * Created by Allan Wang on 2017-05-31.
 */

inline fun <T : View> T.visible(): T {
    visibility = View.VISIBLE
    return this
}

inline fun <T : View> T.invisible(): T {
    visibility = View.INVISIBLE
    return this
}

inline fun <T : View> T.gone(): T {
    visibility = View.GONE
    return this
}

inline fun <T : View> T.invisibleIf(invisible: Boolean): T =
    if (invisible) invisible() else visible()

inline fun <T : View> T.visibleIf(visible: Boolean): T = if (visible) visible() else gone()

inline fun <T : View> T.goneIf(gone: Boolean): T = visibleIf(!gone)

inline val View.isVisible: Boolean
    get() = visibility == View.VISIBLE

inline val View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE

inline val View.isGone: Boolean
    get() = visibility == View.GONE

/**
 * Measure the height of a view if it had match_parent for width and no height restrictions.
 * Note that the view should be visible for this to work.
 */
inline val View.unboundedHeight: Int
    get() {
        measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        return measuredHeight
    }

fun View.snackbar(
    text: String,
    duration: Int = Snackbar.LENGTH_LONG,
    builder: Snackbar.() -> Unit = {}
): Snackbar {
    val snackbar = Snackbar.make(this, text, duration)
    snackbar.builder()
    snackbar.show()
    return snackbar
}

fun View.snackbar(
    @StringRes textId: Int,
    duration: Int = Snackbar.LENGTH_LONG,
    builder: Snackbar.() -> Unit = {}
) = snackbar(
    context.getString(textId),
    duration,
    builder
)

inline val FloatingActionButton.isHidden
    get() = !isShown

fun FloatingActionButton.showIf(show: Boolean) = if (show) show() else hide()

fun FloatingActionButton.hideIf(hide: Boolean) = if (hide) hide() else show()

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun View.hideKeyboard() {
    clearFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        windowToken,
        0
    )
}

fun View.showKeyboard() {
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
        this,
        InputMethodManager.SHOW_IMPLICIT
    )
}

inline val View.parentViewGroup: ViewGroup
    get() = parent as ViewGroup

inline val EditText.value: String get() = text.toString().trim()

inline val TextInputEditText.value: String get() = text.toString().trim()

/**
 * Generates a recycler view with match parent and a linearlayoutmanager, since it's so commonly used
 */
fun Context.fullLinearRecycler(
    rvAdapter: RecyclerView.Adapter<*>? = null,
    configs: RecyclerView.() -> Unit = {}
) =
    RecyclerView(this).apply {
        layoutManager = LinearLayoutManager(this@fullLinearRecycler)
        layoutParams =
            RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
            )
        if (rvAdapter != null) {
            adapter = rvAdapter
        }
        configs()
    }

/**
 * Sets a linear layout manager along with an adapter
 */
fun RecyclerView.withLinearAdapter(rvAdapter: RecyclerView.Adapter<*>) = apply {
    layoutManager = LinearLayoutManager(context)
    adapter = rvAdapter
}

/**
 * Animate a transition a given imageview
 * If it is not shown, the action will be invoked directly and no further actions will be made
 * If it is already shown, scaling and alpha animations will be added to the action
 */
inline fun <T : ImageView> T.fadeScaleTransition(
    duration: Long = 500L,
    minScale: Float = 0.7f,
    crossinline action: T.() -> Unit
) {
    if (!isVisible) {
        action()
    } else {
        var transitioned = false
        ValueAnimator.ofFloat(1.0f, 0.0f, 1.0f).apply {
            this.duration = duration
            addUpdateListener {
                val x = it.animatedValue as Float
                scaleXY = x * (1 - minScale) + minScale
                imageAlpha = (x * 255).toInt()
                if (it.animatedFraction > 0.5f && !transitioned) {
                    transitioned = true
                    action()
                }
            }
            start()
        }
    }
}

/**
 * Attaches a listener to the recyclerview to hide the fab when it is scrolling downwards
 * The fab will reappear when scrolling has stopped or if the user scrolls up
 */
fun FloatingActionButton.hideOnDownwardsScroll(recycler: RecyclerView) {
    recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && !isShown) {
                show()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0 && isShown) {
                hide()
            } else if (dy < 0 && isHidden) {
                show()
            }
        }
    })
}

inline var View.scaleXY
    get() = max(scaleX, scaleY)
    set(value) {
        scaleX = value
        scaleY = value
    }

/**
 * Creates an on touch listener that only emits on a short single tap
 */
@SuppressLint("ClickableViewAccessibility")
inline fun View.setOnSingleTapListener(crossinline onSingleTap: (v: View, event: MotionEvent) -> Unit) {
    setOnTouchListener { v, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> true
            MotionEvent.ACTION_UP -> {
                if (event.eventTime - event.downTime < 100)
                    onSingleTap(v, event)
                true
            }
            else -> false
        }
    }
}
