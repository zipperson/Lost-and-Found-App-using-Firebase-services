package com.example.actuallostofund.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.models.LostItem

class AdminPendingAdapter(
    private val items: List<LostItem>,
    private val onApprove: (LostItem) -> Unit,
    private val onReject: (LostItem) -> Unit
) : RecyclerView.Adapter<AdminPendingAdapter.VH>() {

    inner class VH(v: android.view.View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgItem)
        val name: TextView = v.findViewById(R.id.txtItemName)
        val details: TextView = v.findViewById(R.id.txtLocation)
        val approve: Button = v.findViewById(R.id.btnApprove)
        val reject: Button = v.findViewById(R.id.btnReject)
        val type: TextView = v.findViewById(R.id.txtItemType)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_pending, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        val typeLabel = when {
            item.status.contains("lost") -> "LOST ITEM"
            item.status.contains("found") -> "FOUND ITEM"
            item.status.contains("donated") -> "DONATED ITEM"
            else -> "ITEM"
        }

        holder.type.text = typeLabel

        holder.name.text = item.itemName
        holder.details.text = "${item.locationLost} • ${item.dateLost}"

        val iconRes = when (item.imageKey.lowercase()) {
            "wallet" -> R.drawable.ic_wallet
            "bottle" -> R.drawable.ic_bottle
            "phone" -> R.drawable.ic_phone
            "bag" -> R.drawable.ic_bag
            "notebook" -> R.drawable.ic_notebook
            "shoes" -> R.drawable.ic_shoes
            "book" -> R.drawable.ic_book
            "uniform" -> R.drawable.ic_uniform
            "glasses" -> R.drawable.ic_glasses
            "fan" -> R.drawable.ic_fan
            "id" -> R.drawable.ic_id
            "pouch" -> R.drawable.ic_pouch
            "other" -> R.drawable.ic_other
            else -> R.drawable.ic_lost
        }

        holder.img.setImageResource(iconRes)

        holder.approve.setOnClickListener { onApprove(item) }
        holder.reject.setOnClickListener { onReject(item) }
    }

    override fun getItemCount(): Int = items.size
}
