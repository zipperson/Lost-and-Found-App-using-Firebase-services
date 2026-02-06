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
import com.bumptech.glide.Glide

class LostItemAdapter(
    private var items: List<LostItem>,
    private val actionLabel: String? = null,
    private val onAction: ((LostItem) -> Unit)? = null
) : RecyclerView.Adapter<LostItemAdapter.VH>() {

    private var fullList: List<LostItem> = ArrayList(items)

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgItem)
        val name: TextView = v.findViewById(R.id.txtItemName)
        val location: TextView = v.findViewById(R.id.txtLocation)
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

        holder.name.text = item.itemName
        holder.location.text = item.locationLost
        holder.date.text = item.dateLost

        val iconRes = when (item.imageKey.lowercase()) {
            "wallet" -> R.drawable.ic_wallet
            "bottle" -> R.drawable.ic_bottle
            "phone" -> R.drawable.ic_phone
            "bag" -> R.drawable.ic_bag
            "notebook" -> R.drawable.ic_notebook
            "shoes" -> R.drawable.ic_shoes
            "book" -> R.drawable.ic_book
            "uniform" -> R.drawable.ic_uniform
            "pouch" -> R.drawable.ic_pouch
            "id" -> R.drawable.ic_id
            "fan" -> R.drawable.ic_fan
            "glasses" -> R.drawable.ic_glasses
            else -> R.drawable.ic_lost
        }

        holder.img.setImageResource(iconRes)

        if (actionLabel != null) {
            holder.actionBtn?.visibility = View.VISIBLE
            holder.actionBtn?.text = actionLabel
            holder.actionBtn?.setOnClickListener { onAction?.invoke(item) }
        } else {
            holder.actionBtn?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<LostItem>) {
        fullList = ArrayList(newItems)
        items = ArrayList(newItems)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val lower = query.lowercase()

        items = fullList.filter {
            it.itemName.lowercase().contains(lower) ||
                    it.locationLost.lowercase().contains(lower) ||
                    it.dateLost.lowercase().contains(lower)
        }

        notifyDataSetChanged()
    }
}
