package me.wcy.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.wcy.app.databinding.ActivityCoroutineBinding

/**
 * Created by wangchenyan.top on 2021/9/6.
 */
class CoroutineActivity : ComponentActivity() {
    private val viewBinding by viewBindings<ActivityCoroutineBinding>()
    private var customScope: CoroutineScope? = null
    private val mutex = Mutex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            repeat(100) {
                launch {
                    mutex.withLock {
                        tick("lifecycleScope")
                        delay(10000)
                    }
                }
                delay(3000)
            }
        }

        viewBinding.btnStart.setOnClickListener {
            customScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            customScope?.launch {
                repeat(100) {
                    launch {
                        mutex.withLock {
                            tick("customScope")
                            delay(10000)
                        }
                    }
                    delay(3000)
                }
            }
        }

        viewBinding.btnCancel.setOnClickListener {
            customScope?.cancel()
            customScope = null
        }
    }

    private fun tick(tag: String) {
        Log.e("WCY", "$tag tick in ${Thread.currentThread().name}")
    }
}