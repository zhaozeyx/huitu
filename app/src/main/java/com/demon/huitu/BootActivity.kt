package com.demon.huitu

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.demon.huitu.ui.login.LoginActivity
import com.demon.huitu.util.imageloader.ImageLoader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_boot.*
import java.util.concurrent.TimeUnit

class BootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)
        performUiForward()
//        ImageLoader.loadOptimizedHttpImage(this@BootActivity,"http://172.16.6.112//2017-08-10/598c09a270ddf371010815.jpg")
//                .into(img)
    }

    private fun performUiForward() {
        Observable.just("").subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .delay(1500L, TimeUnit.MILLISECONDS)
                .subscribe(
                        {
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        })
    }

}
