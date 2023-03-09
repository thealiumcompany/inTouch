package com.thealiumcompany.intouch

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.AlertDialog
import androidx.databinding.BindingAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.File

class UserAccount : AppCompatActivity() {

    //Firebase variables
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: DatabaseReference
    private lateinit var user: FirebaseUser
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid

    //XML variables
    private lateinit var logout: TextView
    private lateinit var backUA: ImageButton
    private lateinit var userAccountPP: ImageView
    private lateinit var usernameUA: TextView
    private lateinit var emailUA: TextView
    private lateinit var passwordUA: TextView
    private lateinit var browseLogoButton: Button
    private lateinit var uploadedLogo: ImageView
    private lateinit var logoFileName: TextView
    private lateinit var uploadedPic: ImageView
    private lateinit var photoFileName: TextView
    private lateinit var browsePhotoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account)

        logout = findViewById(R.id.logout_button)
        backUA = findViewById(R.id.back_user_account)
        userAccountPP = findViewById(R.id.user_account_pp)
        usernameUA = findViewById(R.id.username_ua)
        emailUA = findViewById(R.id.email_ua)
        passwordUA = findViewById(R.id.password_ua)
        browseLogoButton = findViewById(R.id.browse_logo_button)
        uploadedLogo = findViewById(R.id.uploaded_logo)
        logoFileName = findViewById(R.id.logo_file_name)
        uploadedPic = findViewById(R.id.uploaded_picture)
        photoFileName = findViewById(R.id.photo_file_name)
        browsePhotoButton = findViewById(R.id.browse_photo)

        auth = Firebase.auth
        storage = Firebase.storage

        logout.setOnClickListener {
            logoutUser()
        }

        backUA.setOnClickListener {
            finish()
        }

        try {
            loadProfilePicture()

            val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val imageUri = data!!.data
                    userAccountPP.setImageURI(imageUri)
                    uploadImageToFirebase(imageUri!!)
                }
            }

            userAccountPP.setOnClickListener {
                val popupMenu = PopupMenu(baseContext, userAccountPP)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId)    {
                        R.id.nav_change_pp ->   {
                            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            resultLauncher.launch(openGallery)
                            true
                        }
                        R.id.nav_delete -> {
                            userAccountPP.setImageResource(R.drawable.ic_blank_user)
                            storage.reference.child(userID).child("Profile Picture").delete()
                            true
                        } else -> true
                    }
                }
                popupMenu.inflate(R.menu.popup_menu)
                popupMenu.show()
            }
        } catch (se: StorageException)  {
            Toast.makeText(this@UserAccount, "Profile picture not set.", Toast.LENGTH_SHORT).show()
        }

        try {
            loadUserLogo()

            val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val imageUri = data!!.data
                    uploadedLogo.setImageURI(imageUri)
                    uploadLogoToFirebase(imageUri!!)
                    logoFileName.text = getFileName(imageUri)
                }
            }

            browseLogoButton.setOnClickListener {
                val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(openGallery)
            }
        } catch (se: StorageException)  {
            Log.v("Storage Exception", "Error with media transfer")
        }

        try {
            loadUserPhoto()

            val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val imageUri = data!!.data
                    uploadedPic.setImageURI(imageUri)
                    uploadPhotoToFirebase(imageUri!!)
                    photoFileName.text = getFileName(imageUri)
                }
            }

            browsePhotoButton.setOnClickListener {
                val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(openGallery)
            }
        } catch (se: StorageException)  {
            Log.v("Storage Exception", "Error with media transfer")
        }

        loadUserDetails()

        usernameUA.setOnClickListener {
            showDialog()
        }

        emailUA.setOnClickListener {
            showDialog()
        }

        passwordUA.setOnClickListener {
            showDialog()
        }
    }

    private fun loadUserPhoto() {
        val remoteProfilePic: StorageReference = storage.reference.child(userID).child("Uploaded Photo")
        remoteProfilePic.downloadUrl
            .addOnSuccessListener {
                Picasso.get().load(it).into(uploadedPic)
            }
            .addOnFailureListener {
                uploadedLogo.setImageResource(R.drawable.img_sample_pp)
            }
    }

    private fun uploadPhotoToFirebase(imageUri: Uri) {
        val fileRef: StorageReference = storage.reference.child(userID).child("Uploaded Photo")
        fileRef.putFile(imageUri)
    }

    private fun loadUserLogo() {
        val remoteProfilePic: StorageReference = storage.reference.child(userID).child("Uploaded Logo")
        remoteProfilePic.downloadUrl
            .addOnSuccessListener {
                Picasso.get().load(it).into(uploadedLogo)
            }
            .addOnFailureListener {
                uploadedLogo.setImageResource(R.drawable.img_alium_logo)
            }
    }

    private fun uploadLogoToFirebase(imageUri: Uri) {
        val fileRef: StorageReference = storage.reference.child(userID).child("Uploaded Logo")
        fileRef.putFile(imageUri)
    }

    private fun logoutUser() {
        var logoutDialog = AlertDialog.Builder(this@UserAccount)
        var sessionManager = SessionManager(this@UserAccount)

        logoutDialog.setTitle("Confirm")
            .setMessage("Are you sure you want to logout?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, which ->
                Firebase.auth.signOut()
                sessionManager.logoutUser()
                startActivity(Intent(this@UserAccount, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("No") {dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val fileRef: StorageReference = storage.reference.child(userID).child("Profile Picture")
        fileRef.putFile(imageUri)
    }

    private fun loadProfilePicture() {
        val remoteProfilePic: StorageReference = storage.reference.child(userID).child("Profile Picture")
        remoteProfilePic.downloadUrl
            .addOnSuccessListener {
                Picasso.get().load(it).into(userAccountPP)
            }
    }

    private fun loadUserDetails()   {
        user = Firebase.auth.currentUser!!
        val userEmail = user.email

        database = FirebaseDatabase.getInstance("https://intouch-6eeb7-default-rtdb.europe-west1.firebasedatabase.app").reference
        val query: Query = database.child("Users").orderByChild("email").equalTo(userEmail)

        if (user != null)   {
            query.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(baseContext, "User details not found. Check your connection.", Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot: DataSnapshot in snapshot.children)   {
                        var usernameD = dataSnapshot.child("username").value as String?
                        var emailD = dataSnapshot.child("email").value as String?

                        usernameUA.text = "Username: $usernameD"
                        emailUA.text = "Email: $emailD"
                    }
                }
            })
        }
    }

    private fun showDialog()    {
        val dialog = Dialog(this@UserAccount)
        dialog.setContentView(R.layout.edit_user_details_dialog)

        val close: ImageButton = dialog.findViewById(R.id.close_dialog)
        val newUserDetails: EditText = dialog.findViewById(R.id.new_user_details)

        close.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    fun Context.getFileName(uri: Uri): String? = when(uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> getContentFileName(uri)
        else -> uri.path?.let(::File)?.name
    }

    private fun Context.getContentFileName(uri: Uri): String? = runCatching {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
        }
    }.getOrNull()
}