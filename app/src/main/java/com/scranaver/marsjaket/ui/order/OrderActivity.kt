package com.scranaver.marsjaket.ui.order

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.scranaver.marsjaket.data.Constants
import com.scranaver.marsjaket.databinding.ActivityOrderBinding

@SuppressLint("NotifyDataSetChanged")
class OrderActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var binding: ActivityOrderBinding

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var backButton: Button

    private var orderList = ArrayList<Order>()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Pesanan"
        }

        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            getAllData()
        }

        backButton = binding.back
        backButton.setOnClickListener {
            onBackPressed()
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(orderList)
        binding.recyclerview.adapter = orderAdapter

        getAllData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getAllData() {
        orderList.clear()
        swipeRefreshLayout.isRefreshing = true
        db.collection("${Constants.userCollectionKey()}/${auth.currentUser?.uid}/${Constants.orderCollectionKey()}").get().addOnSuccessListener { collectionOrders ->
            for (order in collectionOrders.documents) {
                val data = order.data
                orderList.add(Order(data?.get("model").toString(), data?.get("payment_method").toString(), data?.get("shipping_method").toString(), data?.get("qty").toString()))
            }
            orderAdapter.notifyDataSetChanged()
        }.addOnFailureListener { error ->
            Log.e(Constants.logKey(), "Error: ", error)
        }
        swipeRefreshLayout.isRefreshing = false
    }
}