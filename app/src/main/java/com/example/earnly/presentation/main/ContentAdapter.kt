package com.example.earnly.presentation.main

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.earnly.R
import com.example.earnly.data.model.ContentItem

class ContentAdapter(
    private val onItemClick: (ContentItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ContentItem>()

    companion object {
        private const val VIEW_TYPE_ARTICLE = 0
        private const val VIEW_TYPE_INLINE_AD = 1
        private const val VIEW_TYPE_BANNER = 2
    }

    fun setItems(newItems: List<ContentItem>) {
        android.util.Log.d("ContentAdapter", "Установка ${newItems.size} элементов в адаптер")
        
        // Анализ типов элементов
        val articles = newItems.filter { it.contentType == "article" }
        val banners = newItems.filter { it.contentType == "banner" }
        val inlineAds = newItems.filter { it.contentType == "inline_ad" || it.contentType == "item_ad" }
        
        android.util.Log.d("ContentAdapter", "Типы элементов: статьи=${articles.size}, баннеры=${banners.size}, реклама=${inlineAds.size}")
        
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()

        // Логирование для отладки
        newItems.forEachIndexed { index, item ->
            android.util.Log.d("ContentAdapter",
                "Item $index: ${item.contentType}, title=${item.title?.take(10)}...")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when {
            item.contentType == "article" -> {
                android.util.Log.v("ContentAdapter", "Article at $position")
                VIEW_TYPE_ARTICLE
            }
            item.contentType == "banner" -> {
                android.util.Log.v("ContentAdapter", "Banner at $position")
                VIEW_TYPE_BANNER
            }
            else -> {
                android.util.Log.v("ContentAdapter", "Ad at $position")
                VIEW_TYPE_INLINE_AD
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ARTICLE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_article, parent, false)
                ArticleViewHolder(view, onItemClick)
            }
            VIEW_TYPE_BANNER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_ad_banner, parent, false)
                BannerAdViewHolder(view, onItemClick)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_ad_inline, parent, false)
                InlineAdViewHolder(view, onItemClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is ArticleViewHolder -> holder.bind(item)
            is InlineAdViewHolder -> holder.bind(item)
            is BannerAdViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    // ViewHolder for Articles
    inner class ArticleViewHolder(
        itemView: View,
        private val onItemClick: (ContentItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivArticleImage)
        private val titleView: TextView = itemView.findViewById(R.id.tvArticleTitle)
        private val descriptionView: TextView = itemView.findViewById(R.id.tvArticleDescription)

        fun bind(item: ContentItem) {
            titleView.text = item.title
            
            // Clean HTML for preview
            val description = item.description?.let {
                Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT).toString()
            } ?: ""
            descriptionView.text = description
            
            // Simplified image loading
            try {
                val imageUrl = item.getFullImageUrl()
                android.util.Log.d("ArticleViewHolder", "Loading image: $imageUrl")
                
                if (imageUrl != null) {
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imageView)
                } else {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground)
                    android.util.Log.w("ArticleViewHolder", "No image URL for article: ${item.title}")
                }
            } catch (e: Exception) {
                android.util.Log.e("ArticleViewHolder", "Error loading image for: ${item.title}", e)
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
            
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    // ViewHolder for Inline Ads
    inner class InlineAdViewHolder(
        itemView: View,
        private val onItemClick: (ContentItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val adLabel: TextView = itemView.findViewById(R.id.tvAdLabel)
        private val imageView: ImageView = itemView.findViewById(R.id.ivAdImage)
        private val titleView: TextView = itemView.findViewById(R.id.tvAdTitle)
        private val descriptionView: TextView = itemView.findViewById(R.id.tvAdDescription)
        private val orgInfoView: TextView = itemView.findViewById(R.id.tvAdOrgInfo)
        private val actionButton: Button = itemView.findViewById(R.id.btnAdAction)

        fun bind(item: ContentItem) {
            // Show/hide ad label
            adLabel.visibility = if (item.shouldShowAdLabel()) View.VISIBLE else View.GONE
            
            titleView.text = item.title
            descriptionView.text = item.description
            orgInfoView.text = item.orgInfo
            
            // Установка текста кнопки, если есть buttonCaption
            if (!item.buttonCaption.isNullOrEmpty()) {
                actionButton.text = item.buttonCaption
                actionButton.visibility = View.VISIBLE
            } else {
                actionButton.text = itemView.context.getString(R.string.ad_details_button)
                actionButton.visibility = View.VISIBLE
            }
            
            // Set org info visibility
            orgInfoView.visibility = if (item.orgInfo.isNullOrEmpty()) View.GONE else View.VISIBLE
            
            // Simplified image loading
            try {
                val imageUrl = item.getFullImageUrl()
                android.util.Log.d("InlineAdViewHolder", "Loading image: $imageUrl")
                
                if (imageUrl != null) {
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imageView)
                } else {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground)
                    android.util.Log.w("InlineAdViewHolder", "No image URL for ad: ${item.title}")
                }
            } catch (e: Exception) {
                android.util.Log.e("InlineAdViewHolder", "Error loading image for: ${item.title}", e)
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
            
            // Обработка нажатия на кнопку действия
            actionButton.setOnClickListener { onItemClick(item) }
            
            // Обработка нажатия на весь элемент
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    // ViewHolder for Banner Ads
    inner class BannerAdViewHolder(
        itemView: View,
        private val onItemClick: (ContentItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val adLabel: TextView = itemView.findViewById(R.id.tvAdLabel)
        private val imageView: ImageView = itemView.findViewById(R.id.ivAdImage)
        private val titleView: TextView = itemView.findViewById(R.id.tvAdTitle)
        private val descriptionView: TextView = itemView.findViewById(R.id.tvAdDescription)
        private val shortIdView: TextView = itemView.findViewById(R.id.tvShortId)
        private val actionButton: Button = itemView.findViewById(R.id.btnAdAction)

        fun bind(item: ContentItem) {
            // Show/hide ad label based on flag
            adLabel.visibility = if (item.shouldShowAdLabel()) View.VISIBLE else View.GONE
            
            // Set title and description
            titleView.text = item.title
            descriptionView.text = item.description
            
            // Установка текста кнопки, если есть buttonCaption
            if (!item.buttonCaption.isNullOrEmpty()) {
                actionButton.text = item.buttonCaption
            } else {
                actionButton.text = itemView.context.getString(R.string.ad_details_button)
            }
            
            // Simplified image loading
            try {
                val imageUrl = item.getFullImageUrl()
                android.util.Log.d("BannerAdViewHolder", "Loading image: $imageUrl")
                
                if (imageUrl != null) {
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imageView)
                } else {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground)
                    android.util.Log.w("BannerAdViewHolder", "No image URL for banner: ${item.title}")
                }
            } catch (e: Exception) {
                android.util.Log.e("BannerAdViewHolder", "Error loading image for: ${item.title}", e)
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
            
            // Handle click on action button
            actionButton.setOnClickListener { onItemClick(item) }
            
            // Handle click on the entire item
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
} 