package com.demon.huitu.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.demon.huitu.net.model.WxTokenModel;
import com.demon.huitu.net.service.WxService;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * 文件名: LoginA
 * 版    权：  Copyright Sooc. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/7/12
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

public class LoginA extends FragmentActivity {
    @Inject
    WxService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText text = null;
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        service.getAccessTokenCallback("", "", "", new Callback<WxTokenModel>() {
            @Override
            public void onResponse(Call<WxTokenModel> call, Response<WxTokenModel> response) {
                
            }

            @Override
            public void onFailure(Call<WxTokenModel> call, Throwable t) {

            }
        });
    }
}
