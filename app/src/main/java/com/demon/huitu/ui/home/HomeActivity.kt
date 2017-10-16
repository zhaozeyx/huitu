package com.demon.huitu.ui.home

import android.os.Bundle
import android.util.Log
import com.demon.huitu.R
import com.demon.huitu.ui.basic.tab.BaseTabActivity
import com.demon.huitu.ui.profile.ProfileFragment
import com.demon.huitu.ui.task.TaskFragment
import com.demon.huitu.ui.taskmap.TaskMapFragment
import java.math.BigDecimal
import java.math.RoundingMode

class HomeActivity : BaseTabActivity() {

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
    }

    override fun layoutId(): Int {
        return R.layout.main_tab
    }

    override fun contentClazzes(): Array<Class<*>> {
        return arrayOf(TaskMapFragment::class.java, TaskFragment::class.java, ProfileFragment::class.java)
    }

    override fun tabTitles(): Array<String>? {
        return resources.getStringArray(R.array.tab_titles)
    }

    override fun onBackPressed() {
        var decimalFirst: BigDecimal = BigDecimal("3.14")
        var decimalSecond: BigDecimal = BigDecimal("3.10")
        var decimalThird: BigDecimal = BigDecimal("3.16")

        var result1 : BigDecimal = decimalFirst.setScale(1,RoundingMode.HALF_UP)
        var result2 : BigDecimal = decimalSecond.setScale(1,RoundingMode.HALF_UP)
        var result3 : BigDecimal = decimalThird.setScale(1,RoundingMode.HALF_UP)

        Log.d("YZZ", "result1 : $result1")
        Log.d("YZZ", "result2 : $result2")
        Log.d("YZZ", "result3 : $result3")

//        Single.just("a").flatMap { a ->
//            Toast.makeText(this, "${a}_first", Toast.LENGTH_SHORT).show()
//            Single.just("${a}_first")
//        }.flatMap { b -> Single.just("${b}_second") }
//                .subscribe { c -> Toast.makeText(this, c, Toast.LENGTH_SHORT).show() }

//        val list = listOf("1", "2", 22, "3", "33", "4").filterNotNull()
//        val list2 = listOf(listOf("second_0_0", "second_0_1", "second_0_2"), listOf("second_1_0", "second_11"))
//
//        Flowable.fromIterable(list2).flatMap { list -> Flowable.fromIterable(list) }
//                .subscribe({ str -> Toast.makeText(this, str, Toast.LENGTH_SHORT).show() })
//        Flowable.fromIterable(list).concatWith(Flowable.just(Model())).subscribe({ a -> Toast.makeText(this, a.toString(), Toast.LENGTH_SHORT).show() })
//        Flowable.fromIterable(list).map {
//            s ->
//            "转换 $s"
//        }.toList().subscribe { a -> Toast.makeText(this, a.size.toString(), Toast.LENGTH_SHORT).show() }
//        Flowable.fromIterable(list).filter { t -> !TextUtils.isEmpty(t) }
//                .subscribe({ a -> Toast.makeText(this, a.toString(), Toast.LENGTH_SHORT).show() })
//        onBackPressedFlowable.buffer(2).subscribe({ a ->
//            Logger.d("YZZ", a.size.toString())
//            Toast.makeText(this, a.size.toString(), Toast.LENGTH_SHORT).show()
//        })
        super.onBackPressed()
    }

    class Model {
    }

    class ModelSecond {

    }

    class ModelThird {

    }
}

