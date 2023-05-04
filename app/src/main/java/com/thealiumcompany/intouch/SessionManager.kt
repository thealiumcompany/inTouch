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

    //Edit Card variables
    val WEBSITE = "website"
    val NAME = "name"
    val POSITION = "position"
    val PHONE_1 = "phone_1"
    val PHONE_2 = "phone_2"
    val EMAIL_1 = "email_1"
    val EMAIL_2 = "email_2"
    val STREET_ADDRESS = "street_address"
    val CITY_TOWN = "city_town"
    val PRIMARY_COLOR = "primary_color"
    val SECONDARY_COLOR = "secondary_color"
    val TERTIARY_COLOR = "tertiary_color"
    val TEXT_PRIMARY_COLOR = "text_primary_color"
    val TEXT_SECONDARY_COLOR = "text_secondary_color"


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

    fun createEditCardSession(website: String, name: String, position: String, phone_1: String, phone_2: String, email_1: String, email_2: String, streetAddress: String, cityTown: String, primaryColor: String, secondaryColor: String, tertiaryColor: String, textPrimary: String, textSecondary: String) {
        editor.putString(WEBSITE, website)
        editor.putString(NAME, name)
        editor.putString(POSITION, position)
        editor.putString(PHONE_1, phone_1)
        editor.putString(PHONE_2, phone_2)
        editor.putString(EMAIL_1, email_1)
        editor.putString(EMAIL_2, email_2)
        editor.putString(STREET_ADDRESS, streetAddress)
        editor.putString(CITY_TOWN, cityTown)
        editor.putString(PRIMARY_COLOR, primaryColor)
        editor.putString(SECONDARY_COLOR, secondaryColor)
        editor.putString(TERTIARY_COLOR, tertiaryColor)
        editor.putString(TEXT_PRIMARY_COLOR, textPrimary)
        editor.putString(TEXT_SECONDARY_COLOR, textSecondary)

        editor.commit()
    }

    fun getEditCardDetails(): HashMap<String, String>    {
        val cardData: HashMap<String, String> = HashMap()

        cardData[WEBSITE] = usersSession.getString(WEBSITE, context.resources.getString(R.string.alium_website))!!
        cardData[NAME] = usersSession.getString(NAME, context.resources.getString(R.string.my_name))!!
        cardData[POSITION] = usersSession.getString(POSITION, context.resources.getString(R.string.chief_executive_officer))!!
        cardData[PHONE_1] = usersSession.getString(PHONE_1, context.resources.getString(R.string.phone_no_1))!!
        cardData[PHONE_2] = usersSession.getString(PHONE_2, context.resources.getString(R.string.phone_no_2))!!
        cardData[EMAIL_1] = usersSession.getString(EMAIL_1, context.resources.getString(R.string.mishaelopokuboamah_gmail_com))!!
        cardData[EMAIL_2] = usersSession.getString(EMAIL_2, context.resources.getString(R.string.email_user_email_com))!!
        cardData[STREET_ADDRESS] = usersSession.getString(STREET_ADDRESS, context.resources.getString(R.string.house_no_15_gamba_street))!!
        cardData[CITY_TOWN] = usersSession.getString(CITY_TOWN, context.resources.getString(R.string.accra_central))!!
        cardData[PRIMARY_COLOR] = usersSession.getString(PRIMARY_COLOR, "#FB8B23")!!
        cardData[SECONDARY_COLOR] = usersSession.getString(SECONDARY_COLOR, "#FFFFFF")!!
        cardData[TERTIARY_COLOR] = usersSession.getString(TERTIARY_COLOR, "#000000")!!
        cardData[TEXT_PRIMARY_COLOR] = usersSession.getString(TEXT_PRIMARY_COLOR, "#FFFFFF")!!
        cardData[TEXT_SECONDARY_COLOR] = usersSession.getString(TEXT_SECONDARY_COLOR, "#000000")!!

        return cardData
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