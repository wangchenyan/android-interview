package me.wcy.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by wangchenyan.top on 2021/9/6.
 */
class AidlDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent()
        intent.action = "me.wcy.user"
        intent.component = ComponentName("me.wcy.app", "me.wcy.app.aidl.UserService")
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
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