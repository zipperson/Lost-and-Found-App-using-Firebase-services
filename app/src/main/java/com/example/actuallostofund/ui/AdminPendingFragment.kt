package com.example.actuallostofund.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.FirebaseUtils
import com.example.actuallostofund.utils.adapters.AdminPendingAdapter
import com.example.actuallostofund.utils.models.LostItem

/**
 * AdminPendingFragment
 * - shows items with status == "pending"
 * - lets admin approve or reject (update Firestore)
 *
 * NOTE: This expects there to be an adapter that accepts:
 *    (items: MutableList<LostItem>, onApprove: (LostItem)->Unit, onReject: (LostItem)->Unit)
 *
 * If you already have AdminPendingAdapter with a different signature,
 * adjust the adapter construction below accordingly.
 */
class AdminPendingFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private val pending = ArrayList<LostItem>()
    private lateinit var adapter: AdminPendingAdapter // see note below

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_admin_pending, container, false)

        recycler = v.findViewById(R.id.pendingRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        // Adapter: pass callbacks for approve/reject
        adapter = AdminPendingAdapter(
            pending,
            onApprove = { item -> approveCorrectType(item) },
            onReject = { item -> setStatus(item, "rejected") }
        )

        recycler.adapter = adapter

        // Firestore realtime listener: only pending
        FirebaseUtils.db.collection("lost_items")
            .whereIn("status", listOf("pending_lost", "pending_found", "pending_donated"))
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e("ADMIN_PENDING", "snapshot error", err)
                    Toast.makeText(requireContext(), "Failed to load pending items: ${err.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snap == null) {
                    Log.w("ADMIN_PENDING", "snapshot null")
                    return@addSnapshotListener
                }

                pending.clear()
                for (doc in snap.documents) {
                    val item = LostItem(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        itemName = doc.getString("itemName") ?: "",
                        locationLost = doc.getString("locationLost") ?: "",
                        dateLost = doc.getString("dateLost") ?: "",
                        imageKey = doc.getString("imageKey") ?:doc.getString("imageKey") ?: "default",
                        status = doc.getString("status") ?: "pending"
                    )
                    pending.add(item)
                }
                adapter.notifyDataSetChanged()
                Log.d("ADMIN_PENDING", "loaded pending=${pending.size}")
            }

        return v
    }

    // update Firestore status
    private fun setStatus(item: LostItem, newStatus: String) {
        val docRef = FirebaseUtils.db.collection("lost_items").document(item.id)
        docRef.update("status", newStatus)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "${item.itemName} set to $newStatus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("ADMIN_PENDING", "failed to set status", e)
                Toast.makeText(requireContext(), "Failed to update status: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun approveCorrectType(item: LostItem) {

        val newStatus = when (item.status) {
            "pending_lost" -> "approved_lost"
            "pending_found" -> "approved_found"
            "pending_donated" -> "approved_donated"
            else -> "approved"  // fallback
        }

        FirebaseUtils.db.collection("lost_items")
            .document(item.id)
            .update("status", newStatus)
            .addOnSuccessListener {
                Toast.makeText(requireContext(),
                    "${item.itemName} approved as $newStatus",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}
