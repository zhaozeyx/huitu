package com.demon.huitu.preference

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/*
 * 文件名: Preference
 * 版    权：  Copyright Sooc. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/6/8
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
open class Preference<T>(val context: Context, var path: String = "default", val name: String, val default: T)
    : ReadWriteProperty<Any?, T> {

    constructor(context: Context, name: String, default: T) : this(context, "default", name, default)

    val prefs by lazy { context.getSharedPreferences(path, Context.MODE_PRIVATE) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    private fun <U> findPreference(name: String, default: U): U = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("the type is not surppoted")
        }
        res as U
    }

    private fun <U> putPreference(name: String, value: U) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }.apply()
    }
}