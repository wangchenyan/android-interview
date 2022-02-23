package me.wcy.app.viewlifecycle

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * Created by wangchenyan.top on 2021/12/23.
 */
class LifecycleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e("LifecycleView", "onAttachedToWindow")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.e("LifecycleView", "onDetachedFromWindow")
    }
}