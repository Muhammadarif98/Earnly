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
import com.bumptech.glide.Glide
import com.example.earnly.EarnlyApplication
import com.example.earnly.R
import com.example.earnly.data.model.ContentItem
import com.example.earnly.data.repository.ContentRepository
import com.example.earnly.domain.analytics.AnalyticsManager
import com.google.android.material.appbar.MaterialToolbar

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var repository: ContentRepository
    private var contentItem: ContentItem? = null
    
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
        
        // Display article content
        displayArticleContent()
        
        // Display ads if needed
        setupAds()
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
        contentItem?.let { article ->
            // Set up top ad if needed
            if (article.shouldShowTopAd()) {
                setupAdContainer(findViewById(R.id.topAdContainer), "adsTop")
            }
            
            // Set up bottom ad if needed
            if (article.shouldShowBottomAd()) {
                setupAdContainer(findViewById(R.id.bottomAdContainer), "adsBottom")
            }
        }
    }
    
    private fun setupAdContainer(container: FrameLayout, placement: String) {
        container.visibility = View.VISIBLE
        
        // Find ad views
        val adImageView = container.findViewById<ImageView>(R.id.ivAdImage)
        val adLabel = container.findViewById<TextView>(R.id.tvAdLabel)
        
        // Default ad - can be replaced with actual ad data if available
        adLabel.visibility = View.VISIBLE
        
        // You would typically fetch an ad from the API here
        // For simplicity, using a placeholder
        Glide.with(this)
            .load(R.drawable.ic_launcher_foreground)
            .into(adImageView)
        
        // Set click listener for the ad
        container.setOnClickListener {
            // Log ad click
            AnalyticsManager.logAdClick("default_ad", placement)
            
            // Open browser with ad URL
            val adUrl = "https://dohodinfor.ru/?app_key=${EarnlyApplication.APP_KEY}&ad_id=default_ad&placement=$placement"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(adUrl)))
        }
    }
} 