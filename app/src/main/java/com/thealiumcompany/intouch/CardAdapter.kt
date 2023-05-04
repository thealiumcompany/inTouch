package com.thealiumcompany.intouch

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.thealiumcompany.intouch.databinding.CardItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.coroutineContext

class CardAdapter(
    var urls: List<String>,
    var fileNames: List<String>
    ) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    //Firebase variables
    private var auth: FirebaseAuth = Firebase.auth
    private var storage: FirebaseStorage = Firebase.storage
    private lateinit var database: DatabaseReference
    private lateinit var user: FirebaseUser
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid

    inner class CardViewHolder(val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root)  {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                val clickedCard = fileNames[position]

                database = FirebaseDatabase.getInstance("https://intouch-6eeb7-default-rtdb.europe-west1.firebasedatabase.app").reference
                val query: Query = database.child("Cards").child(userID).child(clickedCard)
                query.addValueEventListener(object: ValueEventListener  {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var cardStyle = dataSnapshot.child("cardStyle").value as String?
                        var website = dataSnapshot.child("website").value as String?
                        var name = dataSnapshot.child("name").value as String?
                        var position_ = dataSnapshot.child("position").value as String?
                        var phone1 = dataSnapshot.child("phoneNo1").value as String?
                        var phone2 = dataSnapshot.child("phoneNo2").value as String?
                        var email1 = dataSnapshot.child("email1").value as String?
                        var email2 = dataSnapshot.child("email2").value as String?
                        var streetAddress = dataSnapshot.child("streetAddress").value as String?
                        var cityTown = dataSnapshot.child("cityTown").value as String?

                        var primaryColor = dataSnapshot.child("primaryColor").value as String?
                        var secondaryColor = dataSnapshot.child("secondaryColor").value as String?
                        var tertiaryColor = dataSnapshot.child("tertiaryColor").value as String?
                        var textPrimaryColor = dataSnapshot.child("primaryTextColor").value as String?
                        var textSecondaryColor = dataSnapshot.child("secondaryTextColor").value as String?

                        var sessionManager = SessionManager(binding.root.context)
                        sessionManager.createEditCardSession(website!!, name!!, position_!!, phone1!!, phone2!!, email1!!, email2!!, streetAddress!!, cityTown!!, primaryColor!!, secondaryColor!!, tertiaryColor!!, textPrimaryColor!!, textSecondaryColor!!)

                        var intent = Intent(binding.root.context, EditCard::class.java)
                        intent.putExtra("Clicked Card", clickedCard)
                        intent.putExtra("Card Style", cardStyle)
                        binding.root.context.startActivity(intent)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.v("Error", "Failed to query database")
                    }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            CardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val url = urls[position]
        val fileName = fileNames[position]

        Glide.with(holder.binding.root).load(url).into(holder.binding.loadedCard)
        holder.binding.cardFilename.text = fileName
    }

    override fun getItemCount(): Int {
        return urls.size
    }
}