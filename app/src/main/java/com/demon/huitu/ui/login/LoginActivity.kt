package com.demon.huitu.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import com.demon.huitu.R
import com.demon.huitu.net.model.ResponseModel
import com.demon.huitu.net.model.UserInfo
import com.demon.huitu.net.model.VerifyWXBindModel
import com.demon.huitu.net.model.WxTokenModel
import com.demon.huitu.net.service.AppService
import com.demon.huitu.net.service.WxService
import com.demon.huitu.ui.home.HomeActivity
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LoginActivity : Activity() {

    @Inject
    lateinit var wxService: WxService
    @Inject
    lateinit var appService: AppService

    var code: String = ""
    var unionid: String = ""
    var openid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
    }

    fun initView() {
        user_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (TextUtils.equals("张媛", editable)) {
                    Toast.makeText(this@LoginActivity, "么么哒", Toast.LENGTH_SHORT).show()
                }
            }
        })
        login.setOnClickListener({
            performLogin()
        })
    }

    fun performLogin() {
        if (!TextUtils.isEmpty(user_name.text) && !TextUtils.isEmpty(password.text)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    fun login() {

        Flowable.just(code)
                // 指定发布者所在线程
                .subscribeOn(Schedulers.newThread())
                // 指定观察者所在线程
                .observeOn(Schedulers.newThread())
                // 获取AccessToken
                .flatMap({ code -> wxService.getAccessToken("appId", "", code) })
                // 验证绑定信息
                .flatMap({ wxTokenModel ->
                    unionid = wxTokenModel.unionid
                    openid = wxTokenModel.openid
                    appService.verifyWXBind(wxTokenModel.unionid, wxTokenModel.openid)
                })
                // 获取绑定邮箱号
                .map { wxBindModel -> wxBindModel.data?.email!! }
                // 调用微信登录接口
                .flatMapSingle { email -> appService.wxLogin(email, unionid, openid) }
                // 登录成功的处理
                .subscribe({ responseData -> saveUserInfo(responseData.data) })
    }

    fun loginCallback() {

        wxService.getAccessTokenCallback("", "", "", object : Callback<WxTokenModel> {
            override fun onResponse(call: Call<WxTokenModel>, response: Response<WxTokenModel>) {
                unionid = response.body().unionid
                openid = response.body().openid
                appService.verifyWXBindCallback(openid, unionid, object : Callback<VerifyWXBindModel> {
                    override fun onResponse(call: Call<VerifyWXBindModel>?, response: Response<VerifyWXBindModel>) {
                        var email = response.body().email
                        appService.wxLoginCallback(openid, unionid, email!!, object : Callback<ResponseModel<UserInfo>> {
                            override fun onResponse(call: Call<ResponseModel<UserInfo>>?, response: Response<ResponseModel<UserInfo>>?) {
                                runOnUiThread(Runnable {
                                    // TODO do something UI work
                                })
                                saveUserInfo(response?.body()?.data)
                            }

                            override fun onFailure(call: Call<ResponseModel<UserInfo>>?, t: Throwable?) {
                            }
                        })
                    }

                    override fun onFailure(call: Call<VerifyWXBindModel>?, t: Throwable?) {
                    }
                })
            }

            override fun onFailure(call: Call<WxTokenModel>, t: Throwable) {

            }
        })
    }

    private fun saveUserInfo(data: UserInfo?) {}
}
