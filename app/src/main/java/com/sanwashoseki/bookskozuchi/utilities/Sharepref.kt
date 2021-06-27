package com.sanwashoseki.bookskozuchi.utilities

import android.content.Context
import androidx.preference.PreferenceManager

object Sharepref {
    private const val USER_EMAIL = "USER_EMAIL"
    private const val USER_PASSWORD = "USER_PASSWORD"
    private const val USER_TOKEN = "USER_TOKEN"
    private const val USER_REFRESH_TOKEN = "USER_REFRESH_TOKEN"

    private const val FIREBASE_TOKEN = "FIREBASE_TOKEN"
    private const val DARK_MODE = "DARK_MODE"

    private const val PASS_CODE_VALUE = "PASS_CODE_VALUE"
    private const val PASS_CODE_AUTH = "PASS_CODE_AUTH"
    private const val FINGER_AUTH = "FINGER_AUTH"
    private const val BIOMETRIC_AUTH = "BIOMETRIC_AUTH"

    private const val CATEGORY_ID = "CATEGORY_ID"
    private const val SUB_CATEGORY_ID = "SUB_CATEGORY_ID"
    private const val SUB_CATEGORY_NAME = "SUB_CATEGORY_NAME"
    private const val SEARCH_KEY = "SEARCH_KEY"

    private const val BOOKMARK_INDEX = "BOOKMARK_INDEX"

    private const val VERTICAL_FIRST_TIME = "VERTICAL_FIRST_TIME"

    private const val HORIZONTAL_FIRST_TIME = "HORIZONTAL_FIRST_TIME"


    fun isVerticalFirstTime(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(VERTICAL_FIRST_TIME, true)
    }

    fun setVerticalFirstTime(context: Context?, isFirstTime: Boolean = false) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(VERTICAL_FIRST_TIME, isFirstTime)
            .apply()
    }

    fun isHorizontalFirstTime(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(HORIZONTAL_FIRST_TIME, true)
    }

    fun setHorizontalFirstTime(context: Context?, isFirstTime: Boolean = false) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(HORIZONTAL_FIRST_TIME, isFirstTime)
            .apply()
    }

    @JvmStatic
    fun getUserEmail(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(USER_EMAIL, "")
    }

    private fun setUserEmail(context: Context?, user: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(USER_EMAIL, user)
            .apply()
    }

    @JvmStatic
    fun getUserPassWord(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(USER_PASSWORD, "")
    }

    private fun setUserPassWord(context: Context?, user: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(USER_PASSWORD, user)
            .apply()
    }

    @JvmStatic
    fun getUserToken(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(USER_TOKEN, "")
    }

    fun setUserToken(context: Context?, token: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(USER_TOKEN, token)
            .apply()
    }

    @JvmStatic
    fun getUserRefreshToken(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(USER_REFRESH_TOKEN, "")
    }

    fun setUserRefreshToken(context: Context?, refreshToken: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(USER_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    @JvmStatic
    fun getFirebaseToken(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(FIREBASE_TOKEN, "")
    }

    fun setFirebaseToken(context: Context?, fireToken: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(FIREBASE_TOKEN, fireToken)
            .apply()
    }

    @JvmStatic
    fun getNightMode(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(DARK_MODE, false)
    }

    fun setNightMode(context: Context?, darkMode: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(DARK_MODE, darkMode)
            .apply()
    }

    @JvmStatic
    fun getPassCodeValue(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PASS_CODE_VALUE, "")
    }

    fun setPassCodeValue(context: Context?, passCode: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PASS_CODE_VALUE, passCode)
            .apply()
    }


    @JvmStatic
    fun getPassCodeAuth(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PASS_CODE_AUTH, false)
    }

    fun setPassCodeAuth(context: Context?, isActive: Boolean) {
        if (!isActive) {
            setFingerAuth(context, false)
            setBiometricAuth(context, false)
        }
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(PASS_CODE_AUTH, isActive)
            .apply()
    }

    @JvmStatic
    fun getFingerAuth(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(FINGER_AUTH, false)
    }

    fun setFingerAuth(context: Context?, isActive: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(FINGER_AUTH, isActive)
            .apply()
    }

    @JvmStatic
    fun getBiometricAuth(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(BIOMETRIC_AUTH, false)
    }

    fun setBiometricAuth(context: Context?, isActive: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(BIOMETRIC_AUTH, isActive)
            .apply()
    }

    @JvmStatic
    fun getCategoryId(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(CATEGORY_ID, "")
    }

    fun setCategoryId(context: Context?, categoryId: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(CATEGORY_ID, categoryId)
            .apply()
    }

    @JvmStatic
    fun getSubCategoryId(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(SUB_CATEGORY_ID, "")
    }

    fun setSubCategoryId(context: Context?, subCategoryId: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(SUB_CATEGORY_ID, subCategoryId)
            .apply()
    }

    @JvmStatic
    fun getSubCategoryName(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(SUB_CATEGORY_NAME, "")
    }

    fun setSubCategoryName(context: Context?, subCategoryName: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(SUB_CATEGORY_NAME, subCategoryName)
            .apply()
    }

    @JvmStatic
    fun getKeySearch(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(SEARCH_KEY, "")
    }

    fun setKeySearch(context: Context?, key: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(SEARCH_KEY, key)
            .apply()
    }

    @JvmStatic
    fun getBookmarkIndex(context: Context?): Int {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(BOOKMARK_INDEX, 0)
    }

    fun setBookmarkIndex(context: Context?, index: Int) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(BOOKMARK_INDEX, index)
            .apply()
    }

    fun saveUserInfo(context: Context?, email: String, password: String, token: String, refreshToken: String) {
        setUserEmail(context, email)
        setUserPassWord(context, password)
        setUserToken(context, token)
        setUserRefreshToken(context, refreshToken)
    }

    fun removeUserInfo(context: Context?) {
        setUserEmail(context, "")
        setUserPassWord(context, "")
        setUserToken(context, "")
        setUserRefreshToken(context, "")
    }

    fun getUserInfo(context: Context?) {
        getUserEmail(context)
        getUserPassWord(context)
        getUserToken(context)
    }

}