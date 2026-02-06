package com.example.actuallostofund.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.FirebaseUtils
import com.example.actuallostofund.utils.models.LostItem
import java.util.*
import com.example.actuallostofund.ui.MainActivity

class SubmitLostItemActivity : AppCompatActivity() {

    private var selectedImageKey: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_lost)

        val imgPreview = findViewById<ImageView>(R.id.imgPreview)
        val btnPick = findViewById<Button>(R.id.btnPickImage)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtLocation = findViewById<EditText>(R.id.edtLocation)
        val edtDate = findViewById<EditText>(R.id.edtDate)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupType)
        val radioLost = findViewById<RadioButton>(R.id.radioLost)
        val radioFound = findViewById<RadioButton>(R.id.radioFound)
        val radioDonated = findViewById<RadioButton>(R.id.radioDonated)

        val userEmail = FirebaseUtils.auth.currentUser?.email

        val isAdmin = userEmail in MainActivity.adminEmails

        radioDonated.visibility = if (isAdmin) View.VISIBLE else View.GONE

// Default state (Lost)
        updateFormForType("lost")

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioLost -> updateFormForType("lost")
                R.id.radioFound -> updateFormForType("found")
                R.id.radioDonated -> updateFormForType("donated")
            }
        }


        // -------------------- DATE PICKER --------------------
        edtDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d -> edtDate.setText("$y-${m + 1}-$d") },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // -------------------- ICON PICKER --------------------
        btnPick.setOnClickListener {
            showIconPicker(imgPreview)
        }

        // -------------------- SUBMIT LOST ITEM --------------------
        btnSubmit.setOnClickListener {

            val name = edtName.text.toString().trim()
            val location = edtLocation.text.toString().trim()
            val date = edtDate.text.toString().trim()

            if (name.isEmpty() || location.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveLostItemToFirestore(name, location, date)
        }
    }

    // ========================================================================
    //   ICON PICKER DIALOG → updates preview instantly
    // ========================================================================
    private fun showIconPicker(imgPreview: ImageView) {

        val labels = arrayOf(
            "Wallet", "Bottle", "Phone", "Bag", "Notebook", "Shoes",
            "Book", "Uniform", "Glasses", "Fan", "ID", "Pouch", "Other"
        )

        val keys = arrayOf(
            "wallet", "bottle", "phone", "bag", "notebook", "shoes",
            "book", "uniform", "glasses", "fan", "id", "pouch", "other"
        )

        AlertDialog.Builder(this)
            .setTitle("Choose an item icon")
            .setItems(labels) { _, which ->
                selectedImageKey = keys[which]         // now ALWAYS lowercase
                updatePreview(imgPreview)
            }
            .show()
    }

    // ========================================================================
    //   PREVIEW ICON BASED ON SELECTED KEY
    // ========================================================================
    private fun updatePreview(imgPreview: ImageView) {

        val iconRes = when (selectedImageKey) {
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

        imgPreview.setImageResource(iconRes)
    }
    // changes fields depending on radiobuttoin
    private fun updateFormForType(type: String) {

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtLocation = findViewById<EditText>(R.id.edtLocation)
        val edtDate = findViewById<EditText>(R.id.edtDate)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        when (type) {

            "lost" -> {
                edtName.hint = "Item name (lost)"
                edtLocation.hint = "Location lost"
                edtDate.hint = "Date lost (e.g. 2025-12-01)"
                btnSubmit.text = "SUBMIT LOST ITEM"
            }

            "found" -> {
                edtName.hint = "Item name (found)"
                edtLocation.hint = "Location found"
                edtDate.hint = "Date found"
                btnSubmit.text = "SUBMIT FOUND ITEM"
            }

            "donated" -> {
                edtName.hint = "Item name (donated)"
                edtLocation.hint = "Donation location"
                edtDate.hint = "Date donated"
                btnSubmit.text = "DONATE ITEM"
            }
        }
    }

    // ========================================================================
    //   SAVE LOST ITEM TO FIRESTORE
    // ========================================================================
    private fun saveLostItemToFirestore(name: String, location: String, date: String) {

        val radioLost = findViewById<RadioButton>(R.id.radioLost)
        val radioFound = findViewById<RadioButton>(R.id.radioFound)
        val radioDonated = findViewById<RadioButton>(R.id.radioDonated)

        val status = when {
            radioLost.isChecked -> "pending_lost"
            radioFound.isChecked -> "pending_found"
            radioDonated.isChecked -> "pending_donated"
            else -> "pending_lost"
        }


        val user = FirebaseUtils.auth.currentUser ?: return

        val docId = FirebaseUtils.db.collection("lost_items").document().id

        val item = LostItem(
            id = docId,
            userId = user.uid,
            itemName = name,
            locationLost = location,
            dateLost = date,
            imageKey = selectedImageKey,
            status = status
        )

        FirebaseUtils.db.collection("lost_items")
            .document(docId)
            .set(item)
            .addOnSuccessListener {
                Toast.makeText(this, "Item submitted!", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

}
