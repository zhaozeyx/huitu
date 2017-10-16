package com.demon.huitu.net;
/*
 * 文件名: RpcCallManager
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/4/28
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public interface RpcCallManager {
  <T> void manageRpcCall(Flowable<T> flowable, DisposableSubscriber<T> subscribe);

  class RpcCallManagerImpl implements RpcCallManager {

    private CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    public <T> void manageRpcCall(Flowable<T> flowable, final DisposableSubscriber<T> subscribe) {
      mCompositeSubscription.add(flowable.observeOn(io.reactivex.android.schedulers
          .AndroidSchedulers.mainThread()).subscribeWith
          (subscribe));
    }

    public void unSubscribeAll() {
      mCompositeSubscription.dispose();
    }
  }
}
