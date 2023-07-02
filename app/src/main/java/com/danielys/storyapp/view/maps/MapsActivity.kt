package com.danielys.storyapp.view.maps

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.danielys.storyapp.R
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.data.response.ListStoryItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.danielys.storyapp.databinding.ActivityMapsBinding
import com.danielys.storyapp.view.ViewModelFactoryPreferences
import com.danielys.storyapp.view.login.LoginActivity
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var listStories: List<ListStoryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val pref = UserPreferences.getInstance(dataStore)
        mapsViewModel = ViewModelProvider(this, ViewModelFactoryPreferences(pref)).get(
            MapsViewModel::class.java
        )

        mapsViewModel.getToken().observe(this) { token ->
            if (token != "") {
                mapsViewModel.getStories(token)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        mapsViewModel.stories.observe(this) { getAllStoryResponse ->
            val listStoriesvm = getAllStoryResponse.listStory
            if (listStoriesvm != null) {
                listStories = listStoriesvm as List<ListStoryItem>
                addManyMarker()
            }
        }

        mapsViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        mapsViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, "Error : $errorMessage", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()
    }


    private val boundsBuilder = LatLngBounds.Builder()

    private fun addManyMarker() {

        listStories.forEach { listStoryItem ->
            val latLng = LatLng(listStoryItem.lat as Double, listStoryItem.lon as Double)
            mMap.addMarker(MarkerOptions().position(latLng).title(listStoryItem.name))
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                400
            )
        )
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}