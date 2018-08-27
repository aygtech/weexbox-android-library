/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.weexbox.core.https;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WXOkHttpDispatcher {

  static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
  static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
  static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
  private static final int SUBMIT = 0x01;
  private final OkHttpClient mOkHttpClient;
  private final HandlerThread mDispatcherThread;
  private Handler mUiHandler;
  private DispatcherHandler mDispatcherHandler;

  public WXOkHttpDispatcher(Handler handler) {
    mUiHandler = handler;
    mOkHttpClient = defaultOkHttpClient();
    mDispatcherThread = new HandlerThread("dispatcherThread");
    mDispatcherThread.start();
    mDispatcherHandler = new DispatcherHandler(mDispatcherThread.getLooper(), mOkHttpClient, mUiHandler);
  }

  private static OkHttpClient defaultOkHttpClient() {
    OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .build();
    return client;
  }

  public void dispatchSubmit(WXHttpTask task) {
    mDispatcherHandler.sendMessage(mDispatcherHandler.obtainMessage(SUBMIT, task));
  }

  private static class DispatcherHandler extends Handler {

    private OkHttpClient mOkHttpClient;
    private Handler mUiHandler;

    public DispatcherHandler(Looper looper, OkHttpClient okHttpClient, Handler handler) {
      super(looper);
      mOkHttpClient = okHttpClient;
      mUiHandler = handler;
    }

    @Override
    public void handleMessage(Message msg) {
      int what = msg.what;

      switch (what) {
        case SUBMIT: {
          WXHttpTask task = (WXHttpTask) msg.obj;
          Request.Builder builder = new Request.Builder().header("User-Agent", "WeAppPlusPlayground/1.0").url(task.url);
          WXHttpResponse httpResponse = new WXHttpResponse();
          try {
            Response response = mOkHttpClient.newCall(builder.build()).execute();
            httpResponse.code = response.code();
            httpResponse.data = response.body().bytes();
            task.response = httpResponse;
            mUiHandler.sendMessage(mUiHandler.obtainMessage(1, task));
          } catch (Throwable e) {
            e.printStackTrace();
            httpResponse.code = 1000;
            mUiHandler.sendMessage(mUiHandler.obtainMessage(1, task));
          }
        }
        break;

        default:
          break;
      }
    }

  }
}
