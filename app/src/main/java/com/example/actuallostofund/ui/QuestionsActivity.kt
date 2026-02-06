package com.example.actuallostofund.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.actuallostofund.R

class QuestionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        val btnShowAnswers = findViewById<ImageView>(R.id.btnShowAnswers)

        btnShowAnswers.setOnClickListener {
            startActivity(Intent(this, AnswersActivity::class.java))
        }
    }
}
