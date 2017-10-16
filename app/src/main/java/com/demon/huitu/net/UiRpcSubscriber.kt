package com.demon.huitu.net


import com.demon.huitu.application.BaseApplication
import com.demon.huitu.log.Logger
import com.demon.huitu.net.model.ResponseModel
import io.reactivex.subscribers.DisposableSubscriber

abstract class UiRpcSubscriber<T>(private val application: BaseApplication) : DisposableSubscriber<ResponseModel<T>>() {
    val httpErrorUiNotifier: HttpErrorUiNotifier = application.globalComponent.httpErrorUiNotifier()
    val sessionNotifier: SessionNotifier = application.globalComponent.sessionNotifier()

    public override fun onStart() {
        request(Integer.MAX_VALUE.toLong())
    }

    override fun onComplete() {
        onEnd()
    }

    override fun onError(e: Throwable) {
        onHttpError(RpcHttpError(NetConstant.HttpCodeConstant.UNKNOWN_ERROR, ""))
        Logger.e(TAG, e, e.message)
        onComplete()
    }


    override fun onNext(responseModel: ResponseModel<T>?) {
        if (null == responseModel) {
            onHttpError(RpcHttpError(NetConstant.HttpCodeConstant.UNKNOWN_ERROR, ""))
            return
        }

        when (responseModel.code) {
            NetConstant.HttpCodeConstant.REMOTE_SUCCESS
            -> {
                onSuccess(responseModel.data)
                onComplete()
            }
            NetConstant.HttpCodeConstant.HTTP_ERROR_NOT_FOUND
            -> {
                onHttpError(RpcHttpError(responseModel.code, responseModel
                        .message))
                return
            }
            else -> onApiError(RpcApiError(responseModel.code, responseModel.message))

        }
    }

    fun onApiError(apiError: RpcApiError) {
        onComplete()
    }

    private fun onSessionExpired() {
        sessionNotifier.notifySessionExpired()
    }

    fun onHttpError(httpError: RpcHttpError) {
        httpErrorUiNotifier.notifyUi(httpError)
        onComplete()
    }

    protected abstract fun onSuccess(t: T?)

    protected abstract fun onEnd()

    companion object {
        private val TAG = "SERVER_ERROR"

    }

}
