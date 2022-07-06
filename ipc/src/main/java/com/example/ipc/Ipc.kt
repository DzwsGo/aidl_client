package com.example.ipc

/**
 * description：
 *
 * @author: Lwang
 *
 * @createTime: 2022/7/6 2:37 下午
 */
class Ipc {
  companion object {
    var mServerService: ICommunicationManager? = null
    var mOnReceiveMessageListener: OnReceiveMessageListener? = null

    fun sendMessage(sendMessage:String) {
      mServerService?.receive(sendMessage)
    }

    fun onReceiveMessage(receiveMessage:String) {
      println("客户端接收 json : $receiveMessage")
      mOnReceiveMessageListener?.run {
        onMessageReceive(receiveMessage)
      }
    }
  }


  public interface OnReceiveMessageListener {
    fun onMessageReceive(message:String);
  }
}