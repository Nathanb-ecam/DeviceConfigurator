package com.example.arduinobluetooth.storage

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {
    companion object {
        private const val PREF_NAME = "MyPrefs"
        private const val STORED_CID = "CID"
        private const val STORED_TOPIC = "TOPIC"

    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var cid : String?
        get() = sharedPreferences.getString(STORED_CID, null)
        set(value) = sharedPreferences.edit().putString(STORED_CID, value).apply()

    var topic : String?
        get() = sharedPreferences.getString(STORED_TOPIC, null)
        set(value) = sharedPreferences.edit().putString(STORED_TOPIC, value).apply()

}