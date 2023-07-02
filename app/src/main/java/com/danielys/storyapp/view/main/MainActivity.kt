package com.danielys.storyapp.view.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielys.storyapp.R
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.databinding.ActivityMainBinding
import com.danielys.storyapp.view.ViewModelFactoryPreferences
import com.danielys.storyapp.view.addstory.AddStoryActivity
import com.danielys.storyapp.view.login.LoginActivity
import com.danielys.storyapp.view.maps.MapsActivity

class MainActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val adapter = StoriesAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactoryPreferences(pref)).get(
            MainViewModel::class.java
        )

        binding.floatingActionButtonAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        mainViewModel.getToken().observe(this) { token ->
            if (intent.getStringExtra("token") != null) {
                mainViewModel.tokenInput = intent.getStringExtra("token")!!
                getData()
            }
            else if (token != "") {
                mainViewModel.tokenInput = token
                getData()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun getData() {
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.recyclerViewStories.addItemDecoration(itemDecoration)

        binding.recyclerViewStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.storiesData.observe(this) {
            adapter.submitData(lifecycle, it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemLogout -> {
                with(mainViewModel)
                {
                    setName("")
                    setToken("")
                    setUserId("")
                }
                finish()
                return true
            }

            R.id.itemLanguage -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }

            R.id.itemMaps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return true
        }
    }

}