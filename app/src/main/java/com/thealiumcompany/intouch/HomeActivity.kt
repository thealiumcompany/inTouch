package com.thealiumcompany.intouch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //Firebase variables
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var user: FirebaseUser
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid

    //XML variables
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var openNavDrawer: ImageButton
    private lateinit var userAccountOpen: CardView
    private lateinit var navUsername: TextView
    private lateinit var navProfilePicture: ImageView
    private lateinit var homePP: ImageView
    private lateinit var addNewCard: ImageView
    private lateinit var addNewCardActive: FloatingActionButton
    private lateinit var recycleCardsHome: RecyclerView
    private lateinit var swipeRefreshHome: SwipeRefreshLayout

    //Layout variables
    private lateinit var activeSection: CoordinatorLayout
    private lateinit var inactiveSection: RelativeLayout

    //General variables
    private var latestUploadTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = Firebase.auth
        storage = Firebase.storage

        drawerLayout = findViewById(R.id.drawer_layout_home)
        navView = findViewById(R.id.nav_view)
        openNavDrawer = findViewById(R.id.open_nav_drawer)
        homePP = findViewById(R.id.home_pp)
        addNewCard = findViewById(R.id.add_new_card)
        addNewCardActive = findViewById(R.id.add_new_card_active)
        recycleCardsHome = findViewById(R.id.recycle_cards_home)
        activeSection = findViewById(R.id.active_section)
        inactiveSection = findViewById(R.id.inactive_section)
        swipeRefreshHome = findViewById(R.id.swipe_refresh_home)

        openDrawer()
        backPressed()

        navView.setNavigationItemSelectedListener(this@HomeActivity)

        var editAccount = navView.getHeaderView(0)
        userAccountOpen = editAccount.findViewById(R.id.edit_account)
        navUsername = editAccount.findViewById(R.id.nav_username)
        navProfilePicture = editAccount.findViewById(R.id.nav_profile_picture)

        user = Firebase.auth.currentUser!!
        val userEmail = user.email

        database = FirebaseDatabase.getInstance("https://intouch-6eeb7-default-rtdb.europe-west1.firebasedatabase.app").reference
        val query: Query = database.child("Users").orderByChild("email").equalTo(userEmail)

        if (user != null)   {
            query.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot: DataSnapshot in snapshot.children)   {
                        var usernameD = dataSnapshot.child("username").value as String?

                        navUsername.text = usernameD
                    }
                }
            })
        }

        loadProfilePicture()

        userAccountOpen.setOnClickListener {
            startActivity(Intent(this@HomeActivity, UserAccount::class.java))
        }

        addNewCard.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddNewCard::class.java))
        }

        addNewCardActive.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddNewCard::class.java))
        }

        checkRecyclerView()

        listFiles()

        swipeRefreshHome.setOnRefreshListener {
            refreshItems()
        }
    }

    private fun refreshItems() {
        val handler = Handler()
        handler.postDelayed({
            latestUploadTime = 0
            listFiles()
            recycleCardsHome.adapter?.notifyDataSetChanged()
            swipeRefreshHome.isRefreshing = false
        }, 3000)
    }

    private fun checkRecyclerView() {
        val cardsRef = storage.reference.child(userID).child("Saved Cards/")

        cardsRef.listAll()
            .addOnSuccessListener { listResult ->
                if (listResult.items.isNotEmpty()) {
                    inactiveSection.visibility = View.GONE
                    activeSection.visibility = View.VISIBLE
                } else {
                    inactiveSection.visibility = View.VISIBLE
                    activeSection.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                inactiveSection.visibility = View.VISIBLE
                activeSection.visibility = View.GONE
            }
    }

    private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val cards = storage.reference.child(userID).child("Saved Cards/").listAll().await()
            val cardURLs = mutableListOf<String>()
            val cardNames = mutableListOf<String>()
            val timestamps = mutableListOf<Long>()

            for (card in cards.items) {
                val metadata = card.metadata.await()
                val uploadTime = metadata.creationTimeMillis

                timestamps.add(uploadTime)

                if (uploadTime > latestUploadTime)  {
                    val url = card.downloadUrl.await()
                    val fileName = card.name

                    cardURLs.add(url.toString())
                    cardNames.add(fileName)
                }
            }

            if (cards.items.isNotEmpty()) {
                val metadata = cards.items.last().metadata.await()
                latestUploadTime = metadata.creationTimeMillis
            }

            withContext(Dispatchers.Main)   {
                val sortedURLs = cardURLs.sortedByDescending { timestamps[cardURLs.indexOf(it)] }
                val sortedNames = cardNames.sortedByDescending { timestamps[cardNames.indexOf(it)] }
                val cardAdapter = CardAdapter(sortedURLs, sortedNames)
                recycleCardsHome.apply {
                    adapter = cardAdapter
                    layoutManager = LinearLayoutManager(this@HomeActivity)
                }
            }
        } catch (e: Exception)  {
            Toast.makeText(baseContext, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDrawer()    {
        openNavDrawer.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId)  {
            R.id.nav_my_cards -> drawerLayout.closeDrawer(GravityCompat.START)
            R.id.nav_recently_shared -> startActivity(Intent(this@HomeActivity, RecentlyShared::class.java))
            R.id.nav_support -> startActivity(Intent(this@HomeActivity, SupportActivity::class.java))
            R.id.nav_about_us -> startActivity(Intent(this@HomeActivity, AboutUs::class.java))
                else -> {
                    Toast.makeText(this@HomeActivity, "Invalid selection", Toast.LENGTH_LONG).show()
                }
        }

        return true
    }

    private fun loadProfilePicture() {
        val remoteProfilePic: StorageReference = storage.reference.child(userID).child("Profile Picture")
        remoteProfilePic.downloadUrl
            .addOnSuccessListener {
                Picasso.get().load(it).into(navProfilePicture)
                Picasso.get().load(it).into(homePP)
            }
            .addOnFailureListener {
                navProfilePicture.setImageResource(R.drawable.ic_blank_user)
                homePP.setImageResource(R.drawable.ic_blank_user)
                Toast.makeText(this@HomeActivity, "Profile picture not set.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()

        loadProfilePicture()
        checkRecyclerView()
        listFiles()
        recycleCardsHome.adapter?.notifyDataSetChanged()
    }
}