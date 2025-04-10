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

/**
 * Адаптер для отображения контента в RecyclerView.
 * Поддерживает различные типы элементов: статьи и рекламные блоки.
 * 
 * @param onItemClick Обработчик клика по элементу списка
 */
class ContentAdapter(
    private val onItemClick: (ContentItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Список отображаемых элементов
    private val items = mutableListOf<ContentItem>()

    companion object {
        // Типы элементов для RecyclerView
        private const val VIEW_TYPE_ARTICLE = 0
        private const val VIEW_TYPE_INLINE_AD = 1
        private const val VIEW_TYPE_BANNER = 2
    }

    /**
     * Устанавливает новый список элементов для отображения
     * @param newItems Список элементов контента
     */
    fun setItems(newItems: List<ContentItem>) {
        android.util.Log.d("ContentAdapter", "Установка ${newItems.size} элементов в адаптер")
        
        // Группируем элементы по типам для статистики
        val articles = newItems.filter { it.contentType == "article" }
        val banners = newItems.filter { it.contentType == "banner" }
        val inlineAds = newItems.filter { it.contentType == "inline_ad" || it.contentType == "item_ad" }
        
        android.util.Log.d("ContentAdapter", "Типы элементов: статьи=${articles.size}, баннеры=${banners.size}, реклама=${inlineAds.size}")
        
        // Обновляем данные и уведомляем адаптер
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()

        // Логируем содержимое для отладки
        newItems.forEachIndexed { index, item ->
            android.util.Log.d("ContentAdapter",
                "Item $index: ${item.contentType}, title=${item.title?.take(10)}...")
        }
    }

    /**
     * Определяет тип элемента на указанной позиции
     */
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

    /**
     * Создает ViewHolder в зависимости от типа элемента
     */
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

    /**
     * ViewHolder для отображения статей
     */
    inner class ArticleViewHolder(
        itemView: View,
        private val onItemClick: (ContentItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivArticleImage)
        private val titleView: TextView = itemView.findViewById(R.id.tvArticleTitle)
        private val descriptionView: TextView = itemView.findViewById(R.id.tvArticleDescription)

        /**
         * Заполняет элемент данными статьи
         */
        fun bind(item: ContentItem) {
            // Устанавливаем заголовок
            titleView.text = item.title
            
            // Очищаем HTML-разметку для предпросмотра
            val description = item.description?.let {
                Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT).toString()
            } ?: ""
            descriptionView.text = description
            
            // Загружаем изображение
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
                    // Если URL отсутствует, показываем заглушку
                    imageView.setImageResource(R.drawable.ic_launcher_foreground)
                    android.util.Log.w("ArticleViewHolder", "No image URL for article: ${item.title}")
                }
            } catch (e: Exception) {
                // Обрабатываем ошибки загрузки изображения
                android.util.Log.e("ArticleViewHolder", "Error loading image for: ${item.title}", e)
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
            
            // Устанавливаем обработчик клика на карточку статьи
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

        // Вспомогательный метод для преобразования dp в пиксели
        private fun dpToPx(dp: Int): Int {
            val scale = itemView.context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }

        fun bind(item: ContentItem) {
            android.util.Log.d("ContentAdapter", "Binding InlineAdViewHolder with item: ${item.title}")
            
            // Проверяем наличие валидного описания
            val hasDescription = !item.description.isNullOrEmpty() && item.description != "ne null"
            
            // Проверяем, является ли это специальным баннером по размеру в заголовке
            val isFullWidthBanner = item.title?.let {
                it.contains("320X90", ignoreCase = true) || 
                it.contains("320x90", ignoreCase = true) ||
                it.contains("320X50", ignoreCase = true) ||
                it.contains("320x50", ignoreCase = true) ||
                it.contains("320X250", ignoreCase = true) ||
                it.contains("320x250", ignoreCase = true)
            } ?: false
            
            if (isFullWidthBanner) {
                // Если это специальный баннер, показываем только изображение
                adLabel.visibility = View.GONE
                titleView.visibility = View.GONE
                descriptionView.visibility = View.GONE
                orgInfoView.visibility = View.GONE
                actionButton.visibility = View.GONE
                
                // Настраиваем изображение на всю ширину экрана
                val layoutParams = imageView.layoutParams as ViewGroup.LayoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                
                // Устанавливаем высоту в зависимости от размера баннера
                if (item.title?.contains("320X50", ignoreCase = true) == true || 
                    item.title?.contains("320x50", ignoreCase = true) == true) {
                    layoutParams.height = dpToPx(50)
                    android.util.Log.d("InlineAdViewHolder", "Отображение баннера 320x50")
                } else if (item.title?.contains("320X250", ignoreCase = true) == true || 
                          item.title?.contains("320x250", ignoreCase = true) == true) {
                    layoutParams.height = dpToPx(250)
                    android.util.Log.d("InlineAdViewHolder", "Отображение баннера 320x250")
                } else {
                    // Для 320x90 и других
                    layoutParams.height = dpToPx(90)
                    android.util.Log.d("InlineAdViewHolder", "Отображение баннера 320x90")
                }
                
                imageView.layoutParams = layoutParams
                imageView.adjustViewBounds = true
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                
                // Убираем все внешние отступы
                val params = itemView.layoutParams as? ViewGroup.MarginLayoutParams
                params?.setMargins(0, 0, 0, 0)
                itemView.layoutParams = params
                
                android.util.Log.d("InlineAdViewHolder", "Отображение баннера на всю ширину без отступов")
            } else {
                // Обычный режим отображения
                adLabel.visibility = if (item.shouldShowAdLabel()) View.VISIBLE else View.GONE
                titleView.visibility = View.VISIBLE
                
                titleView.text = item.title
                if (hasDescription) {
                    descriptionView.text = item.description
                }
                
                orgInfoView.visibility = if (item.orgInfo.isNullOrEmpty()) View.GONE else View.VISIBLE
                actionButton.visibility = View.VISIBLE
                
                // Динамическое масштабирование изображения
                val layoutParams = imageView.layoutParams
                // Сохраняем пропорции для динамического масштабирования
                imageView.adjustViewBounds = true
                imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                imageView.layoutParams = layoutParams
                
                // Установка текста кнопки
                if (!item.buttonCaption.isNullOrEmpty()) {
                    actionButton.text = item.buttonCaption
                } else {
                    actionButton.text = itemView.context.getString(R.string.ad_details_button)
                }
                
                android.util.Log.d("InlineAdViewHolder", "Отображение в обычном режиме с динамическим масштабированием")
            }
            
            // Загрузка изображения в любом случае
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
        private val actionButton: TextView = itemView.findViewById(R.id.btnAdAction)
        
        // Вспомогательный метод для преобразования dp в пиксели
        private fun dpToPx(dp: Int): Int {
            val scale = itemView.context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }

        fun bind(item: ContentItem) {
            android.util.Log.d("ContentAdapter", "Binding BannerAdViewHolder with item: ${item.title}")
            
            // Проверяем наличие валидного описания
            val hasDescription = !item.description.isNullOrEmpty() && item.description != "ne null"
            
            // Проверяем, является ли это специальным баннером по размеру в заголовке
            val isFullWidthBanner = item.title?.let {
                it.contains("320X90", ignoreCase = true) || 
                it.contains("320x90", ignoreCase = true) ||
                it.contains("320X50", ignoreCase = true) ||
                it.contains("320x50", ignoreCase = true) ||
                it.contains("320X250", ignoreCase = true) ||
                it.contains("320x250", ignoreCase = true)
            } ?: false
            
            if (isFullWidthBanner) {
                // Если это специальный баннер, показываем только изображение
                adLabel.visibility = View.GONE
                titleView.visibility = View.GONE
                descriptionView.visibility = View.GONE
                actionButton.visibility = View.GONE
                
                // Настраиваем изображение на всю ширину экрана
                val layoutParams = imageView.layoutParams as ViewGroup.LayoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                
                // Устанавливаем высоту в зависимости от размера баннера
                if (item.title?.contains("320X50", ignoreCase = true) == true || 
                    item.title?.contains("320x50", ignoreCase = true) == true) {
                    layoutParams.height = dpToPx(50)
                    android.util.Log.d("BannerAdViewHolder", "Отображение баннера 320x50")
                } else if (item.title?.contains("320X250", ignoreCase = true) == true || 
                          item.title?.contains("320x250", ignoreCase = true) == true) {
                    layoutParams.height = dpToPx(250)
                    android.util.Log.d("BannerAdViewHolder", "Отображение баннера 320x250")
                } else {
                    // Для 320x90 и других
                    layoutParams.height = dpToPx(90)
                    android.util.Log.d("BannerAdViewHolder", "Отображение баннера 320x90")
                }
                
                imageView.layoutParams = layoutParams
                imageView.adjustViewBounds = true
                imageView.scaleType = ImageView.ScaleType.FIT_XY
                
                // Убираем все внешние отступы
                val params = itemView.layoutParams as? ViewGroup.MarginLayoutParams
                params?.setMargins(0, 0, 0, 0)
                itemView.layoutParams = params
                
                android.util.Log.d("BannerAdViewHolder", "Отображение баннера на всю ширину без отступов")
            } else {
                // Обычный режим отображения
                adLabel.visibility = if (item.shouldShowAdLabel()) View.VISIBLE else View.GONE
                titleView.visibility = View.VISIBLE
                
                titleView.text = item.title
                if (hasDescription) {
                    descriptionView.text = item.description
                }
                
                actionButton.visibility = View.VISIBLE
                
                // Динамическое масштабирование изображения
                val layoutParams = imageView.layoutParams
                // Сохраняем пропорции для динамического масштабирования
                imageView.adjustViewBounds = true
                imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                imageView.layoutParams = layoutParams
                
                // Установка текста кнопки
                if (!item.buttonCaption.isNullOrEmpty()) {
                    actionButton.text = item.buttonCaption
                } else {
                    actionButton.text = itemView.context.getString(R.string.ad_details_button)
                }
                
                android.util.Log.d("BannerAdViewHolder", "Отображение в обычном режиме с динамическим масштабированием")
            }
            
            // Загрузка изображения в любом случае
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
            
            // Handle click on action button for normal mode
            actionButton.setOnClickListener { onItemClick(item) }
            
            // Handle click on the entire item
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
} 