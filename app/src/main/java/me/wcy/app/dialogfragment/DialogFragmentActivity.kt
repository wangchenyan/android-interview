package me.wcy.app.dialogfragment

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by wangchenyan.top on 2021/12/23.
 */
class DialogFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = FrameLayout(this)
        content.id = View.generateViewId()
        setContentView(content)

        supportFragmentManager.beginTransaction()
            .replace(content.id, TestFragment())
            .commit()
    }
}