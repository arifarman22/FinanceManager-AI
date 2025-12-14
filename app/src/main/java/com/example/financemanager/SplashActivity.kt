package com.financeai

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.financemanager.MainActivity
import com.financeai.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    // Splash screen duration in milliseconds
    private val SPLASH_DURATION: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make splash screen fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Optional: Animate elements
        animateSplashScreen()

        // Navigate to MainActivity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMainActivity()
        }, SPLASH_DURATION)
    }

    private fun animateSplashScreen() {
        // Fade in animation for logo
        binding.ivLogo.alpha = 0f
        binding.ivLogo.animate()
            .alpha(1f)
            .setDuration(1000)
            .start()

        // Scale animation
        binding.ivLogo.scaleX = 0.5f
        binding.ivLogo.scaleY = 0.5f
        binding.ivLogo.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1000)
            .start()

        // Animate app name with delay
        binding.tvAppName.alpha = 0f
        binding.tvAppName.translationY = 50f
        binding.tvAppName.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(300)
            .setDuration(800)
            .start()

        // Animate tagline with delay
        binding.tvTagline.alpha = 0f
        binding.tvTagline.animate()
            .alpha(0.8f)
            .setStartDelay(600)
            .setDuration(800)
            .start()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)

        // Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        startActivity(intent)
        finish() // Close splash activity so user can't go back to it
    }

    override fun onBackPressed() {
        super.onBackPressed()
        super.onBackPressed()
        // Disable back button during splash screen
        // super.onBackPressed()
    }
}