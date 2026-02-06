package com.example.actuallostofund.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.FirebaseUtils

class ProfileSetupActivity : AppCompatActivity() {

    private var selectedAvatar = "avatar_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        val imgAvatar = findViewById<ImageView>(R.id.imgAvatar)
        val btnPick = findViewById<Button>(R.id.btnPickAvatar)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtFullName = findViewById<EditText>(R.id.edtFullName)
        val edtGrade = findViewById<EditText>(R.id.edtGradeSection)

        // 🔹 Load existing profile
        val user = FirebaseUtils.auth.currentUser
        if (user != null) {
            FirebaseUtils.db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        edtUsername.setText(doc.getString("username"))
                        edtFullName.setText(doc.getString("fullName"))
                        edtGrade.setText(doc.getString("gradeSection"))

                        val avatarKey = doc.getString("avatarKey")
                        if (!avatarKey.isNullOrEmpty()) {
                            selectedAvatar = avatarKey
                            val resId = resources.getIdentifier(
                                avatarKey, "drawable", packageName
                            )
                            imgAvatar.setImageResource(resId)
                        }
                    }
                }
        }

        // 🔹 Open avatar picker dialog
        btnPick.setOnClickListener {
            showAvatarPicker { avatarKey ->
                selectedAvatar = avatarKey
                val resId = resources.getIdentifier(
                    avatarKey, "drawable", packageName
                )
                imgAvatar.setImageResource(resId)
            }
        }

        // 🔹 Save profile
        btnSave.setOnClickListener {
            val uid = FirebaseUtils.auth.currentUser?.uid ?: return@setOnClickListener

            FirebaseUtils.db.collection("users")
                .document(uid)
                .set(
                    mapOf(
                        "username" to edtUsername.text.toString().trim(),
                        "fullName" to edtFullName.text.toString().trim(),
                        "gradeSection" to edtGrade.text.toString().trim(),
                        "avatarKey" to selectedAvatar
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    // ======================================================
    // AVATAR PICKER DIALOG
    // ======================================================
    private fun showAvatarPicker(onSelected: (String) -> Unit) {

        val avatarKeys = listOf(
            "avatar_1",
            "avatar_2",
            "avatar_3",
            "avatar_4",
            "avatar_5",
            "avatar_6",
            "avatar_7",
            "avatar_8",
            "avatar_9",
            "avatar_10"

        )

        val dialogView = layoutInflater.inflate(R.layout.dialog_avatar_picker, null)
        val grid = dialogView.findViewById<GridView>(R.id.avatarGrid)

        grid.adapter = object : BaseAdapter() {
            override fun getCount() = avatarKeys.size
            override fun getItem(position: Int) = avatarKeys[position]
            override fun getItemId(position: Int) = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val img = ImageView(this@ProfileSetupActivity)
                val resId = resources.getIdentifier(
                    avatarKeys[position],
                    "drawable",
                    packageName
                )
                img.setImageResource(resId)
                img.layoutParams = AbsListView.LayoutParams(180, 180)
                img.scaleType = ImageView.ScaleType.CENTER_CROP
                img.setPadding(8, 8, 8, 8)
                return img
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Choose an avatar")
            .setView(dialogView)
            .create()

        grid.setOnItemClickListener { _, _, position, _ ->
            onSelected(avatarKeys[position])
            dialog.dismiss()
        }

        dialog.show()
    }
}
