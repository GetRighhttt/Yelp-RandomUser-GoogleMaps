package com.example.yelpclone.presentation.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.yelpclone.databinding.ActivitySplashScreenBinding
import com.example.yelpclone.presentation.view.main.MainActivity

class ScreenActivity : AppCompatActivity() {
    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivYelp.animate().apply {
            duration = 200L
            rotationXBy(3600F)
        }.withEndAction {
            binding.ivYelp.animate().apply {
                rotationXBy(-360F)
            }
        }

        // delays before moving to other activity
    Handler(Looper.getMainLooper()).postDelayed(
    {
        startActivity(
            Intent(this@ScreenActivity, MainActivity::class.java)
        )
        finish()
    }, 1300)
}

override fun onDestroy() {
    super.onDestroy()
    _binding = null
}
}