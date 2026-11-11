package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("dhahran_halls_prefs", Context.MODE_PRIVATE)

    companion object {
        const val ADMIN_PASSCODE = "932497"
        const val DEFAULT_SCRIPT_URL = "https://script.google.com/macros/s/AKfycbzCo8Ga8IHDAVjiYwSzbLmcY8IpsUu5kHvEKF_vwXSEALUDpj__L3w7FzTpGZC15dsJJw/exec"
        const val GOOGLE_SHEET_URL = "https://docs.google.com/spreadsheets/d/1Pkknc-VJFluTpNaGDHq6S5QhqFjLQT4p4MhA_u9DZE0/edit?usp=sharing"
        const val GOOGLE_SHEET_PUB_CSV = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSpA-L7f-DkZfx6yzkDEsaZaltpJCWmQv4C79nXw8Mk3YLHd1_ZZL1oclHTDP-qO7Shqn2C8ocQqtmv/pub?output=csv"
        
        private const val KEY_IS_ADMIN = "is_admin"
        private const val KEY_APPROVAL_REQUIRED = "approval_required"
        private const val KEY_SCRIPT_URL = "google_script_url"
        private const val KEY_LAST_SYNC_TIME = "last_sync_time"
        private const val KEY_INITIALIZED = "initial_seed_done"
    }

    var isAdmin: Boolean
        get() = prefs.getBoolean(KEY_IS_ADMIN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_ADMIN, value).apply()

    var isApprovalRequired: Boolean
        get() = prefs.getBoolean(KEY_APPROVAL_REQUIRED, true) // Option B default
        set(value) = prefs.edit().putBoolean(KEY_APPROVAL_REQUIRED, value).apply()

    var googleScriptUrl: String
        get() = prefs.getString(KEY_SCRIPT_URL, DEFAULT_SCRIPT_URL) ?: DEFAULT_SCRIPT_URL
        set(value) = prefs.edit().putString(KEY_SCRIPT_URL, value).apply()

    var lastSyncTime: Long
        get() = prefs.getLong(KEY_LAST_SYNC_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_SYNC_TIME, value).apply()

    var isInitialSeedDone: Boolean
        get() = prefs.getBoolean(KEY_INITIALIZED, false)
        set(value) = prefs.edit().putBoolean(KEY_INITIALIZED, value).apply()
}
