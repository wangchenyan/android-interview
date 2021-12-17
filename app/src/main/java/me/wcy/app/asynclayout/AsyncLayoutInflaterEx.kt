package me.wcy.app.asynclayout

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.UiThread
import androidx.core.util.Pools.SynchronizedPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Modified from Google’s [AsyncLayoutInflater](https://developer.android.com/reference/androidx/asynclayoutinflater/view/AsyncLayoutInflater),
 * layout inflate through coroutine's io dispatcher.
 *
 * Created by wangchenyan.top on 2021/10/28.
 */
class AsyncLayoutInflaterEx(context: Context) {
    private val mInflater: LayoutInflater = BasicInflater(context)

    private val mHandlerCallback = Handler.Callback { msg ->
        val request = msg.obj as InflateRequest
        if (request.view == null) {
            request.view = mInflater.inflate(
                request.resid, request.parent, false
            )
        }
        request.callback!!.onInflateFinished(
            request.view!!, request.resid, request.parent
        )
        inflateExecutor.releaseRequest(request)
        true
    }

    var mHandler: Handler = Handler(Looper.getMainLooper(), mHandlerCallback)

    @UiThread
    fun inflate(
        @LayoutRes resid: Int, parent: ViewGroup?,
        callback: OnInflateFinishedListener
    ) {
        val request = inflateExecutor.obtainRequest()
        request.inflater = this
        request.resid = resid
        request.parent = parent
        request.callback = callback
        inflateExecutor.inflateAsync(request)
    }

    interface OnInflateFinishedListener {
        fun onInflateFinished(view: View, @LayoutRes resid: Int, parent: ViewGroup?)
    }

    private data class InflateRequest(
        var inflater: AsyncLayoutInflaterEx? = null,
        var parent: ViewGroup? = null,
        var resid: Int = 0,
        var view: View? = null,
        var callback: OnInflateFinishedListener? = null,
    )

    private class BasicInflater(context: Context?) : LayoutInflater(context) {
        override fun cloneInContext(newContext: Context): LayoutInflater {
            return BasicInflater(newContext)
        }

        @Throws(ClassNotFoundException::class)
        override fun onCreateView(name: String, attrs: AttributeSet): View {
            for (prefix in sClassPrefixList) {
                try {
                    val view = createView(name, prefix, attrs)
                    if (view != null) {
                        return view
                    }
                } catch (e: ClassNotFoundException) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }
            return super.onCreateView(name, attrs)
        }

        companion object {
            private val sClassPrefixList = arrayOf(
                "android.widget.",
                "android.webkit.",
                "android.app."
            )
        }
    }

    private class InflateExecutor : CoroutineScope by MainScope() {
        private val mRequestPool = SynchronizedPool<InflateRequest>(10)

        fun obtainRequest(): InflateRequest {
            var obj = mRequestPool.acquire()
            if (obj == null) {
                obj = InflateRequest()
            }
            return obj
        }

        fun releaseRequest(obj: InflateRequest) {
            obj.callback = null
            obj.inflater = null
            obj.parent = null
            obj.resid = 0
            obj.view = null
            mRequestPool.release(obj)
        }

        fun inflateAsync(request: InflateRequest) {
            launch(Dispatchers.IO) {
                try {
                    request.view = request.inflater!!.mInflater.inflate(
                        request.resid, request.parent, false
                    )
                } catch (ex: RuntimeException) {
                    // Probably a Looper failure, retry on the UI thread
                    Log.w(
                        TAG, "Failed to inflate resource in the background! Retrying on the UI"
                                + " thread", ex
                    )
                }
                Message.obtain(request.inflater!!.mHandler, 0, request)
                    .sendToTarget()
            }
        }
    }

    companion object {
        private const val TAG = "AsyncLayoutInflater"
        private val inflateExecutor by lazy { InflateExecutor() }
    }
}