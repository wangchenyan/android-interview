package me.wcy.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.facebook.common.references.CloseableReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.wcy.app.asynclayout.AsyncLayoutActivity
import me.wcy.app.databinding.ActivityMainBinding
import java.io.Closeable

class MainActivity : AppCompatActivity() {
    private val viewBinding by viewBindings<ActivityMainBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        //startActivity(Intent(this, FlowDemoActivity::class.java))

        viewBinding.btnFlow.setOnClickListener {
            startActivity(Intent(this, FlowDemoActivity::class.java))
        }
        viewBinding.btnCoroutine.setOnClickListener {
            startActivity(Intent(this, CoroutineActivity::class.java))
        }
        viewBinding.btnAsyncLayout.setOnClickListener {
            startActivity(Intent(this, AsyncLayoutActivity::class.java))
        }

        lifecycleScope.launch {
            val res = test()
            Log.e("WCY", "res=$res")
        }
    }

    private suspend fun test(): String {
        return withContext(Dispatchers.IO) {
            cancel()
            return@withContext "lalala"
        }
    }

    private fun closeable() {
        val model = Model()
        val ref = CloseableReference.of(model)
        ref.get()
        // do something
        ref.close()
    }
}

class Model : Closeable {
    var value: Int = 0

    override fun close() {

    }
}
