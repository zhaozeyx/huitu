package com.demon.huitu.injection

/*
 * 文件名: ActivityBus
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/2
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

import javax.inject.Qualifier

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
internal annotation class ActivityBus
