package com.nishant.oralvisapp.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.nishant.oralvisapp.databinding.ActivityImageViewerBinding

class ImageViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.imageViewerRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back button
        binding.btnBack.setOnClickListener { onBackPressed() }

        // Get image path from intent
        val imagePath = intent.getStringExtra("imagePath")
        if (imagePath != null) {
            Glide.with(this)
                .load(imagePath)
                .fitCenter()
                .into(binding.imageViewFull)
        }
    }
}