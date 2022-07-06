// ICommunicationManager.aidl
package com.example.ipc;
import com.example.ipc.IClientConn;

// Declare any non-default types here with import statements

interface ICommunicationManager {
  void receive(String json);
  void registClientConn(IClientConn clientConn);
  void unRegist(IClientConn clientConn);
}