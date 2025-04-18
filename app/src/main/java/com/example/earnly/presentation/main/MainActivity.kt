package com.example.earnly.presentation.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.earnly.EarnlyApplication
import com.example.earnly.R
import com.example.earnly.data.model.ContentItem
import com.example.earnly.data.preferences.PreferenceManager
import com.example.earnly.domain.analytics.AnalyticsManager
import com.example.earnly.presentation.article.ArticleDetailActivity
import com.example.earnly.presentation.onboarding.OnboardingActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var contentAdapter: ContentAdapter
    private lateinit var preferenceManager: PreferenceManager
    
    // Тестируем вкладки, указанные в ТЗ
    private val tabs = listOf("main","other")
    
    // Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View
    private lateinit var errorLayout: View
    private lateinit var btnRetry: Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferenceManager = PreferenceManager(this)
        
        // Check if first launch, show onboarding if needed
        if (preferenceManager.isFirstLaunch()) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_main)
        
        // Log screen view
        AnalyticsManager.logScreenView(getString(R.string.main_screen))
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)
        btnRetry = findViewById(R.id.btnRetry)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        tabLayout = findViewById(R.id.tabLayout)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Setup adapter
        contentAdapter = ContentAdapter { item ->
            onContentItemClicked(item)
        }
        recyclerView.adapter = contentAdapter
        
        // Track scroll depth
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                
                if (totalItemCount > 0) {
                    val scrollPercentage = (lastVisibleItem.toFloat() / totalItemCount.toFloat() * 100).toInt()
                    AnalyticsManager.logScrollDepth(getString(R.string.main_screen), scrollPercentage)
                }
            }
        })
        
        // Setup tabs
        
        // Add tabs
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_money_ways))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_other_ways))
        
        // Set tab selection listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedTab = tabs[tab.position]
                android.util.Log.d(TAG, "Выбрана вкладка: $selectedTab (позиция: ${tab.position})")
                viewModel.loadContent(selectedTab)
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            
            override fun onTabReselected(tab: TabLayout.Tab) {
                val selectedTab = tabs[tab.position]
                android.util.Log.d(TAG, "Повторно выбрана вкладка: $selectedTab (позиция: ${tab.position})")
                viewModel.loadContent(selectedTab)
            }
        })
        
        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            android.util.Log.d(TAG, "Обновление контента по свайпу")
            viewModel.refreshContent()
        }
        
        // Handle retry button
        btnRetry.setOnClickListener {
            viewModel.loadContent(tabs[tabLayout.selectedTabPosition])
        }
        
        // Observe ViewModel
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewModel.contentState.observe(this) { state ->
            when (state) {
                is MainViewModel.ContentState.Loading -> showLoading()
                is MainViewModel.ContentState.Success -> {
                    // Останавливаем индикатор обновления, если он активен
                    if (swipeRefreshLayout.isRefreshing) {
                        swipeRefreshLayout.isRefreshing = false
                    }
                    
                    val allItems = state.data.recyclerItems
                    android.util.Log.d(TAG, "==================== API RESPONSE ANALYSIS ====================")
                    android.util.Log.d(TAG, "Received total items: ${allItems.size}")
                    
                    // Анализ по типам контента
                    val articleCount = allItems.count { it.contentType == "article" }
                    val bannerCount = allItems.count { it.contentType == "banner" }
                    val inlineAdCount = allItems.count { it.contentType == "inline_ad" || it.contentType == "item_ad" }
                    
                    android.util.Log.d(TAG, "Content breakdown: Articles: $articleCount, Banners: $bannerCount, Inline ads: $inlineAdCount")
                    
                    // Подробный вывод всех элементов для анализа
                    allItems.forEachIndexed { index, item ->
                        android.util.Log.d(TAG, "Item $index: Type=${item.contentType}, Title=${item.title}, HasImage=${!item.imageUrl.isNullOrEmpty()}")
                    }
                    android.util.Log.d(TAG, "==============================================================")
                    
                    showContent(allItems)
                    
                    // Log success
                    AnalyticsManager.logEvent("content_displayed", 
                        mapOf("items_count" to allItems.size.toString()))
                }
                is MainViewModel.ContentState.Error -> {
                    // Останавливаем индикатор обновления, если он активен
                    if (swipeRefreshLayout.isRefreshing) {
                        swipeRefreshLayout.isRefreshing = false
                    }
                    
                    showError(state.message)
                }
            }
        }
        
        viewModel.selectedContent.observe(this) { content ->
            content?.let {
                startArticleDetailActivity(it)
                viewModel.clearSelectedContent() // Clear the selected content to avoid reprocessing
            }
        }
    }
    
    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.GONE
    }
    
    private fun showContent(contentItems: List<ContentItem>) {
        android.util.Log.d(TAG, "Displaying ${contentItems.size} items")
        
        // Убираем экран загрузки
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        
        // Отображаем контент
        if (contentItems.isEmpty()) {
            android.util.Log.w(TAG, "Content list is empty, showing error")
            showError("Нет доступного контента")
            return
        }
        
        contentAdapter.setItems(contentItems)
        recyclerView.visibility = View.VISIBLE
    }
    
    private fun showError(message: String = getString(R.string.error_no_internet)) {
        android.util.Log.d(TAG, "Показываем экран ошибки: $message")
        
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        
        try {
            val errorMessageTextView = errorLayout.findViewById<TextView>(R.id.tvErrorMessage)
            errorMessageTextView.text = message
        } catch (e: Exception) {
            android.util.Log.w(TAG, "TextView tvErrorMessage не найден в errorLayout")
        }
        
        btnRetry.setOnClickListener {
            viewModel.loadContent(tabs[tabLayout.selectedTabPosition])
        }
        
        // Log error
        AnalyticsManager.logError(getString(R.string.main_content_loading_error), message)
    }
    
    private fun onContentItemClicked(item: ContentItem) {
        when (item.contentType) {
            getString(R.string.article_type) -> {
                // Track article click
                AnalyticsManager.logArticleView(
                    item.articleId ?: getString(R.string.unknown_title), 
                    item.title ?: getString(R.string.unknown_title)
                )
                
                // Open article detail
                val intent = Intent(this, ArticleDetailActivity::class.java)
                intent.putExtra(ArticleDetailActivity.EXTRA_CONTENT_ITEM, item)
                startActivity(intent)
            }
            getString(R.string.banner_type), getString(R.string.inline_ad_type), getString(R.string.item_ad_type) -> {
                // Track ad click
                AnalyticsManager.logAdClick(
                    item.bannerId ?: getString(R.string.unknown_title), 
                    when (item.contentType) {
                        getString(R.string.inline_ad_type) -> getString(R.string.inline_placement)
                        getString(R.string.banner_type) -> getString(R.string.banner_placement)
                        else -> getString(R.string.item_ad_placement)
                    }
                )
                
                // Open ad link
                val adUrl = "https://dohodinfor.ru/?app_key=${EarnlyApplication.APP_KEY}&ad_id=${item.bannerId}&placement=${
                    when (item.contentType) {
                        getString(R.string.inline_ad_type) -> getString(R.string.inline_placement)
                        getString(R.string.banner_type) -> getString(R.string.banner_placement)
                        else -> getString(R.string.item_ad_placement)
                    }
                }"
                
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(adUrl))
                    startActivity(intent)
                } catch (e: Exception) {
                    AnalyticsManager.logError(
                        getString(R.string.main_ad_click_error), 
                        getString(R.string.error_open_url, adUrl)
                    )
                }
            }
        }
    }
    
    private fun startArticleDetailActivity(content: ContentItem) {
        val intent = Intent(this, ArticleDetailActivity::class.java)
        intent.putExtra(ArticleDetailActivity.EXTRA_CONTENT_ITEM, content)
        startActivity(intent)
    }
    
    override fun onBackPressed() {
        // Убираем ненужную проверку layoutError
        super.onBackPressed()
    }
} 