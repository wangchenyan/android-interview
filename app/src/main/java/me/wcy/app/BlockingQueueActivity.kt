package me.wcy.app

import android.os.*
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import me.wcy.app.databinding.ActivityBlockingQueueBinding
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors

/**
 * Created by wangchenyan.top on 2021/9/6.
 */
class BlockingQueueActivity : ComponentActivity() {
    private val viewBinding by viewBindings<ActivityBlockingQueueBinding>()
    private val toBeDispatchedMessages: BlockingQueue<String> = ArrayBlockingQueue(100)

    val routeHandlerThread =
        HandlerThread(BlockingQueueActivity::class.java.simpleName).apply { start() }
    val routeHandler = object : Handler(routeHandlerThread.looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    val msg = msg.obj.toString()
                    toBeDispatchedMessages.add(msg)
                }
                1 -> {
                    if (toBeDispatchedMessages.isNotEmpty()) {
                        startDispatchMessages()
                        scheduleNextDispatch()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        Thread {
            for (i in 0..1000) {
                enqueueNewMessage(i.toString())
                //SystemClock.sleep(10)
            }
        }.start()
    }

    fun enqueueNewMessage(newMessage: String) {
//        routeHandler.sendMessage(Message.obtain(routeHandler, 0, newMessage))
        toBeDispatchedMessages.put(newMessage)
        scheduleNextDispatch()
    }

    private fun scheduleNextDispatch() {
        routeHandler.sendEmptyMessage(1)
    }

    private fun startDispatchMessages() {
        val dispatchedMessage = toBeDispatchedMessages.poll() ?: return
            onRouterMessageReceived(dispatchedMessage)
    }

    private val coroutineDispatcher by lazy {
        Executors.newSingleThreadScheduledExecutor { Thread(it, "message-dispatcher") }
            .asCoroutineDispatcher()
    }

    private fun onRouterMessageReceived(message: String) {
        lifecycleScope.launch(coroutineDispatcher) {
            SystemClock.sleep(1000L)
            Log.e("WCY", "dispatch msg：$message")
        }
    }
}