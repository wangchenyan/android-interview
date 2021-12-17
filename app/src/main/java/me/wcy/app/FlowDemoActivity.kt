package me.wcy.app

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by wangchenyan.top on 2021/9/6.
 */
class FlowDemoActivity : ComponentActivity(),
    CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launch {
            test()
        }
    }

    private suspend fun test() {
        val list = listOf(1, 2, 3)
        val resList = mutableListOf<String>()
        Log.e("WCY", "start")
        list.map { flowOf(it) }
            .merge()
            .map {
                SystemClock.sleep(3000)
                it.toString()
            }
            .flowOn(Dispatchers.IO)
            .collect {
                resList.add(it)
            }
        Log.e("WCY", "end")
    }

    private fun testRxjava() {
        Log.e("WCY", "rx start")
        val list = listOf(1, 2, 3)
        Observable.fromIterable(list)
            .observeOn(Schedulers.io())
            .map {
                SystemClock.sleep(3000)
                it.toString()
            }
            .toList()
            .subscribe { t ->
                Log.e("WCY", "rx end")
            }
    }
}