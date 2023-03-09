package com.thealiumcompany.intouch

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

@BindingAdapter("setImageViewTint")
fun setImageViewTint(view: ImageView, color: String) {
    view.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
}

@BindingAdapter("setTextColor")
fun setTextColor(textView: TextView, color: String) {
    textView.setTextColor(Color.parseColor(color))
}

@BindingAdapter("setCardViewColor")
fun setCardViewColor(cardView: CardView, color: String) {
    cardView.setCardBackgroundColor(Color.parseColor(color))
}

@BindingAdapter("setImageViewURI")
fun setImageViewURI(view: ImageView, imageURL: String?) {
    var storage: FirebaseStorage = Firebase.storage
    val userID = FirebaseAuth.getInstance().currentUser!!.uid

    val remoteProfilePic: StorageReference = storage.reference.child(userID).child("Uploaded Logo")
    remoteProfilePic.downloadUrl
        .addOnSuccessListener {
            Picasso.get().load(it).into(view)
        }
        .addOnFailureListener {
            view.setImageResource(R.drawable.img_alium_logo)
        }
}

@BindingAdapter("setUserPhotoURI")
fun setUserPhotoURI(view: ImageView, imageURL: String?) {
    var storage: FirebaseStorage = Firebase.storage
    val userID = FirebaseAuth.getInstance().currentUser!!.uid

    val remoteProfilePic: StorageReference = storage.reference.child(userID).child("Uploaded Photo")
    remoteProfilePic.downloadUrl
        .addOnSuccessListener {
            Picasso.get().load(it).into(view)
        }
        .addOnFailureListener {
            view.setImageResource(R.drawable.img_sample_pp)
        }
}
