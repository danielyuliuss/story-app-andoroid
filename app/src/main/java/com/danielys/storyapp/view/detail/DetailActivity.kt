package com.danielys.storyapp.view.detail

import android.animation.ObjectAnimator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.danielys.storyapp.R
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.databinding.ActivityDetailBinding
import com.danielys.storyapp.view.ViewModelFactoryPreferences

class DetailActivity : AppCompatActivity() {
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var binding: ActivityDetailBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        val pref = UserPreferences.getInstance(dataStore)
        detailViewModel = ViewModelProvider(this, ViewModelFactoryPreferences(pref)).get(
            DetailViewModel::class.java
        )


        val idStory = intent.getStringExtra("id_story")

        detailViewModel.getToken().observe(this) {
            if (idStory != null) {
                detailViewModel.getDetail(it, idStory)
            } else {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.failed_load),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

        detailViewModel.dataDetail.observe(this) { detailStoryResponse ->
            if (detailStoryResponse.error != true) {
                Glide.with(this)
                    .load(detailStoryResponse.story?.photoUrl)
                    .into(binding.imageViewFoto)
                binding.textViewNamaDetail.text = detailStoryResponse.story?.name
                binding.textViewDeskripsi.text = detailStoryResponse.story?.description
            } else {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.failed_load),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

        detailViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, "Error : $errorMessage", Toast.LENGTH_SHORT).show()
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.textViewDeskripsi, View.ALPHA, 1f).apply {
            duration = 1500
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}