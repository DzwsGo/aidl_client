package com.example.aidl_client

import android.app.Application
import android.content.Intent
import com.example.ipc.Ipc
import com.example.ipc.Ipc.OnReceiveMessageListener
import com.example.ipc.LocalService

/**
 * description：
 *
 * @author: Lwang
 *
 * @createTime: 2022/7/6 2:36 下午
 */
class BaseApplication : Application() {
  companion object {
    var application:BaseApplication? = null
  }
  override fun onCreate() {
    super.onCreate()
    application = this

    Ipc.mOnReceiveMessageListener = object: OnReceiveMessageListener {
      override fun onMessageReceive(message: String) {
        if(message.contains("main")) {
          val intent = Intent(application, MainActivity::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
          startActivity(intent)
        }
      }

    }
  }
}