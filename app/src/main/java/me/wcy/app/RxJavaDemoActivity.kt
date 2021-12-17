package me.wcy.app

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.github.mthli.rxcoroutineschedulers.asScheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Created by wangchenyan.top on 2021/9/6.
 */
class RxJavaDemoActivity : AppCompatActivity() {
    private var currentPosition = PublishSubject.create<Long>()
    private var disposable: Disposable? = null
    private var time = 0L

    private val playbackFsmDispatcher by lazy {
        Executors.newSingleThreadExecutor {
            Thread(it, "playback-msg-dispatch-thread")
        }.asCoroutineDispatcher()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_send).setOnClickListener {
            currentPosition.onNext(++time)
            Log.e("WCY", "send")
        }
        findViewById<Button>(R.id.btn_dispose).setOnClickListener {
            disposable?.dispose()
        }
        findViewById<Button>(R.id.btn_subscribe).setOnClickListener {
            disposable = currentPosition
                .observeOn(playbackFsmDispatcher.asScheduler())
                .buffer(2, 1)
                .observeOn(Dispatchers.Main.asScheduler())
                .subscribe {
                    Log.e("WCY", "message positions: $it")
                }
        }
        findViewById<Button>(R.id.btn_count_down).setOnClickListener {
            Single.create<String> {
                SystemClock.sleep(5000)
                it.onSuccess("1")
            }
                .subscribeOn(playbackFsmDispatcher.asScheduler())
                .observeOn(Dispatchers.Main.asScheduler())
                .subscribe { t ->
                    Log.e("WCY", "block finish")
                }
        }
    }
}