package me.wcy.app.asynclayout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * 组件布局异步 inflate 工具类，在 [androidx.fragment.app.Fragment.onCreateView] 中返回 [AsyncLayoutHelper.onCreateView]，
 * 在 [androidx.fragment.app.Fragment.onViewCreated] 中调用 [AsyncLayoutHelper.waitViewCreated] 等待异步 inflate 完成，再更新 UI。
 *
 * 注意：除了上述两个方法，在其他生命周期方法中获取到的 View 可能为空，如果需要在其他生命周期操作 View，需要对 View 判空，
 * 或者通过 [AsyncLayoutHelper.isViewCreated] 判断。
 *
 * Created by wangchenyan.top on 2021/10/28.
 */
class AsyncLayoutHelper {
    private lateinit var asyncLayoutInflater: AsyncLayoutInflaterEx
    private var onViewCreated: ((view: View) -> Unit)? = null
    private var isViewCreated = false

    fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        layoutId: Int,
        placeholderId: Int = 0
    ): View? {
        container ?: return null
        val root = FrameLayout(container.context)
        if (placeholderId > 0) {
            inflater.inflate(placeholderId, root)
        }

        asyncLayoutInflater = AsyncLayoutInflaterEx(container.context)
        asyncLayoutInflater.inflate(
            layoutId,
            root,
            object : AsyncLayoutInflaterEx.OnInflateFinishedListener {
                override fun onInflateFinished(view: View, resid: Int, parent: ViewGroup?) {
                    if (parent != null && parent.childCount > 0) {
                        parent.removeAllViews()
                    }
                    parent?.addView(view)
                    isViewCreated = true
                    onViewCreated?.invoke(view)
                    onViewCreated = null
                }
            })

        return root
    }

    fun waitViewCreated(onViewCreated: (view: View) -> Unit) {
        this.onViewCreated = onViewCreated
    }
}