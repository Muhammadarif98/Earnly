package com.example.earnly.presentation.article

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.earnly.EarnlyApplication
import com.example.earnly.R
import com.example.earnly.data.model.ContentItem
import com.example.earnly.data.repository.ContentRepository
import com.example.earnly.domain.analytics.AnalyticsManager
import com.example.earnly.domain.util.Resource
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var repository: ContentRepository
    private var contentItem: ContentItem? = null
    private var topAdItem: ContentItem? = null
    private var bottomAdItem: ContentItem? = null
    
    // Для отслеживания времени просмотра статьи
    private var articleOpenTime: Long = 0
    
    companion object {
        const val EXTRA_CONTENT_ITEM = "extra_content_item"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)
        
        repository = ContentRepository(this)
        articleOpenTime = System.currentTimeMillis()
        
        // Set up the toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { 
            logArticleCloseEvent()
            finish() 
        }
        
        // Get the content item from the intent
        contentItem = intent.getSerializableExtra(getString(R.string.extra_content_item)) as? ContentItem
        
        if (contentItem == null) {
            finish()
            return
        }
        
        // Log screen view
        AnalyticsManager.logScreenView(getString(R.string.article_detail_screen))
        
        // Log article view
        contentItem?.let { article ->
            AnalyticsManager.logArticleView(
                article.articleId ?: getString(R.string.unknown_title), 
                article.title ?: getString(R.string.unknown_title)
            )
        }
        
        // Load ads
        loadAds()
        
        // Display article content
        displayArticleContent()
    }
    
    override fun onDestroy() {
        logArticleCloseEvent()
        super.onDestroy()
    }
    
    private fun logArticleCloseEvent() {
        contentItem?.let { article ->
            val timeSpentSec = ((System.currentTimeMillis() - articleOpenTime) / 1000).toInt()
            AnalyticsManager.logArticleClose(
                article.articleId ?: getString(R.string.unknown_title), 
                timeSpentSec
            )
            AnalyticsManager.logArticleViewDuration(
                article.articleId ?: getString(R.string.unknown_title), 
                timeSpentSec
            )
        }
    }
    
    private fun loadAds() {
        lifecycleScope.launch {
            val loadStartTime = System.currentTimeMillis()
            
            // Log ad load start
            AnalyticsManager.logAdLoadStart(getString(R.string.article_top_placement))
            AnalyticsManager.logAdLoadStart(getString(R.string.article_bottom_placement))
            
            try {
                val response = repository.getContentForTab(getString(R.string.money_tab)) // Используем текущую вкладку
                when (response) {
                    is Resource.Success -> {
                        val loadTime = System.currentTimeMillis() - loadStartTime
                        
                        response.data?.let { apiResponse ->
                            // Get top ad
                            topAdItem = apiResponse.topAdItems.firstOrNull()
                            topAdItem?.let { ad ->
                                AnalyticsManager.logAdLoadSuccess(
                                    ad.bannerId ?: getString(R.string.unknown_title), 
                                    getString(R.string.article_top_placement), 
                                    loadTime
                                )
                            }
                            
                            // Get bottom ad
                            bottomAdItem = apiResponse.bottomAdItems.firstOrNull()
                            bottomAdItem?.let { ad ->
                                AnalyticsManager.logAdLoadSuccess(
                                    ad.bannerId ?: getString(R.string.unknown_title), 
                                    getString(R.string.article_bottom_placement), 
                                    loadTime
                                )
                            }
                            
                            // Setup ads
                            setupAds()
                        }
                    }
                    is Resource.Error -> {
                        // Log error but continue showing article
                        AnalyticsManager.logError(
                            getString(R.string.article_detail_ads_error), 
                            getString(R.string.error_loading_ads, response.message)
                        )
                    }
                    is Resource.Loading -> {
                        // Do nothing, wait for success or error
                    }
                }
            } catch (e: Exception) {
                // Log error but continue showing article
                AnalyticsManager.logError(
                    getString(R.string.article_detail_ads_error), 
                    getString(R.string.error_loading_ads, e.message ?: getString(R.string.unknown_error))
                )
            }
        }
    }
    
    private fun setupAds() {
        // Setup top ad if available
        topAdItem?.let {
            if (contentItem?.shouldShowTopAd() == true) {
                setupAdContainer(findViewById(R.id.topAdContainer), it, getString(R.string.article_top_placement))
            }
        }
        
        // Setup bottom ad if available
        bottomAdItem?.let {
            if (contentItem?.shouldShowBottomAd() == true) {
                setupAdContainer(findViewById(R.id.bottomAdContainer), it, getString(R.string.article_bottom_placement))
            }
        }
    }
    
    private fun setupAdContainer(container: FrameLayout, adItem: ContentItem, placement: String) {
        // Inflate ad view
        val adView = layoutInflater.inflate(R.layout.item_ad_banner, container, false)
        
        // Add the ad view to the container
        container.addView(adView)
        container.visibility = View.VISIBLE
        
        // Set ad content
        val adImage = adView.findViewById<ImageView>(R.id.ivAdImage)
        val adTitle = adView.findViewById<TextView>(R.id.tvAdTitle)
        val adDescription = adView.findViewById<TextView>(R.id.tvAdDescription)
        val adLabelView = adView.findViewById<TextView>(R.id.tvAdLabel)
        val adButton = adView.findViewById<TextView>(R.id.btnAdAction)
        
        // Set data
        adTitle.text = adItem.title
        adDescription.text = adItem.description
        
        // Show/hide ad label
        adLabelView.visibility = if (adItem.shouldShowAdLabel()) View.VISIBLE else View.GONE
        
        // Set button text
        adButton.text = adItem.buttonCaption ?: getString(R.string.learn_more)
        
        // Load image
        adItem.getFullImageUrl()?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .into(adImage)
        }
        
        // Set click listener for the button
        adButton.setOnClickListener {
            // Log ad click
            AnalyticsManager.logAdClick(adItem.bannerId ?: getString(R.string.unknown_title), placement)
            
            // Open ad link
            val adUrl = "https://dohodinfor.ru/?app_key=${EarnlyApplication.APP_KEY}&ad_id=${adItem.bannerId}&placement=$placement"
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(adUrl))
                startActivity(intent)
            } catch (e: Exception) {
                AnalyticsManager.logError(
                    getString(R.string.ad_click_error), 
                    getString(R.string.error_open_url, adUrl)
                )
            }
        }
        
        // Log ad impression
        AnalyticsManager.logAdImpression(adItem.bannerId ?: getString(R.string.unknown_title), placement)
    }
    
    private fun displayArticleContent() {
        contentItem?.let { article ->
            // Set title
            findViewById<TextView>(R.id.tvArticleTitle).text = article.title
            
            // Set content using HTML
            val contentTextView = findViewById<TextView>(R.id.tvArticleContent)
            if (!article.description.isNullOrEmpty()) {
                val htmlText = Html.fromHtml(article.description, Html.FROM_HTML_MODE_COMPACT)
                contentTextView.text = htmlText
            } else {
                contentTextView.visibility = View.GONE
            }
            
            // Set image if available
            val imageView = findViewById<ImageView>(R.id.ivArticleImage)
            article.getFullImageUrl()?.let { imageUrl ->
                Glide.with(this)
                    .load(imageUrl)
                    .into(imageView)
            } ?: run {
                // Hide image view if no image
                imageView.visibility = View.GONE
            }
            
            // Log article view in repository
            article.articleId?.let { id ->
                repository.logContentView(id)
            }
        }
    }
} 