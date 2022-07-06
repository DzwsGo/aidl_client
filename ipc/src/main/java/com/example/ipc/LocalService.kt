package com.example.ipc

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.RemoteException
import com.example.ipc.IClientConn.Stub

/**
 * description：
 *
 * @author: Lwang
 *
 * @createTime: 2022/7/6 2:29 下午
 */
class LocalService : Service() {
  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()
    println("客户端服务 oncreate")
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    println("客户端服务 onStartCommand")
    bind()
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onDestroy() {
    println("客户端服务 onDestroy")
    unbind()
    super.onDestroy()
  }

  private val clientConn: IClientConn = object : Stub() {
    @Throws(RemoteException::class) override fun receiveFromServer(json: String) {
      Ipc.onReceiveMessage(json)
    }
  }
  private val connection: ServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
      Ipc.mServerService = ICommunicationManager.Stub.asInterface(service)
      try {
        //设置死亡代理
        service.linkToDeath(mDeathRecipient, 0)
      } catch (e: RemoteException) {
        e.printStackTrace()
      }
      try {
        Ipc.mServerService?.registClientConn(clientConn)
        Ipc.sendMessage("i'm client")
      } catch (e: RemoteException) {
        e.printStackTrace()
      }
    }

    override fun onServiceDisconnected(name: ComponentName) {
      Ipc.mServerService = null
    }
  }

  /**
   * 监听Binder是否死亡
   */
  private val mDeathRecipient: DeathRecipient = object : DeathRecipient {
    override fun binderDied() {
      if (Ipc.mServerService == null) {
        return
      }
      Ipc.mServerService!!.asBinder().unlinkToDeath(this, 0)
      Ipc.mServerService = null
      //重新绑定
      bind()
    }
  }

  private fun bind() {
    if (Ipc.mServerService == null) {
      val intent = Intent()
      intent.action = "com.example.ipc.CommunicationService"
      //从 Android 5.0开始 隐式Intent绑定服务的方式已不能使用,所以这里需要设置Service所在服务端的包名
      intent.setPackage("com.example.aidl_example")
      bindService(intent, connection, BIND_AUTO_CREATE)
    }

  }


  private fun unbind() {
    if (connection != null && Ipc.mServerService!!.asBinder().isBinderAlive) {
      try {
        Ipc.mServerService!!.unRegist(clientConn)
      } catch (e: RemoteException) {
        e.printStackTrace()
      }
      unbindService(connection)
    }
  }

}