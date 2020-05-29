package com.joweltisso.android.instascrape

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatDelegate

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.LightTheme)
        }
        setContentView(R.layout.activity_menu)

        val aboutLayout: LinearLayout = findViewById(R.id.aboutLayout)
        val instructionsLayout: ScrollView = findViewById(R.id.instructionsLayout)
        val btnAbout: ToggleButton = findViewById(R.id.btnAbout)
        val btnPolicy: Button = findViewById(R.id.btnPolicy)
        val btnInstructions: ToggleButton = findViewById(R.id.btnInstructions)

        btnInstructions.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                instructionsLayout.visibility = View.VISIBLE
                btnAbout.isChecked = false
            } else {
                instructionsLayout.visibility = View.GONE
            }
        }

        btnAbout.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                aboutLayout.visibility = View.VISIBLE
                btnInstructions.isChecked = false
            } else {
                aboutLayout.visibility = View.GONE
            }
        }

        btnPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://privacy-policy-forapp.netlify.app/privacy-policy2/")))
        }
    }
}
