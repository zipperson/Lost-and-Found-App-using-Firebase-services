package com.example.actuallostofund.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.FirebaseUtils
import com.example.actuallostofund.utils.adapters.LostItemAdapter
import com.example.actuallostofund.utils.models.LostItem

class FoundItemsFragment : Fragment(), Searchable {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: LostItemAdapter

    private val allItems = ArrayList<LostItem>()
    private val visibleItems = ArrayList<LostItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_found, container, false)

        recycler = v.findViewById(R.id.foundRecycler)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = LostItemAdapter(
            visibleItems,
            actionLabel = "CLAIM"
        ) { item -> showClaimDialog(item) }

        recycler.adapter = adapter
        loadFoundItems()

        return v
    }

    private fun loadFoundItems() {
        FirebaseUtils.db.collection("lost_items")
            .whereEqualTo("status", "approved_found")
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
                            it.locationLost.contains(query, true)
                }
            )
        }
        adapter.notifyDataSetChanged()
    }

    private fun showClaimDialog(item: LostItem) {
        val view = layoutInflater.inflate(R.layout.dialog_claim_item, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.MyDialog_NoTitle)
            .setView(view)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        view.findViewById<View>(R.id.btnConfirmClaim).setOnClickListener {
            FirebaseUtils.db.collection("lost_items")
                .document(item.id)
                .update("status", "claimed")
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnCancelClaim).setOnClickListener {
            dialog.dismiss()
        }
    }
}
