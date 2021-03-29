package me.wcy.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val intent = Intent()
        intent.action = "me.wcy.user"
        intent.component = ComponentName("me.wcy.app", "me.wcy.app.aidl.UserService")
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
    }

    override fun onStart() {
        super.onStart()
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                Log.e("Life", "onCreate")
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                Log.e("Life", "onStart")
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                Log.e("Life", "onResume")
            }
        })
    }

    private var userInterface: IUserInterface? = null

    private val conn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            userInterface = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            userInterface = IUserInterface.Stub.asInterface(service)
        }
    }
}
