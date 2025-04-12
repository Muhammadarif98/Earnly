package com.example.earnly.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.earnly.data.preferences.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.example.earnly.R
import com.example.earnly.domain.analytics.AnalyticsManager
import com.example.earnly.presentation.main.MainActivity
import com.example.earnly.presentation.onboarding.OnboardingAdapter
import com.example.earnly.presentation.onboarding.OnboardingItem
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var skipButton: MaterialButton
    private lateinit var nextButton: MaterialButton
    private lateinit var getStartedButton: MaterialButton
    private lateinit var indicatorsContainer: ViewGroup
    private lateinit var preferenceManager: PreferenceManager
    
    private val onboardingItems = listOf(
        OnboardingItem(
            imageResId = R.drawable.onboarding_1,
            title = getString(R.string.onboarding_title_1),
            description = getString(R.string.onboarding_desc_1)
        ),
        OnboardingItem(
            imageResId = R.drawable.onboarding_2,
            title = getString(R.string.onboarding_title_2),
            description = getString(R.string.onboarding_desc_2)
        ),
        OnboardingItem(
            imageResId = R.drawable.onboarding_3,
            title = getString(R.string.onboarding_title_3),
            description = getString(R.string.onboarding_desc_3)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        
        // Инициализация PreferenceManager
        preferenceManager = PreferenceManager(this)
        
        // Логирование просмотра экрана онбординга
        AnalyticsManager.logScreenView(getString(R.string.onboarding_screen_name))
        
        viewPager = findViewById(R.id.viewPagerOnboarding)
        skipButton = findViewById(R.id.btnSkip)
        nextButton = findViewById(R.id.btnNext)
        
        // Создадим кнопку для последней страницы
        getStartedButton = MaterialButton(this).apply {
            text = getString(R.string.onboarding_start)
            visibility = View.GONE
            id = View.generateViewId()
        }
        
        indicatorsContainer = findViewById(R.id.layoutIndicators)
        
        // Настройка ViewPager с адаптером
        viewPager.adapter = OnboardingAdapter(onboardingItems)
        
        skipButton.setOnClickListener {
            AnalyticsManager.logEvent(
                getString(R.string.event_onboarding_skip), 
                mapOf(getString(R.string.param_position) to viewPager.currentItem.toString())
            )
            finishOnboarding()
        }
        
        nextButton.setOnClickListener {
            val currentPosition = viewPager.currentItem
            AnalyticsManager.logEvent(
                getString(R.string.event_onboarding_next), 
                mapOf(getString(R.string.param_position) to currentPosition.toString())
            )
            
            if (currentPosition < onboardingItems.size - 1) {
                viewPager.currentItem = currentPosition + 1
            }
        }
        
        getStartedButton.setOnClickListener {
            AnalyticsManager.logEvent(getString(R.string.event_onboarding_complete), emptyMap())
            finishOnboarding()
        }
        
        // Слушатель для отслеживания смены страницы
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                
                // Логирование перехода между страницами онбординга
                AnalyticsManager.logEvent(
                    getString(R.string.event_onboarding_page_viewed),
                    mapOf(getString(R.string.param_position) to position.toString())
                )
                
                // Обновление видимости кнопок в зависимости от позиции
                updateButtonsVisibility(position)
            }
        })
    }
    
    private fun updateButtonsVisibility(position: Int) {
        val isLastPage = position == onboardingItems.size - 1
        
        skipButton.visibility = if (isLastPage) View.GONE else View.VISIBLE
        nextButton.visibility = if (isLastPage) View.GONE else View.VISIBLE
        getStartedButton.visibility = if (isLastPage) View.VISIBLE else View.GONE
    }
    
    private fun finishOnboarding() {
        // Сохраняем информацию, что онбординг пройден
        preferenceManager.setFirstLaunchCompleted()
        
        // Переходим в главный экран
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
} 