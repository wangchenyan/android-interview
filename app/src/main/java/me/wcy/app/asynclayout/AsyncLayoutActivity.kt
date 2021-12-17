package me.wcy.app.asynclayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.wcy.app.R
import me.wcy.app.databinding.ActivityAsyncLayoutBinding
import me.wcy.app.viewBindings

/**
 * Created by wangchenyan.top on 2021/9/6.
 */
class AsyncLayoutActivity : AppCompatActivity() {
    private val viewBinding by viewBindings<ActivityAsyncLayoutBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        supportFragmentManager.beginTransaction().replace(R.id.content, AsyncLayoutFragment())
            .commitAllowingStateLoss()
    }
}