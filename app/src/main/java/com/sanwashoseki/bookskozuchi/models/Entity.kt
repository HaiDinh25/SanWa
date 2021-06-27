package com.sanwashoseki.bookskozuchi.models

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject

open class Entity {
    override fun toString(): String {
        return try {
            val json = JSONObject(GSON.toJson(this))
            json.toString(4)
        } catch (e: JSONException) {
            Gson().toJson(this)
        }
    }

    companion object {
        private val GSON = GsonBuilder().serializeNulls().create()
    }
}