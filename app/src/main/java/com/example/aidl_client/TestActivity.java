package com.example.aidl_client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ipc.IClientConn;
import com.example.ipc.ICommunicationManager;
import com.example.ipc.Ipc;
import com.example.ipc.LocalService;

/**
 * description：
 *
 * @author: Lwang
 * @createTime: 2022/7/6 1:50 下午
 */

public class TestActivity extends Activity implements View.OnClickListener {
  private ICommunicationManager mService;


  private IClientConn clientConn = new IClientConn.Stub() {
    @Override public void receiveFromServer(String json) throws RemoteException {
      System.out.println("客户端接收 json : " + json);
    }
  };
  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mService = ICommunicationManager.Stub.asInterface(service);
      try {
        //设置死亡代理
        service.linkToDeath(mDeathRecipient, 0);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
      try {
        mService.registClientConn(clientConn);
        mService.receive("i'm client");
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mService = null;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
    init();
  }

  private void init() {

    findViewById(R.id.tvTest).setOnClickListener(this);

    bind();
  }

  @Override
  public void onClick(View v) {
    //unbind();

    Ipc.Companion.sendMessage("i'm client click1");

    //try {
    //  mService.receive("i'm client onClick");
    //} catch (RemoteException e) {
    //  e.printStackTrace();
    //}
  }

  private void bind() {
    //Intent intent = new Intent();
    //intent.setAction("com.example.ipc.CommunicationService");
    ////从 Android 5.0开始 隐式Intent绑定服务的方式已不能使用,所以这里需要设置Service所在服务端的包名
    //intent.setPackage("com.example.aidl_example");
    //bindService(intent, connection, Context.BIND_AUTO_CREATE);

    startService(new Intent(TestActivity.this,LocalService.class));
  }

  /**
   * 监听Binder是否死亡
   */
  private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
    @Override
    public void binderDied() {
      if (mService == null) {
        return;
      }
      mService.asBinder().unlinkToDeath(mDeathRecipient, 0);
      mService = null;
      //重新绑定
      bind();
    }
  };

  private void unbind() {
    if (connection != null && mService.asBinder().isBinderAlive()) {
      try {
        mService.unRegist(clientConn);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
      unbindService(connection);
    }
  }

  @Override
  protected void onDestroy() {
    unbind();
    super.onDestroy();
  }
}

