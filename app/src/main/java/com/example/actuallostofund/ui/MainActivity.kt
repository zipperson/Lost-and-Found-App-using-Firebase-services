package com.example.actuallostofund.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.actuallostofund.R
import com.example.actuallostofund.utils.FirebaseUtils
import androidx.appcompat.app.AppCompatDelegate
import android.widget.EditText
import com.google.firebase.messaging.FirebaseMessaging
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout

    private lateinit var btnUser: ImageButton
    private lateinit var btnLost: ImageButton
    private lateinit var btnFound: ImageButton
    private lateinit var btnClaimed: ImageButton
    private lateinit var btnSubmit: ImageButton
    private lateinit var btnPending: ImageButton

    private lateinit var btnQuestion: ImageButton

    private lateinit var btnDonated: ImageButton
    private lateinit var btnToggleVisible: ImageButton // small toggle inside toolbar
    private lateinit var btnNavToggle: ImageButton // small toggle at bottom of drawer
    companion object {
        val adminEmails = setOf(
            "aenmarco@gmail.com",
            "qatagbanlog@gmail.com",
            "blancheocyaranon@gmail.com",
            "julianegayle0713@gmail.com",
            "kianjosephyes@gmail.com",
            "nicoleccnshs@gmail.com",
            "resemillavecienej@gmail.com",
            "jvanndp@gmail.com"
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    FirebaseUtils.db.collection("users")
                        .document(FirebaseUtils.auth.currentUser!!.uid)
                        .update("fcmToken", token)
                }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar = findViewById<EditText>(R.id.searchBar)

        searchBar.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (fragment is Searchable) {
                    fragment.onSearch(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val drawerAvatar = findViewById<ImageView>(R.id.btn_nav_user)

        FirebaseUtils.db.collection("users")
            .document(FirebaseUtils.auth.currentUser!!.uid)
            .addSnapshotListener { snap, _ ->
                val key = snap?.getString("avatarKey") ?: return@addSnapshotListener
                val res = resources.getIdentifier(key, "drawable", packageName)
                drawerAvatar.setImageResource(res)
            }


        // Drawer + toolbar
        drawer = findViewById(R.id.drawerLayout)

        // NOTE: in your XML the toolbar id is "topToolbar"
        val toolbar = findViewById<Toolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayOptions(0)  // remove auto buttons


        //* ActionBarDrawerToggle shows the hamburger icon in the toolbar and syncs drawer state
        /*val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState() */

        // find nav buttons (IDs taken from your activity_main.xml)
        btnUser = findViewById(R.id.btn_nav_user)
        btnLost = findViewById(R.id.btn_nav_lost)
        btnFound = findViewById(R.id.btn_nav_found)
        btnClaimed = findViewById(R.id.btn_nav_claimed)
        btnDonated = findViewById(R.id.btn_nav_donated)
        btnSubmit = findViewById(R.id.btn_nav_submit)
        btnPending = findViewById(R.id.btn_nav_pending)
        btnQuestion = findViewById(R.id.btn_questions)
        btnToggleVisible = findViewById(R.id.btn_toggle_visible) // toolbar toggle
        btnNavToggle = findViewById(R.id.btn_nav_toggle) // drawer bottom toggle

        // toolbar toggle button (the small ImageButton inside the toolbar) - open/close drawer
        btnToggleVisible.setOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
            else drawer.openDrawer(GravityCompat.START)
        }
        // logout button
        btnNavToggle.setOnClickListener {
            FirebaseUtils.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // show pending button only to admin (change email to your admin account)

        val isAdmin = FirebaseUtils.auth.currentUser?.email in adminEmails

        btnPending.visibility = if (isAdmin) View.VISIBLE else View.GONE


        // navigation actions

        btnUser.setOnClickListener {
            startActivity(Intent(this, ProfileSetupActivity::class.java))
            drawer.closeDrawer(GravityCompat.START)
        }

        btnDonated.setOnClickListener {
            loadFragment(DonatedItemsFragment())
            drawer.closeDrawer(GravityCompat.START)
        }

        btnLost.setOnClickListener {
            loadFragment(LostItemsFragment())
            drawer.closeDrawer(GravityCompat.START)
        }

        btnFound.setOnClickListener {
            loadFragment(FoundItemsFragment())
            drawer.closeDrawer(GravityCompat.START)
        }

        btnClaimed.setOnClickListener {
            // if you have ClaimedItemsFragment, load it. Otherwise fallback to FoundItemsFragment
            try {
                loadFragment(ClaimedItemsFragment())
            } catch (e: Exception) {
                loadFragment(FoundItemsFragment())
            }
            drawer.closeDrawer(GravityCompat.START)
        }

        btnPending.setOnClickListener {
            // admin only
            loadFragment(AdminPendingFragment())
            drawer.closeDrawer(GravityCompat.START)
        }

        btnSubmit.setOnClickListener {
            startActivity(Intent(this, SubmitLostItemActivity::class.java))
            drawer.closeDrawer(GravityCompat.START)
        }

        btnQuestion.setOnClickListener {
            val intent = Intent(this, QuestionsActivity::class.java)
            startActivity(intent)
        }


        // default fragment on app start
        loadFragment(LostItemsFragment())
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

}
