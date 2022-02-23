package me.wcy.app.viewlifecycle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.wcy.app.databinding.ActivityViewLifecycleBinding
import me.wcy.app.viewBindings

/**
 * Created by wangchenyan.top on 2021/12/23.
 */
class ViewLifecycleActivity : AppCompatActivity() {
    private val viewBinding: ActivityViewLifecycleBinding by viewBindings()
    private lateinit var lifecycleView: LifecycleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        lifecycleView = LifecycleView(applicationContext)
        viewBinding.addView.setOnClickListener {
            if (lifecycleView.parent == null) {
                viewBinding.root.addView(lifecycleView)
            }
        }
        viewBinding.removeView.setOnClickListener {
            if (lifecycleView.parent != null) {
                viewBinding.root.removeView(lifecycleView)
            }
        }
        viewBinding.finish.setOnClickListener {
            onBackPressed()
        }
    }
}