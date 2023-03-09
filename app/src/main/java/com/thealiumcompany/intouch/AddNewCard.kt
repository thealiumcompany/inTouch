package com.thealiumcompany.intouch

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewStub
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.thealiumcompany.intouch.databinding.ActivityAddNewCardBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class AddNewCard : AppCompatActivity() {

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
    private lateinit var activityAddNewCardBinding: ActivityAddNewCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
        auth = Firebase.auth
        database = Firebase.database.reference

        showDialog()

        val cardDetails = CardDetails()
        cardDetails.website = resources.getString(R.string.alium_website)
        cardDetails.name = resources.getString(R.string.my_name)
        cardDetails.position = resources.getString(R.string.chief_executive_officer)
        cardDetails.phone1 = resources.getString(R.string.phone_no_1)
        cardDetails.phone2 = resources.getString(R.string.phone_no_2)
        cardDetails.email1 = resources.getString(R.string.mishaelopokuboamah_gmail_com)
        cardDetails.email2 = resources.getString(R.string.email_user_email_com)
        cardDetails.streetAddress = resources.getString(R.string.house_no_15_gamba_street)
        cardDetails.cityTown = resources.getString(R.string.accra_central)

        val colorBinding = ColorBinding()
        colorBinding.primaryColor = "#FB8B23"
        colorBinding.secondaryColor = "#FFFFFF"
        colorBinding.tertiaryColor = "#000000"
        colorBinding.textPrimaryColor = "#FFFFFF"
        colorBinding.textSecondaryColor = "#000000"

        activityAddNewCardBinding = DataBindingUtil.setContentView(this@AddNewCard, R.layout.activity_add_new_card)
        activityAddNewCardBinding.cardDetails = cardDetails
        activityAddNewCardBinding.colorBinding = colorBinding

        currentCardEditingSection = findViewById(R.id.current_card_editing_section)
        currentCardFrontSection = findViewById(R.id.current_card_front_section)
        currentCardBackSection = findViewById(R.id.current_card_back_section)
        currentCardBack = findViewById(R.id.current_card_mback)
        currentCardFront = findViewById(R.id.current_card_mfront)
        backShow = findViewById(R.id.back_visible)
        frontShow = findViewById(R.id.front_visible)
        primaryColor = findViewById(R.id.primary_color)
        secondaryColor = findViewById(R.id.secondary_color)
        tertiaryColor = findViewById(R.id.tertiary_color)
        textPrimaryColor = findViewById(R.id.text_primary_color)
        textSecondaryColor = findViewById(R.id.text_secondary_color)
        primaryColorCode = findViewById(R.id.primary_color_code)
        secondaryColorCode = findViewById(R.id.secondary_color_code)
        tertiaryColorCode = findViewById(R.id.tertiary_color_code)
        textPrimaryColorCode = findViewById(R.id.text_primary_color_code)
        textSecondaryColorCode = findViewById(R.id.text_secondary_color_code)
        currentCardName = findViewById(R.id.current_card_name)
        saveButton = findViewById(R.id.save_new_card_button)
        cardWebsite = findViewById(R.id.card_website)
        cardName = findViewById(R.id.card_name)
        cardPosition = findViewById(R.id.card_position)
        cardPhone1 = findViewById(R.id.card_phone_no_1)
        cardPhone2 = findViewById(R.id.card_phone_no_2)
        cardEmail1 = findViewById(R.id.card_email1)
        cardEmail2 = findViewById(R.id.card_email2)
        cardStreetAddress = findViewById(R.id.card_street_address)
        cardCityTown = findViewById(R.id.card_city_town)
        progressBarAddNew = findViewById(R.id.progress_bar_add_new)

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
        val dialog = Dialog(this@AddNewCard)
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
                ActivityCompat.requestPermissions(this@AddNewCard, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                ActivityCompat.requestPermissions(this@AddNewCard, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)

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
        ColorPickerDialog.Builder(this@AddNewCard)
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
        ColorPickerDialog.Builder(this@AddNewCard)
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
        ColorPickerDialog.Builder(this@AddNewCard)
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
        ColorPickerDialog.Builder(this@AddNewCard)
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
        ColorPickerDialog.Builder(this@AddNewCard)
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

    private fun showDialog() {
        val dialog = Dialog(this@AddNewCard)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.select_card_style_dialog)

        //Do all coding stuff here
        val cardStyles: RadioGroup = dialog.findViewById(R.id.card_styles)
        val cardPreview: ImageView = dialog.findViewById(R.id.card_preview)
        val chooseStyle: ImageView = dialog.findViewById(R.id.choose_style)

        cardStyles.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.card_style_elegant -> {
                    cardPreview.setImageResource(R.drawable.img_card_elegant)
                }
                R.id.card_style_3d -> {
                    cardPreview.setImageResource(R.drawable.img_card_3d)
                }
                R.id.card_style_sharp -> {
                    cardPreview.setImageResource(R.drawable.img_card_sharp)
                }
                R.id.card_style_clean -> {
                    cardPreview.setImageResource(R.drawable.img_card_clean)
                }
                R.id.card_style_regal -> {
                    cardPreview.setImageResource(R.drawable.img_card_regal)
                }
            }
        }

        chooseStyle.setOnClickListener {
            when (cardStyles.checkedRadioButtonId) {
                R.id.card_style_elegant -> {
                    Toast.makeText(
                        this@AddNewCard,
                        "You like the elegant side of life. Much respect.",
                        Toast.LENGTH_LONG
                    ).show()

                    currentCardName.text = "Elegant"

                    currentCardBack.layoutResource = R.layout.card_design_elegant_back
                    currentCardFront.layoutResource = R.layout.card_design_elegant_front
                    currentCardFront.inflate()
                    currentCardBack.inflate()

                    dialog.dismiss()
                }
                R.id.card_style_3d -> {
                    Toast.makeText(
                        this@AddNewCard,
                        "Admit it, you chose this one because it looked really cool.",
                        Toast.LENGTH_LONG
                    ).show()

                    currentCardName.text = "3D"

                    //Set card design and inflate
                    currentCardBack.layoutResource = R.layout.card_design_3d_back
                    currentCardFront.layoutResource = R.layout.card_design_3d_front
                    currentCardFront.inflate()
                    currentCardBack.inflate()

                    dialog.dismiss()
                }
                R.id.card_style_sharp -> {
                    Toast.makeText(
                        this@AddNewCard,
                        "You strike me as the type who loves wearing a suit.",
                        Toast.LENGTH_LONG
                    ).show()

                    currentCardName.text = "Sharp"

                    currentCardBack.layoutResource = R.layout.card_design_sharp_back
                    currentCardFront.layoutResource = R.layout.card_design_sharp_front
                    currentCardFront.inflate()
                    currentCardBack.inflate()

                    dialog.dismiss()
                }
                R.id.card_style_clean -> {
                    Toast.makeText(
                        this@AddNewCard,
                        "Clean and simple. You have good taste.",
                        Toast.LENGTH_LONG
                    ).show()

                    currentCardName.text = "Clean"

                    currentCardBack.layoutResource = R.layout.card_design_clean_back
                    currentCardFront.layoutResource = R.layout.card_design_clean_front
                    currentCardFront.inflate()
                    currentCardBack.inflate()

                    dialog.dismiss()
                }
                R.id.card_style_regal -> {
                    Toast.makeText(
                        this@AddNewCard,
                        "This is a really professional one. I admire your bravery.",
                        Toast.LENGTH_LONG
                    ).show()

                    currentCardName.text = "Regal"

                    currentCardBack.layoutResource = R.layout.card_design_regal_back
                    currentCardFront.layoutResource = R.layout.card_design_regal_front
                    currentCardFront.inflate()
                    currentCardBack.inflate()

                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}