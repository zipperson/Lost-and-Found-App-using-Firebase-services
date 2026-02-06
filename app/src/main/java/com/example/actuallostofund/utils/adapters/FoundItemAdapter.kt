package com.example.actuallostofund.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.models.LostItem

class FoundItemAdapter(
    private val items: List<LostItem>,
    private val actionLabel: String? = "Claim",
    private val onAction: ((LostItem) -> Unit)? = null
) : RecyclerView.Adapter<FoundItemAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgItem)
        val name: TextView = v.findViewById(R.id.txtItemName)
        val date: TextView = v.findViewById(R.id.txtDate)
        val actionBtn: Button? = v.findViewById(R.id.btnAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lost_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.name.text = item.itemName.ifBlank { "No name" }
        holder.date.text = item.dateLost.ifBlank { "No date" }

        // Convert imageKey → icon resource
        val iconRes = when (item.imageKey?.lowercase()) {
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
            else -> R.drawable.ic_found
        }
        holder.img.setImageResource(iconRes)

        // Show CLAIM button if enabled
        if (actionLabel != null && onAction != null && holder.actionBtn != null) {
            holder.actionBtn.visibility = View.VISIBLE
            holder.actionBtn.text = actionLabel

            holder.actionBtn.setOnClickListener {
                onAction.invoke(item)
            }
        } else {
            holder.actionBtn?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size
}
