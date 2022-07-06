// IClientConn.aidl
package com.example.ipc;

// Declare any non-default types here with import statements

interface IClientConn {
   void receiveFromServer(String json);
}