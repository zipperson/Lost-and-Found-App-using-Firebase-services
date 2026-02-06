package com.example.actuallostofund.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.FirebaseUtils
import com.example.actuallostofund.utils.adapters.LostItemAdapter
import com.example.actuallostofund.utils.models.LostItem

class DonatedItemsFragment : Fragment() {

    private val donatedItems = ArrayList<LostItem>()
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: LostItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_donated, container, false)

        recycler = view.findViewById(R.id.donatedRecycler)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = LostItemAdapter(donatedItems)
        recycler.adapter = adapter

        loadDonatedItems()

        return view
    }

    private fun loadDonatedItems() {
        FirebaseUtils.db.collection("lost_items")
            .whereEqualTo("status", "approved_donated")
            .addSnapshotListener { snap, _ ->
                donatedItems.clear()
                snap?.documents?.forEach { doc ->
                    donatedItems.add(
                        LostItem(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            itemName = doc.getString("itemName") ?: "",
                            locationLost = doc.getString("locationLost") ?: "",
                            dateLost = doc.getString("dateLost") ?: "",
                            imageKey = doc.getString("imageKey") ?: "avatar_1",
                            status = "donated"
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
    }
}
