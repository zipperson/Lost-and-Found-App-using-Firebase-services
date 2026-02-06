package com.example.actuallostofund.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.AvatarUtils

class AvatarPickerAdapter(
    private val onSelect: (String) -> Unit
) : RecyclerView.Adapter<AvatarPickerAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avatar, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val key = AvatarUtils.avatarKeys[position]
        holder.img.setImageResource(AvatarUtils.getAvatarRes(key))

        holder.itemView.setOnClickListener {
            onSelect(key)
        }
    }

    override fun getItemCount() = AvatarUtils.avatarKeys.size
}
