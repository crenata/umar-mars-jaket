package com.scranaver.marsjaket.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.scranaver.marsjaket.data.Constants
import com.scranaver.marsjaket.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var binding: ActivityProfileBinding

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var backButton: Button

    private lateinit var nameTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var ttlTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var usernameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Profile"
        }

        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            getData()
        }

        backButton = binding.back
        backButton.setOnClickListener {
            onBackPressed()
        }

        nameTextView = binding.name
        phoneTextView = binding.phone
        addressTextView = binding.address
        genderTextView = binding.gender
        ttlTextView = binding.ttl
        emailTextView = binding.email
        usernameTextView = binding.username

        getData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData() {
        swipeRefreshLayout.isRefreshing = true
        db.document("${Constants.userCollectionKey()}/${auth.currentUser?.uid}").get().addOnSuccessListener { snapshot ->
            val userData = snapshot.data
            nameTextView.text = userData?.get("name").toString()
            phoneTextView.text = userData?.get("phone").toString()
            addressTextView.text = userData?.get("address").toString()
            genderTextView.text = userData?.get("gender").toString()
            ttlTextView.text = userData?.get("ttl").toString()
            emailTextView.text = userData?.get("email").toString()
            usernameTextView.text = userData?.get("username").toString()
        }.addOnFailureListener { error ->
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }
        swipeRefreshLayout.isRefreshing = false
    }
}