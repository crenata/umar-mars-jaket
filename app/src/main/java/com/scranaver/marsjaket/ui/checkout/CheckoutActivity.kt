package com.scranaver.marsjaket.ui.checkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.scranaver.marsjaket.R
import com.scranaver.marsjaket.data.Constants
import com.scranaver.marsjaket.databinding.ActivityCheckoutBinding
import java.util.*

class CheckoutActivity : AppCompatActivity() {
    private var userDocRef: CollectionReference = FirebaseFirestore.getInstance().collection(Constants.userCollectionKey())
    private var auth: FirebaseAuth = Firebase.auth

    private lateinit var binding: ActivityCheckoutBinding

    private lateinit var loading: ProgressBar
    private lateinit var orderButton: Button
    private lateinit var backButton: Button

    private lateinit var modelRadioGroup: RadioGroup
    private lateinit var modelAlmamater: RadioButton
    private lateinit var modelBoomber: RadioButton
    private lateinit var modelParka: RadioButton
    private lateinit var modelJeans: RadioButton

    private lateinit var paymentMethodRadioGroup: RadioGroup
    private lateinit var paymentMethodCash: RadioButton
    private lateinit var paymentMethodDebit: RadioButton

    private lateinit var shippingMethodRadioGroup: RadioGroup
    private lateinit var shippingMethodGosend: RadioButton
    private lateinit var shippingMethodCod: RadioButton
    private lateinit var shippingMethodJnt: RadioButton

    private lateinit var qtyEditText: EditText
    private lateinit var tosCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = ""
        }

        loading = binding.loading
        orderButton = binding.order
        backButton = binding.back
        qtyEditText = binding.qty
        tosCheckBox = binding.tos

        modelRadioGroup = binding.model
        modelAlmamater = binding.almamater
        modelBoomber = binding.boomber
        modelParka = binding.parka
        modelJeans = binding.jeans

        paymentMethodRadioGroup = binding.paymentMethod
        paymentMethodCash = binding.cash
        paymentMethodDebit = binding.debit

        shippingMethodRadioGroup = binding.shippingMethod
        shippingMethodGosend = binding.gosend
        shippingMethodCod = binding.cod
        shippingMethodJnt = binding.jnt

        orderButton.setOnClickListener {
            order()
        }
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun order() {
        val qty: String = qtyEditText.text.toString()
        val tos: Boolean = tosCheckBox.isChecked

        val model: Int = modelRadioGroup.checkedRadioButtonId
        var modelValue: String = ""
        val paymentMethod: Int = paymentMethodRadioGroup.checkedRadioButtonId
        var paymentMethodValue: String = ""
        val shippingMethod: Int = shippingMethodRadioGroup.checkedRadioButtonId
        var shippingMethodValue: String = ""

        when (model) {
            modelAlmamater.id -> {
                modelValue = "Almamater"
            }
            modelBoomber.id -> {
                modelValue = "Boomber"
            }
            modelParka.id -> {
                modelValue = "Parka"
            }
            modelJeans.id -> {
                modelValue = "Jeans"
            }
        }
        if (modelValue.isBlank()) {
            modelAlmamater.error = "Model is required!"
            modelAlmamater.requestFocus()
            return
        }
        when (paymentMethod) {
            paymentMethodCash.id -> {
                paymentMethodValue = "Cash"
            }
            paymentMethodDebit.id -> {
                paymentMethodValue = "Debit"
            }
        }
        if (paymentMethodValue.isBlank()) {
            paymentMethodCash.error = "Payment Method is required!"
            paymentMethodCash.requestFocus()
            return
        }
        when (shippingMethod) {
            shippingMethodGosend.id -> {
                shippingMethodValue = "Gosend"
            }
            shippingMethodCod.id -> {
                shippingMethodValue = "COD"
            }
            shippingMethodJnt.id -> {
                shippingMethodValue = "Jnt"
            }
        }
        if (shippingMethodValue.isBlank()) {
            shippingMethodGosend.error = "Shipping Method is required!"
            shippingMethodGosend.requestFocus()
            return
        }
        if (qty.isBlank()) {
            qtyEditText.error = "Quantity is required!"
            qtyEditText.requestFocus()
            return
        }
        if (!tos) {
            tosCheckBox.error = "You must agree with this!"
            tosCheckBox.requestFocus()
            return
        }

        loading.visibility = View.VISIBLE
        orderButton.isEnabled = false

        val newOrder = hashMapOf(
            "model" to modelValue,
            "payment_method" to paymentMethodValue,
            "shipping_method" to shippingMethodValue,
            "qty" to qty,
            "created_at" to Timestamp(Date())
        )
        val user = auth.currentUser
        if (user != null) {
            userDocRef.document(user.uid).collection(Constants.orderCollectionKey()).add(newOrder).addOnSuccessListener {
                loading.visibility = View.GONE
                orderButton.isEnabled = true
                Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener { error ->
                loading.visibility = View.GONE
                orderButton.isEnabled = true
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            loading.visibility = View.GONE
            orderButton.isEnabled = true
            Toast.makeText(applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }
}