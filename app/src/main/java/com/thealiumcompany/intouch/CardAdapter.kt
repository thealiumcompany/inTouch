package com.thealiumcompany.intouch

import android.content.Intent
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
                var clickedCard = fileNames[position]

                var intent = Intent(binding.root.context, EditCard::class.java)
                intent.putExtra("Clicked Card", clickedCard)
                binding.root.context.startActivity(intent)
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