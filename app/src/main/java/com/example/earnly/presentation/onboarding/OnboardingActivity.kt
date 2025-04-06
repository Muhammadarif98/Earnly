package com.example.earnly.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.earnly.R
import com.example.earnly.data.preferences.PreferenceManager
import com.example.earnly.domain.analytics.AnalyticsManager
import com.example.earnly.presentation.main.MainActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var indicatorsContainer: LinearLayout
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        preferenceManager = PreferenceManager(this)
        
        // Log screen view
        AnalyticsManager.logScreenView("onboarding")

        // Set the onboarding items
        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.ic_launcher_foreground, // Replace with actual images
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_desc_1)
            ),
            OnboardingItem(
                R.drawable.ic_launcher_foreground, // Replace with actual images
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_desc_2)
            ),
            OnboardingItem(
                R.drawable.ic_launcher_foreground, // Replace with actual images
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_desc_3)
            )
        )

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerOnboarding)
        indicatorsContainer = findViewById(R.id.layoutIndicators)
        
        // Set up the adapter
        onboardingAdapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = onboardingAdapter
        
        // Set up indicators
        setupIndicators()
        setCurrentIndicator(0)
        
        // Set up page change callback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                
                // Update Next button text if last page
                val btnNext = findViewById<android.widget.Button>(R.id.btnNext)
                btnNext.text = if (position == onboardingItems.size - 1) {
                    getString(R.string.onboarding_start)
                } else {
                    getString(R.string.onboarding_next)
                }
            }
        })
        
        // Set up click listeners
        findViewById<android.widget.Button>(R.id.btnNext).setOnClickListener {
            if (viewPager.currentItem == onboardingItems.size - 1) {
                // If on the last page, proceed to the main app
                finishOnboarding()
            } else {
                // Move to the next page
                viewPager.currentItem = viewPager.currentItem + 1
            }
        }
        
        findViewById<android.widget.Button>(R.id.btnSkip).setOnClickListener {
            finishOnboarding()
        }
    }
    
    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onboardingAdapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.indicator_inactive
                )
            )
            indicators[i]?.layoutParams = layoutParams
            indicatorsContainer.addView(indicators[i])
        }
    }
    
    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    if (i == position) {
                        R.drawable.indicator_active
                    } else {
                        R.drawable.indicator_inactive
                    }
                )
            )
        }
    }
    
    private fun finishOnboarding() {
        preferenceManager.setFirstLaunchCompleted()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
} 