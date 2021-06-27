package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    @SuppressLint("SimpleDateFormat")
    fun getLongTime(date: String): Long {
        val f = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS")
        try {
            val d = f.parse(date)
            return d.time
        } catch (e: Exception) {
        }
        return 0
    }

    @SuppressLint("SimpleDateFormat")
    fun getLongTimeWithoutSecond(date: String): Long {
        val f = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        try {
            val d = f.parse("$date:00")
            return d.time
        } catch (e: Exception) {

        }
        return 0
    }

    fun getCreatedFormat(s: String): String {
        try {
            val long = getLongTime(s)
            val format = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            return format.format(long)
        } catch (e: Exception) {
            return s
        }
    }

    fun getMyTemplateFormat(s: String): String {
        try {
            val long = getLongTime(s)
            val format = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            return format.format(long)
        } catch (e: Exception) {
            return s
        }
    }

    fun getTime(time: Long): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getLongyyyyMMdd(date: String): Long {
        val f = SimpleDateFormat("yyyy/MM/dd")
        try {
            val d = f.parse(date)
            return d.time
        } catch (e: ParseException) {
        }
        return 0
    }

    @SuppressLint("SimpleDateFormat")
    fun getHHmm(date: String): Long {
        val f = SimpleDateFormat("HH:mm")
        try {
            val d = f.parse(date)
            return d.time
        } catch (e: ParseException) {
        }
        return 0
    }

}