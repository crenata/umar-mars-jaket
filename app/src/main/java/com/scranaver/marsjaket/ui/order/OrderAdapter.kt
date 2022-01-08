package com.scranaver.marsjaket.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scranaver.marsjaket.databinding.ItemsBinding

class OrderAdapter(private var orderList: List<Order>): RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    inner class ViewHolder(val orderBinding: ItemsBinding): RecyclerView.ViewHolder(orderBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val orderBinding = ItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(orderBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val orderItem: Order = orderList[position]
            with(orderItem) {
                orderBinding.model.text = this.model
                orderBinding.paymentMethod.text = this.paymentMethod
                orderBinding.shippingMethod.text = this.shippingMethod
                orderBinding.qty.text = this.qty
            }
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}