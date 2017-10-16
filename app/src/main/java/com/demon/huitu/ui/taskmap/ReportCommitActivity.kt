package com.demon.huitu.ui.taskmap

/*
 * 文件名: ReportCommitActivity
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/15
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import butterknife.ButterKnife
import com.demon.huitu.R
import com.demon.huitu.injection.DaggerServiceComponent
import com.demon.huitu.injection.ServiceModule
import com.demon.huitu.net.service.AppService
import com.demon.huitu.ui.basic.BasicActivity
import com.demon.huitu.ui.imagepick.ImageDisplayPresenter
import com.demon.huitu.ui.imagepick.ImageDisplayView
import kotlinx.android.synthetic.main.activity_report_commit.*
import javax.inject.Inject

open class ReportCommitActivity : BasicActivity() {

    @Inject
    lateinit var mAppService: AppService

    private var mReportId: String? = null
    private val mShortCutChooseDialog: AlertDialog? = null
    private val mPreConditions: Array<String>? = null
    private var mDisplayPresenter: ImageDisplayPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_commit)
        ButterKnife.bind(this)
        injectService()
        initData()
        initView()
        initLayerChooseDialog()
    }

    private fun injectService() {
        DaggerServiceComponent.builder().serviceModule(ServiceModule()).build().inject(this)
    }

    private fun initData() {
        mReportId = intent.getStringExtra(KEY_REPORT_ID)
    }

    private fun initView() {
        titleBar!!.setNavigationOnClickListener { finish() }
        mDisplayPresenter = ImageDisplayPresenter(this, 6, img_display_view as ImageDisplayView, ImageDisplayPresenter
                .COMPLETE_TASK)
        btn_send.setOnClickListener { onBtnSendClicked() }
    }

    private fun initLayerChooseDialog() {
        //    mPreConditions = getResources()
        //        .getStringArray(R.array.complete_condition);
        //    mShortCutChooseDialog = new AlertDialog.Builder(this).setSingleChoiceItems(mPreConditions,
        //        0, new DialogInterface.OnClickListener() {
        //
        //          @Override
        //          public void onClick(DialogInterface dialogInterface, int i) {
        //            descValue.setText(mPreConditions[i]);
        //            mShortCutChooseDialog.dismiss();
        //          }
        //        }).create();
    }

    fun onBtnSendClicked() {
        if (TextUtils.isEmpty(desc_value!!.text)) {
            showShortToast(R.string.activity_report_commit_info_desc_null)
            return
        }
        showProgressDialog("", true)
        //    mDisplayPresenter.uploadImg(new ImageDisplayPresenter.UploadCallback() {
        //      @Override
        //      public void onUploadCompleted(String url) {
        //        manageRpcCall(mAppService.completeLog(mReportId, descValue.getText().toString(),
        //            getComponent().loginSession().getUserId(), url), new UiRpcSubscriber<String>() {
        //
        //
        //          @Override
        //          protected void onSuccess(String s) {
        //            getComponent().globalBus().post(new TaskCompleteEvent());
        //            finish();
        //          }
        //
        //          @Override
        //          protected void onEnd() {
        //            closeProgressDialog();
        //          }
        //
        //          @Override
        //          public void onApiError(RpcApiError apiError) {
        //            super.onApiError(apiError);
        //            showShortToast(apiError.getMessage());
        //          }
        //
        //          @Override
        //          public void onHttpError(RpcHttpError httpError) {
        //            super.onHttpError(httpError);
        //            showHttpError(httpError);
        //          }
        //        });
        //      }
        //
        //      @Override
        //      public void onUploadFailed() {
        //        showShortToast(R.string.upload_img_failed);
        //      }
        //
        //    });
        //manageRpcCall(mAppService.completeLog(mReportId,descValue.getText().toString(),get));
    }


    override fun onDestroy() {
        super.onDestroy()
        mDisplayPresenter!!.onDestroy()
        if (null != mShortCutChooseDialog && mShortCutChooseDialog.isShowing) {
            mShortCutChooseDialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        mDisplayPresenter!!.onActivityResult(requestCode, resultCode, data)
    }

    inner class TaskCompleteEvent

    companion object {
        private val KEY_REPORT_ID = "report_id"

        fun makeIntent(context: Context, reportId: String): Intent {
            val intent = Intent(context, ReportCommitActivity::class.java)
            val bundle = Bundle()
            bundle.putString(KEY_REPORT_ID, reportId)
            intent.putExtras(bundle)
            return intent
        }
    }
}
