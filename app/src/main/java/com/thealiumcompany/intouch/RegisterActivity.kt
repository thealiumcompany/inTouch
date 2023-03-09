package com.thealiumcompany.intouch

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    //Firebase variables
    private lateinit var auth: FirebaseAuth

    //XML Variables
    private lateinit var emailFieldReg: EditText
    private lateinit var usernameFieldReg: EditText
    private lateinit var passwordFieldReg: EditText
    private lateinit var confirmFieldReg: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBarReg: ProgressBar
    private lateinit var loginInstead: TextView
    private lateinit var showHidePassReg: ImageButton
    private lateinit var showHidePassReg1: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailFieldReg = findViewById(R.id.email_field_reg)
        usernameFieldReg = findViewById(R.id.username_field)
        passwordFieldReg = findViewById(R.id.pass_field_reg)
        confirmFieldReg = findViewById(R.id.confirm_pass_field)
        registerButton = findViewById(R.id.register_button)
        progressBarReg = findViewById(R.id.progress_bar_reg)
        loginInstead = findViewById(R.id.login_instead)
        showHidePassReg = findViewById(R.id.show_hide_password_reg)
        showHidePassReg1 = findViewById(R.id.show_hide_password_reg1)

        //Show Hide Password Logic
        showHidePassReg.setOnClickListener {
            if (passwordFieldReg.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)   {
                passwordFieldReg.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                showHidePassReg.setImageResource(R.drawable.ic_eye_open)
            } else if (passwordFieldReg.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL)  {
                passwordFieldReg.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showHidePassReg.setImageResource(R.drawable.ic_eye_closed)
            }
        }

        showHidePassReg1.setOnClickListener {
            if (confirmFieldReg.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)   {
                confirmFieldReg.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                showHidePassReg1.setImageResource(R.drawable.ic_eye_open)
            } else if (confirmFieldReg.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL)  {
                confirmFieldReg.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showHidePassReg1.setImageResource(R.drawable.ic_eye_closed)
            }
        }

        auth = Firebase.auth

        registerButton.setOnClickListener {
            registerNewUser()
        }

        loginInstead.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    private fun registerNewUser() {
        var email = emailFieldReg.text.toString().trim()
        var username = usernameFieldReg.text.toString().trim()
        var password = passwordFieldReg.text.toString().trim()
        var confirmPass = confirmFieldReg.text.toString().trim()

        if (email.isEmpty()) {
            emailFieldReg.error = "Email is required."
            emailFieldReg.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailFieldReg.error = "Please provide a valid email."
            emailFieldReg.requestFocus()
            return
        }

        if (username.isEmpty()) {
            usernameFieldReg.error = "Username is required."
            usernameFieldReg.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passwordFieldReg.error = "Password is required."
            passwordFieldReg.requestFocus()
            return
        }

        if (password.length < 6) {
            passwordFieldReg.error = "Password must be longer than six characters."
            passwordFieldReg.requestFocus()
            return
        }

        if (confirmPass != password) {
            confirmFieldReg.error = "Your passwords do not match."
            confirmFieldReg.requestFocus()
            return
        }

        progressBarReg.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isComplete) {
                    var user = User(email, username, password)

                    FirebaseDatabase.getInstance("https://intouch-6eeb7-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference("Users")
                        .child(username)
                        .setValue(user)
                        .addOnCompleteListener { it ->
                            if (it.isComplete) {
                                Toast.makeText(
                                    this,
                                    "User registration successful.",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBarReg.visibility = View.GONE
                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "User registration failed. Please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_LONG)
                        .show()
                    progressBarReg.visibility = View.GONE
                }
            }
    }
}