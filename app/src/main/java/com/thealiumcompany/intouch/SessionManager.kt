package com.thealiumcompany.intouch

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private lateinit var usersSession: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var context: Context

    //Session variables
    val SESSION_USERSESSION = "userLoginSession"
    val SESSION_REMEMBERME = "rememberMe"

    //User Session variables
    val IS_LOGIN = "IsLoggedIn"
    val KEY_EMAIL = "email"
    val KEY_PASSWORD = "password"
    val KEY_USERNAME = "username"

    //Remember Me variables
    val IS_REMEMBERME = "IsRememberMe"
    val KEY_SESSIONEMAIL = "email"
    val KEY_SESSIONPASSWORD = "password"


    init {
        this.context = context
        usersSession = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE)
        editor = usersSession.edit()
    }

    //User session
    fun storeUsername(username: String) {
        editor.putBoolean(IS_LOGIN, true)

        editor.putString(KEY_USERNAME, username)

        editor.commit()
    }

    fun getUsername(): HashMap<String, String>  {
        val userData: HashMap<String, String> = HashMap()

        userData[KEY_USERNAME] = usersSession.getString(KEY_USERNAME, "Username not found.")!!

        return userData
    }

    fun createLoginSession(username: String, email: String, password: String) {
        editor.putBoolean(IS_LOGIN, true)

        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PASSWORD, password)

        editor.commit()
    }

    fun getUserSessionDetails(): HashMap<String, String>    {
        val userData: HashMap<String, String> = HashMap()

        userData[KEY_USERNAME] = usersSession.getString(KEY_USERNAME, null)!!
        userData[KEY_EMAIL] = usersSession.getString(KEY_EMAIL, null)!!
        userData[KEY_PASSWORD] = usersSession.getString(KEY_PASSWORD, null)!!

        return userData
    }

    fun checkLogin(): Boolean   {
        return usersSession.getBoolean(IS_LOGIN, false)
    }

    fun logoutUser()    {
        editor.clear()
        editor.commit()
    }

    //RememberMe
    fun createRememberSession(email: String, password: String) {
        editor.putBoolean(IS_REMEMBERME, true)

        editor.putString(KEY_SESSIONEMAIL, email)
        editor.putString(KEY_SESSIONPASSWORD, password)

        editor.commit()
    }

    fun getRememberSessionDetails(): HashMap<String, String>    {
        val userData: HashMap<String, String> = HashMap()

        userData[KEY_SESSIONEMAIL] = usersSession.getString(KEY_SESSIONEMAIL, null)!!
        userData[KEY_SESSIONPASSWORD] = usersSession.getString(KEY_SESSIONPASSWORD, null)!!

        return userData
    }

    fun checkRememberMe(): Boolean   {
        return usersSession.getBoolean(IS_REMEMBERME, false)
    }
}