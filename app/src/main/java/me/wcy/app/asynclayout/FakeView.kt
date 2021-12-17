package me.wcy.app.asynclayout

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Created by wangchenyan.top on 2021/10/28.
 */
class FakeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
       // SystemClock.sleep(3000)
    }
}