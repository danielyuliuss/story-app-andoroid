package com.danielys.storyapp.view.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.danielys.storyapp.data.response.ListStoryItem
import com.danielys.storyapp.databinding.ItemStoryBinding
import com.danielys.storyapp.view.detail.DetailActivity

class StoriesAdapter(private val context: Context) :
    PagingDataAdapter<ListStoryItem, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ItemStoryBinding, private val context: Context) :
        ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {

            Glide.with(context).load(data.photoUrl).into(binding.imageViewStory)
            binding.textViewNama.text = data.name

            binding.imageViewStory.setOnClickListener {

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        it.context as Activity,
                        androidx.core.util.Pair(binding.textViewNama, "name"),
                        androidx.core.util.Pair(binding.imageViewStory, "photo"),
                    )

                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("id_story", data.id)
                ContextCompat.startActivity(context, intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem, newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem, newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}