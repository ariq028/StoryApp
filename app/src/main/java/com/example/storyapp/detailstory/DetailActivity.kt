package com.example.storyapp.detailstory

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        val url = intent.getStringExtra(EXTRA_URL)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)

        // Use the retrieved data as needed
        // For example, you can set the retrieved data to views in your layout
        val tvUsername: TextView = findViewById(R.id.tvDetailUsername)
        val tvDescription: TextView = findViewById(R.id.tvDetailDescription)
        val imageView: ImageView = findViewById(R.id.iv_detail)
        tvUsername.text = username
        tvDescription.text = description
        Glide.with(this).load(url).into(imageView)
    }

    companion object {
        const val EXTRA_USERNAME = "EXTRA_USERNAME"
        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
    }
}
