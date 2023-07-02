package com.danielys.storyapp.view.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.danielys.storyapp.R
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.databinding.ActivityAddStoryBinding
import com.danielys.storyapp.utils.createCustomTempFile
import com.danielys.storyapp.utils.reduceFileImage
import com.danielys.storyapp.utils.uriToFile
import com.danielys.storyapp.view.ViewModelFactoryPreferences
import com.danielys.storyapp.view.login.LoginActivity
import com.danielys.storyapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isLocation: Boolean = false
    private lateinit var locationLast: Location

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.permission_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                    Toast.makeText(
                        this,
                        "Grant the permission first for saving location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.imageViewAdd.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.imageViewAdd.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val pref = UserPreferences.getInstance(dataStore)
        addStoryViewModel = ViewModelProvider(this, ViewModelFactoryPreferences(pref)).get(
            AddStoryViewModel::class.java
        )

        addStoryViewModel.getToken().observe(this) { token ->
            if (token != "") {
                binding.buttonUpload.setOnClickListener {
                    if (isLocation) uploadImageLocation(token) else uploadImage(token)
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        addStoryViewModel.responseUpload.observe(this) { response ->
            if (response.error == false) {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.success_upload),
                    Toast.LENGTH_SHORT
                ).show()
                val intentList = Intent(this@AddStoryActivity, MainActivity::class.java)
                startActivity(intentList)
                finish()
            } else {
                Toast.makeText(
                    this,
                    applicationContext.getString(R.string.failed_upload),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        addStoryViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, "Error : $errorMessage", Toast.LENGTH_SHORT).show()
        }

        addStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.buttonCamera.setOnClickListener { startTakePhoto() }
        binding.buttonGaleri.setOnClickListener { startGallery() }

        binding.editTextDescription.addTextChangedListener {
            if (binding.editTextDescription.text.isNotEmpty() && getFile != null) {
                binding.buttonUpload.isEnabled = true
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.checkBoxLocation.setOnClickListener {
            if (!binding.checkBoxLocation.isChecked) {
                binding.checkBoxLocation.isChecked = false
                isLocation = false
            } else {
                getMyLastLocation()
            }
        }
    }

    private fun uploadImage(token: String) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = binding.editTextDescription.text.toString()
                .toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            addStoryViewModel.uploadFile(token, imageMultipart, description)

        } else {
            Toast.makeText(
                this,
                applicationContext.getString(R.string.insertfirst),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadImageLocation(token: String) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val longitude =
                locationLast.longitude.toString().toRequestBody("text/plain".toMediaType())
            val latitude =
                locationLast.latitude.toString().toRequestBody("text/plain".toMediaType())

            val description = binding.editTextDescription.text.toString()
                .toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            addStoryViewModel.uploadFile(token, imageMultipart, description, latitude, longitude)

        } else {
            Toast.makeText(
                this,
                applicationContext.getString(R.string.insertfirst),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.danielys.storyapp.view.addstory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser =
            Intent.createChooser(intent, applicationContext.getString(R.string.choosepicture))
        launcherIntentGallery.launch(chooser)
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    binding.checkBoxLocation.isChecked = true
                    isLocation = true
                    locationLast = location
                } else {
                    binding.checkBoxLocation.isChecked = false
                    Toast.makeText(
                        this,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}