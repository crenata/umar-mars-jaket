package com.scranaver.marsjaket.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.scranaver.marsjaket.R
import com.scranaver.marsjaket.data.Constants
import com.scranaver.marsjaket.databinding.ActivityRegisterBinding
import com.scranaver.marsjaket.ui.login.LoginActivity

class RegisterActivity: AppCompatActivity() {
    private var userDocRef: CollectionReference = FirebaseFirestore.getInstance().collection(Constants.userCollectionKey())
    private var auth: FirebaseAuth = Firebase.auth

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var loading: ProgressBar
    private lateinit var registerButton: Button

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var ttlEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var genderMale: RadioButton
    private lateinit var genderFemale: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = ""
        }

        loading = binding.loading
        registerButton = binding.register
        nameEditText = binding.name
        phoneEditText = binding.phone
        addressEditText = binding.address
        ttlEditText = binding.ttl
        emailEditText = binding.email
        usernameEditText = binding.username
        passwordEditText = binding.password
        genderRadioGroup = binding.gender
        genderMale = binding.male
        genderFemale = binding.female

        registerButton.setOnClickListener {
            register()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToLogin()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun register() {
        val name: String = nameEditText.text.toString()
        val phone: String = phoneEditText.text.toString()
        val address: String = addressEditText.text.toString()
        val gender: Int = genderRadioGroup.checkedRadioButtonId
        var genderValue: String = ""
        val ttl: String = ttlEditText.text.toString()
        val email: String = emailEditText.text.toString()
        val username: String = usernameEditText.text.toString()
        val password: String = passwordEditText.text.toString()

        if (name.isBlank()) {
            nameEditText.error = "Name is required!"
            nameEditText.requestFocus()
            return
        }
        if (phone.isBlank()) {
            phoneEditText.error = "Phone is required!"
            phoneEditText.requestFocus()
            return
        }
        if (address.isBlank()) {
            addressEditText.error = "Address is required!"
            addressEditText.requestFocus()
            return
        }
        when (gender) {
            genderMale.id -> {
                genderValue = "Male"
            }
            genderFemale.id -> {
                genderValue = "Female"
            }
        }
        if (genderValue.isBlank()) {
            genderMale.error = "Gender is required!"
            genderMale.requestFocus()
            return
        }
        if (ttl.isBlank()) {
            ttlEditText.error = "TTL is required!"
            ttlEditText.requestFocus()
            return
        }
        if (email.isBlank()) {
            emailEditText.error = "Email is required!"
            emailEditText.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Email is invalid!"
            emailEditText.requestFocus()
            return
        }
        if (username.isBlank()) {
            usernameEditText.error = "Username is required!"
            usernameEditText.requestFocus()
            return
        }
        if (password.isBlank()) {
            passwordEditText.error = "Password is required!"
            passwordEditText.requestFocus()
            return
        }
        if (password.length < 6) {
            passwordEditText.error = "Min 6 characters!"
            passwordEditText.requestFocus()
            return
        }

        loading.visibility = View.VISIBLE
        registerButton.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val newUser = hashMapOf(
                "name" to name,
                "phone" to phone,
                "address" to address,
                "gender" to genderValue,
                "ttl" to ttl,
                "email" to email,
                "username" to username
            )
            val user = auth.currentUser
            if (user != null) {
                userDocRef.document(user.uid).set(newUser).addOnSuccessListener {
                    loading.visibility = View.GONE
                    registerButton.isEnabled = true
                    Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_LONG).show()
                    goToLogin()
                }.addOnFailureListener { error ->
                    loading.visibility = View.GONE
                    registerButton.isEnabled = true
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                loading.visibility = View.GONE
                registerButton.isEnabled = true
                Toast.makeText(applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { error ->
            loading.visibility = View.GONE
            registerButton.isEnabled = true
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }
    }
}