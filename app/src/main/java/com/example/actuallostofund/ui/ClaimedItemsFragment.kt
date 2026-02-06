package com.example.actuallostofund.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.FirebaseUtils
import com.example.actuallostofund.utils.adapters.LostItemAdapter
import com.example.actuallostofund.utils.models.LostItem

class ClaimedItemsFragment : Fragment(), Searchable {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: LostItemAdapter

    private val allItems = ArrayList<LostItem>()
    private val visibleItems = ArrayList<LostItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_claimed, container, false)

        recycler = v.findViewById(R.id.claimedRecycler)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = LostItemAdapter(visibleItems)
        recycler.adapter = adapter

        loadClaimedItems()
        return v
    }

    private fun loadClaimedItems() {
        FirebaseUtils.db.collection("lost_items")
            .whereEqualTo("status", "claimed")
            .addSnapshotListener { snap, _ ->
                allItems.clear()
                visibleItems.clear()

                snap?.documents?.forEach {
                    allItems.add(it.toObject(LostItem::class.java)!!.copy(id = it.id))
                }

                visibleItems.addAll(allItems)
                adapter.notifyDataSetChanged()
            }
    }

    override fun onSearch(query: String) {
        visibleItems.clear()

        if (query.isBlank()) {
            visibleItems.addAll(allItems)
        } else {
            visibleItems.addAll(
                allItems.filter {
                    it.itemName.contains(query, true) ||
                            it.locationLost.contains(query, true) ||
                            it.dateLost.contains(query, true)
                }
            )
        }
        adapter.notifyDataSetChanged()
    }
}
