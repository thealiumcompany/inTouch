package com.thealiumcompany.intouch

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.thealiumcompany.intouch.databinding.ActivityAddNewCardBinding
import com.thealiumcompany.intouch.databinding.ActivityEditCardBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class EditCard : AppCompatActivity() {
    //Firebase variables
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: DatabaseReference
    private lateinit var user: FirebaseUser
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid

    //XML variables
    private lateinit var currentCardEditingSection: RelativeLayout
    private lateinit var currentCardFrontSection: RelativeLayout
    private lateinit var currentCardBackSection: RelativeLayout
    private lateinit var currentCardBack: ViewStub
    private lateinit var currentCardFront: ViewStub
    private lateinit var backShow: FloatingActionButton
    private lateinit var frontShow: FloatingActionButton
    private lateinit var primaryColor: CardView
    private lateinit var secondaryColor: CardView
    private lateinit var tertiaryColor: CardView
    private lateinit var textPrimaryColor: CardView
    private lateinit var textSecondaryColor: CardView
    private lateinit var currentCardName: TextView
    private lateinit var saveButton: Button
    private lateinit var progressBarAddNew: ProgressBar

    //CardDetailsVariables
    private lateinit var primaryColorCode: TextView
    private lateinit var secondaryColorCode: TextView
    private lateinit var tertiaryColorCode: TextView
    private lateinit var textPrimaryColorCode: TextView
    private lateinit var textSecondaryColorCode: TextView
    private lateinit var cardWebsite: EditText
    private lateinit var cardName: EditText
    private lateinit var cardPosition: EditText
    private lateinit var cardPhone1: EditText
    private lateinit var cardPhone2: EditText
    private lateinit var cardEmail1: EditText
    private lateinit var cardEmail2: EditText
    private lateinit var cardStreetAddress: EditText
    private lateinit var cardCityTown: EditText

    //Binding variables
    private lateinit var activityEditCardBinding: ActivityEditCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
        auth = Firebase.auth
        database = Firebase.database.reference

        val clickedCard = intent.getStringExtra("Clicked Card")

        backPressed()

        val cardDetails = CardDetails()
        val colorBinding = ColorBinding()

        val sessionManager = SessionManager(this@EditCard)
        val cardDetail: HashMap<String, String> = sessionManager.getEditCardDetails()

        cardDetails.website = cardDetail[sessionManager.WEBSITE]
        cardDetails.name = cardDetail[sessionManager.NAME]
        cardDetails.position = cardDetail[sessionManager.POSITION]
        cardDetails.phone1 = cardDetail[sessionManager.PHONE_1]
        cardDetails.phone2 = cardDetail[sessionManager.PHONE_2]
        cardDetails.email1 = cardDetail[sessionManager.EMAIL_1]
        cardDetails.email2 = cardDetail[sessionManager.EMAIL_2]
        cardDetails.streetAddress = cardDetail[sessionManager.STREET_ADDRESS]
        cardDetails.cityTown = cardDetail[sessionManager.CITY_TOWN]

        colorBinding.primaryColor = cardDetail[sessionManager.PRIMARY_COLOR]
        colorBinding.secondaryColor = cardDetail[sessionManager.SECONDARY_COLOR]
        colorBinding.tertiaryColor = cardDetail[sessionManager.TERTIARY_COLOR]
        colorBinding.textPrimaryColor = cardDetail[sessionManager.TEXT_PRIMARY_COLOR]
        colorBinding.textSecondaryColor = cardDetail[sessionManager.TEXT_SECONDARY_COLOR]

        activityEditCardBinding = DataBindingUtil.setContentView(this@EditCard, R.layout.activity_edit_card)
        activityEditCardBinding.cardDetails = cardDetails
        activityEditCardBinding.colorBinding = colorBinding

        currentCardEditingSection = findViewById(R.id.edit_card_editing_section)
        currentCardFrontSection = findViewById(R.id.edit_card_front_section)
        currentCardBackSection = findViewById(R.id.edit_card_back_section)
        currentCardBack = findViewById(R.id.edit_card_back)
        currentCardFront = findViewById(R.id.edit_card_front)
        backShow = findViewById(R.id.edit_back_visible)
        frontShow = findViewById(R.id.edit_front_visible)
        primaryColor = findViewById(R.id.edit_primary_color)
        secondaryColor = findViewById(R.id.edit_secondary_color)
        tertiaryColor = findViewById(R.id.edit_tertiary_color)
        textPrimaryColor = findViewById(R.id.edit_text_primary_color)
        textSecondaryColor = findViewById(R.id.edit_text_secondary_color)
        primaryColorCode = findViewById(R.id.primary_color_code_edit)
        secondaryColorCode = findViewById(R.id.secondary_color_code_edit)
        tertiaryColorCode = findViewById(R.id.tertiary_color_code_edit)
        textPrimaryColorCode = findViewById(R.id.text_primary_color_code_edit)
        textSecondaryColorCode = findViewById(R.id.text_secondary_color_code_edit)
        currentCardName = findViewById(R.id.current_card_name_edit)
        saveButton = findViewById(R.id.save_edited_card_button)
        cardWebsite = findViewById(R.id.card_website_edit)
        cardName = findViewById(R.id.card_name_edit)
        cardPosition = findViewById(R.id.card_position_edit)
        cardPhone1 = findViewById(R.id.card_phone_no_1_edit)
        cardPhone2 = findViewById(R.id.card_phone_no_2_edit)
        cardEmail1 = findViewById(R.id.card_email1_edit)
        cardEmail2 = findViewById(R.id.card_email2_edit)
        cardStreetAddress = findViewById(R.id.card_street_address_edit)
        cardCityTown = findViewById(R.id.card_city_town_edit)
        progressBarAddNew = findViewById(R.id.progress_bar_edit)

        currentCardName.text = clickedCard

        inflateCorrectCardBG()

        backShow.setOnClickListener {
            currentCardFront.visibility = View.INVISIBLE
            currentCardBack.visibility = View.VISIBLE
        }

        frontShow.setOnClickListener {
            currentCardBack.visibility = View.INVISIBLE
            currentCardFront.visibility = View.VISIBLE
        }

        primaryColor.setOnClickListener {
            setPrimaryColor()
        }

        secondaryColor.setOnClickListener {
            setSecondaryColor()
        }

        tertiaryColor.setOnClickListener {
            setTertiaryColor()
        }

        textPrimaryColor.setOnClickListener {
            setTextPrimaryColor()
        }

        textSecondaryColor.setOnClickListener {
            setTextSecondaryColor()
        }

        saveButton.setOnClickListener {
            convertToImage()
        }
    }

    private fun convertToImage() {
        val dialog = Dialog(this@EditCard)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.save_card_dialog)

        val cardFrontPreview: ImageView = dialog.findViewById(R.id.card_front_preview)
        val cardBackPreview: ImageView = dialog.findViewById(R.id.card_back_preview)
        val cardName: EditText = dialog.findViewById(R.id.card_name)
        val closeDialog: ImageView = dialog.findViewById(R.id.close_dialog)
        val downloadCardButton: ImageView = dialog.findViewById(R.id.download_card_button)
        val saveToDatabase: ImageView = dialog.findViewById(R.id.save_to_database)

        var cardNameText = cardName.text.toString().trim()

        currentCardBack.visibility = View.VISIBLE
        currentCardFront.visibility = View.VISIBLE

        var bitmap: Bitmap = Bitmap.createBitmap(currentCardFrontSection.width, currentCardFrontSection.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        currentCardFrontSection.draw(canvas)
        cardFrontPreview.setImageBitmap(bitmap)

        var bitmap1: Bitmap = Bitmap.createBitmap(currentCardBackSection.width, currentCardBackSection.height, Bitmap.Config.ARGB_8888)
        var canvas1 = Canvas(bitmap1)
        currentCardBackSection.draw(canvas1)
        cardBackPreview.setImageBitmap(bitmap1)

        closeDialog.setOnClickListener {
            dialog.dismiss()
        }

        downloadCardButton.setOnClickListener {
            if (cardName.text.isNotEmpty()) {
                ActivityCompat.requestPermissions(this@EditCard, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                ActivityCompat.requestPermissions(this@EditCard, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)

                try {
                    val draw: BitmapDrawable = cardFrontPreview.drawable as BitmapDrawable
                    val bitmap: Bitmap = draw.bitmap

                    val outStream: FileOutputStream?
                    val sdCard: File = Environment.getExternalStorageDirectory()
                    val dir = File(sdCard.absolutePath + "/inTouch")
                    dir.mkdirs()
                    val fileName = String.format("$cardNameText Front.png", System.currentTimeMillis())
                    val outFile = File(dir, fileName)
                    outStream = FileOutputStream(outFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                    outStream.flush()
                    outStream.close()

                    val draw1: BitmapDrawable = cardBackPreview.drawable as BitmapDrawable
                    val bitmap1: Bitmap = draw1.bitmap

                    val outStream1: FileOutputStream?
                    val sdCard1: File = Environment.getExternalStorageDirectory()
                    val dir1 = File(sdCard1.absolutePath + "/inTouch")
                    dir1.mkdirs()
                    val fileName1 = String.format("$cardNameText Back.png", System.currentTimeMillis())
                    val outFile1 = File(dir1, fileName1)
                    outStream1 = FileOutputStream(outFile1)
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, outStream1)
                    outStream1.flush()
                    outStream1.close()
                    Toast.makeText(baseContext, "Saved To Gallery", Toast.LENGTH_LONG).show()
                } catch (e: Exception)  {
                    Toast.makeText(baseContext, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show()
                }
            } else  {
                Toast.makeText(baseContext, "Please enter a card name.", Toast.LENGTH_LONG).show()
            }
        }

        saveToDatabase.setOnClickListener {
            var primaryCC = primaryColorCode.text.toString().trim()
            var secondaryCC = secondaryColorCode.text.toString().trim()
            var tertiaryCC = tertiaryColorCode.text.toString().trim()
            var primaryTextCC = textPrimaryColorCode.text.toString().trim()
            var secondaryTextCC = textSecondaryColorCode.text.toString().trim()
            var website = cardWebsite.text.toString().trim()
            var name = cardName.text.toString().trim()
            var position = cardPosition.text.toString().trim()
            var phone1 = cardPhone1.text.toString().trim()
            var phone2 = cardPhone2.text.toString().trim()
            var email1 = cardEmail1.text.toString().trim()
            var email2 = cardEmail2.text.toString().trim()
            var streetAddress = cardStreetAddress.text.toString().trim()
            var cityTown = cardCityTown.text.toString().trim()
            var cardStyle = currentCardName.text.toString().trim()

            var card = Card(
                primaryCC,
                secondaryCC,
                tertiaryCC,
                primaryTextCC,
                secondaryTextCC,
                website,
                name,
                position,
                phone1,
                phone2,
                email1,
                email2,
                streetAddress,
                cityTown,
                cardStyle
            )

            if (cardName.text.isNotEmpty())  {
                progressBarAddNew.visibility = View.VISIBLE

                val draw: BitmapDrawable = cardBackPreview.drawable as BitmapDrawable
                val bitmap: Bitmap = draw.bitmap

                val fileRef: StorageReference = storage.reference.child(userID).child("Saved Cards").child(cardName.text.toString().trim())

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data: ByteArray = baos.toByteArray()
                fileRef.putBytes(data)

                FirebaseDatabase.getInstance("https://intouch-6eeb7-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("Cards")
                    .child(userID)
                    .child(cardName.text.toString().trim())
                    .setValue(card)
                    .addOnCompleteListener {
                        if (it.isComplete) {
                            Toast.makeText(baseContext, "Saved to Database", Toast.LENGTH_SHORT).show()
                            progressBarAddNew.visibility = View.GONE
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Check connection and try again",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressBarAddNew.visibility = View.GONE
                        }
                    }
            } else {
                Toast.makeText(baseContext, "Enter a card name", Toast.LENGTH_SHORT).show()
            }

        }

        dialog.show()
    }

    private fun setPrimaryColor() {
        ColorPickerDialog.Builder(this@EditCard)
            .setTitle("Select Primary Color")
            .setPreferenceName("Color Picker")
            .setPositiveButton("Confirm", ColorEnvelopeListener { envelope, fromUser ->
                primaryColor.setCardBackgroundColor(Color.parseColor("#" + envelope.hexCode))
                primaryColorCode.text = "#" + envelope.hexCode
            })
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }

    private fun setSecondaryColor() {
        ColorPickerDialog.Builder(this@EditCard)
            .setTitle("Select Primary Color")
            .setPreferenceName("Color Picker")
            .setPositiveButton("Confirm", ColorEnvelopeListener { envelope, fromUser ->
                secondaryColor.setCardBackgroundColor(Color.parseColor("#" + envelope.hexCode))
                secondaryColorCode.text = "#" + envelope.hexCode
            })
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }

    private fun setTertiaryColor() {
        ColorPickerDialog.Builder(this@EditCard)
            .setTitle("Select Primary Color")
            .setPreferenceName("Color Picker")
            .setPositiveButton("Confirm", ColorEnvelopeListener { envelope, fromUser ->
                tertiaryColor.setCardBackgroundColor(Color.parseColor("#" + envelope.hexCode))
                tertiaryColorCode.text = "#" + envelope.hexCode
            })
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }

    private fun setTextPrimaryColor() {
        ColorPickerDialog.Builder(this@EditCard)
            .setTitle("Select Primary Color")
            .setPreferenceName("Color Picker")
            .setPositiveButton("Confirm", ColorEnvelopeListener { envelope, fromUser ->
                textPrimaryColor.setCardBackgroundColor(Color.parseColor("#" + envelope.hexCode))
                textPrimaryColorCode.text = "#" + envelope.hexCode
            })
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }

    private fun setTextSecondaryColor() {
        ColorPickerDialog.Builder(this@EditCard)
            .setTitle("Select Primary Color")
            .setPreferenceName("Color Picker")
            .setPositiveButton("Confirm", ColorEnvelopeListener { envelope, fromUser ->
                textSecondaryColor.setCardBackgroundColor(Color.parseColor("#" + envelope.hexCode))
                textSecondaryColorCode.text = "#" + envelope.hexCode
            })
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }

    private fun inflateCorrectCardBG() {
        when (intent.getStringExtra("Card Style"))    {
            "Elegant" -> {
                currentCardBack.layoutResource = R.layout.card_design_elegant_back
                currentCardFront.layoutResource = R.layout.card_design_elegant_front
                currentCardFront.inflate()
                currentCardBack.inflate()
            }

            "Regal" -> {
                currentCardBack.layoutResource = R.layout.card_design_regal_back
                currentCardFront.layoutResource = R.layout.card_design_regal_front
                currentCardFront.inflate()
                currentCardBack.inflate()
            }

            "Clean" -> {
                currentCardBack.layoutResource = R.layout.card_design_clean_back
                currentCardFront.layoutResource = R.layout.card_design_clean_front
                currentCardFront.inflate()
                currentCardBack.inflate()
            }

            "Sharp" -> {
                currentCardBack.layoutResource = R.layout.card_design_sharp_back
                currentCardFront.layoutResource = R.layout.card_design_sharp_front
                currentCardFront.inflate()
                currentCardBack.inflate()
            }

            "3D" -> {
                currentCardBack.layoutResource = R.layout.card_design_3d_back
                currentCardFront.layoutResource = R.layout.card_design_3d_front
                currentCardFront.inflate()
                currentCardBack.inflate()
            }
        }
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    fun purgatory() {
        val clickedCard = intent.getStringExtra("Clicked Card")

        val cardDetails = CardDetails()
        val colorBinding = ColorBinding()

        database = FirebaseDatabase.getInstance("https://intouch-6eeb7-default-rtdb.europe-west1.firebasedatabase.app").reference
        val query: Query = database.child("Cards").child(userID).child(clickedCard!!)
        query.addValueEventListener(object: ValueEventListener  {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cardDetails.website = dataSnapshot.child("website").value as String?
                cardDetails.name = dataSnapshot.child("name").value as String?
                cardDetails.position = dataSnapshot.child("position").value as String?
                cardDetails.phone1 = dataSnapshot.child("phoneNo1").value as String?
                cardDetails.phone2 = dataSnapshot.child("phoneNo2").value as String?
                cardDetails.email1 = dataSnapshot.child("email1").value as String?
                cardDetails.email2 = dataSnapshot.child("email2").value as String?
                cardDetails.streetAddress = dataSnapshot.child("streetAddress").value as String?
                cardDetails.cityTown = dataSnapshot.child("cityTown").value as String?

                colorBinding.primaryColor = dataSnapshot.child("primaryColor").value as String?
                colorBinding.secondaryColor = dataSnapshot.child("secondaryColor").value as String?
                colorBinding.tertiaryColor = dataSnapshot.child("tertiaryColor").value as String?
                colorBinding.textPrimaryColor = dataSnapshot.child("primaryTextColor").value as String?
                colorBinding.textSecondaryColor = dataSnapshot.child("secondaryTextColor").value as String?
            }

            override fun onCancelled(error: DatabaseError) {
                Log.v("Error", "Failed to query database")
            }
        })

        inflateCorrectCardBG()

        val sessionManager = SessionManager(this@EditCard)
        val cardDetail: HashMap<String, String> = sessionManager.getEditCardDetails()

        cardDetails.website = cardDetail[sessionManager.WEBSITE]
        cardDetails.name = cardDetail[sessionManager.NAME]
        cardDetails.position = cardDetail[sessionManager.POSITION]
        cardDetails.phone1 = cardDetail[sessionManager.PHONE_1]
        cardDetails.phone2 = cardDetail[sessionManager.PHONE_2]
        cardDetails.email1 = cardDetail[sessionManager.EMAIL_1]
        cardDetails.email2 = cardDetail[sessionManager.EMAIL_2]
        cardDetails.streetAddress = cardDetail[sessionManager.STREET_ADDRESS]
        cardDetails.cityTown = cardDetail[sessionManager.CITY_TOWN]

        colorBinding.primaryColor = cardDetail[sessionManager.PRIMARY_COLOR]
        colorBinding.secondaryColor = cardDetail[sessionManager.SECONDARY_COLOR]
        colorBinding.tertiaryColor = cardDetail[sessionManager.TERTIARY_COLOR]
        colorBinding.textPrimaryColor = cardDetail[sessionManager.TEXT_PRIMARY_COLOR]
        colorBinding.textSecondaryColor = cardDetail[sessionManager.TEXT_SECONDARY_COLOR]

        val cardDetailsPH = CardDetails()
        cardDetailsPH.website = resources.getString(R.string.alium_website)
        cardDetailsPH.name = resources.getString(R.string.my_name)
        cardDetailsPH.position = resources.getString(R.string.chief_executive_officer)
        cardDetailsPH.phone1 = resources.getString(R.string.phone_no_1)
        cardDetailsPH.phone2 = resources.getString(R.string.phone_no_2)
        cardDetailsPH.email1 = resources.getString(R.string.mishaelopokuboamah_gmail_com)
        cardDetailsPH.email2 = resources.getString(R.string.email_user_email_com)
        cardDetailsPH.streetAddress = resources.getString(R.string.house_no_15_gamba_street)
        cardDetailsPH.cityTown = resources.getString(R.string.accra_central)

        val colorBindingPH = ColorBinding()
        colorBindingPH.primaryColor = "#FB8B23"
        colorBindingPH.secondaryColor = "#FFFFFF"
        colorBindingPH.tertiaryColor = "#000000"
        colorBindingPH.textPrimaryColor = "#FFFFFF"
        colorBindingPH.textSecondaryColor = "#000000"

        activityEditCardBinding.cardDetails = cardDetails
        activityEditCardBinding.colorBinding = colorBinding
    }
}