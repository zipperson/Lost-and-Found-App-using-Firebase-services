package com.example.actuallostofund.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.FirebaseUtils

class ProfileActivity : AppCompatActivity() {

    private var selectedAvatar = "avatar_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚠️ MAKE SURE THIS MATCHES YOUR XML FILE NAME
        setContentView(R.layout.activity_profile_setup)

        val imgAvatar = findViewById<ImageView>(R.id.imgAvatar)
        val btnPick = findViewById<Button>(R.id.btnPickAvatar)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtName = findViewById<EditText>(R.id.edtFullName)
        val edtGrade = findViewById<EditText>(R.id.edtGradeSection)

        // Pick avatar
        btnPick.setOnClickListener {
            showAvatarPicker { avatarKey ->
                selectedAvatar = avatarKey
                val resId = resources.getIdentifier(avatarKey, "drawable", packageName)
                imgAvatar.setImageResource(resId)
            }
        }

        // Save profile
        btnSave.setOnClickListener {
            val user = FirebaseUtils.auth.currentUser
            if (user == null) {
                Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseUtils.db.collection("users")
                .document(user.uid)
                .set(
                    mapOf(
                        "username" to edtUsername.text.toString().trim(),
                        "fullName" to edtName.text.toString().trim(),
                        "gradeSection" to edtGrade.text.toString().trim(),
                        "avatarKey" to selectedAvatar
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // =====================================================
    // AVATAR PICKER DIALOG
    // =====================================================
    private fun showAvatarPicker(onSelected: (String) -> Unit) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_avatar_picker, null)
        val grid = dialogView.findViewById<GridView>(R.id.avatarGrid)

        val avatarKeys = listOf(
            "avatar_1",
            "avatar_2",
            "avatar_3",
            "avatar_4",
            "avatar_5"
        )

        val adapter = object : BaseAdapter() {
            override fun getCount() = avatarKeys.size
            override fun getItem(position: Int) = avatarKeys[position]
            override fun getItemId(position: Int) = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val img = ImageView(this@ProfileActivity)
                val resId = resources.getIdentifier(
                    avatarKeys[position],
                    "drawable",
                    packageName
                )
                img.setImageResource(resId)
                img.layoutParams = AbsListView.LayoutParams(180, 180)
                img.scaleType = ImageView.ScaleType.CENTER_CROP
                img.setPadding(12, 12, 12, 12)
                return img
            }
        }

        grid.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        grid.setOnItemClickListener { _, _, position, _ ->
            onSelected(avatarKeys[position])
            dialog.dismiss()
        }

        dialog.show()
    }
}
