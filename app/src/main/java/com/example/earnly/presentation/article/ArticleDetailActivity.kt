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
    
    companion object {
        const val EXTRA_CONTENT_ITEM = "extra_content_item"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)
        
        repository = ContentRepository(this)
        
        // Set up the toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        
        // Get the content item from the intent
        contentItem = intent.getSerializableExtra(EXTRA_CONTENT_ITEM) as? ContentItem
        
        if (contentItem == null) {
            finish()
            return
        }
        
        // Log screen view
        AnalyticsManager.logScreenView("article_detail")
        
        // Load ads
        loadAds()
        
        // Display article content
        displayArticleContent()
    }
    
    private fun loadAds() {
        lifecycleScope.launch {
            try {
                val response = repository.getContentForTab("money") // Используем текущую вкладку
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { apiResponse ->
                            // Get top ad
                            topAdItem = apiResponse.topAdItems.firstOrNull()
                            
                            // Get bottom ad
                            bottomAdItem = apiResponse.bottomAdItems.firstOrNull()
                            
                            // Setup ads
                            setupAds()
                        }
                    }
                    is Resource.Error -> {
                        // Log error but continue showing article
                        AnalyticsManager.logError("article_detail_ads", "Failed to load ads: ${response.message}")
                    }
                    is Resource.Loading -> {
                        // Do nothing, wait for success or error
                    }
                }
            } catch (e: Exception) {
                // Log error but continue showing article
                AnalyticsManager.logError("article_detail_ads", "Failed to load ads: ${e.message ?: "Unknown error"}")
            }
        }
    }
    
    private fun displayArticleContent() {
        contentItem?.let { article ->
            // Set title
            findViewById<TextView>(R.id.tvArticleTitle).text = article.title
            
            // Set content using HTML
            val contentTextView = findViewById<TextView>(R.id.tvArticleContent)
            article.description?.let { content ->
                contentTextView.text = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    @Suppress("DEPRECATION")
                    Html.fromHtml(content)
                }
            }
            
            // Load image
            val imageView = findViewById<ImageView>(R.id.ivArticleImage)
            article.getFullImageUrl()?.let { imageUrl ->
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(imageView)
            }
            
            // Log article view
            article.articleId?.let { id ->
                repository.logContentView(id)
            }
        }
    }
    
    private fun setupAds() {
        // Set up top ad if needed
        if (contentItem?.shouldShowTopAd() == true) {
            setupAdContainer(findViewById(R.id.topAdContainer), topAdItem, "adsTop")
        }
        
        // Set up bottom ad if needed
        if (contentItem?.shouldShowBottomAd() == true) {
            setupAdContainer(findViewById(R.id.bottomAdContainer), bottomAdItem, "adsBottom")
        }
    }
    
    private fun setupAdContainer(container: FrameLayout, adItem: ContentItem?, placement: String) {
        if (adItem == null) {
            container.visibility = View.GONE
            return
        }
        
        container.visibility = View.VISIBLE
        
        // Find ad views
        val adImageView = container.findViewById<ImageView>(R.id.ivAdImage)
        val adTitle = container.findViewById<TextView>(R.id.tvAdTitle)
        val adDescription = container.findViewById<TextView>(R.id.tvAdDescription)
        val adLabel = container.findViewById<TextView>(R.id.tvAdLabel)
        val adButton = container.findViewById<TextView>(R.id.btnAdAction)
        
        // Set ad content and visibility
        adTitle.visibility = if (adItem.title.isNullOrEmpty()) View.GONE else View.VISIBLE
        adTitle.text = adItem.title
        
        adDescription.visibility = if (adItem.description.isNullOrEmpty()) View.GONE else View.VISIBLE
        adDescription.text = adItem.description
        
        // Show ad label if needed
        adLabel.visibility = if (adItem.shouldShowAdLabel()) View.VISIBLE else View.GONE
        
        // Set button text if available
        adButton.text = adItem.buttonCaption ?: getString(R.string.ad_learn_more)
        
        // Load ad image
        adItem.getFullImageUrl()?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(adImageView)
        }
        
        // Set click listener for the ad
        container.setOnClickListener {
            // Log ad click
            AnalyticsManager.logAdClick(adItem.bannerId ?: "unknown", placement)
            
            // Open browser with ad URL
            val adUrl = "https://dohodinfor.ru/?app_key=${EarnlyApplication.APP_KEY}&ad_id=${adItem.bannerId}&placement=$placement"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(adUrl)))
        }
    }
} 