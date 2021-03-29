package me.wcy.app.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import me.wcy.app.IUserInterface

/**
 * Created by wcy on 2021/3/1.
 */
class UserService : Service() {
    class UserInterface : IUserInterface.Stub() {
        override fun getUserId(): Long {
            return 1
        }

        override fun getUserName(userId: Long): String {
            return "username-$userId"
        }
    }

    private val userInterface = UserInterface()

    override fun onBind(intent: Intent?): IBinder? {
        return userInterface
    }
}