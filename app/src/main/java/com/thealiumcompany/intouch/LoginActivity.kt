package com.thealiumcompany.intouch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    //Firebase Variables
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var database: DatabaseReference

    //XML Variables
    private lateinit var registerInstead: TextView
    private lateinit var emailFieldLogin: EditText
    private lateinit var passFieldLogin: EditText
    private lateinit var showHidePasswordLogin: ImageButton
    private lateinit var loginButton: Button
    private lateinit var progressBarLogin: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth

        val splashScreen = installSplashScreen()

        val currentUser = auth.currentUser
        if (currentUser != null)    {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerInstead = findViewById(R.id.register_instead)
        emailFieldLogin = findViewById(R.id.email_field_login)
        passFieldLogin = findViewById(R.id.pass_field_login)
        showHidePasswordLogin = findViewById(R.id.show_hide_password_login)
        loginButton = findViewById(R.id.login_button)
        progressBarLogin = findViewById(R.id.progress_bar_login)

        //Show Hide Password Logic
        showHidePasswordLogin.setOnClickListener {
            if (passFieldLogin.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)   {
                passFieldLogin.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                showHidePasswordLogin.setImageResource(R.drawable.ic_eye_open)
            } else if (passFieldLogin.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL)  {
                passFieldLogin.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showHidePasswordLogin.setImageResource(R.drawable.ic_eye_closed)
            }
        }

        loginButton.setOnClickListener {
            loginUser()
        }

        registerInstead.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        var email = emailFieldLogin.text.toString().trim()
        var password = passFieldLogin.text.toString().trim()

        if (email.isEmpty())    {
            emailFieldLogin.error = "Please enter your email."
            emailFieldLogin.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())   {
            emailFieldLogin.error = "Please enter a valid email."
            emailFieldLogin.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passFieldLogin.error = "Please enter your password"
            passFieldLogin.requestFocus()
            return
        }

        progressBarLogin.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful)    {
                    progressBarLogin.visibility = View.GONE
                    Toast.makeText(baseContext, "Welcome back!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(baseContext, HomeActivity::class.java))
                    finish()
                } else  {
                    progressBarLogin.visibility = View.GONE
                    Toast.makeText(baseContext, "Login failed. Check credentials and try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onResume() {
        super.onResume()

        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        }
    }
}