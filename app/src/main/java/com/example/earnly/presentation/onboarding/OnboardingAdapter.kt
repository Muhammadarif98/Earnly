package com.example.earnly.presentation.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.earnly.R

class OnboardingAdapter(private val onboardingItems: List<OnboardingItem>) : 
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_onboarding_slide, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }

    inner class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageOnboarding = itemView.findViewById<ImageView>(R.id.ivOnboardingImage)
        private val textTitle = itemView.findViewById<TextView>(R.id.tvOnboardingTitle)
        private val textDescription = itemView.findViewById<TextView>(R.id.tvOnboardingDescription)

        fun bind(onboardingItem: OnboardingItem) {
            imageOnboarding.setImageResource(onboardingItem.imageResId)
            textTitle.text = onboardingItem.title
            textDescription.text = onboardingItem.description
        }
    }
}

data class OnboardingItem(
    val imageResId: Int,
    val title: String,
    val description: String
) 