package com.demon.huitu.util

/*
 * 文件名: Test
 * 版    权：  Copyright Sooc. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/6/16
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
class Test {
    fun main(args: Array<String>) {
      printFool(D())
    }

    open class C

    class D : C()

    fun C.foo() = "c"
    fun D.foo() = "d"

    fun printFool(c :C) {
        println(c.foo());
    }
}

